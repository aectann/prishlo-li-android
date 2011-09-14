package org.aectann.postage;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class PostageListActivity extends FragmentActivity {

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
    list.setOnItemClickListener(new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> list, View view, int position, long id) {
        String trackingNumber = trackingsInfoAdapter.getTrackingNumber(position);
        Intent intent = new Intent(PostageListActivity.this, PostageActivity.class);
        intent.putExtra(Constants.TRACKING_NUMBER, trackingNumber);
        startActivityForResult(intent, SHOW_POSTAGE);
      }});
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

  private void refresh() {
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
          new TrackingStatusRefreshTask(this) {
            protected void onProgressUpdate(TrackingInfo... values) {
              TrackingInfo loadedInfo = values[0];
              loadedInfo.setName(name);
              TrackingStorageUtils.store(PostageListActivity.this, loadedInfo);
              refresh();
            };
          }.execute(trackingNumber);
          break;
        case SHOW_POSTAGE:
          refresh();
          break;
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
}
