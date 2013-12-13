package edu.cmu.cs.vlis.timetable.draw;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.cmu.cs.vlis.timetable.R;
import edu.cmu.cs.vlis.timetable.listener.CourseItemClickListener;
import edu.cmu.cs.vlis.timetable.obj.Lecture;
import edu.cmu.cs.vlis.timetable.obj.RichLecture;

public class LectureItemsContentFiller extends ItemsContentFiller {
    public enum LECTURE_MODE {
        COMBINED_MODE, SEPERATE_MODE;
    }

    private Lecture[] lectures;
    private LECTURE_MODE mode;
    private String instructor = null;

    public LectureItemsContentFiller(Activity currentActivity, ViewGroup rootView,
            Lecture[] lectures, String instructor, LECTURE_MODE mode) {
        super(currentActivity, rootView);
        this.lectures = lectures;
        this.instructor = instructor;
        this.mode = mode;
    }

    @Override
    public void fillContent() {
        if (mode == LECTURE_MODE.SEPERATE_MODE) {
            MultiColorLinearLayoutAdapter colorLinearLayoutAdapter = new MultiColorLinearLayoutAdapter(
                    currentActivity, rootView);
            RichLecture richLecture;
            ItemDrawer itemDrawer;

            for (Lecture lecture : lectures) {
                richLecture = new RichLecture(lecture);
                itemDrawer = new DetailedItemDrawer(currentActivity, rootView);

                itemDrawer.setElement(0, richLecture.getCourseCode());
                itemDrawer.setElement(1, richLecture.getType());
                itemDrawer.setElement(2, richLecture.getDuration());
                itemDrawer.setElement(3, richLecture.getLocation());

                itemDrawer.setOnClickListener(new CourseItemClickListener(currentActivity, lecture
                        .getCourse_id(), lecture.getCourse_code()));

                colorLinearLayoutAdapter.addView(itemDrawer.getView());
            }
        }
        else {
            LayoutInflater inflater = LayoutInflater.from(currentActivity);
            View combinedLectureView = inflater.inflate(R.layout.combined_lectures_item, rootView,
                    false);
            
            // draw lecture time slots
            LinearLayout lectureTimeSlotContainer = (LinearLayout) combinedLectureView
                    .findViewById(R.id.leftLectureTimeSlotContainer);
            
            ItemDrawer itemDrawer; 
            RichLecture richLecture;
            for (Lecture lecture : lectures) {
                itemDrawer = new LectureTimeSlotItemDrawer(currentActivity, lectureTimeSlotContainer);
                richLecture = new RichLecture(lecture);
                
                itemDrawer.setElement(0, richLecture.getWeekDay());
                itemDrawer.setElement(1, richLecture.getDuration());
                
                lectureTimeSlotContainer.addView(itemDrawer.getView());
            }
            
            // draw lecture location 
            TextView lectureLocation = (TextView) combinedLectureView.findViewById(R.id.lectureLocationTextView);
            lectureLocation.setText(lectures[0].getLocation());
            
            // draw lecture instructor, if exists
            if (instructor != null) {
                TextView lectureInstructor = (TextView) combinedLectureView.findViewById(R.id.lectureInstructorTextView);
                lectureInstructor.setText(instructor);
            }
            
            rootView.addView(combinedLectureView);
        }
    }
}
