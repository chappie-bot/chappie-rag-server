package org.chappiebot.chappie.ingesting;

import io.vertx.core.json.JsonArray;
import org.chappiebot.chappie.document.DocumentLocation;
import org.chappiebot.chappie.summary.Summary;
import org.chappiebot.chappie.summary.SummaryException;

/**
 * Ingesting data
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
public interface Ingestor {
    public String getIngestorName();
    public Summary ingestDocument(String product, String version, DocumentLocation documentLocation) throws IngestingException, SummaryException;
    public void ingestSummaries(String product, String version, JsonArray summaryArray);
}
