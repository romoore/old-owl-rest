package org.grailrtls.json;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.grailrtls.libworldmodel.client.protocol.messages.Attribute;

@Path("/snapshot/test")
@Produces(MediaType.TEXT_PLAIN)
public class WMTester {

	@GET
	@Path("/text")
	public String getPlainWS(@QueryParam("uri") String uri,
			@QueryParam("attribute") String attribute) {
		return makePlain(uri, attribute);
	}

	private String makePlain(final String uri,
			final String attributeName) {
		WorldState state = new WorldState();
		state.setUri(uri);
		Attribute attr = new Attribute();
		attr.setAttributeName(attributeName);
		attr.setData(new byte[] {0});
		attr.setOriginName("WM Test");
		attr.setCreationDate(System.currentTimeMillis());
		attr.setExpirationDate(0l);
		state.setAttributes(new Attribute[]{attr});
		return state.toString();
	}

	private String makeHtml(final String uri, final String attributeName) {
		
		StringBuilder sb = new StringBuilder("<html><head><title>REST Title</title></head><body><h1>WM Test</h1>");
		sb.append("<dl>");
		sb.append("<dt>").append(uri).append("</dt>");
		sb.append("<dd>");
		sb.append("<ul>").append("<li>").append(attributeName).append("/").append("FALSE").append(" WM Test</li></ul>");
		sb.append("</dd></dl>");
		return sb.toString();
	}

	// The Java method will process HTTP GET requests
	@GET
	@Path("/html")
	@Produces(MediaType.TEXT_HTML)
	public String getHtmlWS(@QueryParam("uri") String uri,
			@QueryParam("attribute") String attribute) {
		return makeHtml(uri,attribute);

	}

	@GET
	@Path("/json")
	@Produces(MediaType.APPLICATION_JSON)
	public WorldState getJsonWS(@QueryParam("uri") String uri,
			@QueryParam("attribute") String attribute) {
		WorldState state = new WorldState();
		state.setUri(uri);
		Attribute attr = new Attribute();
		attr.setAttributeName(attribute);
		attr.setData(new byte[] {0});
		attr.setOriginName("WM Test");
		attr.setCreationDate(System.currentTimeMillis());
		attr.setExpirationDate(0l);
		state.setAttributes(new Attribute[]{attr});
		return state;
	}
}
