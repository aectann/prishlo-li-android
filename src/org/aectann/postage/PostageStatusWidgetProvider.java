package org.aectann.postage;

import static org.aectann.postage.TrackingStorageUtils.isStored;
import static org.aectann.postage.TrackingStorageUtils.isUpdateNeeded;
import static org.aectann.postage.TrackingStorageUtils.loadStoredTrackingInfo;
import static org.aectann.postage.TrackingStorageUtils.store;

import java.util.Date;

import javax.xml.parsers.FactoryConfigurationError;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.RemoteViews;

public class PostageStatusWidgetProvider extends AppWidgetProvider {

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    for (int id : appWidgetIds) {
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.postage_status);
      updateWidget(context, appWidgetManager, id, views);
    }
  }
  
  public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int id,
      RemoteViews views) throws FactoryConfigurationError {
    String trackingNumber = PreferenceManager.getDefaultSharedPreferences(context).getString(String.valueOf(id), null);
    if (trackingNumber != null) {
      TrackingInfo trackingInfo = null;
      if (isStored(context, trackingNumber)) {
        if (isUpdateNeeded(context, trackingNumber)) {
          trackingInfo = TrackingStatusRefreshTask.syncRequest(context, trackingNumber);
          store(context, trackingInfo);
        } else {
          trackingInfo = loadStoredTrackingInfo(trackingNumber, context);
        }
      }
      if (trackingInfo != null) {
        String status = trackingInfo.getStatus();
        views.setTextViewText(R.id.status, status != null ? status : context.getString(R.string.no_data));
        views.setTextViewText(R.id.title, trackingInfo.getName());
        Date date = (Date) trackingInfo.getDate();
        if (date != null) {
          views.setTextViewText(R.id.date_month, DateFormat.format("MMM",date));
          views.setViewVisibility(R.id.date_month, View.VISIBLE);
          views.setTextViewText(R.id.date_day, DateFormat.format("d", date));
        } else {
          views.setTextViewText(R.id.date_month,"");
          views.setViewVisibility(R.id.date_month, View.GONE);
          views.setTextViewText(R.id.date_day, "X");
        }
        Intent showPostageInfo = new Intent(context, PostageActivity.class);
        showPostageInfo.setData(Uri.fromParts(context.getPackageName(), trackingNumber, null));
        PendingIntent pendingAppIntent = PendingIntent.getActivity(context, 0, showPostageInfo, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingAppIntent);
      } else {
        views.setTextViewText(R.id.status, context.getString(R.string.no_data));
        views.setViewVisibility(R.id.title, View.GONE);
        views.setTextViewText(R.id.date_month,"");
        views.setViewVisibility(R.id.date_month, View.GONE);
        views.setTextViewText(R.id.date_day, "X");
      }
      appWidgetManager.updateAppWidget(id, views);
    }
  }
  
  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    super.onDeleted(context, appWidgetIds);
    for (int id : appWidgetIds) {
      PreferenceManager.getDefaultSharedPreferences(context).edit().remove(String.valueOf(id)).commit();
    }
  }
  
}
