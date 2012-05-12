package org.grailrtls.json.serialize;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.grailrtls.json.model.SearchWrapper;
import org.grailrtls.json.model.WorldStateWrapper;
import org.grailrtls.json.model.WorldState;

public class SearchSerializer extends JsonSerializer<SearchWrapper> {
  @Override
  public void serialize(SearchWrapper wrapper, JsonGenerator arg1,
      SerializerProvider arg2) throws IOException, JsonGenerationException {

    String callback = wrapper.getCallback();
    boolean useCallback = (callback != null && callback.trim().length() > 0);
    if (useCallback) {
      callback = callback.trim();
      arg1.writeRaw(callback.trim() + "(");
    }

    // arg1.writeStartArray();
    if (wrapper.getUris() == null) {
      arg1.writeRaw("[]");
    } else {
      arg1.writeObject(wrapper.getUris());
    }
    // arg1.writeEndArray();

    if (useCallback) {
      arg1.writeRaw(");");
    }
  }
}
