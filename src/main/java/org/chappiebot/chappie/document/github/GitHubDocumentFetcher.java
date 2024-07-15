package org.chappiebot.chappie.document.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.chappiebot.chappie.document.DocumentFetcher;
import org.chappiebot.chappie.document.DocumentFetcherException;
import org.chappiebot.chappie.document.DocumentLocation;
import org.chappiebot.chappie.github.GitHubClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class GitHubDocumentFetcher implements DocumentFetcher {
    
    @Inject @RestClient
    GitHubClient gitHubClient;
    
    @Inject
    ObjectMapper objectMapper;

    @Override
    public String getName() {
        return "GitHub";
    }
    
    @Override
    public String getPathFormat(){
        return "{repoOwner}/{repoName}/{path}";
    }
    
    @Override
    public List<DocumentLocation> findDocumentLocations(String path, String version, List<String> allowedTypes, Optional<String> token) throws DocumentFetcherException {
        List<DocumentLocation> allContents = new ArrayList<>();
        String[] pathParts = path.split("/");
        if(pathParts.length<3)throw new DocumentFetcherException("GitHub Document Fetcher configuration error. Please provide path using format " + getPathFormat());
        String repoOwner = pathParts[0];
        String repoName = pathParts[1];
        String r = repoOwner + "/" + repoName + "/";
        String repoPath = path.substring(r.length());
        if(token.isEmpty())throw new DocumentFetcherException("GitHub Document Fetcher configuration error. Please provide a token");
        
        findDocumentLocations(allContents, repoOwner, repoName, repoPath, version, allowedTypes, "Bearer " + token.get());
        
        return allContents;
    }
    
    private void findDocumentLocations(List<DocumentLocation> allContents, String repoOwner, String repoName, String repoPath, String version, List<String> allowedTypes, String token){
        List<DocumentLocation> fetchedContents = fetchContents(repoOwner, repoName, repoPath, version, token);
        for(DocumentLocation content: fetchedContents){
            if (DIR.equals(content.type())) {
                findDocumentLocations(allContents,repoOwner, repoName, content.path(), version, allowedTypes, token);
            } else {
                String type = content.type();
                if(allowedTypes.isEmpty() || allowedTypes.contains(type)){
                    //if(content.name().equalsIgnoreCase("dev-ui.adoc")){ // TODO: Make this a feature ?
                        allContents.add(content);
                    //}else {
                    //    Log.warnf("Ignoring %s", content.name());
                    //}
                }
            }
        }
    }

    private List<DocumentLocation> fetchContents(String repoOwner, String repoName, String path, String version, String token) {
        try {
            Response response = gitHubClient.getContents(repoOwner, repoName, path, token, version);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed to fetch contents [" + repoOwner + "/" + repoName + "/" + path);
            }
            String json = response.readEntity(String.class);
            return toDocumentLocationList(json);
        }catch(WebApplicationException wae){
            throw new RuntimeException("Failed to fetch contents [" + repoOwner + "/" + repoName + "/" + path + "]", wae);
        }
    }
    
    private List<DocumentLocation> toDocumentLocationList(String json){
        List<DocumentLocation> documentLocations = new ArrayList<>();
        try {
            JsonNode jsonArray = objectMapper.readTree(json);
            if (jsonArray.isArray()) {
                for (JsonNode jsonNode : jsonArray) {
                    DocumentLocation dl = toDocumentLocation(jsonNode);
                    if(dl!=null){
                        documentLocations.add(toDocumentLocation(jsonNode));
                    }
                }
            }
            return documentLocations;
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private DocumentLocation toDocumentLocation(JsonNode jsonNode){
        String name = jsonNode.get("name").asText();
        
        String dirOrFile = jsonNode.get("type").asText();
        if ("dir".equals(dirOrFile)) {
            return new DocumentLocation(name,DIR,jsonNode.get("download_url").asText(), jsonNode.get("path").asText());
        } else if ("file".equals(dirOrFile)) {
            String type = "?";
            if(name.contains(".")){
                type = name.substring(name.lastIndexOf('.') + 1);
            }
            return new DocumentLocation(name,type,jsonNode.get("download_url").asText(),jsonNode.get("path").asText());
        }
        return null;
    }
    
    private static final String DIR = "_DIR_";
}
