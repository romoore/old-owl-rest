package org.grailrtls.json;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.grailrtls.libworldmodel.client.Response;


@Path("/snapshot")
public class SnapshotResource {

  @Produces(MediaType.APPLICATION_JSON)
  @GET
  public WorldState[] getAccount(@QueryParam("uri") final String uri) {
    Response resp = WorldModelJson.cwc.getCurrentSnapshot(uri, ".*");
    org.grailrtls.libworldmodel.client.WorldState state;
    try {
      state = resp.get();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    
    if(resp == null || state == null || state.getURIs().size() == 0){
      return null;
    }
    
    WorldState[] respStates = new WorldState[state.getURIs().size()];
    int i = 0;
    for(String rUri : state.getURIs()){
      
      WorldState iState = new WorldState();
      iState.setUri(rUri);
      
      Collection<org.grailrtls.libworldmodel.client.protocol.messages.Attribute> rAttrs = state.getState(rUri);
      Attribute[] attrs = new Attribute[rAttrs.size()];
      int j = 0;
      for(org.grailrtls.libworldmodel.client.protocol.messages.Attribute a : rAttrs){
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
    
    return respStates;
      
  }
}
