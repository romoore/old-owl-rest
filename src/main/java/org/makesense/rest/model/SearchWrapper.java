package org.makesense.rest.model;

public class SearchWrapper {
  private String[] uris;
  private String callback;

  
  public String getCallback() {
    return callback;
  }

  public void setCallback(String callback) {
    this.callback = callback;
  }

  public String[] getUris() {
    return uris;
  }

  public void setUris(String[] uris) {
    this.uris = uris;
  }
}
