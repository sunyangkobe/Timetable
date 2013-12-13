from django.db import models
from django.contrib.auth.models import User
from django.db.models.signals import post_save
from rest_framework.authtoken.models import Token
from core_utils import *
import datetime

DEFAULT_USER = User.objects.all()[0]


class University(models.Model):
    university_name = models.CharField(max_length=100)
    abbrev_name = models.CharField(max_length=50, default="ohteru")


class Course(models.Model):
    univ_id = models.ForeignKey(University, verbose_name="university id", db_index=True)
    semester = models.CharField(max_length=100, default="13fall")
    course_name = models.CharField(max_length=255)
    instructor = models.CharField(max_length=100)
    description = models.CharField(max_length=500, default="")
    course_code = models.CharField(max_length=50)
    section = models.CharField(max_length=10, default="A")


class Lecture(models.Model):
    MONDAY = 'Mon'
    TUESDAY = 'Tue'
    WEDNESDAY = 'Wed'
    THURSDAY = 'Thu'
    FRIDAY = 'Fri'
    SATURDAY = 'Sat'
    SUNDAY = 'Sun'
    UNKNOWN = 'TBA'

    WEEKDAYS = (
        (MONDAY, 'Monday'),
        (TUESDAY, 'Tuesday'),
        (WEDNESDAY, 'Wednesday'),
        (THURSDAY, 'Thursday'),
        (FRIDAY, 'Friday'),
        (SATURDAY, 'Saturday'),
        (SUNDAY, 'Sunday'),
        (UNKNOWN, 'TBA')
    )

    ONCE_ONE_WEEK = '1,1w'
    ONCE_TWO_WEEK = '1,2w'
    ONLY_ONCE = '1,1s'

    FREQUENCY = (
        (ONCE_ONE_WEEK, 'Once a week'),
        (ONCE_TWO_WEEK, 'Once two weeks'),
        (ONLY_ONCE, 'Once a semester'),
    )

    LECTURE = 'L'
    TUTORIAL = 'T'
    RECITATION = 'R'

    TYPES = (
        (LECTURE, 'Lecture'),
        (TUTORIAL, 'Tutorial'),
        (RECITATION, 'Recitation'),
    )
    location = models.CharField(max_length=50, default="Unknown")
    start_time = models.TimeField(default=datetime.time(0, 0), db_index=True)
    end_time = models.TimeField(default=datetime.time(0, 0))
    weekday = models.CharField(max_length=3, choices=WEEKDAYS, default=MONDAY, db_index=True)
    frequency = models.CharField(max_length=4, choices=FREQUENCY, default=ONCE_ONE_WEEK)
    type = models.CharField(max_length=1, choices=TYPES, default=LECTURE)
    course_id = models.ForeignKey(Course)
    course_code = models.CharField(max_length=50, default="Unknown")
    course_name = models.CharField(max_length=255, default="Unknown")


class Task(models.Model):
    TASK = 'T'
    EXAM = 'E'

    TYPES = (
        (TASK, 'Task'),
        (EXAM, 'Exam'),
    )

    type = models.CharField(max_length=1, choices=TYPES, default=TASK)
    location = models.CharField(max_length=30, default="flexible")
    start_time = models.TimeField(db_index=True)
    end_time = models.TimeField()
    date = models.DateField()
    name = models.CharField(max_length=50)
    notes = models.CharField(max_length=1000, default="Notes")
    course_id = models.ForeignKey(Course)
    course_code = models.CharField(max_length=50, default="Unknown")
    course_name = models.CharField(max_length=255, default="Unknown")
    create_date = models.DateTimeField(auto_now_add=True, default=datetime.datetime.now(), db_index=True)
    create_user = models.ForeignKey(User, default=DEFAULT_USER)
    is_public = models.BooleanField(default=False)


class Post(models.Model):
    parent = models.ForeignKey("self", null=True, blank=True)
    content = models.CharField(max_length=500)
    timestamp = models.DateTimeField(auto_now_add=True)
    user_id = models.ForeignKey(User, db_index=True)
    course_id = models.ForeignKey(Course, db_index=True)
    username = models.CharField(max_length=100, default="Unknown")
    like_num = models.IntegerField(default=0)
    children_num = models.IntegerField(default=0)


class UserProfile(models.Model):
    user = models.OneToOneField(User)
    university_id = models.ForeignKey(University)
    status = models.CharField(max_length=140, default="")
    courses = models.ManyToManyField(Course)
    friends = models.ManyToManyField(User, related_name="friends")
    tasks = models.ManyToManyField(Task)
    lectures = models.ManyToManyField(Lecture)
    likes = models.ManyToManyField(Post)
    semester_start = models.DateField(default=datetime.date(1970, 1, 1))
    semester_end = models.DateField(default=datetime.date(1970, 1, 1))

    MAN = 'M'
    WOMAN = 'W'
    UNKNOWN = 'U'

    GENDER = (
        (MAN, 'Man'),
        (WOMAN, 'Woman'),
        (UNKNOWN, 'Unknown')
    )

    gender = models.CharField(max_length=1, choices=GENDER)


class Message(models.Model):
    user_from = models.ForeignKey(User, related_name="from", db_index=True)
    user_to = models.ForeignKey(User, related_name="to", db_index=True)
    read = models.BooleanField(default=False, db_index=True)
    fetched = models.BooleanField(default=False, db_index=True)
    title = models.CharField(max_length=255, default="Message")
    content = models.CharField(max_length=1000, default="content")
    sent_date = models.DateTimeField(auto_now_add=True, db_index=True)
    reference_id = models.IntegerField(default=-1)
    display = models.BooleanField(default=True)

    INVITATION = 'I'
    MESSAGE = 'M'
    COMMENT = 'C'
    TYPES = (
        (INVITATION, 'Invitation'),
        (MESSAGE, 'Message'),
        (COMMENT, 'Comment'),
    )

    type = models.CharField(max_length=1, choices=TYPES, default='M')


def create_auto_token(sender, instance=None, created=False, **kwargs):
    if created:
        token = Token.objects.create(user=instance)
        token.save()


post_save.connect(create_auto_token, sender=User)

