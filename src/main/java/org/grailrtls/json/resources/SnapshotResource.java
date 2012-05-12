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
import org.grailrtls.json.model.WorldStateWrapper;
import org.grailrtls.json.model.WorldState;
import org.grailrtls.libworldmodel.client.Response;

@Path("/snapshot")
public class SnapshotResource {

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public WorldStateWrapper getCurrentSnapshot(
			@QueryParam("uri") final String uri,
			@QueryParam("attribute") @DefaultValue(".*") final String attribute,
			@QueryParam("timestamp") @DefaultValue("0") final long timestamp,
			@QueryParam("callback") @DefaultValue("") final String callback) {

	  WorldStateWrapper wrapper = new WorldStateWrapper();
	  wrapper.setCallback(callback);
	  
	  
		if (uri == null || uri.trim().length() == 0) {
			WorldState errState =  WorldState
					.getErrorState("error.missing parameter", "Missing required parameter \"uri\".");
			wrapper.setResponse(new WorldState[]{errState});
			return wrapper;
		}
		Response resp = null;
		if (timestamp == 0) {
			resp = WorldModelJson.cwc.getCurrentSnapshot(uri, attribute);
		} else {
			resp = WorldModelJson.cwc.getSnapshot(uri, timestamp, timestamp,
					attribute);
		}
		org.grailrtls.libworldmodel.client.WorldState state;
		try {
			state = resp.get();
		} catch (Exception e) {
		  WorldState errState =  WorldState
          .getErrorState("error.internal", e.getMessage());
      wrapper.setResponse(new WorldState[]{errState});
      return wrapper;
		}

		if (resp == null || state == null || state.getURIs().size() == 0) {
      wrapper.setResponse(new WorldState[]{new WorldState()});
      return wrapper;
		}

		WorldState[] respStates = new WorldState[state.getURIs().size()];
		int i = 0;
		for (String rUri : state.getURIs()) {

			WorldState iState = new WorldState();
			iState.setUri(rUri);

			Collection<org.grailrtls.libworldmodel.client.protocol.messages.Attribute> rAttrs = state
					.getState(rUri);
			Attribute[] attrs = new Attribute[rAttrs.size()];
			int j = 0;
			for (org.grailrtls.libworldmodel.client.protocol.messages.Attribute a : rAttrs) {
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
