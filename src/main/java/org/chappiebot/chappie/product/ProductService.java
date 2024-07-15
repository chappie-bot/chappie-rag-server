 package org.chappiebot.chappie.product;

import io.quarkus.cache.CacheResult;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import org.chappiebot.chappie.github.GitHubClient;
import org.chappiebot.chappie.github.GitHubTag;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class ProductService {

    @Inject @RestClient
    GitHubClient gitHubClient;
    
    public List<Product> getAll() {
        return Product.listAll(Sort.by("name"));
    }

    @CacheResult(cacheName = "product-cache")
    @Transactional
    public Product getProduct(String name) {
        return Product.find("name", name).firstResult();
    }

    @CacheResult(cacheName = "product-tags-cache")
    public List<GitHubTag> getTags(String name) {
        Product product = Product.find("name", name).firstResult();
        return gitHubClient.getRepositoryTags(product.repoOwner, product.repoName);
    }
}