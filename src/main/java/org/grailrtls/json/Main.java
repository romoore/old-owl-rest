package org.grailrtls.json;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.container.grizzly2.GrizzlyWebContainerFactory;
import com.sun.jersey.api.core.ApplicationAdapter;
import com.sun.jersey.api.json.JSONConfiguration;

import org.glassfish.grizzly.http.server.HttpServer;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;

public class Main {
  
  private static WorldModelJson wmServer = null;

  private static int getPort(int defaultPort) {
    // grab port from environment, otherwise fall back to default port 9998
    String httpPort = System.getProperty("jersey.test.port");
    if (null != httpPort) {
      try {
        return Integer.parseInt(httpPort);
      } catch (NumberFormatException e) {
      }
    }
    return defaultPort;
  }

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://localhost/grailrest/").port(getPort(9998)).build();
  }

  public static final URI BASE_URI = getBaseURI();

  protected static HttpServer startServer(final String host, final int cPort) throws IOException {
    final Map<String, String> initParams = new HashMap<String, String>();

    wmServer = new WorldModelJson(host, cPort);
    ApplicationAdapter rc = new ApplicationAdapter(wmServer);
    rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);


    System.out.println("Starting grizzly2...");
    return GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
  }
  
  protected static String getWmHost(final String defaultHost){
    String wmHost = System.getProperty("grail.wm.host");
    if(wmHost == null){
      return defaultHost;
    }
    return wmHost;
  }
  
  protected static int getWmClientPort(final int defaultPort){
    String portStr = System.getProperty("grail.wm.client.port");
    if(portStr == null){
      return defaultPort;
    }
    return Integer.parseInt(portStr);
  }

  public static void main(String[] args) throws IOException {
    
    String host = getWmHost(args.length < 1 ? "localhost" : args[0]);
    int clientPort = getWmClientPort(args.length < 2 ? 7010 : Integer.parseInt(args[1]));
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
