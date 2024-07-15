package org.chappiebot.chappie.product;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

/**
 * This allows starting the process to ingest documents needed for a certain product
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@Path("/api")
public class ProductResource {
    
    @Inject
    ProductService productService;
            
    @GET 
    @Path("/product")
    public Response getAllProducts(){
        return Response.ok(productService.getAll()).build();
    }
    
    @GET 
    @Path("/product/{name}")
    public Response getProduct(@PathParam("name") String name){
        return Response.ok(productService.getProduct(name)).build();
    }
    
    @GET 
    @Path("/product/{name}/tags")
    public Response getProductTags(@PathParam("name") String name){
        return Response.ok(productService.getTags(name)).build();
    }
    
}
