package org.aectann.postage;

import android.app.Application;

public class Postages extends Application {

  private boolean listUpdated;
  
  public void setListUpdated() {
    listUpdated = true;
  }

  public boolean isListUpdated() {
    return listUpdated;
  }
}
