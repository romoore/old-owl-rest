package org.grailrtls.json;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.grailrtls.libcommon.util.NumericUtils;
import org.grailrtls.libworldmodel.types.DataConverter;

public class AttributeSerializer extends JsonSerializer<Attribute> {

  @Override
  public void serialize(Attribute arg0, JsonGenerator arg1,
      SerializerProvider arg2) throws IOException, JsonGenerationException {
    String dataAsJson = NumericUtils.toEmptyHexString(arg0.getData());
    if (arg0.getData() != null) {
      try {
        dataAsJson = String.valueOf(DataConverter.decodeUri(
            arg0.getAttributeName(), arg0.getData()));
      } catch (IllegalArgumentException e) {
        // No known decoder for this type, stick to hex string...
      }
    }
    arg1.writeStartObject();
    arg1.writeStringField("attributeName", arg0.getAttributeName());
    arg1.writeStringField("origin", arg0.getOriginName());
    arg1.writeNumberField("creationDate", arg0.getCreationDate());
    arg1.writeNumberField("expirationDate", arg0.getExpirationDate());
    arg1.writeStringField("data", dataAsJson);
    arg1.writeEndObject();

  }

}
