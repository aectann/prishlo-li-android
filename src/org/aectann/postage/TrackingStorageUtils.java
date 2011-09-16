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

  private static final int FOUR_HOURS = 14400000;

  public static void delete(Context context, String trackingNumber){
    try {
      File trackingFile = getTrackingFile(context, trackingNumber);
      if (trackingFile.exists()) {
        trackingFile.delete();
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static void store(Context context, TrackingInfo trackingInfo){
    try {
      File trackingFile = getTrackingFile(context, trackingInfo.getTrackingNumber());
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
      File trackingFile = getTrackingFile(context, trackingNumber);
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

  public static boolean isStored(Context context, String trackingNumber) {
    return getTrackingFile(context, trackingNumber).exists();
  }
  
  public static boolean isUpdateNeeded(Context context, String trackingNumber) {
    File trackingFile = getTrackingFile(context, trackingNumber);
    return !trackingFile.exists() || System.currentTimeMillis() - trackingFile.lastModified() > FOUR_HOURS;
  }

  private static File getTrackingFile(Context context, String trackingNumber) {
    return new File(context.getDir(Constants.TRACKINGS, MODE_PRIVATE), trackingNumber);
  }

}
