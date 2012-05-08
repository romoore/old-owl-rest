package org.grailrtls.json.resources;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.grailrtls.json.WorldModelJson;
import org.grailrtls.json.model.Attribute;
import org.grailrtls.json.model.WorldState;
import org.grailrtls.libworldmodel.client.Response;
import org.grailrtls.libworldmodel.client.StepResponse;

@Path("/range")
public class RangeResource {

  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public WorldState[] getRange(@QueryParam("uri") final String uri,
      @QueryParam("attribute") @DefaultValue(".*") final String attribute,
      @QueryParam("start") final long start, @QueryParam("end") final long end) {

    StepResponse resp = null;
    resp = WorldModelJson.cwc.getRangeRequest(uri, start, end, attribute);
    org.grailrtls.libworldmodel.client.WorldState state;

    ArrayList<WorldState> respStates = new ArrayList<WorldState>();
    while (!resp.isComplete() && !resp.isError()) {

      try {
        Thread.sleep(1);
      } catch (InterruptedException ie) {
        // Ignored
      }

    }

    if (resp.isError()) {
      WorldState errState = new WorldState();
      errState.setUri("error");
      Attribute errAttr = new Attribute();
      errAttr.setAttributeName("message");
      try {
        errAttr.setData(resp.getError().getMessage().getBytes("UTF-16BE"));
      } catch (UnsupportedEncodingException e) {
        // FIXME: Handle this when we're ready to release.
        // This really shouldn't happen...
      }
      errState.setAttributes(new Attribute[] { errAttr });
      return new WorldState[] { errState };
    }

    while (resp.hasNext()) {
      try {
        state = resp.next();
      } catch (Exception e) {
        e.printStackTrace();
        break;
      }

      if (resp == null || state == null || state.getURIs().size() == 0) {
        return respStates.toArray(new WorldState[] {});
      }

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
        respStates.add(iState);
      }
    }

    return respStates.toArray(new WorldState[] {});

  }
}
