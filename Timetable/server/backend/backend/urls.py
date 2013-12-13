from django.conf.urls import patterns, include, url

# Uncomment the next two lines to enable the admin:
from django.contrib import admin

admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'backend.views.home', name='home'),
    # url(r'^backend/', include('backend.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),
    #include register function
    url(r'^register/','login.views.register'),
    url(r'^api-token-auth/'
        , 'login.views.get_token'),
    url(r'^check-user/(?P<username>[^\s]+)/$','login.views.check_user_exists'),
    url(r'^course/$',
        'core.views.view_basic_course_info'),
    url(r'^course/info/(?P<course_id>\d+)/$',
        'core.views.view_complete_course_info'),
    url(r'^course/lectures/(?P<course_id>\d+)/$',
        'core.views.view_course_lectures'),
    url(r'^course/comments/(?P<course_id>\d+)/$',
        'core.views.view_course_comments'),
    url(r'^enroll/courses/$','core.views.add_course'),
    url(r'^enroll/lectures/$','core.views.add_lecture'),
    url(r'^user/profile/$','core.views.view_user_profile'),
    url(r'^user/lectures/$','core.views.view_user_lectures'),
    url(r'^user/courses/$','core.views.view_user_courses'),
    url(r'^user/tasks/$','core.views.view_user_tasks'),
    url(r'^user/add/task/$','core.views.create_task'),
    url(r'^user/add/lecture/$','core.views.create_lecture'),
    url(r'^user/add/comment/$','core.views.create_post'),
    url(r'^user/confirm/$','core.views.add_friend'),
    url(r'^user/like/$','core.views.like_post'),
    url(r'^user/unlike/$','core.views.unlike_post'),
    url(r'^user/week/$','core.views.view_week_summary'),
    url(r'^user/add/semester/$','core.views.set_semester_start_and_end'),
    url(r'^user/delete/course/$','core.views.delete_course'),
    url(r'^user/delete/task/$','core.views.delete_task'),
    url(r'^user/dates/$','core.views.get_start_and_end_date'),
    url(r'^user/invite/$','core.views.send_invitation'),
    url(r'^user/is-friend/$','core.views.view_friendship'),
    url(r'^post/$','core.views.get_post_comments'),
    url(r'^user/edit/task/$','core.views.update_task'),
    url(r'^user/mark-as-read/$','core.views.mark_as_read'),
    url(r'^user/notifications/$','core.views.check_notification'),
    url(r'^user/messages/$','core.views.view_unread_messages'),
    url(r'^user/messages/clear/$','core.views.clear_all_messages'),
    url(r'^user/messages/mute/$','core.views.mute_message'),
    url(r'^user/status/','core.views.update_status'),
    url(r'^user/decline/','core.views.decline_request'),
    url(r'^user/friends/','core.views.view_friends'),
    url(r'^user/change-password/','login.views.change_password'),
    url(r'^user/edit/profile/','core.views.edit_profile'),
    url(r'^user/forget-password/','login.views.forget_password'),
    #url(r'^', include('login.urls')),
)
