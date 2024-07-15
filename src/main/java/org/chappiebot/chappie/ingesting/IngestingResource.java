package org.chappiebot.chappie.ingesting;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.time.Duration;
import org.chappiebot.chappie.product.ProductService;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

/**
 * This allows starting the process to ingest documents needed for a certain product
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@Path("/api")
public class IngestingResource {
    
    @Inject
    IngestingService ingestingService;
    
    @Inject
    ProductService productService;
    
    @GET 
    @Path("/ingest/{product}/{version}")
    @APIResponses({
        @APIResponse(description = "Queued", name = "Accepted", responseCode = "202"),
        @APIResponse(description = "Already queued", name = "Too many requests", responseCode = "429")
    })
    public Response ingestAllForACertainProductAndVersion(@PathParam("product") String product, @PathParam("version") String version){
        version = version.trim();
        product = product.trim();
        Status status = ingestingService.getStatus(product, version);
        if(status!=null){
            RunStage stage = status.runStage();
            if(stage.equals(RunStage.IN_PROGRESS)){
                return Response.status(Response.Status.TOO_MANY_REQUESTS).entity("Busy ingesting, " + ingestingService.getPersentageIngested() + "% done").build();
            }else if(stage.equals(RunStage.INGESTED)){
                return Response.ok(status).build();
            }
        }else {
            boolean hasQueued = ingestingService.queue(product, version);
            if(hasQueued){
                Log.info("Queued " + product + " " + version);
                return Response.accepted().build(); // We started the process
            }
        }
        return ingestAllForACertainProductAndVersion(product, version);
    }
    
    @GET
    @Path("/status/current")
    public Response getCurrentIngestionStatus(){
        return Response.ok(ingestingService.getCurrentStatus()).build();
    }
    
    @GET
    @Path("/status/{product}/{version}")
    public Response getStatus(@PathParam("product") String product, @PathParam("version") String version){
        Status status = ingestingService.getStatus(product, version);
        if(status!=null){
            return Response.ok(status).build();
        }else{
            // See if it's the current status
            Status currentStatus = ingestingService.getCurrentStatus();
            if(currentStatus!=null && currentStatus.product().name.equals(product) && currentStatus.version().equals(version)){
                return Response.ok(currentStatus).build();
            }
        }
        return Response.ok(new Status(RunStage.NOT_INGESTED, productService.getProduct(product), version, null, 0, 0, Duration.ZERO, null)).build();
        
    }
    
    @GET
    @Path("/status/persentageIngested")
    public Response getCurrentIngestionStatusPersentageIngested(){
        return Response.ok(ingestingService.getPersentageIngested()).build();
    }
}
