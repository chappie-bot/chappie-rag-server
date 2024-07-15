package org.chappiebot.chappie.ingesting.asciidoc;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.PostConstruct;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.chappiebot.chappie.document.DocumentLocation;
import org.chappiebot.chappie.ingesting.IngestingException;
import org.chappiebot.chappie.ingesting.Ingestor;
import org.chappiebot.chappie.summary.Summary;
import org.chappiebot.chappie.summary.SummaryException;
import org.chappiebot.chappie.summary.SummaryService;

/**
 * This ingest ascii doc (typically the project documentation)
 * TODO: This does not have to be asciidoc specific, rather remote text.
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@ApplicationScoped
public class AsciiDocIngestor implements Ingestor {

    @Inject
    EmbeddingStore store;

    @Inject
    EmbeddingModel embeddingModel;

    @Inject
    SummaryService summaryService;
    
    EmbeddingStoreIngestor embeddingStoreIngestor;
    
    @PostConstruct
    public void init(){
        this.embeddingStoreIngestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .documentSplitter(DocumentSplitters.recursive(8192, 0))
                .build();
    }
    
    @Override
    public String getIngestorName() {
        return "AsciiDoc";
    }

    @Override
    public Summary ingestDocument(String product, String version, DocumentLocation documentLocation) throws IngestingException, SummaryException{
        String name = documentLocation.name().substring(0, documentLocation.name().lastIndexOf('.'));
        
        Document document = loadDocument(documentLocation);
        
        Summary summary = summaryService.getSummary(document.text(), product, version, name, "asciidoc"); // TODO: get type from document loader
        
        document.metadata().put("type", "page");
        document.metadata().put("uuid", summary.uuid());
        document.metadata().put("product", product);
        document.metadata().put("version", version);
        document.metadata().put("title", summary.title());
        if(summary.categories()!=null && !summary.categories().isEmpty()){
            document.metadata().put("categories", String.join(",", summary.categories()));
        }
        document.metadata().put("filename", documentLocation.name());
        document.metadata().put("url", documentLocation.url());
        this.storeDocument(document);
        return summary;
        
    }
    
    @Override
    public void ingestSummaries(String product, String version, JsonArray summaryArray){
        JsonObject documentationIndex = JsonObject.of("product", product, "version", version,"summaries",summaryArray);
        String jsonString = documentationIndex.encodePrettily();
        System.out.println(jsonString);
        Document document = new Document(jsonString);
        document.metadata().put("type", "index");
        document.metadata().put("product", product);
        document.metadata().put("version", version);
        this.embeddingStoreIngestor.ingest(document);
    }
    
    private Document loadDocument(DocumentLocation documentLocation) throws IngestingException{
        try {
            return UrlDocumentLoader.load(documentLocation.url(), new TextDocumentParser());
        }catch(Throwable t){
            throw new IngestingException(t);
        }
    }
    
    private void storeDocument(Document document) throws IngestingException {
        try {
            embeddingStoreIngestor.ingest(document);
        }catch (Throwable t){
            throw new IngestingException("Error ingesting dococument",t);
        }
    }
    
}
