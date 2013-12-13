package edu.cmu.cs.vlis.timetable.obj;

import java.util.Date;
import java.util.Map;

import android.app.Activity;
import edu.cmu.cs.vlis.timetable.util.Constants.NetworkStatus;

public final class MockDataProvider extends DataProvider {

    public MockDataProvider(Activity currentActivity) {
        super(currentActivity);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readObject(String apiUrl, Map<String, String> params, Class<T> valueType) {
        if (valueType.isAssignableFrom(Lecture[].class)) {
            return (T) generateMockLectureList();
        }
        else if (valueType.isAssignableFrom(Task[].class)) {
            return (T) generateMockTaskList();
        }
        else if (valueType.isAssignableFrom(Course.class)) {
            return (T) generateMockCourse();
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    Task[] generateMockTaskList() {
        Task task1 = new Task();
        task1.setCourse_id(10605);
        Date date1;
        date1 = new Date(113, 11, 11);
        task1.setDate(date1);
        Date startTime1 = new Date(113, 10, 11, 19, 30);
        task1.setDate(date1);
        task1.setStart_time(startTime1);
        task1.setLocation("GHC4615");
        task1.setName("ML HW1 DUE");
        task1.setNotes("I hate it");
        task1.setType("task");

        Task task2 = new Task();
        task2.setCourse_id(11741);
        Date date2;
        date2 = new Date(113, 11, 11);
        task2.setDate(date2);
        Date startTime2 = new Date(113, 10, 11, 21, 00);
        task2.setStart_time(startTime2);
        task2.setLocation("HUNT");
        task2.setName("IR HW2 DUE");
        task2.setNotes("I hate it");
        task2.setType("task");

        return new Task[] { task1, task2 };
    }

    @SuppressWarnings("deprecation")
    Lecture[] generateMockLectureList() {
        Lecture lecture1 = new Lecture();
        lecture1.setCourse_code("11791");
        lecture1.setCourse_name("Information Retrieval");
        lecture1.setStart_time(new Date(113, 10, 11, 18, 30));
        lecture1.setEnd_time(new Date(113, 10, 11, 19, 30));
        lecture1.setFrequency("1 w1");
        lecture1.setId(1);
        lecture1.setLocation("WH 6501");
        lecture1.setType("L");
        lecture1.setWeekday("Monday");

        Lecture lecture2 = new Lecture();
        lecture2.setCourse_code("15826");
        lecture2.setCourse_name("Multimedia database and data mining");
        lecture2.setStart_time(new Date(113, 10, 11, 20, 30));
        lecture2.setEnd_time(new Date(113, 10, 11, 22, 30));
        lecture2.setFrequency("2 w1");
        lecture2.setId(2);
        lecture2.setLocation("GHC 4605");
        lecture2.setType("R");
        lecture2.setWeekday("Monday");

        return new Lecture[] { lecture1, lecture2 };
    }

    Course generateMockCourse() {
        Course course = new Course();
        course.setCourse_code("15826");
        course.setId(1111);
        course.setCourse_name("Multimedia database and data mining");
        course.setInstructor("A & B");
        course.setLectures(generateMockLectureList());
        course.setTasks(generateMockTaskList());
        return course;
    }

    @Override
    public NetworkStatus getLastNetworkStatus() {
        return NetworkStatus.VALID;
    }
}