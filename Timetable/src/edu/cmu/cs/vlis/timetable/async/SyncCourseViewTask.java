package edu.cmu.cs.vlis.timetable.async;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.vlis.timetable.CourseViewActivity;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.obj.Course;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Lecture;
import edu.cmu.cs.vlis.timetable.obj.Post;
import edu.cmu.cs.vlis.timetable.obj.Task;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;
import edu.cmu.cs.vlis.timetable.util.Utils;

public class SyncCourseViewTask extends LoadingBarAsyncTask<Void, Void, Void> {
    private Activity currentActivity;
    private int courseId;
    private NetworkStatus status;
    private Course course;
    private Post[] posts;
    private Task[] tasks;
    private Lecture[] lectures;

    public SyncCourseViewTask(Activity currentActivity, int courseId) {
        super(currentActivity);
        this.currentActivity = currentActivity;
        this.courseId = courseId;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected Void doInBackground(Void... courseIdInfo) {

        // get posts information from server
        JsonDataProvider dataProvider = new JsonDataProvider(currentActivity);
        posts = dataProvider.readObject(APIAddr.GET_POST + String.valueOf(courseId)
                + "?comment_num=2", null, Post[].class);
        status = dataProvider.getLastNetworkStatus();
        if (status != NetworkStatus.VALID) return null;

        // get tasks information from server
        String timeStamp = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss").format(new Date());
        tasks = dataProvider.readObject(APIAddr.GET_TASK + "?timestamp=" + timeStamp
                + "&task_today=False" + "&course_id=" + String.valueOf(courseId), null,
                Task[].class);
        status = dataProvider.getLastNetworkStatus();
        if (status != NetworkStatus.VALID) return null;

        // get lectures information from server
        JsonNode responseNode = dataProvider.readJsonNode(APIAddr.GET_COURSE_DETAIL + courseId,
                null);
        try {
            ObjectMapper mapper = new ObjectMapper();
            lectures = mapper
                    .readValue(responseNode.get("lecturelist").toString(), Lecture[].class);
            course = mapper.readValue(responseNode.get("course").toString(), Course.class);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        status = dataProvider.getLastNetworkStatus();

        return null;
    }

    @Override
    protected void postExecute(Void result) {
        if (status == NetworkStatus.VALID) {
            ((CourseViewActivity) currentActivity).setCourse(course);
            ((CourseViewActivity) currentActivity).setupActionBar();
            ((CourseViewActivity) currentActivity).drawPostView(posts);
            ((CourseViewActivity) currentActivity).drawTaskView(tasks);
            ((CourseViewActivity) currentActivity).drawLectureView(lectures);
            ((CourseViewActivity) currentActivity).setSyncTaskComplete(true);
        }
        else {
            currentActivity.findViewById(R.id.mainContent).setVisibility(
                    View.INVISIBLE);
            Utils.displayNetworkErrorMessage(status, currentActivity);
        }
    }
}
