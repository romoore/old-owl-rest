package com.owlplatform.rest.model;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.codehaus.jackson.annotate.JsonIgnore;

public class WorldState {
  
  @JsonIgnore
	private String uri;
	private Attribute[] attributes;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Attribute[] getAttributes() {
		return attributes;
	}

	public void setAttributes(Attribute[] attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.valueOf(this.uri));

		if (this.attributes != null) {

			for (Attribute attr : this.attributes) {
				sb.append("\n\t");
				sb.append(attr.getAttributeName());
				sb.append('/');
				sb.append(Arrays.toString(attr.getData()));
				sb.append(' ');
				sb.append(attr.getOriginName());

			}
		}
		return sb.toString();
	}
	
	public static WorldState getErrorState(final String type, final String message){
		 WorldState errState = new WorldState();
	      errState.setUri(type);
	      Attribute errAttr = new Attribute();
	      errAttr.setAttributeName("error.message");
	      try {
	        errAttr.setData(message.getBytes("UTF-16BE"));
	      } catch (UnsupportedEncodingException e) {
	        // FIXME: Handle this when we're ready to release.
	        // This really shouldn't happen...
	      }
	      errAttr.setOriginName("grail-rest-server");
	      errState.setAttributes(new Attribute[] { errAttr });
	      return errState;
	}
}
