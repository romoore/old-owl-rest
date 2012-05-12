package org.grailrtls.json;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.grailrtls.json.model.Attribute;
import org.grailrtls.json.model.SearchWrapper;
import org.grailrtls.json.model.WorldStateWrapper;
import org.grailrtls.json.model.WorldState;
import org.grailrtls.json.serialize.AttributeSerializer;
import org.grailrtls.json.serialize.SearchSerializer;
import org.grailrtls.json.serialize.WSWSerializer;
import org.grailrtls.json.serialize.WorldStateSerializer;

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
