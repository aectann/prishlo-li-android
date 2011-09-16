package org.aectann.postage;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PostageActivity extends AsyncTaskAwareActivity {

  private TextView trackingNumber;

  private ListView list;
  
  class ViewHolder {
    TextView status;
    TextView dateMonth;
    TextView dateDay;
  }

  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.postage);
    trackingNumber = (TextView) findViewById(R.id.trackingNumber);
    String tracking = getTrackingNumber();
    trackingNumber.setText(tracking);
    TrackingInfo trackingInfo = TrackingStorageUtils.loadStoredTrackingInfo(tracking, this);
    if (trackingInfo == null) {
      Toast.makeText(this, R.string.parcel_not_found_in_your_list, Toast.LENGTH_LONG).show();
      finish();
      return;
    }
    setTitle(trackingInfo.getName());
    list = (ListView) findViewById(R.id.list);
    list.setEmptyView(findViewById(R.id.empty));
    updateList(trackingInfo);
  }

  private void updateList(TrackingInfo trackingInfo) {
    List<TrackingStatus> statuses = trackingInfo.getStatuses();
    Collections.sort(statuses, Collections.reverseOrder());
    
    list.setAdapter(new ArrayAdapter<TrackingStatus>(this, R.layout.status_item, statuses) {
      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        if (view == null) {
          view = getLayoutInflater().inflate(R.layout.status_item, null);
          holder = new ViewHolder();
          holder.status = (TextView) view.findViewById(R.id.status);
          holder.dateMonth = (TextView) view.findViewById(R.id.date_month);
          holder.dateDay = (TextView) view.findViewById(R.id.date_day);
          view.setTag(holder);
        } else {
          holder = (ViewHolder) view.getTag();
        }
        TrackingStatus item = getItem(position);
        holder.status.setText(item.toString());
        Date date = (Date) item.getDate();
        holder.dateMonth.setText(DateFormat.format("MMM",date));
        holder.dateMonth.setVisibility(View.VISIBLE);
        holder.dateDay.setText(DateFormat.format("d", date));
        return view;
      }
    });
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.postage_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    setResult(RESULT_OK);
    switch (item.getItemId()) {
      case R.id.delete:
        TrackingStorageUtils.delete(this, getTrackingNumber());
        finish();
        break;
      case R.id.refresh:
        executeTask(new TrackingStatusRefreshTask(this) {
          
          ProgressDialog dialog;
          
          TrackingInfo updated;
          
          @Override
          protected void onPreExecute() {
            dialog = ProgressDialog.show(PostageActivity.this, getString(R.string.updating), getString(R.string.loading_postage_data));
          }
          
          protected void onProgressUpdate(TrackingInfo... values) {
            TrackingInfo updatedTrackingInfo = values[0];
            TrackingStorageUtils.store(PostageActivity.this, updatedTrackingInfo);
            updated = updatedTrackingInfo;
          };
          
          @Override
          protected void onPostExecute(String result) {
            updateList(updated);
            dialog.dismiss();
          }
        }, getTrackingNumber());
        break;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);
  }
  
  private String getTrackingNumber() {
    String trackingNumber = getIntent().getStringExtra(Constants.TRACKING_NUMBER);
    if (trackingNumber == null || trackingNumber.length() == 0) {
      trackingNumber = getIntent().getData().getSchemeSpecificPart();
    }
    return trackingNumber;
  }
}
