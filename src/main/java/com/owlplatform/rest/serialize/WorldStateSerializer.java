package com.owlplatform.rest.serialize;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import com.owlplatform.rest.model.WorldState;

public class WorldStateSerializer extends JsonSerializer<WorldState>{
	 @Override
	  public void serialize(WorldState arg0, JsonGenerator arg1,
	      SerializerProvider arg2) throws IOException, JsonGenerationException {
	   
	    arg1.writeStartObject();
	    arg1.writeStringField("identifier", arg0.getIdentifier());
	    arg1.writeObjectField("attributes", arg0.getAttributes());
	    arg1.writeEndObject();

	  }
}
