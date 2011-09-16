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



import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PostageListActivity extends AsyncTaskAwareActivity {

  private static final int ADD_POSTAGE = 100;
  private static final int SHOW_POSTAGE = 101;
  private ListView list;
  private TrackingsInfoAdapter trackingsInfoAdapter;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list);
    list = (ListView) findViewById(android.R.id.list);
    trackingsInfoAdapter = new TrackingsInfoAdapter(this);
    list.setAdapter(trackingsInfoAdapter);
    list.setOnItemClickListener(getOnItemClickListener());
    list.setEmptyView(findViewById(R.id.empty));
  }

  protected OnItemClickListener getOnItemClickListener() {
    return new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> list, View view, int position, long id) {
        String trackingNumber = trackingsInfoAdapter.getTrackingNumber(position);
        Intent intent = new Intent(PostageListActivity.this, PostageActivity.class);
        intent.putExtra(Constants.TRACKING_NUMBER, trackingNumber);
        startActivityForResult(intent, SHOW_POSTAGE);
      }};
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.list_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.add:
        startActivityForResult(new Intent(this, AddPostageActivity.class), ADD_POSTAGE);
        break;
      case R.id.refresh:
        refresh();
        break;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }
  
  @Override
  protected void onStart() {
    super.onStart();
    Postages application = (Postages) getApplication();
    if (!application.isListUpdated()) {
      refresh();
    }
  }

  private void refresh() {
    final String[] trackingNumbers = trackingsInfoAdapter.getTrackingNumbers();
    executeTask(new TrackingStatusRefreshTask(this) {
      
      int count = 0;
      ProgressDialog dialog;
      
      protected void onPreExecute() {
        dialog = new ProgressDialog(PostageListActivity.this);
        dialog.setTitle(getString(R.string.updating));
        dialog.setMessage(getString(R.string.loading_postages_data));
        dialog.setIndeterminate(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMax(trackingNumbers.length);
        dialog.show();
      };
      
      protected void onProgressUpdate(TrackingInfo... values) {
        for (TrackingInfo trackingInfo : values) {
          count++;
          TrackingStorageUtils.store(PostageListActivity.this, trackingInfo);
          dialog.setProgress(count);
        }
      };
      
      protected void onPostExecute(String result) {
        dialog.dismiss();
        PostageStatusWidgetProvider.updateWidgets(PostageListActivity.this, trackingNumbers);
        reloadFromStorage();
        Postages application = (Postages) getApplication();
        application.setListUpdated();
      };
    }, trackingNumbers);
  }

  private void reloadFromStorage() {
    trackingsInfoAdapter.notifyDataSetChanged();
    list.invalidate();
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      switch (requestCode) {
        case ADD_POSTAGE:
          String trackingNumber = data.getStringExtra(Constants.TRACKING_NUMBER);
          final String name = data.getStringExtra(Constants.NAME);
          executeTask(new TrackingStatusRefreshTask(this) {
            
            ProgressDialog dialog;
            
            @Override
            protected void onPreExecute() {
              dialog = ProgressDialog.show(PostageListActivity.this, getString(R.string.search), getString(R.string.searching_postage_data));
            }
            
            protected void onProgressUpdate(TrackingInfo... values) {
              TrackingInfo loadedInfo = values[0];
              loadedInfo.setName(name);
              TrackingStorageUtils.store(PostageListActivity.this, loadedInfo);
            };
            
            @Override
            protected void onPostExecute(String result) {
              reloadFromStorage();
              dialog.dismiss();
            }
          }, trackingNumber);
          break;
        case SHOW_POSTAGE:
          reloadFromStorage();
          break;
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
}
