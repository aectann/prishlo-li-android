package org.aectann.postage;

import static android.content.Context.MODE_PRIVATE;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
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
  private File[] trackings;
  
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
    trackings = context.getDir(Constants.TRACKINGS, MODE_PRIVATE).listFiles();
    Arrays.sort(trackings, new Comparator<File>() {

      @Override
      public int compare(File f1, File f2) {
        return -Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()) ;
      }});
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
    return trackings[position].getName();
  }

  @Override
  public TrackingInfo getItem(int position) {
    return TrackingStorageUtils.loadStoredTrackingInfo(getTrackingNumber(position), this.context);
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