package com.owlplatform.rest.resources;

import java.util.Collection;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.owlplatform.worldmodel.client.Response;

import com.owlplatform.rest.WorldModelJson;
import com.owlplatform.rest.model.Attribute;
import com.owlplatform.rest.model.SearchWrapper;
import com.owlplatform.rest.model.WorldState;
import com.owlplatform.rest.model.WorldStateWrapper;

@Path("/search")
public class SearchResource {

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public SearchWrapper getCurrentSnapshot(
			@QueryParam("q") final String identifierRegex,
			@QueryParam("cb") @DefaultValue("") final String callback) {

	  SearchWrapper wrapper = new SearchWrapper();
	  wrapper.setCallback(callback);
	  
	  
		if (identifierRegex == null || identifierRegex.trim().length() == 0) {
			
			return wrapper;
		}
		
		String[] matchingUris = WorldModelJson.searchIdentifiers(identifierRegex);
		wrapper.setUris(matchingUris);
		
		return wrapper;

	}
}
