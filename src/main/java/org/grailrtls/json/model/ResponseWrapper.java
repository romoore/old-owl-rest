package org.grailrtls.json.model;

public class ResponseWrapper {
  private WorldState[] response;
  private String callback;

  public WorldState[] getResponse() {
    return response;
  }

  public void setResponse(WorldState[] response) {
    this.response = response;
  }

  public String getCallback() {
    return callback;
  }

  public void setCallback(String callback) {
    this.callback = callback;
  }
}
