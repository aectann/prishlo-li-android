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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackingInfo implements Serializable, Comparable<TrackingInfo>{

  private static final long serialVersionUID = 4795313244600591432L;

  private String name;
  
  private String trackingNumber;
  
  private String weight;
  
  private String kind;
  
  private String from;
  
  private List<TrackingStatus> statuses;
  
  public TrackingInfo(String name, String trackingNumber, String weight, String kind, String from) {
    super();
    this.name = name;
    this.trackingNumber = trackingNumber;
    this.weight = weight;
    this.kind = kind;
    this.from = from;
    this.statuses = new ArrayList<TrackingStatus>();
  }

  public String getName() {
    if (name == null || name.trim().length() == 0) {
      return getTrackingNumber();
    }
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTrackingNumber() {
    return trackingNumber;
  }

  public void setTrackingNumber(String trackingNumber) {
    this.trackingNumber = trackingNumber;
  }

  public List<TrackingStatus> getStatuses() {
    return statuses;
  }
  
  public void addStatus(TrackingStatus status) {
    statuses.add(status);
  }

  public String getStatus() {
    TrackingStatus status = getLatestStatusOrNull();
    if (status != null) {
      return status.toString();
    }
    return null;
  }

  private TrackingStatus getLatestStatusOrNull() {
    TrackingStatus status;
    if (statuses.size() > 0) {
      status = statuses.get(statuses.size() - 1);
    } else {
      status = null;
    }
    return status;
  }

  public Date getDate() {
    TrackingStatus status = getLatestStatusOrNull();
    if (status != null) {
      return status.getDate();
    }
    return null;
  }

  public String getWeight() {
    return weight;
  }

  public void setWeight(String weight) {
    this.weight = weight;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  @Override
  public int compareTo(TrackingInfo another) {
    Date date = getDate();
    Date anotherDate = another.getDate();
    if (date != null) {
      if (anotherDate != null) {
        return -date.compareTo(anotherDate);
      } else {
        return -1;
      }
    } else {
      if (anotherDate == null) {
        return -getName().compareTo(another.getName());
      } else {
        return 1;
      }
    }
  }
  
}
