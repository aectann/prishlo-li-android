package org.aectann.postage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackingInfo implements Serializable{

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
  
}
