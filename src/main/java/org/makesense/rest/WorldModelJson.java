package org.makesense.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.grailrtls.libworldmodel.client.ClientWorldConnection;
import org.makesense.rest.resources.RangeResource;
import org.makesense.rest.resources.SearchResource;
import org.makesense.rest.resources.SnapshotResource;

public class WorldModelJson extends Application {

  public static final ClientWorldConnection cwc = new ClientWorldConnection();

  public WorldModelJson(final String host, final int port) {
    if (host != null && host.trim().length() > 0) {
      cwc.setHost(host);
    }
    if (port > 0 && port < 65536) {
      cwc.setPort(port);
    }
    
    if(!cwc.connect()){
      throw new RuntimeException("Unable to connect to world model @" + cwc.toString());
    }
    
    
  }

  @Override
  public Set<Class<?>> getClasses() {
    
    final Set<Class<?>> classes = new HashSet<Class<?>>();

    // register root resources
    classes.add(SnapshotResource.class);
    classes.add(RangeResource.class);
    classes.add(SearchResource.class);

    // register Jackson ObjectMapper resolver
    classes.add(ObjectMapper.class);
    
    // Register custom serializer
    classes.add(ObjectMapperProvider.class);

    return classes;
  }
  
  public void shutdown(){
    this.cwc.disconnect();
  }
}
