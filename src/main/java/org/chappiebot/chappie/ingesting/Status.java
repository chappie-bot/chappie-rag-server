package org.chappiebot.chappie.ingesting;

import java.time.Duration;
import java.util.List;
import org.chappiebot.chappie.product.Product;
import org.chappiebot.chappie.document.DocumentLocation;

public record Status(RunStage runStage, Product product, String version, DocumentLocation documentLocation, double persentageIngested, int documentsIngested, Duration timeSinceStarted, List<DocumentLocation> failedQueue) {
    
    Status markAsDone(){
        return new Status(RunStage.INGESTED, product, version, documentLocation, persentageIngested, documentsIngested, timeSinceStarted, failedQueue);
    }
}
