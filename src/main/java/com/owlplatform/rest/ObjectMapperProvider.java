package com.owlplatform.rest;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

import com.owlplatform.rest.model.Attribute;
import com.owlplatform.rest.model.SearchWrapper;
import com.owlplatform.rest.model.WorldState;
import com.owlplatform.rest.model.WorldStateWrapper;
import com.owlplatform.rest.serialize.AttributeSerializer;
import com.owlplatform.rest.serialize.SearchSerializer;
import com.owlplatform.rest.serialize.WSWSerializer;
import com.owlplatform.rest.serialize.WorldStateSerializer;

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
