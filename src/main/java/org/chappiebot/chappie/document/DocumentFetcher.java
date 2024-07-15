package org.chappiebot.chappie.document;

import java.util.List;
import java.util.Optional;

/**
 * Fetching info and contents of documents
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
public interface DocumentFetcher {
    public String getName();
    public String getPathFormat();
    
    default List<DocumentLocation> findDocumentLocations(String path, String version) throws DocumentFetcherException {
        return findDocumentLocations(path, version, List.of());
    }
    default List<DocumentLocation> findDocumentLocations(String path, String version, List<String> allowedTypes) throws DocumentFetcherException {
        return findDocumentLocations(path, version, allowedTypes, Optional.empty());
    }
    
    /**
     * Find all document at a certain location using a certain fetcher
     * @param path The root path to the location.
     * @param allowedTypes List all file types that should be included. Empty list means all.
     * @param token Optional token if needed
     * @return List of Document Locations
     * @throws DocumentFetcherException 
     */
    public List<DocumentLocation> findDocumentLocations(String path, String version, List<String> allowedTypes, Optional<String> token) throws DocumentFetcherException;
}
