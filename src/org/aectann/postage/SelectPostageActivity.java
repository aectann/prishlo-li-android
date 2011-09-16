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

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class SelectPostageActivity extends PostageListActivity {

  private int widgetId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setResult(Activity.RESULT_CANCELED);
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
      widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    Toast.makeText(this, R.string.select_a_postage, Toast.LENGTH_SHORT).show();
  }

  @Override
  protected OnItemClickListener getOnItemClickListener() {
    return new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> list, View row, int position, long id) {
        TrackingInfo trackingInfo = (TrackingInfo) list.getAdapter().getItem(position);
        PostageStatusWidgetProvider.registerWidget(SelectPostageActivity.this, widgetId, trackingInfo);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(SelectPostageActivity.this);
        PostageStatusWidgetProvider.updateWidget(SelectPostageActivity.this, appWidgetManager, widgetId);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, resultValue);
        finish();
      }
    };
  }

}
