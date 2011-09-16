package org.aectann.postage;

import static org.aectann.postage.TrackingStorageUtils.isStored;
import static org.aectann.postage.TrackingStorageUtils.isUpdateNeeded;
import static org.aectann.postage.TrackingStorageUtils.loadStoredTrackingInfo;
import static org.aectann.postage.TrackingStorageUtils.store;

import java.util.Arrays;
import java.util.Date;

import javax.xml.parsers.FactoryConfigurationError;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
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
      updateWidget(context, appWidgetManager, id);
    }
  }
  
  public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int id) throws FactoryConfigurationError {
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.postage_status);
    String trackingNumber = getTrackingNumberForWidget(context, id);
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
        renderEmpty(context, views);
      } 
    } else {
      renderEmpty(context, views);
    }
    appWidgetManager.updateAppWidget(id, views);
  }

  private static void renderEmpty(Context context, RemoteViews views) {
    views.setTextViewText(R.id.status, context.getString(R.string.no_data));
    views.setViewVisibility(R.id.title, View.GONE);
    views.setTextViewText(R.id.date_month,"");
    views.setViewVisibility(R.id.date_month, View.GONE);
    views.setTextViewText(R.id.date_day, "X");
  }

  private static String getTrackingNumberForWidget(Context context, int id) {
    return PreferenceManager.getDefaultSharedPreferences(context).getString(String.valueOf(id), null);
  }
  
  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    super.onDeleted(context, appWidgetIds);
    for (int id : appWidgetIds) {
      deregisterWidget(context, id);
    }
  }

  public static void deregisterWidget(Context context, int id) {
    PreferenceManager.getDefaultSharedPreferences(context).edit().remove(String.valueOf(id)).commit();
  }
  
  public static void registerWidget(Context context, int id, TrackingInfo trackingInfo) {
    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(String.valueOf(id), trackingInfo.getTrackingNumber()).commit();
  }
  
  public static void updateWidgets(Context context, String... trackingNumbers) {
    Arrays.sort(trackingNumbers);
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, PostageStatusWidgetProvider.class));
    for (int id : appWidgetIds) {
      String trackingNumber = getTrackingNumberForWidget(context, id);
      if (trackingNumber != null) {
        int index = Arrays.binarySearch(trackingNumbers, trackingNumber);
        if (index > -1) {
          updateWidget(context, appWidgetManager, id);
        }
      }
    }
  }
}
