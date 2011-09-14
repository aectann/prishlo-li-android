package org.aectann.postage;

import static android.content.Context.MODE_PRIVATE;

import java.util.Arrays;
import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

final class TrackingsInfoAdapter extends BaseAdapter {
  
  private final Context context;
  private final LayoutInflater layoutInflater;
  private TrackingInfo[] trackings;
  
  class ViewHolder {
    TextView status;
    TextView title;
    TextView dateMonth;
    TextView dateDay;
  }

  /**
   * @param postageListActivity
   */
  public TrackingsInfoAdapter(Context context) {
    this.context = context;
    layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    updateTrackingsList();
  }

  private void updateTrackingsList() {
    String[] trackingNumbers = context.getDir(Constants.TRACKINGS, MODE_PRIVATE).list();
    trackings = new TrackingInfo[trackingNumbers.length];
    for (int i = 0; i < trackingNumbers.length; i++) {
      trackings[i] = TrackingStorageUtils.loadStoredTrackingInfo(trackingNumbers[i], context);
    }
    Arrays.sort(trackings);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup list) {
    View view = convertView;
    ViewHolder holder;
    if (view == null) {
      view = layoutInflater.inflate(R.layout.postage_item, null);
      holder = new ViewHolder();
      holder.status = (TextView) view.findViewById(R.id.status);
      holder.title = (TextView) view.findViewById(R.id.title);
      holder.dateMonth = (TextView) view.findViewById(R.id.date_month);
      holder.dateDay = (TextView) view.findViewById(R.id.date_day);
      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }
    TrackingInfo item = getItem(position);
    String status = item.getStatus();
    holder.status.setText(status != null ? status : "Нет данных");
    holder.title.setText(item.getName());
    Date date = (Date) item.getDate();
    if (date != null) {
      holder.dateMonth.setText(DateFormat.format("MMM",date));
      holder.dateMonth.setVisibility(View.VISIBLE);
      holder.dateDay.setText(DateFormat.format("d", date));
    } else {
      holder.dateMonth.setText("");
      holder.dateMonth.setVisibility(View.GONE);
      holder.dateDay.setText("X");
    }
    return view;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }
  
  public String getTrackingNumber(int position) {
    return trackings[position].getTrackingNumber();
  }
  
  public String[] getTrackingNumbers() {
    String[] result = new String[trackings.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = trackings[i].getTrackingNumber();
    }
    return result;
  }

  @Override
  public TrackingInfo getItem(int position) {
    return trackings[position];
  }

  @Override
  public int getCount() {
    return trackings.length;
  }
  
  @Override
  public void notifyDataSetChanged() {
    updateTrackingsList();
    super.notifyDataSetChanged();
  }
}