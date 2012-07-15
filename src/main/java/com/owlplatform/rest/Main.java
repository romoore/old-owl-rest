package com.owlplatform.rest;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.api.container.filter.GZIPContentEncodingFilter;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * Configures, launches, and controls this application.
 * 
 * @author Robert Moore
 * 
 */
public class Main {

  /**
   * A reference to the WorldModelJson server to create.
   */
  private static WorldModelJson wmServer = null;

  /**
   * Determines the default binding port for incoming HTTP connections to this
   * server.
   * 
   * @param defaultPort
   *          the default to use if the system property is not defined.
   * @return the system property, if defined, else the default value.
   */
  private static int getPort(int defaultPort) {
    // grab port from environment, otherwise fall back to default port 9998
    String httpPort = System.getProperty("bind.port");
    if (null != httpPort) {
      try {
        return Integer.parseInt(httpPort);
      } catch (NumberFormatException e) {
        System.err.println("Invalid bind port: " + httpPort
            + ". Defaulting to " + defaultPort);
      }
    }
    return defaultPort;
  }

  /**
   * Determines the local bind hostname for incoming HTTP connections. Expects
   * the "bind.host" system property.
   * 
   * @return the local bound hostname, or "localhost" if undefined.
   */
  private static URI getBaseURI() {
    String bindHost = System.getProperty("bind.host");
    if (bindHost == null) {
      bindHost = "localhost";
    }

    return UriBuilder.fromUri("http://" + bindHost + "/grailrest/")
        .port(getPort(9998)).build();
  }

  /**
   * Stores the base URI value for requests to this application.
   */
  public static final URI BASE_URI = getBaseURI();

  /**
   * Configures and starts a grizzly server providing a WorldModelJson service
   * that connects to the world model server at "host:cport".
   * 
   * @param host
   *          the hostname of the world model.
   * @param cPort
   *          the port for client connections.
   * @return an instance of a grizzly HTTP server
   * @throws IOException
   */
  @SuppressWarnings("unchecked")
  protected static HttpServer startServer(final String host, final int cPort)
      throws IOException {
    // final Map<String, String> initParams = new HashMap<String, String>();

    wmServer = new WorldModelJson(host, cPort);
    ApplicationAdapter rc = new ApplicationAdapter(wmServer);
    rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
    rc.getContainerResponseFilters().add(new GZIPContentEncodingFilter());

    System.out.println("Starting grizzly2...");
    return  GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
  }

  /**
   * Grabs the system property value for the world model hostname, or the
   * default provided.
   * 
   * @param defaultHost
   *          the default hostname.
   * @return the system property value if present, or the default if not.
   */
  protected static String getWmHost(final String defaultHost) {
    String wmHost = System.getProperty("wm.host");
    if (wmHost == null) {
      return defaultHost;
    }
    return wmHost;
  }

  /**
   * Grabs the system property for world model client port numbers, or uses a
   * default provided.
   * 
   * @param defaultPort
   *          the default value in case the property is not set.
   * @return the system property value if set, or the default value if not.
   */
  protected static int getWmClientPort(final int defaultPort) {
    String portStr = System.getProperty("wm.client.port");
    if (portStr == null) {
      return defaultPort;
    }
    return Integer.parseInt(portStr);
  }

  /**
   * Parse arguments, start a grizzly HTTP server, and start a WorldModelJson
   * running on it.
   * 
   * @param args
   *          world model server hostname, client port
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {

    String host = getWmHost(args.length < 1 ? "localhost" : args[0]);
    int clientPort = getWmClientPort(args.length < 2 ? 7010 : Integer
        .parseInt(args[1]));
    // Grizzly 2 initialization
    HttpServer httpServer = startServer(host, clientPort);
    System.out.println(String.format(
        "Jersey app started with WADL available at "
            + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
    System.in.read();
    httpServer.stop();
    wmServer.shutdown();
  }
}
