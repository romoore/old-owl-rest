package org.grailrtls.json;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

  final ObjectMapper mapper;

  public ObjectMapperProvider() {
    this.mapper = new ObjectMapper();
    SimpleModule module = new SimpleModule("SimpleModule", new Version(1, 0, 0,
        null));
    module.addSerializer(Attribute.class, new AttributeSerializer());
    this.mapper.registerModule(module);
  }

  @Override
  public ObjectMapper getContext(Class<?> arg0) {
    return this.mapper;
  }

}
