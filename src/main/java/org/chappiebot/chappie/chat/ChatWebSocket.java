package org.chappiebot.chappie.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.websocket.server.ServerEndpoint;

import io.quarkiverse.langchain4j.ChatMemoryRemover;
import io.quarkus.logging.Log;
import io.vertx.core.Vertx;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.chappiebot.chappie.summary.Summary;
import org.chappiebot.chappie.product.Product;
import org.chappiebot.chappie.product.ProductService;

@ServerEndpoint("/ws/chappie/chat")
public class ChatWebSocket {

    @Inject
    IndexAssistant indexAssistant;

    @Inject
    ChatAssistant chatAssistant;

    @Inject
    ProductService productService;
    
    @Inject
    Vertx vertx;
    
    @Inject
    ObjectMapper objectMapper;
    
    @OnOpen
    public void onOpen(Session session) {
        // session.getBasicRemote().sendText(product.greeting);
    }
    
    @OnClose
    void onClose(Session session) {
        try {
            ChatMemoryRemover.remove(indexAssistant, session);
            ChatMemoryRemover.remove(productService, session);
        }catch (Throwable t){}
    }

    @OnMessage
    public void onMessage(String jsonMessage, Session session) {
        Log.info("jsonMessage = " + jsonMessage);
        Message message = toMessage(jsonMessage);
        Log.info("Message from web socket = " + message);
        
        vertx.<Object[]>executeBlocking(promise -> {
            try {
                Product product = productService.getProduct(message.product());
                
                Summary summary = indexAssistant.findRelevantSummary(session, message.message(), message.product(), message.version());
                String response = chatAssistant.chat(session,
                            message.message(),
                            product.userRole,
                            product.name,
                            product.systemMessage,
                            product.scope,
                            product.alias,
                            message.version(),
                            summary.uuid());
                promise.complete(new Object[]{response, summary});
                
            } catch (Exception e) {
                promise.fail(e);
            }
        }, res -> {
            if (res.succeeded()) {
                Object[] result = res.result();
                String response = (String) result[0];
                Summary summary = (Summary) result[1];
                System.out.println(summary.title() + " | " + summary.uuid());
                vertx.executeBlocking(promise -> {
                    try {
                        session.getBasicRemote().sendText(response);
                        promise.complete();
                    } catch (IOException e) {
                        promise.fail(e);
                    }
                }, false, asyncResult -> {
                    if (asyncResult.failed()) {
                        Logger.getLogger(ChatWebSocket.class.getName()).log(Level.SEVERE, "Failed to send message", asyncResult.cause());
                    }
                });
            } else {
                Logger.getLogger(ChatWebSocket.class.getName()).log(Level.SEVERE, "Failed to process message", res.cause());
            }
        });
    }
    
    private Message toMessage(String jsonMessage){
        try {
            return objectMapper.readValue(jsonMessage, Message.class);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}