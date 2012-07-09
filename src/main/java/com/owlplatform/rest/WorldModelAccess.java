/*
 * Owl Platform
 * Copyright (C) 2012 Robert Moore and the Owl Platform
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *  
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package com.owlplatform.rest;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.owlplatform.worldmodel.Attribute;
import com.owlplatform.worldmodel.client.ClientWorldConnection;
import com.owlplatform.worldmodel.client.StepResponse;
import com.owlplatform.worldmodel.client.WorldState;

/**
 * Wrapper class for interactions with the world model. Hides how the data is
 * actually obtained and stored from the rest of the app.
 * 
 * @author Robert Moore
 * 
 */
public class WorldModelAccess {

  /**
   * Logger for the accessor and its updater.
   */
  static final Logger log = LoggerFactory.getLogger(WorldModelAccess.class);

  /**
   * The current state of the world model, as provided by the stream request.
   */
  final Map<String, Collection<Attribute>> currentState = new ConcurrentHashMap<String, Collection<Attribute>>();

  /**
   * Connector for client-side (data request).
   */
  final ClientWorldConnection cwc;

  /**
   * A thread-based class that updates the current state of the world model
   * based on a stream request for all data.
   * 
   * @author Robert Moore
   * 
   */
  private class StreamUpdater implements Runnable {

    /**
     * Flag for the thread to keep running.
     */
    private boolean keepRunning = true;

    /**
     * Creates a new stream updater for this accessor.
     */
    public StreamUpdater() {

    }

    @Override
    public void run() {

      while (this.keepRunning) {
        while (!WorldModelAccess.this.cwc.isConnected()) {
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            continue;
          }
        }
        WorldModelAccess.this.currentState.clear();
        StepResponse stream = WorldModelAccess.this.cwc.getStreamRequest(".*",
//            System.currentTimeMillis(), 0l, "(^(?!link).*)");
//            System.currentTimeMillis(), 0l, "^[^(link|average )].*");
            System.currentTimeMillis(),0l,".*");
        while (!stream.isComplete() && !stream.isError()) {
          try {
            System.out.println("Waiting for an update...");
            WorldState state = stream.next();
            System.out.println(state.toString());

            for (String id : state.getIdentifiers()) {
              Collection<Attribute> newAttribs = state.getState(id);
              Collection<Attribute> currAttribs = WorldModelAccess.this.currentState
                  .get(id);
              if (currAttribs == null) {
                WorldModelAccess.this.currentState.put(id, newAttribs);
                continue;
              }
              // Go through each new attribute, updating the state
              for (Attribute nA : newAttribs) {
                // Skip anything that isn't current
                if (nA.getExpirationDate() != 0l) {
                  continue;
                }
                // Go through the current state, removing the obsoleted value if
                // present
                boolean matched = false;
                for (Iterator<Attribute> iter = currAttribs.iterator(); iter
                    .hasNext();) {
                  Attribute cA = iter.next();
                  if (nA.getAttributeName().equals(cA.getAttributeName())
                      && nA.getOriginName().equals(cA.getOriginName())
                      && nA.getCreationDate() > cA.getCreationDate()) {
                    iter.remove();
                    matched = true;
                    break;
                  }
                }
                // Add the new value
                if(matched){
                  currAttribs.add(nA);
                  System.out.println("Updated " + nA);
                }
              }
            }

          } catch (Exception e) {
            log.error("Unable to get next world model state.", e);
            continue;
          }
        }
      }

      WorldModelAccess.this.cwc.disconnect();
      this.keepRunning = true;
    }

    /**
     * Terminates the updater.
     */
    public void shutdown() {
      this.keepRunning = false;
    }
  }

  /**
   * The updater for this accessor.
   */
  private final StreamUpdater updater = new StreamUpdater();
  /**
   * Thread for running the updater.
   */
  private final Thread updateThread;

  /**
   * Creates a new accessor for a world model with the specified client
   * connection parameters.
   * 
   * @param cwc the client-world model connection to use.
   */
  public WorldModelAccess(final ClientWorldConnection cwc) {
    this.cwc = cwc;
    this.updateThread = new Thread(this.updater);
  }

  /**
   * Starts the updater thread processing updates.
   */
  public void startup(){
    this.updateThread.start();
  }
  
  /**
   * Shuts down this accessor and performs any necessary clean-up.
   */
  public void shutdown() {
    if (this.updateThread != null) {
      this.updater.shutdown();
      this.updateThread.interrupt();
    }
  }

  /**
   * Returns the Identifiers and current attribute values in the world model for
   * the provided Identifier and Attribute regular expression values.
   * 
   * @param idRegex
   *          the Identifier regular expression to match.
   * @param attributeRegexes
   *          the Attribute regular expression to match.
   * @return the set of Identifiers and corresponding Attribute values that are
   *         "current" in the world model.
   */
  public WorldState getCurrentSnapshot(final String idRegex,
      final String... attributeRegexes) {
    WorldState state = new WorldState();
    Pattern idPattern = Pattern.compile(idRegex, Pattern.MULTILINE
        | Pattern.DOTALL);
    log.debug("Generated Identifier pattern: {}", idPattern);
    Pattern[] attPatterns = null;
    if (attributeRegexes != null) {
      attPatterns = new Pattern[attributeRegexes.length];
      for (int i = 0; i < attributeRegexes.length; ++i) {
        attPatterns[i] = Pattern.compile(attributeRegexes[i], Pattern.MULTILINE
            | Pattern.DOTALL);
        log.debug("Generated attribute pattern: {}",attPatterns[i]);
      }
    }

    idForLoop: for (String id : this.currentState.keySet()) {
      Matcher m = idPattern.matcher(id);
      if (!m.matches()) {
        log.debug("ID {} does not match {}",id,idPattern);
        continue;
      }
      // Next check attributes
      if (attPatterns == null) {
        log.info("No attributes to match. Adding {}", id);
        Collection<Attribute> matchedAttributes = this.currentState.get(id);
        state.addState(id, matchedAttributes);
        continue;
      }
      Collection<Attribute> attributes = this.currentState.get(id);
      // No attributes, 
      if (attributes == null) {
        log.info("No attributes available. Skipping {}", id);
        continue;
      }
      attrPatternForLoop: for (Pattern attributePattern : attPatterns) {
        for (Attribute chkAttribute : attributes) {
          Matcher am = attributePattern
              .matcher(chkAttribute.getAttributeName());
          if (am.matches()) {
            log.debug("Matched {} with {}", attributePattern, chkAttribute.getAttributeName());
            // We got a match, so check the next attribute regex
            continue attrPatternForLoop;
          }
        }
        log.debug("No match for pattern {}", attributePattern);
        /*
         * No match (Since we exited this loop). That means we can't add this
         * id, since not all attribute regexes matched
         */
        continue idForLoop;
      }
      log.info("Matched all attribute patterns for {}",id);
      // Made it through all attribute regexes, so add the state
      state.addState(id, attributes);
    }
    // Return whatever we've matched
    return state;
  }

}
