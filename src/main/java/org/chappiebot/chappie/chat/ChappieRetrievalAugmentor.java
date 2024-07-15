package org.chappiebot.chappie.chat;

//import dev.langchain4j.model.chat.ChatLanguageModel;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import io.quarkiverse.langchain4j.redis.RedisEmbeddingStore;
import jakarta.inject.Singleton;
//import jakarta.inject.Inject;
import java.util.function.Supplier;

@Singleton
public class ChappieRetrievalAugmentor implements Supplier<RetrievalAugmentor> {
    
//    @Inject
//    ChatLanguageModel chatLanguageModel;
    
    private final RetrievalAugmentor augmentor;
    
    public ChappieRetrievalAugmentor(RedisEmbeddingStore store, EmbeddingModel model) {
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(model)
                .embeddingStore(store)
                .maxResults(1)
                .build();
        
        augmentor = DefaultRetrievalAugmentor
                .builder()
                
                //.queryTransformer(new CompressingQueryTransformer(chatLanguageModel))
                .contentRetriever(contentRetriever)
                .build();
    }

    @Override
    public RetrievalAugmentor get() {
        return augmentor;
    }
}
