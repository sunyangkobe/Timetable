package edu.cmu.cs.vlis.timetable.async;

import java.io.IOException;

import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.vlis.timetable.AddCourseActivity;
import edu.cmu.cs.vlis.timetable.obj.Course;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Lecture;
import edu.cmu.cs.vlis.timetable.util.APIAddr;

public class GetCourseDetailsAsyncTask extends LoadingBarAsyncTask<Course, Void, Course> {

    public GetCourseDetailsAsyncTask(AddCourseActivity activity) {
        super(activity);
    }

    @Override
    protected Course doInBackground(Course... params) {
        long courseId = params[0].getId();
        JsonDataProvider dataProvider = new JsonDataProvider(getActivity());
        JsonNode responseNode = dataProvider.readJsonNode(APIAddr.GET_COURSE_DETAIL + courseId,
                null);
        Log.d("KOBE", responseNode.toString());
        try {
            ObjectMapper mapper = new ObjectMapper();
            Course course = mapper.readValue(responseNode.get("course").toString(), Course.class);
            Lecture[] lectures = mapper.readValue(responseNode.get("lecturelist").toString(),
                    Lecture[].class);
            course.setLectures(lectures);
            return course;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void postExecute(Course course) {
        if (course != null) {
            ((AddCourseActivity) getActivity()).reDrawView(course);
        }
        else {
            Toast.makeText(getActivity(), "Network or server error...", Toast.LENGTH_LONG).show();
        }
    }

}
