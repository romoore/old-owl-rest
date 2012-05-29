package org.makesense.rest.resources;

import java.util.Collection;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.grailrtls.libworldmodel.client.Response;
import org.makesense.rest.WorldModelJson;
import org.makesense.rest.model.Attribute;
import org.makesense.rest.model.SearchWrapper;
import org.makesense.rest.model.WorldState;
import org.makesense.rest.model.WorldStateWrapper;

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
