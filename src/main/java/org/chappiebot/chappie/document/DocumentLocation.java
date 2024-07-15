package org.chappiebot.chappie.document;

/**
 * a Document is something that can be ingested
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
public record DocumentLocation(String name, String type, String url, String path) {
    
}
