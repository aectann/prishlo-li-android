package org.aectann.postage;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

public class AsyncTaskAwareActivity extends FragmentActivity {

  @SuppressWarnings("rawtypes")
  private List<AsyncTask> tasks = new ArrayList<AsyncTask>();

  public AsyncTaskAwareActivity() {
    super();
  }

  @SuppressWarnings("rawtypes")
  protected void executeTask(AsyncTask task, String... params) {
    for (Iterator<AsyncTask> iterator = tasks.iterator(); iterator.hasNext();) {
      AsyncTask next = iterator.next();
      if (next.isCancelled() || next.getStatus() == AsyncTask.Status.FINISHED){
        iterator.remove();
      }
    }
    tasks.add(task);
    task.execute(params);
  }

  @Override
  protected void onPause() {
    for (Iterator<AsyncTask> iterator = tasks.iterator(); iterator.hasNext();) {
      AsyncTask next = iterator.next();
      next.cancel(false);
    }
    tasks.clear();
    super.onPause();
  }

}
