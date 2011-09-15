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
      data.putExtra(Constants.TRACKING_NUMBER, text.toString());
      data.putExtra(Constants.NAME, name.getText().toString());
      setResult(RESULT_OK, data);
      finish();
    } else {
      Toast.makeText(this, R.string.provide_postage_number, Toast.LENGTH_SHORT).show();
    }
  }
  
}
