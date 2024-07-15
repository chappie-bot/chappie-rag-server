package org.chappiebot.chappie.chat;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import org.chappiebot.chappie.summary.Summary;

@RegisterAiService(retrievalAugmentor = ChappieRetrievalAugmentor.class)
@ApplicationScoped
public interface IndexAssistant {

    // TODO: Use metadata here.
    @SystemMessage("""
            You are an AI assistant trying to find the relavant summary in a list of summaries. The content is about {product}. 
            All the summaries is provided in the ingested document with the following metadata:
                   type=index,
                   product={product},
                   version={version}
            """)
            
    @UserMessage("""
        Your task is to respond with the summary in json format what best match a possible place that can answer the question delimited by ---.
        Do NOT include the code markup (```json) at the start of the json, keep it parsable json that can be parsed into a JsonObject.
        The json must contain the following fields from the ingested document: title, uuid
        ---
        {question}
        ---
    """)
    Summary findRelevantSummary(@MemoryId Object session, String question, String product, String version);
}
