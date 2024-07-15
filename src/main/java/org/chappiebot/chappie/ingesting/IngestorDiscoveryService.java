package org.chappiebot.chappie.ingesting;

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
 * Give access to discovered ingestors
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@ApplicationScoped
public class IngestorDiscoveryService {
    @Inject @Any
    private Instance<Ingestor> ingestors;
    private final Map<String,Ingestor> ingestorsMap = new HashMap<>();
    
    public void init(@Observes StartupEvent event) {
        discover();
    }
    
    public Optional<Ingestor> getIngestor(String name) {
        if (this.ingestorsMap.containsKey(name)) {
            return Optional.of(this.ingestorsMap.get(name));
        }
        return Optional.empty();
    }
    
    @Produces
    public Map<String,Ingestor> produceIngestors(){
        return this.ingestorsMap;
    }
    
    public void discover(){
        Iterator<Ingestor> ingestorIterator = ingestors.iterator();
        while (ingestorIterator.hasNext()) {
            Ingestor ingestor = ingestorIterator.next();
            this.ingestorsMap.put(ingestor.getIngestorName(),ingestor);
        }
    }
}