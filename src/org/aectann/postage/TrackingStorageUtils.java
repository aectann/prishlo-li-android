package org.aectann.postage;

import static android.content.Context.MODE_PRIVATE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

public class TrackingStorageUtils {

  public static void delete(Context context, String trackingNumber){
    try {
      File trackingFile = new File(context.getDir(Constants.TRACKINGS, MODE_PRIVATE), trackingNumber);
      if (trackingFile.exists()) {
        trackingFile.delete();
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static void store(Context context, TrackingInfo trackingInfo){
    try {
      File trackingFile = new File(context.getDir(Constants.TRACKINGS, MODE_PRIVATE), trackingInfo.getTrackingNumber());
      if (trackingFile.exists()) {
        trackingFile.delete();
      }
      ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(trackingFile));
      out.writeObject(trackingInfo);
      out.close();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static TrackingInfo loadStoredTrackingInfo(String trackingNumber, Context context) {
    ObjectInput in = null;
    try {
      File trackingFile = new File(context.getDir(Constants.TRACKINGS, MODE_PRIVATE), trackingNumber);
      in = new ObjectInputStream(new FileInputStream(trackingFile));
      return (TrackingInfo) in.readObject();
    } catch (FileNotFoundException e) {
      return null;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
    }
  }

}