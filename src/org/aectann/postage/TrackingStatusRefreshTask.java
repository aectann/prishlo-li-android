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

import java.io.InputStream;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.os.AsyncTask;

public class TrackingStatusRefreshTask extends AsyncTask<String, TrackingInfo, String> {

  private final Context context;

  /**
   * @param context
   */
  TrackingStatusRefreshTask(Context context) {
    this.context = context;
  }

  @Override
  protected String doInBackground(String... trackingNumbers) {
    for (String trackingNumber : trackingNumbers) {
      try {
        TrackingInfo trackingInfo = syncRequest(context, trackingNumber);
        if (trackingInfo != null) {
          publishProgress(trackingInfo);
        } else {
          return context.getString(R.string.error_loading_postage_data);
        }
      } catch (Exception e) {
        return context.getString(R.string.error_loading_postage_data);
      }
    }
    return null;
  }

  public static TrackingInfo syncRequest(Context context, String tracking) throws FactoryConfigurationError {
    TrackingInfo result = null;
    if (tracking != null && tracking.length() > 0) {
      tracking = tracking.toUpperCase();
      HttpClient client = new DefaultHttpClient();
      try {
        HttpResponse response = client.execute(new HttpGet("http://prishlo.li/" + tracking + ".xml"));
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
          InputStream content = response.getEntity().getContent();
          DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
          DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
          Document document = documentBuilder.parse(content);
          String weight = getFirstVaueOrNull(document, "weight");
          String from = getFirstVaueOrNull(document, "from");
          String kind = getFirstVaueOrNull(document, "kind");
          TrackingInfo old = TrackingStorageUtils.loadStoredTrackingInfo(tracking, context);
          result = new TrackingInfo(old != null ? old.getName() : null, tracking, weight, kind, from);
          NodeList checkpoints = document.getElementsByTagName("checkpoint");

          SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
          for (int i = 0; i < checkpoints.getLength(); i++) {
            Node current = checkpoints.item(i);
            NamedNodeMap attributes = current.getAttributes();
            Node state = attributes.getNamedItem("state");
            Node attribute = attributes.getNamedItem("attribute");
            Node date = attributes.getNamedItem("date");
            
            String dateString = date.getNodeValue();
            String attributeString = attribute.getNodeValue();
            String stateString = state.getNodeValue(); 
            String locationString =  current.getFirstChild().getNodeValue();
            result.addStatus(new TrackingStatus(stateString, dateFormat.parse(dateString), attributeString, locationString));
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      } 
    }
    return result;
  }

  private static String getFirstVaueOrNull(Document document, String tag) {
    NodeList elements = document.getElementsByTagName(tag);
    String result = null;
    if (elements.getLength() > 0) {
       Node firstChild = elements.item(0).getFirstChild();
       String nodeValue;
       if (firstChild != null && (nodeValue = firstChild.getNodeValue())!= null && nodeValue.length() > 0) {
         result = nodeValue;
       }
    }
    return result;
  }
}