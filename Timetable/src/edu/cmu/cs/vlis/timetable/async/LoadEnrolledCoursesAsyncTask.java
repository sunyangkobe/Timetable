package edu.cmu.cs.vlis.timetable.async;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.vlis.timetable.fragment.PlannerFragment;
import edu.cmu.cs.vlis.timetable.obj.Course;
import edu.cmu.cs.vlis.timetable.obj.JsonDataProvider;
import edu.cmu.cs.vlis.timetable.obj.Lecture;
import edu.cmu.cs.vlis.timetable.obj.Task;
import edu.cmu.cs.vlis.timetable.util.APIAddr;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public class LoadEnrolledCoursesAsyncTask extends LoadingBarAsyncTask<Void, Void, List<Course>> {

    private PlannerFragment fragment;

    public LoadEnrolledCoursesAsyncTask(PlannerFragment fragment) {
        super(fragment.getActivity());
        this.fragment = fragment;
    }

    @Override
    protected void preExecute() {
        fragment.setAddCourseButtonGone();
    }

    @Override
    protected List<Course> doInBackground(Void... params) {
        List<Course> courses = new ArrayList<Course>();
        JsonDataProvider dataProvider = new JsonDataProvider(getActivity());
        JsonNode responseNode = dataProvider.readJsonNode(APIAddr.GET_ENROLLED_COURSES, null);
        if (dataProvider.getLastNetworkStatus() != NetworkStatus.VALID) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        // TODO: ask mimi to treat lectures and tasks as attributes of course
        for (JsonNode node : responseNode) {
            Course course;
            try {
                course = mapper.readValue(node.get("course").toString(), Course.class);
                Lecture[] lectures = mapper.readValue(node.get("lectures").toString(),
                        Lecture[].class);
                course.setLectures(lectures);
                Task[] tasks = mapper.readValue(node.get("tasks").toString(), Task[].class);
                course.setTasks(tasks);
                courses.add(course);
            }
            catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return courses;
    }

    @Override
    protected void postExecute(List<Course> courses) {
        if (courses == null) {
            Toast.makeText(getActivity(), "Network or server error...", Toast.LENGTH_LONG).show();
        }
        else {
            fragment.draw(courses);
        }
    }

}
