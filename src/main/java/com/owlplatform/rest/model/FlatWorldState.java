package com.owlplatform.rest.model;

public class FlatWorldState {
	protected String uri;
	protected String attributeName;
	protected long creationDate;
	protected long expirationDate;
	protected String originName;
	protected byte[] data;
	public String getUri() {
		return this.uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getAttributeName() {
		return this.attributeName;
	}
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	public long getCreationDate() {
		return this.creationDate;
	}
	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
	public long getExpirationDate() {
		return this.expirationDate;
	}
	public void setExpirationDate(long expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getOriginName() {
		return this.originName;
	}
	public void setOriginName(String originName) {
		this.originName = originName;
	}
	public byte[] getData() {
		return this.data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
}
