package org.chappiebot.chappie.github;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Get some info from GitHub
 * @author Phillip Kruger(phillip.kruger@gmail.com)
 */
@Path("/repos")
@RegisterRestClient(configKey = "github-api")
public interface GitHubClient {
    
    @GET
    @Path("/{owner}/{repo}/contents/{path}")
    Response getContents(@PathParam("owner") String owner, 
                         @PathParam("repo") String repo, 
                         @PathParam("path") String path, 
                         @HeaderParam("Authorization") String authToken,
                         @QueryParam("ref") String ref);
    
    @GET
    @Path("/{owner}/{repo}/tags")
    List<GitHubTag> getRepositoryTags(@PathParam("owner") String owner, @PathParam("repo") String repo);
}
