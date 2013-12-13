package edu.cmu.cs.vlis.timetable.util;

public class APIAddr {
    private static final String BASE_ADDR = "http://23.92.17.12";
    public static final String API_TOKEN_AUTH = BASE_ADDR + "/api-token-auth/";
    public static final String CHECK_USER = BASE_ADDR + "/check-user/";
    public static final String REGISTER = BASE_ADDR + "/register/";
    public static final String GET_LECTURE = BASE_ADDR + "/user/lectures/";
    public static final String GET_TASK = BASE_ADDR + "/user/tasks/";
    public static final String GET_WEEK_SUMMARY = BASE_ADDR + "/user/week/";
    public static final String GET_PROFILE = BASE_ADDR + "/user/profile/";
    public static final String GET_SCHOOL_COURSES = BASE_ADDR + "/course/";
    public static final String ENROLL_COURSE = BASE_ADDR + "/enroll/courses/";
    public static final String GET_COURSE_DETAIL = BASE_ADDR + "/course/info/";
    public static final String GET_POST = BASE_ADDR + "/course/comments/";
    public static final String GET_ENROLLED_COURSES = BASE_ADDR + "/user/courses/";
    public static final String LIKE_POST = BASE_ADDR + "/user/like/";
    public static final String UNLIKE_POST = BASE_ADDR + "/user/unlike/";
    public static final String QUIT_ENROLLED_COURSE = BASE_ADDR + "/user/delete/course/";
    public static final String SYNC_SEMESTER_DATE = BASE_ADDR + "/user/add/semester/";
    public static final String GET_SEMESTER_DATE = BASE_ADDR + "/user/dates/";
    public static final String POST_COMMENT = BASE_ADDR + "/user/add/comment/";
    public static final String POST_STATUS = BASE_ADDR + "/user/status/";
    public static final String GET_POST_RELATED_COMMENT = BASE_ADDR + "/post/";
    public static final String CREATE_NEW_TASK = BASE_ADDR + "/user/add/task/";
    public static final String EDIT_USER_TASK = BASE_ADDR + "/user/edit/task/";
    public static final String DELETE_USER_TASK = BASE_ADDR + "/user/delete/task/";
    public static final String GET_NOTIFICATION = BASE_ADDR + "/user/notifications/";
    public static final String GET_MESSAGE = BASE_ADDR + "/user/messages/";
    public static final String MARK_MESSAGE_AS_READ = BASE_ADDR + "/user/mark-as-read/";
    public static final String CONFIRM_FRIEND_REQUEST = BASE_ADDR + "/user/confirm/";
    public static final String SEND_FRIEND_REQUEST = BASE_ADDR + "/user/invite/";
    public static final String MARK_MESSAGE_AS_MUTE = BASE_ADDR + "/user/messages/mute/";
    public static final String CLEAR_ALL_MESSAGE = BASE_ADDR + "/user/messages/clear/";
    public static final String DECLINE_FRIEND_REQUEST = BASE_ADDR + "/user/decline/";
    public static final String GET_FRIEND_INFO = BASE_ADDR + "/user/friends/";
    public static final String CHANGE_PASSWORD = BASE_ADDR + "/user/change-password/";
    public static final String EDIT_PROFILE = BASE_ADDR + "/user/edit/profile/";
    public static final String RECOVER_PASSWORD = BASE_ADDR + "/user/forget-password/";
}