package org.chappiebot.chappie.chat;

/**
 * Define a message
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
public record Message(MessageType type, String product, String version, String message) {
    
}
