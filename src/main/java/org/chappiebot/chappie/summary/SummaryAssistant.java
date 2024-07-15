package org.chappiebot.chappie.summary;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Use AI to create a summary
 */
@RegisterAiService
@ApplicationScoped
public interface SummaryAssistant {

    @SystemMessage("""
            You are an AI assistant summarizing documentation about {product}.
            Your response must be in json, and only json format. The json response should contain 3 fields
                   namely:title, categories, summary       
            """)
            
    @UserMessage("""
            Given this documentation in {format} format, that is related to {product}, can you provide a title, categories and a short summary, 
            that includes things needed to match this documentation to a question that a user might have, 
            so that we can show where the answer might be. Respond with a json file that contains 3 fields: title, categories, summary. None of the fields
            can be null or empty, you should always include all 3 fields with values.
            Do NOT include the code markup (```json) at the start of the json, keep it parsable json that can be parsed into a JsonObject.
                   
            Your task is to summarizing documentation delimited by ---.
            ---
            {documentation}
            ---
              
    """)
    JsonObject summarize(String documentation, String product, String format);
}
