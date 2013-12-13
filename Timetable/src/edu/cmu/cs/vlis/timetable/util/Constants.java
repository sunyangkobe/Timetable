package edu.cmu.cs.vlis.timetable.util;

import org.apache.http.HttpStatus;

public class Constants {
    public enum NetworkStatus {
        NETWORK_ERROR(-1), CREATED(HttpStatus.SC_CREATED), BAD_REQUEST(HttpStatus.SC_BAD_REQUEST), VALID(
                HttpStatus.SC_OK), UNAUTHORIZED(HttpStatus.SC_UNAUTHORIZED), DUPLICATED(
                HttpStatus.SC_CONFLICT), NOT_FOUND(HttpStatus.SC_NOT_FOUND);

        private int errorCode;

        private NetworkStatus(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }

    public enum NavigationTargets {
        HOME(0), PLANNER(1), NOTIFICATIONS(2), CHAT(3), FRIENDS(4), PROFILE(5), SETTINGS(6);
        private int position;

        private NavigationTargets(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    public static final String SHARED_PREFERENCE_FILE_NAME = "preference_file";
    
    public static final int POST_MAX_CHARACTER_NUMBER = 255;
    public static final int STATUS_MAX_CHARACTER_NUMBER = 140;
    public static final int COMMENT_MAX_CHARACTER_NUMBER = 255;
    
    public static final int DEFAULT_CHECK_INTERVAL = 60000;
    
}
