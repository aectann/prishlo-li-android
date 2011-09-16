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
import java.util.Date;

public class TrackingStatus implements Serializable, Comparable<TrackingStatus>{
  
  private static final long serialVersionUID = -3355684502183851259L;

  private String state;
  
  private Date date;
  
  private String attribute;
  
  private String location;

  public TrackingStatus(String state, Date date, String attribute, String location) {
    super();
    this.state = state;
    this.date = date;
    this.attribute = attribute;
    this.location = location;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getAttribute() {
    return attribute;
  }

  public void setAttribute(String attribute) {
    this.attribute = attribute;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
  
  @Override
  public String toString() {
    boolean hasAttribute = attribute != null && attribute.trim().length() > 0;
    return location + " (" + state + (hasAttribute ? ":" + attribute : "") + ")";
  }

  @Override
  public int compareTo(TrackingStatus another) {
    return getDate().compareTo(another.getDate());
  }
}