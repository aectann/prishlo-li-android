/*
 * Copyright 2011 Konstantin Burov (aectann@gmail.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.aectann.postage;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.AsyncTask;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class AsyncTaskAwareActivity extends SherlockFragmentActivity {

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
