package org.chappiebot.chappie.document;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

/**
 * Give access to discovered document fetchers
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@ApplicationScoped
public class DocumentFetcherDiscoveryService {
    @Inject @Any
    private Instance<DocumentFetcher> documentFetchers;
    private final Map<String,DocumentFetcher> documentFetchersMap = new HashMap<>();
    
    public void init(@Observes StartupEvent event) {
        discover();
    }
    
    public Optional<DocumentFetcher> getDocumentFetcher(String name) {
        if (this.documentFetchersMap.containsKey(name)) {
            return Optional.of(this.documentFetchersMap.get(name));
        }
        return Optional.empty();
    }
    
    @Produces
    public Map<String,DocumentFetcher> produceDocumentFetchers(){
        return this.documentFetchersMap;
    }
    
    public void discover(){
        
        Iterator<DocumentFetcher> documentFetcherIterator = documentFetchers.iterator();
        while (documentFetcherIterator.hasNext()) {
            DocumentFetcher documentFetcher = documentFetcherIterator.next();
            this.documentFetchersMap.put(documentFetcher.getName(),documentFetcher);
        }
    }
}