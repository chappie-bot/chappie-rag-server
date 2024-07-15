package org.chappiebot.chappie.summary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingResult;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.IntArrayList;
import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Facade the Summary AI Service, with file based persistence.
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@ApplicationScoped
public class SummaryService {
    
    @Inject
    SummaryAssistant summaryAssistant;
    
    @Inject
    ObjectMapper objectMapper;
    
    @ConfigProperty(name = "chappie.data.dir", defaultValue = "data")
    Path dataDir;
    
    @ConfigProperty(name = "chappie.document.token.limit", defaultValue = "30000")
    int tokenLimit;
    
    EncodingRegistry encodingRegistry = Encodings.newLazyEncodingRegistry();
    
    public Summary getSummary(String documentation, String product, String version, String name, String format) throws SummaryException{
        // First check if this documetation is too long
        Encoding encoding = encodingRegistry.getEncoding(EncodingType.CL100K_BASE);
        EncodingResult encodingResult = encoding.encode(documentation, tokenLimit);
        if(encodingResult.isTruncated()){
            Log.warn("Truncating document " + name);
            IntArrayList encoded = encodingResult.getTokens();
            documentation = encoding.decode(encoded);
            return getSummary(documentation, product, version, name, format);
        }else{
        
            // First check if this has already been summarized.
            Path localPath = localPath(product, version, name);
            if (Files.exists(localPath)) {
                try {
                    String jsonString = Files.readString(localPath);
                    return objectMapper.readValue(jsonString, Summary.class);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
            Log.infof("[%s] not available offline, creating now", name);

            try {
                JsonObject summary = summaryAssistant.summarize(documentation, product, format);
                String s = summary.getString("summary");
                if(s==null || s.isBlank())throw new NullPointerException("Summary should not be null");
                summary.put("uuid", UUID.randomUUID().toString());
                if(!summary.containsKey("title") || null==summary.getString("title") || summary.getString("title").isBlank()){
                    summary.put("title", name);
                }

                try {
                    if(!Files.exists(dataDir)){
                        Files.createDirectories(dataDir);
                    }
                    Files.writeString(localPath, summary.encodePrettily());
                    return objectMapper.readValue(summary.encodePrettily(), Summary.class);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            } catch (Throwable t){
                throw new SummaryException("Summary creation failed", t);
            }
        }
    }
    
    private Path localPath(String product, String version, String name){
        return dataDir.resolve(String.format(FILE_NAME_FORMAT, product, version, name));
    }
    
    private static final String FILE_NAME_FORMAT = "%s_%s_%s.json";
}
