package edu.cmu.cs.vlis.timetable.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import edu.cmu.cs.vlis.timetable.async.DeleteTaskAsyncTask;
import edu.cmu.cs.vlis.timetable.obj.Task;

public class DeleteTaskDialogOnClickListener implements OnClickListener {

    private DeleteTaskAsyncTask asyncTask;
    private Task data;

    public DeleteTaskDialogOnClickListener(DeleteTaskAsyncTask deleteTaskAsyncTask, Task data) {
        this.asyncTask = deleteTaskAsyncTask;
        this.data = data;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
    }

}
