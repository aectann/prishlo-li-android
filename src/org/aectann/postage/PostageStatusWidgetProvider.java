package org.aectann.postage;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

public class PostageStatusWidgetProvider extends AppWidgetProvider {

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    for (int id : appWidgetIds) {
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.postage_status);
//      String trackingNumber = PreferenceManager.getDefaultSharedPreferences(context).getString(PostageActivity.TRACKING, "");
//      TrackingInfo trackingInfo = TrackingStatusRefreshTask.syncRequest(context, trackingNumber);
//      views.setTextViewText(R.id.title, trackingNumber);
//      views.setTextViewText(R.id.status, trackingInfo.getStatus());
//      views.setTextViewText(R.id.date, DateFormat.getMediumDateFormat(context).format(trackingInfo.getDate()));
      appWidgetManager.updateAppWidget(new int[] {id}, views);
    }
  }

}
