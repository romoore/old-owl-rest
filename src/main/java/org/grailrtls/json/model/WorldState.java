package org.grailrtls.json.model;

import java.util.Arrays;

import org.grailrtls.libworldmodel.types.DataConverter;

public class WorldState {
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

}
