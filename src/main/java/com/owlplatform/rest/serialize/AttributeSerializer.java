package com.owlplatform.rest.serialize;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.util.StdDateFormat;


import com.owlplatform.common.util.NumericUtils;
import com.owlplatform.rest.model.Attribute;
import com.owlplatform.worldmodel.types.DataConverter;

public class AttributeSerializer extends JsonSerializer<Attribute> {

  
  @Override
  public void serialize(Attribute arg0, JsonGenerator arg1,
      SerializerProvider arg2) throws IOException, JsonGenerationException {
    String dataAsJson = NumericUtils.toEmptyHexString(arg0.getData());
    if (arg0.getData() != null) {
      try {
        dataAsJson = DataConverter.asString(
            arg0.getAttributeName(), arg0.getData());
      } catch (IllegalArgumentException e) {
        // No known decoder for this type, stick to hex string...
      }
    }
    
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    format.setTimeZone(TimeZone.getTimeZone("GMT"));
    
    
    arg1.writeStartObject();
    arg1.writeStringField("attributeName", arg0.getAttributeName());
    arg1.writeStringField("origin", arg0.getOriginName());
    arg1.writeStringField("creationDate", format.format(new Date(arg0.getCreationDate())));
    arg1.writeStringField("expirationDate", arg0.getExpirationDate() == 0 ? "" : format.format(new Date(arg0.getExpirationDate())));
    arg1.writeStringField("data", dataAsJson);
    arg1.writeEndObject();

  }

}
