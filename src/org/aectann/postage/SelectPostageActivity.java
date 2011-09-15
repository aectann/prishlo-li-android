package org.aectann.postage;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RemoteViews;
import android.widget.Toast;

public class SelectPostageActivity extends PostageListActivity {

  private int mAppWidgetId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setResult(Activity.RESULT_CANCELED);
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    Toast.makeText(this, R.string.select_a_postage, Toast.LENGTH_SHORT).show();
  }

  @Override
  protected OnItemClickListener getOnItemClickListener() {
    return new OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> list, View row, int position, long id) {
        TrackingInfo trackingInfo = (TrackingInfo) list.getAdapter().getItem(position);
        PreferenceManager.getDefaultSharedPreferences(SelectPostageActivity.this).edit()
            .putString(String.valueOf(mAppWidgetId), trackingInfo.getTrackingNumber()).commit();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(SelectPostageActivity.this);
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.postage_status);
        PostageStatusWidgetProvider.updateWidget(SelectPostageActivity.this, appWidgetManager, mAppWidgetId, views);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
      }
    };
  }
}
