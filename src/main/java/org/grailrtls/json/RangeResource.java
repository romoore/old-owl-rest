package org.grailrtls.json;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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
      System.out.println("Processing next...");
      try {
        state = resp.next();
      } catch (Exception e) {
        e.printStackTrace();
        return respStates.toArray(new WorldState[] {});
      }
      System.out.println("Got next...");

      if (resp == null || state == null || state.getURIs().size() == 0) {
        System.out.println("No response.");
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
    
    System.out.println("All done.");

    return respStates.toArray(new WorldState[]{});

  }
}
