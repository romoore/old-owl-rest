package com.owlplatform.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.codehaus.jackson.map.ObjectMapper;

import com.owlplatform.rest.resources.RangeResource;
import com.owlplatform.rest.resources.SearchResource;
import com.owlplatform.rest.resources.SnapshotResource;
import com.owlplatform.worldmodel.client.ClientWorldConnection;
import com.owlplatform.worldmodel.client.Response;
import com.owlplatform.worldmodel.client.StepResponse;
import com.owlplatform.worldmodel.client.WorldState;
import com.owlplatform.worldmodel.types.DataConverter;

/**
 * Provides a JSON-formatted interface to the world model for web applications.
 * 
 * @author Robert Moore
 * 
 */
public class WorldModelJson extends Application {

  /**
   * Basic connector for communicating with the world model as a client.
   */
  private static final ClientWorldConnection cwc = new ClientWorldConnection();
  /**
   * Special accessor that provides caching for the current world model state.
   */
  private static WorldModelAccess wma;

  /**
   * Creates a new WorldModelJson object, connecting to the world model at
   * {@code host:port} as a client.
   * 
   * @param host
   *          the hostname of the world model server.
   * @param port
   *          the port number for client connections.
   */
  public WorldModelJson(final String host, final int port) {
    if (host != null && host.trim().length() > 0) {
      cwc.setHost(host);
    }
    if (port > 0 && port < 65536) {
      cwc.setPort(port);
    }

    if (!cwc.connect(10000)) {
      throw new RuntimeException("Unable to connect to world model @"
          + cwc.toString());
    }
    
    // Custom converters here
    DataConverter.putConverter("displayName", "String");
    DataConverter.putConverter("room", "String");
    DataConverter.putConverter("alert.email", "String");
    DataConverter.putConverter("alert.sms", "String");
    DataConverter.putConverter("wet", "Boolean");
    DataConverter.putConverter("closed", "Boolean");
		DataConverter.putConverter("light level", "Integer");
		DataConverter.putConverter("temperature.celsius", "Double");
    
    wma = new WorldModelAccess(cwc);
    wma.startup();

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

  /**
   * Returns a current snapshot of the world model, as known by this object.
   * 
   * @param idRegex
   *          the regular expression to use when matching Identifiers.
   * @param attributeRegexes
   *          the regular expressions to use when matching Attributes. All
   *          Attribute regular expressions must be matched for the identifier
   *          to be included.
   * @return the set of Identifiers and corresponding current states in the
   *         world model.
   */
  public static WorldState getCurrentSnapshot(final String idRegex,
      final String... attributeRegexes) {
    return wma.getCurrentSnapshot(idRegex, attributeRegexes);
  }

  /**
   * Returns the Response object created by a {@code ClientWorldConnection} when
   * requesting an historic snapshot.
   * 
   * @param idRegex
   *          the regular expression for matching Identifiers.
   * @param timestamp
   *          the point in time for the snapshot.
   * @param attributeRegexes
   *          the set of regular expressions for matching Attributes. All
   *          Attribute regular expressions must match for an Identifier and its
   *          Attribute values to be returned.
   * @return the Response generated by the request.
   */
  public static Response getSnapshot(final String idRegex,
      final long timestamp, final String... attributeRegexes) {
    return cwc.getSnapshot(idRegex, timestamp, timestamp, attributeRegexes);
  }

  /**
   * Returns the {@code StepResponse} object obtained from a
   * {@code ClientWorldConnection} when requesting a range request from the
   * world model.
   * 
   * @param idRegex
   *          the regular expression for matching Identifiers.
   * @param start
   *          the earliest timestamp to include in the range
   * @param end
   *          the latest timestamp to include in the range
   * @param attributeRegexes
   *          the regular expressions used to match Attributes. All Attribute
   *          regular expressions must match for the Identifier and its
   *          Attribute values to be returned.
   * @return the set of matching Identifiers and their Attribute values, in
   *         increasing time order, that are valid between the start and end
   *         timestamps.
   */
  public static StepResponse getRangeRequest(final String idRegex,
      final long start, final long end, final String... attributeRegexes) {
    return cwc.getRangeRequest(idRegex, start, end, attributeRegexes);
  }

  /**
   * Returns the set of Identifier values that match the provided regular
   * expression.
   * 
   * @param idRegex
   *          the regular expression for matching Identifier values.
   * @return the set of matching Identifiers.
   */
  public static String[] searchIdentifiers(final String idRegex) {
    return cwc.searchId(idRegex);
  }

  /**
   * Terminates all threads, releases resources for this object.
   */
  public void shutdown() {
    wma.shutdown();
    cwc.disconnect();

  }
}
