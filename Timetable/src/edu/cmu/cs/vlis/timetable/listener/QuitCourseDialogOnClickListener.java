package edu.cmu.cs.vlis.timetable.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import edu.cmu.cs.vlis.timetable.async.QuitCourseAsyncTask;

public class QuitCourseDialogOnClickListener implements OnClickListener {

    private QuitCourseAsyncTask asyncTask;
    private int courseId;

    public QuitCourseDialogOnClickListener(QuitCourseAsyncTask quitCourseAsyncTask, int courseId) {
        this.asyncTask = quitCourseAsyncTask;
        this.courseId = courseId;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, courseId);
    }

}
