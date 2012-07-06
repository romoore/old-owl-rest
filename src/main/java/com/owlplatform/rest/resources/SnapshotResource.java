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
import com.owlplatform.rest.model.WorldState;
import com.owlplatform.rest.model.WorldStateWrapper;

/**
 * Returns a snapshot of the world model at some point in time.  The time value of "0" (default)
 * requests the current snapshot of the world model.
 * @author Robert Moore
 *
 */
@Path("/snapshot")
public class SnapshotResource {

	/**
	 * Gets a snapshot from the world model for an Identifier regex and set of 
	 * Attribute regexes.  If timestamp is specified, then it provides the snapshot
	 * at that point in time, or if timestamp is missing or 0, provides the current snapshot.
	 * @param identifierRegex the regular expression for matching Identifiers.
	 * @param attributeRegex the regular expression for matching Attributes.
	 * @param timestamp the time for the snapshot.
	 * @param callback a callback function to wrap the JSON response.
	 * @return a JSON-formatted response containing the requested snapshot, optionally wrapped in a 
	 * callback function if the callback parameter is provided.
	 */
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public WorldStateWrapper getSnapshot(
			@QueryParam("q") final String identifierRegex,
			@QueryParam("a") @DefaultValue(".*") final String attributeRegex,
			@QueryParam("ts") @DefaultValue("0") final long timestamp,
			@QueryParam("cb") @DefaultValue("") final String callback) {

	  WorldStateWrapper wrapper = new WorldStateWrapper();
	  wrapper.setCallback(callback);
	  
	  
		if (identifierRegex == null || identifierRegex.trim().length() == 0) {
			WorldState errState =  WorldState
					.getErrorState("error.missing parameter", "Missing required parameter \"q\".");
			wrapper.setResponse(new WorldState[]{errState});
			return wrapper;
		}
		Response resp = null;
		com.owlplatform.worldmodel.client.WorldState state;
		if (timestamp == 0) {
			state = WorldModelJson.getCurrentSnapshot(identifierRegex, attributeRegex);
		} else {
			resp = WorldModelJson.getSnapshot(identifierRegex, timestamp, 
					attributeRegex);
			try {
	      state = resp.get();
	    } catch (Exception e) {
	      WorldState errState =  WorldState
	          .getErrorState("error.internal", e.getMessage());
	      wrapper.setResponse(new WorldState[]{errState});
	      return wrapper;
	    }
		}
		
		

		if (state == null || state.getIdentifiers().size() == 0) {
      wrapper.setResponse(new WorldState[]{new WorldState()});
      return wrapper;
		}

		WorldState[] respStates = new WorldState[state.getIdentifiers().size()];
		int i = 0;
		for (String rId : state.getIdentifiers()) {

			WorldState iState = new WorldState();
			iState.setIdentifier(rId);

			Collection<com.owlplatform.worldmodel.Attribute> rAttrs = state
					.getState(rId);
			Attribute[] attrs = new Attribute[rAttrs.size()];
			int j = 0;
			for (com.owlplatform.worldmodel.Attribute a : rAttrs) {
				Attribute newAttr = new Attribute();
				newAttr.setAttributeName(a.getAttributeName());
				newAttr.setOriginName(a.getOriginName());
				newAttr.setCreationDate(a.getCreationDate());
				newAttr.setExpirationDate(a.getExpirationDate());
				newAttr.setData(a.getData());
				attrs[j++] = newAttr;
			}
			iState.setAttributes(attrs);
			respStates[i++] = iState;
		}
		
		wrapper.setResponse(respStates);

		return wrapper;

	}
}
