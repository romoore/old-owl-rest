package com.owlplatform.rest.resources;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;


import com.owlplatform.rest.WorldModelJson;
import com.owlplatform.rest.model.Attribute;
import com.owlplatform.rest.model.WorldState;
import com.owlplatform.worldmodel.client.StepResponse;

@Path("/range")
public class RangeResource {

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public WorldState[] getRange(
			@QueryParam("q") final String identifierRegex,
			@QueryParam("a") @DefaultValue(".*") final String attributeRegex,
			@QueryParam("st") final Long start,
			@QueryParam("et") final Long end) {

		if (identifierRegex == null || identifierRegex.trim().length() == 0) {
			return new WorldState[] { WorldState
					.getErrorState("error.missing parameter", "Missing required parameter \"uri\".") };
		}
		if (start == null) {
			return new WorldState[] { WorldState
					.getErrorState("error.missing parameter", "Missing required parameter \"start\".") };
		}

		if (end == null) {
			return new WorldState[] { WorldState
					.getErrorState("error.missing parameter", "Missing required parameter \"end\".") };
		}
		StepResponse resp = null;
		resp = WorldModelJson.getRangeRequest(identifierRegex, start, end, attributeRegex);
		com.owlplatform.worldmodel.client.WorldState state;

		ArrayList<WorldState> respStates = new ArrayList<WorldState>();
		while (!resp.isComplete() && !resp.isError()) {

			try {
				Thread.sleep(1);
			} catch (InterruptedException ie) {
				// Ignored
			}

		}

		if (resp.isError()) {
			return new WorldState[] { WorldState.getErrorState("error.service", resp.getError()
					.getMessage()) };
		}

		while (resp.hasNext()) {
			try {
				state = resp.next();
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}

			if (resp == null || state == null || state.getIdentifiers().size() == 0) {
				return respStates.toArray(new WorldState[] {});
			}

			for (String rUri : state.getIdentifiers()) {

				WorldState iState = new WorldState();
				iState.setUri(rUri);

				Collection<com.owlplatform.worldmodel.Attribute> rAttrs = state
						.getState(rUri);
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
				respStates.add(iState);
			}
		}

		return respStates.toArray(new WorldState[] {});

	}
}
