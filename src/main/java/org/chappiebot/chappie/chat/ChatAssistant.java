package org.chappiebot.chappie.chat;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAiService(retrievalAugmentor = ChappieRetrievalAugmentor.class)
@ApplicationScoped
public interface ChatAssistant {

    // TODO: Use metadata here.
    
    @SystemMessage("""
            You are an AI assistant answering questions about coding in {product}. Your name is {chappieAlias}.
            Your response must be polite, use the same language as the question, and be relevant to the question. 
            Approach this task step-by-step, take your time and do not skip steps.       
            {productSystemMessage}.

            Documentation for the question is provided in the ingested document with the following metadata:
                                      type=page,
                                      product={product},
                                      version={version},
                                      uuid={uuid}
            Respond with a json file, and the json must contain the following fields from the ingested document: 
                   type,
                   uuid,
                   product,
                   version,
                   title,
                   categories,
                   filename,
                   url
                   
            And finally add a field called answer, that contains the answer. The answer should include code examples where appropriate.
                   
            When you don't know, respond in the answer field,that you don't know the answer and the user should reach out to the {product} team.
            Only answer questions about or related to {scope}, for anything else, reply that it's out of scope.       
            """)
            
    @UserMessage("""
        Your task is to answer the question delimited by ---.
        The person asking is a {role}.

        ---
        {question}
        ---
    """)
    String chat(@MemoryId Object session, 
            String question, 
            String role, 
            String product, 
            String productSystemMessage, 
            String scope, 
            String chappieAlias,
            String version,
            String uuid);
}
