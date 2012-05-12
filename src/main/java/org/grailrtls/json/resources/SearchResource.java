package org.grailrtls.json.resources;

import java.util.Collection;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.grailrtls.json.WorldModelJson;
import org.grailrtls.json.model.Attribute;
import org.grailrtls.json.model.SearchWrapper;
import org.grailrtls.json.model.WorldStateWrapper;
import org.grailrtls.json.model.WorldState;
import org.grailrtls.libworldmodel.client.Response;

@Path("/search")
public class SearchResource {

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public SearchWrapper getCurrentSnapshot(
			@QueryParam("uri") final String uri,
			@QueryParam("callback") @DefaultValue("") final String callback) {

	  SearchWrapper wrapper = new SearchWrapper();
	  wrapper.setCallback(callback);
	  
	  
		if (uri == null || uri.trim().length() == 0) {
			
			return wrapper;
		}
		
		String[] matchingUris = WorldModelJson.cwc.searchURI(uri);
		wrapper.setUris(matchingUris);
		
		return wrapper;

	}
}
