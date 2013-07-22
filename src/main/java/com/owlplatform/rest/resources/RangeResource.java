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
import com.owlplatform.rest.model.WorldStateWrapper;
import com.owlplatform.worldmodel.client.StepResponse;

@Path("/range")
public class RangeResource {

  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public WorldStateWrapper getRange(@QueryParam("q") final String identifierRegex,
      @QueryParam("a") @DefaultValue(".*") final String attributeRegex,
      @QueryParam("st") final Long start, @QueryParam("et") final Long end,
      @QueryParam("cb") @DefaultValue("") final String callback) {

    WorldStateWrapper wrapper = new WorldStateWrapper();
    wrapper.setCallback(callback);
    
    if (identifierRegex == null || identifierRegex.trim().length() == 0) {
      WorldState errState =  WorldState
          .getErrorState("error.missing parameter", "Missing required parameter \"q\".");
      wrapper.setResponse(new WorldState[]{errState});
      return wrapper;
      
     
    }
    if (start == null) {
      WorldState errState =  WorldState
          .getErrorState("error.missing parameter", "Missing required parameter \"st\".");
      wrapper.setResponse(new WorldState[]{errState});
      return wrapper;
    }

    if (end == null) {
      WorldState errState =  WorldState
          .getErrorState("error.missing parameter", "Missing required parameter \"et\".");
      wrapper.setResponse(new WorldState[]{errState});
      return wrapper;
    }

   

    StepResponse resp = null;
    resp = WorldModelJson.getRangeRequest(identifierRegex, start, end,
        attributeRegex);
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
      WorldState errState = WorldState.getErrorState("error.service", resp
          .getError().getMessage());
      wrapper.setResponse(new WorldState[]{errState});
      return wrapper;
    }

    while (resp.hasNext()) {
      try {
        state = resp.next();
      } catch (Exception e) {
        e.printStackTrace();
        break;
      }

      if (resp == null || state == null || state.getIdentifiers().size() == 0) {
        wrapper.setResponse(respStates.toArray(new WorldState[]{}));
        return wrapper;
      }

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
        respStates.add(iState);
      }
    }
    
    wrapper.setResponse(respStates.toArray(new WorldState[] {}));
    return wrapper;

  }
}
