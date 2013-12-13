# Create your views here.


from django.core.exceptions import ObjectDoesNotExist

from rest_framework.authentication import TokenAuthentication
from rest_framework import status, serializers
from rest_framework.decorators import api_view, authentication_classes
from rest_framework.response import Response
from backend.utils import *
from models import *
from datetime import datetime, timedelta
from collections import defaultdict
from core_utils import *

TIME_STAMP_FORMAT = "%Y:%m:%d:%H:%M:%S"
DATE_FORMAT = "%Y:%m:%d"

NUM_TO_WEEKDAY = {
    1: 'Mon',
    2: 'Tue',
    3: 'Wed',
    4: 'Thu',
    5: 'Fri',
    6: 'Sat',
    7: 'Sun'
}

WEEKDAY_TO_NUM = {
    'Mon': 1,
    'Tue': 2,
    'Wed': 3,
    'Thu': 4,
    'Fri': 5,
    'Sat': 6,
    'Sun': 7
}

WEEKDAYS_SORTED = [weekday for num, weekday in sorted(NUM_TO_WEEKDAY.items(), key=lambda d: d[0])]

TASK_FIELDS = {'type', 'location', 'start_time', 'end_time', 'date', 'name', 'notes'}

USER_SELF = 0
IS_FRIEND = 1
FRIEND_REQUEST_SENT = 2
NOT_FRIEND = 3

COURSE = "course"
COURSES = "courses"
LECTURES = "lectures"
TASKS = "tasks"
AVATAR_PRE = "avatar_"
DEFAULT_AVATAR = "avatar_default"

class CourseSerializer(serializers.ModelSerializer):
    class Meta:
        model = Course
        fields = ('id', 'semester', 'course_name',
                  'section', 'instructor', 'description',
                  'course_code',)


class LectureSerializer(serializers.ModelSerializer):
    class Meta:
        model = Lecture


class UniversitySerializer(serializers.ModelSerializer):
    class Meta:
        model = University


class TaskSerializer(serializers.ModelSerializer):
    class Meta:
        model = Task


class PostSerializer(serializers.ModelSerializer):
    avatar = serializers.SerializerMethodField('get_avatar')

    class Meta:
        model = Post

    def get_avatar(self,obj):
        return get_user_avatar(obj.user_id)


class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = UserProfile


class MessageSerializer(serializers.ModelSerializer):
    class Meta:
        model = Message


#helper functions
def get_user_profile(user):
    user_profile, created = UserProfile.objects.get_or_create(user=user)
    return user_profile


def get_m2m_relation(user, model):
    user_profile = get_user_profile(user)
    many_to_many_field = model._meta.object_name.lower() + "s"
    return user_profile.__getattribute__(many_to_many_field)


def get_wrapper(key):
    def key_wrapper(value=None):
        return {key: value}

    return key_wrapper


def get_user_liked_posts(user_profile):
    return set([like.id for like in user_profile.likes.all()])



def get_username(user):
    return user.first_name + " " + user.last_name


def get_model_field_mapping(model):
    fields = model._meta.fields
    field_mapping = dict()
    for field in fields:
        field_mapping[field.name] = field
    return field_mapping


def get_lecture_summary(lecture_dict):
    summary = ""
    for lec_type, name in Lecture.TYPES:
        if lec_type in lecture_dict:
            lec_name = name
            if lecture_dict[lec_type] > 1:
                lec_name += "s"
            summary += "{0} {1} ".format(lecture_dict[lec_type], lec_name)
    if summary == "":
        return "No lectures"
    return summary


def get_task_summary(task_dict):
    task_num = task_dict.get("num", None)
    if task_num is None:
        return "No tasks"
    if task_num == 1:
        return task_dict["name"]
    else:
        return "{0} and {1} other tasks".format(task_dict["name"], task_dict["num"] - 1)

#LECTURE_WRAPPER = get_wrapper("lecturelist")
#TASK_WRAPPER = get_wrapper("tasklist")
#POST_WRAPPER = get_wrapper("postlist")


def convert_timestamp_datetime(datetime_str, format_str=TIME_STAMP_FORMAT):
    """
    convert yyyy:mm:dd:HH:MM:YY to datetime struct
    """
    return datetime.strptime(datetime_str, format_str)


def get_week_start_end(_date):
    """
    Get the start and end of week, given the date
    """
    day_in_week = _date.weekday()
    beginning_offset = timedelta(days=day_in_week)
    week_start = _date - beginning_offset
    end_offset = timedelta(days=6 - day_in_week)
    week_end = _date + end_offset

    return week_start, week_end


def lecture_ord(_lecture):
    return str(WEEKDAY_TO_NUM[_lecture.weekday]) + " " + str(_lecture.start_time)


def task_ord(_task):
    return str(_task.date) + " " + str(_task.start_time)


def sort_objects(objects, comp_func):
    return sorted(objects, key=lambda obj: comp_func(obj))


def extract_model_fields(request, accepted_fields):
    return {field: value for (field, value) in request.DATA.items() if field in accepted_fields}


def extend_course_fields(fields):
    """
    Append course information into model
    """
    course_id = fields.get('course_id', None)
    if course_id is not None:
        try:
            course = Course.objects.get(pk=course_id)
            course_code = course.course_code
            course_name = course.course_name
            fields["course_id"] = course.id
            fields["course_code"] = course_code
            fields["course_name"] = course_name
        except ObjectDoesNotExist:
            return fields
    return fields


def update_parent_childnum(fields):
    parent = fields.get('parent', None)
    if parent is not None:
        try:
            parent_post = Post.objects.get(id=parent)
            parent_post.children_num += 1
            parent_post.save()
        except ObjectDoesNotExist:
            return fields
    return fields


def message_summary(typed_messages, description_1, description_2):
    user_set = set()
    for message in typed_messages:
        user_set.add(message.user_from.id)
    first_message = typed_messages[0]
    username = get_username(first_message.user_from)
    if len(user_set) == 1:
        notification = "{0} has {1}".format(username, description_1)
    else:
        notification = "{0} and {1} other people have {2}". \
            format(username, len(user_set) - 1, description_2)
    return notification


def get_notifications(messages):
    notifications = dict()
    messages_by_types = defaultdict(list)
    for message in messages:
        messages_by_types[message.type].append(message)
    if len(messages_by_types[Message.INVITATION]) > 0:
        notifications["friend"] = message_summary(messages_by_types[Message.INVITATION],
                                                  "sent friend request to you",
                                                  "sent friend requests to you")
    if len(messages_by_types[Message.COMMENT]) > 0:
        notifications["comment"] = message_summary(messages_by_types[Message.COMMENT],
                                                   "commented on your post",
                                                   "commented on your post")
    return notifications


def check_friend_status(user1, user2):
    if user1.id == user2.id:
        return USER_SELF
    is_friend = user1.userprofile.friends.filter(id=user2.id)
    if is_friend.count() == 1:
        return IS_FRIEND
    has_sent_message = Message.objects.filter(user_from=user1,
                                              user_to=user2,
                                              type=Message.INVITATION,
                                              sent_date__gte=datetime.now() - timedelta(days=7))
    if has_sent_message.count() > 0:
        return FRIEND_REQUEST_SENT
    return NOT_FRIEND


def add_user_like_post_field(user, comments, many=False):
    likes = get_user_liked_posts(user.userprofile)
    serialized_posts = PostSerializer(instance=comments, many=many).data
    USER_LIKE_POST = 'user_like_post'
    if many is False:
        if serialized_posts['id'] not in likes:
            serialized_posts[USER_LIKE_POST] = False
        else:
            serialized_posts[USER_LIKE_POST] = True
    else:
        for post in serialized_posts:
            if post['id'] not in likes:
                post[USER_LIKE_POST] = False
            else:
                post[USER_LIKE_POST] = True
    return serialized_posts


#CRUD implementations
def add_entry_to_profile(request, model):
    """
    Base implementation to add many-to-many relation entities from
    UseProfile model, such as courses,lectures and tasks.
    """
    user = request.user
    user_profile = get_user_profile(user)
    model_name = model._meta.object_name.lower()
    entry_id = request.DATA.get('id', None)
    if entry_id is None:
        return Response(JsonErrorMessage("Please provide {0}".format(model_name)).data,
                        status=status.HTTP_400_BAD_REQUEST)
    try:
        model_object = model.objects.get(pk=entry_id)
    except ObjectDoesNotExist:
        return Response(JsonErrorMessage("{0} doesn't exist".format(model_name)).data,
                        status=status.HTTP_404_NOT_FOUND)
    try:
        # if model is 'Course', the corresponding manytomany
        # field in UserProfile is 'courses', the same for Lecture and Task
        many_to_many_field = model_name + "s"
        relation_manager = user_profile.__getattribute__(many_to_many_field)
    except AttributeError:
        return Response(JsonErrorMessage("Cannot add {0} to UserProfile object".format(model_name)).data,
                        status=status.HTTP_400_BAD_REQUEST)
    relation_manager.add(model_object)
    return Response(SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


def create_and_insert_entry(request, model, model_serializer,
                            preprocess_steps=None, add_profile=False):
    user = request.user
    model_fields = set([field.name for field in model._meta.fields])
    field_dict = extract_model_fields(request, model_fields)
    if preprocess_steps is not None:
        field_dict = preprocess_steps(field_dict)
    serialized_object = model_serializer(data=field_dict)
    if serialized_object.is_valid():
        created_object = serialized_object.object
        created_object.save()
    else:
        return Response(serialized_object._errors, status=status.HTTP_400_BAD_REQUEST)
    if add_profile:
        relation_manager = get_m2m_relation(user, model)
        relation_manager.add(created_object)
    return Response({"id": created_object.id}, status=status.HTTP_200_OK)


def delete_entry(request, model):
    user = request.user
    user_profile = get_user_profile(user)
    user_objects = get_m2m_relation(user, model)
    model_id = request.DATA.get('id', None)
    if model_id is None:
        return Response(data=JsonErrorMessage("No id is provided").data,
                        status=status.HTTP_400_BAD_REQUEST)
    try:
        entry = model.objects.get(id=model_id)
    except ObjectDoesNotExist:
        return Response(data=JsonErrorMessage("Object doesn't found").data,
                        status=status.HTTP_404_NOT_FOUND)
    user_objects.remove(entry)
    user_profile.save()
    return Response(data=SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


def update_entry(request, model, accept_fields):
    model_fields = get_model_field_mapping(model)
    object_id = request.DATA.get('id', None)
    if object_id is None:
        return Response(data=NO_ID_FOUND_ERROR.data,
                        status=status.HTTP_400_BAD_REQUEST)
    try:
        object_entry = model.objects.get(id=object_id)
    except ObjectDoesNotExist:
        return Response(data=INVALID_OBJECT_ID_ERROR,
                        status=status.HTTP_400_BAD_REQUEST)
    update_data = {field: value for (field, value) in request.DATA.items() if field in accept_fields}
    for field in update_data:
        parsed_value = model_fields[field].to_python(update_data[field])
        object_entry.__setattr__(field, parsed_value)
    object_entry.save()
    return Response(data=SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def view_basic_course_info(request):
    user = request.user
    user_profile = get_user_profile(user)
    try:
        university = user_profile.university_id
    except ObjectDoesNotExist:
        return Response(JsonErrorMessage("No such university").data,
                        status=status.HTTP_404_NOT_FOUND)
    univ_id = university.id
    match_courses = Course.objects.filter(univ_id=univ_id).only('id', 'course_code', 'section')
    if match_courses.count() == 0:
        return Response(JsonErrorMessage("No match courses").data,
                        status=status.HTTP_404_NOT_FOUND)
    course_info_list = list()
    for course in match_courses:
        course_info_list.append({"id": course.id, "course_code": course.course_code, "section": course.section})
    return Response(course_info_list, status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def view_complete_course_info(request, course_id):
    if course_id is None:
        return Response(JsonErrorMessage("Invalid course_id").data, status=status.HTTP_400_BAD_REQUEST)
    try:
        course = Course.objects.get(id=course_id)
    except ObjectDoesNotExist:
        return Response(JsonErrorMessage("This course doesn't exist.").data,
                        status=status.HTTP_400_BAD_REQUEST)
    course_lectures = Lecture.objects.filter(course_id=course.id)
    course_info = dict()
    course_info["course"] = CourseSerializer(instance=course).data
    course_info["lecturelist"] = LectureSerializer(instance=course_lectures, many=True).data
    return Response(data=course_info, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def add_course(request):
    response = add_entry_to_profile(request, Course)
    if response.status_code != status.HTTP_200_OK:
        return response
    course_id = request.DATA.get('id')
    lectures = Lecture.objects.filter(course_id=course_id)
    user = request.user
    user_profile = get_user_profile(user)
    for lecture in lectures:
        user_profile.lectures.add(lecture)
    user_profile.save()
    return Response(data=SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def add_lecture(request):
    return add_entry_to_profile(request, Lecture)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def add_task(request):
    return add_entry_to_profile(request, Task)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def create_task(request):
    request.DATA["create_user"] = request.user.id
    return create_and_insert_entry(request, Task, TaskSerializer, extend_course_fields, add_profile=True)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def create_lecture(request):
    return create_and_insert_entry(request, Lecture, LectureSerializer, extend_course_fields, add_profile=True)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def view_user_courses(request):
    user = request.user
    user_courses = get_m2m_relation(user, Course)
    user_lectures = get_m2m_relation(user, Lecture)
    user_tasks = get_m2m_relation(user, Task)
    courses = user_courses.all()
    lectures = user_lectures.all()
    tasks = user_tasks.all()
    course_info = dict()
    for course in courses:
        course_info[course.id] = dict()
        course_info[course.id][COURSE] = CourseSerializer(instance=course).data
        course_info[course.id][LECTURES] = list()
        course_info[course.id][TASKS] = list()
    for lecture in lectures:
        if lecture.course_id.id in course_info:
            course_info[lecture.course_id.id][LECTURES].append(lecture)
    for task in tasks:
        if task.course_id.id in course_info:
            course_info[task.course_id.id][TASKS].append(task)
    for course in course_info:
        course_info[course][LECTURES] = \
            LectureSerializer(instance=course_info[course][LECTURES], many=True).data
        course_info[course][TASKS] = \
            TaskSerializer(instance=course_info[course][TASKS], many=True).data
    return Response(course_info.values(), status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def view_user_lectures(request):
    user = request.user
    user_lectures = get_m2m_relation(user, Lecture)
    timestamp = request.QUERY_PARAMS.get("timestamp", None)
    course_id = request.QUERY_PARAMS.get("course_id", None)
    if course_id is not None:
        matched_lectures = user_lectures.filter(course_id=course_id)
    else:
        matched_lectures = user_lectures.all()
    if timestamp is None:
        matched_lectures = sort_objects(matched_lectures, lecture_ord)
        return Response(data=LectureSerializer(instance=matched_lectures, many=True).data,
                        status=status.HTTP_200_OK)
    else:
        date_time = convert_timestamp_datetime(timestamp)
        weekday = NUM_TO_WEEKDAY[date_time.isoweekday()]
        lectures_of_day = matched_lectures.filter(weekday=weekday)
        start_time = date_time.time()
        future_lectures = sort_objects([lecture for lecture in lectures_of_day
                                        if lecture.start_time >= start_time], lecture_ord)

        return Response(data=LectureSerializer(instance=future_lectures, many=True).data,
                        status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def view_user_tasks(request):
    """
    view for user's future tasks.
    Parameter:
    timestamp = YYYY:MM:DD:HH:MM:SS
    task_today = boolean
    If timestamp is not specified, return all upcoming tasks
    If specified, return upcoming tasks for that day.
    """
    user = request.user
    user_tasks = get_m2m_relation(user, Task)
    timestamp = request.QUERY_PARAMS.get("timestamp", None)
    is_today = request.QUERY_PARAMS.get("task_today", None)
    course_id = request.QUERY_PARAMS.get("course_id", None)
    if course_id is not None:
        match_tasks = user_tasks.filter(course_id=course_id)
    else:
        match_tasks = user_tasks.all()
    if timestamp is None:
        match_tasks = sort_objects(match_tasks, task_ord)
        return Response(data=TaskSerializer(instance=match_tasks, many=True).data,
                        status=status.HTTP_200_OK)
    else:
        date_time = convert_timestamp_datetime(timestamp)
        if is_today is None or is_today == u"False":
            all_future_tasks = match_tasks.filter(date__gte=date_time.date())
            upcoming_tasks = [task for task in all_future_tasks if
                              (task.start_time >= date_time.time() or task.date > date_time.date())]
            upcoming_tasks = sort_objects(upcoming_tasks, task_ord)
            return Response(data=TaskSerializer(instance=upcoming_tasks, many=True).data,
                            status=status.HTTP_200_OK)
        else:
            today_tasks = match_tasks.filter(date=date_time.date())
            today_upcoming_tasks = [task for task in today_tasks if task.start_time >= date_time.time()]
            today_upcoming_tasks = sort_objects(today_upcoming_tasks, task_ord)
            return Response(data=TaskSerializer(instance=today_upcoming_tasks, many=True).data,
                            status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def create_post(request):
    request.DATA['user_id'] = request.user.id
    request.DATA['username'] = request.user.first_name + " " + request.user.last_name
    response = create_and_insert_entry(request,
                                       Post,
                                       PostSerializer,
                                       preprocess_steps=update_parent_childnum)
    if response.status_code != status.HTTP_200_OK:
        return response
    parent_id = request.DATA.get('parent', None)
    if parent_id is None:
        return response
    course_id = request.DATA.get('course_id', None)
    parent_post = Post.objects.get(id=parent_id)
    user_to = parent_post.user_id
    user_from = request.user
    if parent_id is not None and user_from.id != user_to.id:
        course = Course.objects.get(id=course_id)
        course_name = course.course_code + " " + course.section
        user_name = get_username(user_from)
        title = "{0} commented on your post in {1}".format(user_name, course_name)
        content = request.DATA['content']
        if len(content) > 100:
            content = content[:100] + "..."
        msg = Message.objects.create(user_from=user_from,
                                     user_to=user_to,
                                     content=content,
                                     title=title,
                                     type=Message.COMMENT,
                                     reference_id=int(parent_id))
        msg.save()
    return response


@api_view(['GET'])
def view_course_lectures(request, course_id):
    lectures = Lecture.objects.filter(course_id=course_id)
    if lectures.count() == 0:
        return Response(JsonErrorMessage("No lectures found"), status=status.HTTP_404_NOT_FOUND)
    return Response(data=LectureSerializer(instance=lectures, many=True).data, status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def view_course_comments(request, course_id):
    comment_num = request.QUERY_PARAMS.get('comment_num', None)
    if comment_num is not None:
        try:
            comment_num = int(comment_num)
        except ValueError:
            return Response(data=JsonErrorMessage("comment_num is not integer").data,
                            status=status.HTTP_400_BAD_REQUEST)
    else:
        comment_num = 50
    comments = Post.objects.filter(course_id=course_id). \
                   filter(parent=None).order_by('-timestamp')[:comment_num]
    user = request.user
    serialized_posts = add_user_like_post_field(user, comments, many=True)
    return Response(data=serialized_posts, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def like_post(request):
    user = request.user
    user_profile = get_user_profile(user)
    post_id = request.DATA.get('post_id', None)
    try:
        post = Post.objects.get(id=post_id)
        user_liked_posts = get_user_liked_posts(user_profile)
        if post.id in user_liked_posts:
            return Response(data=JsonErrorMessage("Already liked the post").data,
                            status=status.HTTP_403_FORBIDDEN)
        post.like_num += 1
        post.save()
        user_profile.likes.add(post)
    except ObjectDoesNotExist:
        return Response(data=JsonErrorMessage("Post doesn't exist").data,
                        status=status.HTTP_404_NOT_FOUND)
    return Response(data=SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def unlike_post(request):
    user = request.user
    user_profile = get_user_profile(user)
    post_id = request.DATA.get('post_id', None)
    try:
        post = Post.objects.get(id=post_id)
        if post.like_num > 0:
            post.like_num -= 1
        post.save()
        user_profile.likes.remove(post)
    except ObjectDoesNotExist:
        return Response(data=JsonErrorMessage("Post doesn't exist").data,
                        status=status.HTTP_404_NOT_FOUND)
    return Response(data=SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def view_week_summary(request):
    user = request.user
    datetime_str = request.QUERY_PARAMS.get('datetime', None)
    if datetime_str is None:
        return Response(data=JsonErrorMessage("No date specify").data,
                        status=status.HTTP_400_BAD_REQUEST)
    _datetime = convert_timestamp_datetime(datetime_str)
    _date = _datetime.date()
    _time = _datetime.time()
    week_start, week_end = get_week_start_end(_date)

    user_lectures = get_m2m_relation(user, Lecture)
    user_tasks = get_m2m_relation(user, Task)

    lectures = user_lectures.all()
    tasks = user_tasks.filter(date__gte=week_start).filter(date__lte=week_end)

    summary = dict()

    for weekday in NUM_TO_WEEKDAY.values():
        summary[weekday] = dict()
        summary[weekday][LECTURES] = dict()
        summary[weekday][TASKS] = dict()

    for lecture in lectures:
        weekday_summary = summary[lecture.weekday][LECTURES]
        if lecture.type not in weekday_summary:
            weekday_summary[lecture.type] = 0
        weekday_summary[lecture.type] += 1

    for task in tasks:
        task_date = task.date
        task_weekday = NUM_TO_WEEKDAY[task_date.isoweekday()]
        weekday_summary = summary[task_weekday][TASKS]
        if "name" not in weekday_summary:
            weekday_summary["name"] = task.name
        if "num" not in weekday_summary:
            weekday_summary["num"] = 0
        weekday_summary["num"] += 1

    day_summaries = list()

    today_weekday = _date.isoweekday()
    passed_day = 0
    for weekday in WEEKDAYS_SORTED:
        if WEEKDAY_TO_NUM[weekday] < today_weekday:
            passed_day += 1
        weekday_abstract_summary = dict()
        weekday_abstract_summary["day"] = weekday
        weekday_abstract_summary[LECTURES] = get_lecture_summary(summary[weekday][LECTURES])
        weekday_abstract_summary[TASKS] = get_task_summary(summary[weekday][TASKS])
        day_summaries.append(weekday_abstract_summary)

    week_summary = dict()

    remaining_task_num = 0
    remaining_lecture_num = 0

    for lecture in lectures:
        lecture_weekday = WEEKDAY_TO_NUM[lecture.weekday]
        current_weekday = _date.isoweekday()
        if lecture_weekday > current_weekday:
            remaining_lecture_num += 1
        elif lecture_weekday == current_weekday and lecture.start_time > _time:
            remaining_lecture_num += 1

    for task in tasks:
        if task.date > _date or (task.date == _date and task.start_time > _time):
            remaining_task_num += 1

    week_summary["remaining_task_num"] = remaining_task_num
    week_summary["remaining_lecture_num"] = remaining_lecture_num
    week_summary["day_summaries"] = day_summaries
    week_summary["passed_day"] = passed_day

    return Response(data=week_summary, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def set_semester_start_and_end(request):
    user = request.user
    user_profile = get_user_profile(user)
    start_date_str = request.DATA.get("start_date", None)
    end_date_str = request.DATA.get("end_date", None)
    if start_date_str is None and end_date_str is None:
        return Response(data=JsonMessage("Incomplete information. Require both start and end date").data,
                        status=status.HTTP_400_BAD_REQUEST)
    try:
        if start_date_str is not None:
            start_date = convert_timestamp_datetime(start_date_str, format_str=DATE_FORMAT).date()
            user_profile.semester_start = start_date
        if end_date_str is not None:
            end_date = convert_timestamp_datetime(end_date_str, format_str=DATE_FORMAT).date()
            user_profile.semester_end = end_date
    except Exception:
        return Response(data=JsonErrorMessage("Invalid Date").data,
                        status=status.HTTP_400_BAD_REQUEST)

    user_profile.save()
    return Response(data=SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def delete_task(request):
    return delete_entry(request, Task)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def delete_course(request):
    response = delete_entry(request, Course)
    if response.status_code != status.HTTP_200_OK:
        return response
    course_id = request.DATA.get('id')
    user_profile = get_user_profile(request.user)
    user_lectures = get_m2m_relation(request.user, Lecture)
    deleted_lectures = user_lectures.filter(course_id=course_id)
    for lecture in deleted_lectures:
        user_lectures.remove(lecture)
    user_profile.save()
    return Response(data=SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def get_post_comments(request):
    post_id = request.QUERY_PARAMS.get('id', None)
    if post_id is None:
        return Response(JsonErrorMessage("No id provided").data,
                        status=status.HTTP_400_BAD_REQUEST)
    try:
        post = Post.objects.get(id=post_id)
    except ObjectDoesNotExist:
        return Response(JsonErrorMessage("Post doesn't exist").data,
                        status=status.HTTP_404_NOT_FOUND)
    children = Post.objects.filter(parent=post_id)
    post_comment = dict()
    post_comment['post'] = add_user_like_post_field(request.user, post, many=False)
    post_comment['comments'] = add_user_like_post_field(request.user, children, many=True)
    return Response(data=post_comment, status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def get_start_and_end_date(request):
    user = request.user
    user_profile = get_user_profile(user)
    start_date = user_profile.semester_start
    end_date = user_profile.semester_end
    return Response(data={"start_date": start_date, "end_date": end_date},
                    status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def view_user_profile(request):
    user_id = request.QUERY_PARAMS.get('id', None)
    if user_id is not None:
        try:
            user = User.objects.get(id=user_id)
        except ObjectDoesNotExist:
            return Response(data=INVALID_OBJECT_ID_ERROR.data,
                            status=status.HTTP_404_NOT_FOUND)
    else:
        user = request.user
    user_info = dict()
    user_info['id'] = user.id
    user_profile = get_user_profile(user)
    friend_status = check_friend_status(request.user, user)
    user_info['friend_status'] = friend_status
    user_info['username'] = user.username
    user_info['first_name'] = user.first_name
    user_info['last_name'] = user.last_name
    user_info['email'] = user.email
    user_info['university'] = user_profile.university_id.university_name
    user_info['gender'] = user_profile.gender
    user_info['status'] = user_profile.status
    user_info['avatar'] = get_user_avatar(user)

    user_courses = user_profile.courses.all()
    user_comments = Post.objects.filter(user_id=user.id).order_by('-timestamp')
    if friend_status == 0 or friend_status == 1:
        user_info['courses'] = CourseSerializer(instance=user_courses, many=True).data
        user_info['comments'] = PostSerializer(instance=user_comments[:10], many=True).data
    return Response(data=user_info, status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def view_friendship(request):
    user = request.user
    person_id = request.QUERY_PARAMS.get('id', None)
    if person_id is None:
        return Response(data=JsonErrorMessage("Id not found").data,
                        status=status.HTTP_400_BAD_REQUEST)
    try:
        person_id = int(person_id)
    except ValueError:
        return Response(data=JsonErrorMessage("Id is not integer").data,
                        status=status.HTTP_400_BAD_REQUEST)
    user_friends = get_user_profile(user).friends
    if user_friends.filter(id=person_id).count() > 0:
        return Response(data={"is_friend": True}, status=status.HTTP_200_OK)
    else:
        return Response(data={"is_friend": False}, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def send_invitation(request):
    user_from = request.user
    user_to_id = request.DATA.get('id', None)
    if user_to_id is None:
        return Response(NO_ID_FOUND_ERROR.data,
                        status=status.HTTP_400_BAD_REQUEST)
    try:
        user_to = User.objects.get(id=user_to_id)
    except ObjectDoesNotExist:
        return Response(INVALID_OBJECT_ID_ERROR.data,
                        status=status.HTTP_404_NOT_FOUND)
    msg_title = "Friend invitation from {0}".format(user_from.first_name + " " + user_from.last_name)
    msg_content = "Please add me as your friend"
    message = Message.objects.create(user_from=user_from,
                                     user_to=user_to,
                                     title=msg_title,
                                     content=msg_content,
                                     type=Message.INVITATION)
    message.save()
    return Response(SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def mark_as_read(request):
    msg_id = request.DATA.get('id', None)
    if msg_id is None:
        return Response(NO_ID_FOUND_ERROR.data,
                        status=status.HTTP_400_BAD_REQUEST)
    try:
        msg = Message.objects.get(id=msg_id)
    except ObjectDoesNotExist:
        return Response(INVALID_OBJECT_ID_ERROR.data,
                        status=status.HTTP_404_NOT_FOUND)
    msg.read = True
    msg.save()
    return Response(SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def check_notification(request):
    user = request.user
    unread_messages = Message.objects.filter(user_to=user.id).filter(read=False)
    new_messages = unread_messages.filter(fetched=False)
    unread_num = unread_messages.count()
    if new_messages.count() == 0:
        return Response(data={"unread": unread_num}, status=status.HTTP_200_OK)
    for message in new_messages:
        message.fetched = True
        message.save()
    notifications = get_notifications(unread_messages)
    notifications["unread"] = unread_num
    return Response(data=notifications, status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def view_unread_messages(request):
    user = request.user
    all_messages = Message.objects.filter(user_to=user.id, display=True).order_by('-sent_date')
    return Response(data=MessageSerializer(instance=all_messages, many=True).data,
                    status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def clear_all_messages(request):
    user = request.user
    all_messages = Message.objects.filter(user_to=user.id, display=True)
    for message in all_messages:
        message.display = False
        message.read = True
        message.save()
    return Response(data=SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def mute_message(request):
    msg_id = request.DATA.get('id', None)
    if msg_id is None:
        return Response(NO_ID_FOUND_ERROR.data, status=status.HTTP_400_BAD_REQUEST)
    try:
        message = Message.objects.get(id=msg_id)
    except ObjectDoesNotExist:
        return Response(INVALID_OBJECT_ID_ERROR.data, status=status.HTTP_404_NOT_FOUND)
    message.display = False
    message.save()
    return Response(SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def add_friend(request):
    user = request.user
    user_profile = get_user_profile(user)
    friend_id = request.DATA.get('id', None)
    if friend_id is None:
        return Response(NO_ID_FOUND_ERROR.data, status=status.HTTP_200_OK)
    try:
        friend = User.objects.get(id=friend_id)
    except ObjectDoesNotExist:
        return Response(INVALID_OBJECT_ID_ERROR.data, status=status.HTTP_404_NOT_FOUND)
    user_profile.friends.add(friend)
    friend_profile = get_user_profile(friend)
    friend_profile.friends.add(user)
    user_profile.save()
    friend_profile.save()
    return Response(SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def update_task(request):
    return update_entry(request, Task, TASK_FIELDS)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def update_status(request):
    user = request.user
    new_status = request.DATA.get('status', None)
    if new_status is None:
        return Response(NO_ID_FOUND_ERROR.data, status=status.HTTP_400_BAD_REQUEST)
    user.userprofile.status = new_status
    user.userprofile.save()
    return Response(SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def decline_request(request):
    user = request.user
    user_from_id = request.DATA.get('id', None)
    if user_from_id is None:
        return Response(NO_ID_FOUND_ERROR.data, status=status.HTTP_400_BAD_REQUEST)
    messages = Message.object.filter(user_from_id=user_from_id,
                                     user_to_id=user.id,
                                     type=Message.INVITATION)
    for message in messages:
        message.delete()
    return Response(SUCCESS_MESSAGE.data, status=status.HTTP_200_OK)


@api_view(['GET'])
@authentication_classes((TokenAuthentication,))
def view_friends(request):
    user = request.user
    user_friends = user.userprofile.friends.all()
    friends_info = list()
    for friend in user_friends:
        friends_info.append({'id': friend.id,
                             'fullname': get_username(friend),
                             'avatar':get_user_avatar(friend)})
    return Response(data=friends_info, status=status.HTTP_200_OK)


@api_view(['POST'])
@authentication_classes((TokenAuthentication,))
def edit_profile(request):
    user = request.user
    first_name = request.DATA.get("first_name", None)
    if first_name is not None:
        user.first_name = first_name
    last_name = request.DATA.get("last_name", None)
    if last_name is not None:
        user.last_name = last_name
    university = request.DATA.get("university", None)
    if university is not None:
        university_obj, created = University.objects.get_or_create(university_name=university)
        user.userprofile.university_id = university_obj
        user.userprofile.save()
    avatar = request.DATA.get("avatar", None)
    if avatar is not None:
        print "avatar",avatar
        cache.set(get_user_avatar_key(user), avatar, timeout=0)
        #print cache.get(get_user_avatar_key(user))
        #print get_user_avatar_key(user)
    user.save()
    return Response(data=SUCCESS_MESSAGE.data)


def view_course_tasks(request, course_id):
    pass
