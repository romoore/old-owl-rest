package org.makesense.rest;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.makesense.rest.model.Attribute;
import org.makesense.rest.model.SearchWrapper;
import org.makesense.rest.model.WorldState;
import org.makesense.rest.model.WorldStateWrapper;
import org.makesense.rest.serialize.AttributeSerializer;
import org.makesense.rest.serialize.SearchSerializer;
import org.makesense.rest.serialize.WSWSerializer;
import org.makesense.rest.serialize.WorldStateSerializer;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

  final ObjectMapper mapper;

  public ObjectMapperProvider() {
    this.mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule("SimpleModule", new Version(1, 0, 0,
        null));
    module.addSerializer(Attribute.class, new AttributeSerializer());
    module.addSerializer(WorldState.class, new WorldStateSerializer());
    module.addSerializer(WorldStateWrapper.class, new WSWSerializer());
    module.addSerializer(SearchWrapper.class, new SearchSerializer());
    this.mapper.registerModule(module);
  }

  @Override
  public ObjectMapper getContext(Class<?> arg0) {
    return this.mapper;
  }

}
