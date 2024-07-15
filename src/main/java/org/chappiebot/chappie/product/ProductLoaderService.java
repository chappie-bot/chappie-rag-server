package org.chappiebot.chappie.product;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Load product defined in config. Useful for local development
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@ApplicationScoped
public class ProductLoaderService {
    @ConfigProperty(name = "chappie.product.name")
    Optional<String> name;
    @ConfigProperty(name = "chappie.product.webpage")
    Optional<String> webpage;
    @ConfigProperty(name = "chappie.product.scope")
    Optional<String> scope;
    @ConfigProperty(name = "chappie.product.systemMessage")
    Optional<String> systemMessage;
    @ConfigProperty(name = "chappie.product.userRole")
    Optional<String> userRole;
    @ConfigProperty(name = "chappie.product.repoUrl")
    Optional<String> repoUrl;
    @ConfigProperty(name = "chappie.product.repoOwner")
    Optional<String> repoOwner;
    @ConfigProperty(name = "chappie.product.repoName")
    Optional<String> repoName;
    @ConfigProperty(name = "chappie.product.repoAuthToken")
    Optional<String> repoAuthToken;
    @ConfigProperty(name = "chappie.product.alias")
    Optional<String> alias;
    @ConfigProperty(name = "chappie.product.greeting")
    Optional<String> greeting;
    @ConfigProperty(name = "chappie.product.documentSet.ingestorName")
    Optional<String> ingestorName;
    @ConfigProperty(name = "chappie.product.documentSet.documentLoaderName")
    Optional<String> documentLoaderName;
    
    @Transactional
    public void init(@Observes StartupEvent event) {
        if(name.isPresent() && 
                webpage.isPresent() && 
                scope.isPresent() && 
                systemMessage.isPresent() && 
                userRole.isPresent() && 
                repoUrl.isPresent() && 
                repoOwner.isPresent() && 
                repoName.isPresent() && 
                repoAuthToken.isPresent() && 
                alias.isPresent() && 
                greeting.isPresent() && 
                ingestorName.isPresent() && 
                documentLoaderName.isPresent()){
            
            DocumentSet ds = new DocumentSet();
            ds.ingestorName = ingestorName.get();
            ds.documentLoaderName = documentLoaderName.get();
            
            Product p = new Product();
            p.name = name.get();
            p.webpage = webpage.get();
            p.scope = scope.get();
            p.systemMessage = systemMessage.get();
            p.userRole = userRole.get();
            p.repoUrl = repoUrl.get();
            p.repoOwner = repoOwner.get();
            p.repoName = repoName.get();
            p.repoAuthToken = repoAuthToken.get();
            p.alias = alias.get();
            p.greeting = greeting.get();
            p.documentSet = ds;
            
            p.persist();
        }
        
    }
    
}