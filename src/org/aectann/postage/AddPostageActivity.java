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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AddPostageActivity extends FragmentActivity {

  private TextView trackingNumber;
  
  private TextView name;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.add);
    trackingNumber = (TextView) findViewById(R.id.tracking_number);
    name = (TextView) findViewById(R.id.name);
  }
  
  public void add(View v){
    CharSequence text = trackingNumber.getText();
    if (text.length() > 0) {
      Intent data = new Intent();
      data.putExtra(Constants.TRACKING_NUMBER, text.toString().trim());
      data.putExtra(Constants.NAME, name.getText().toString());
      setResult(RESULT_OK, data);
      finish();
    } else {
      Toast.makeText(this, R.string.provide_postage_number, Toast.LENGTH_SHORT).show();
    }
  }
  
}
