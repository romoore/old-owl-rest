package org.makesense.rest.model;


public class Attribute{

  protected String attributeName;
  protected long creationDate;
  protected long expirationDate;
  protected String originName;
  protected byte[] data;
  public String getAttributeName() {
    return attributeName;
  }
  public void setAttributeName(String attributeName) {
    this.attributeName = attributeName;
  }
  public long getCreationDate() {
    return creationDate;
  }
  public void setCreationDate(long creationDate) {
    this.creationDate = creationDate;
  }
  public long getExpirationDate() {
    return expirationDate;
  }
  public void setExpirationDate(long expirationDate) {
    this.expirationDate = expirationDate;
  }
  public String getOriginName() {
    return originName;
  }
  public void setOriginName(String originName) {
    this.originName = originName;
  }
  public byte[] getData() {
    return data;
  }
  public void setData(byte[] data) {
    this.data = data;
  }
  
}
