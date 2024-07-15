package org.chappiebot.chappie.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

/**
 * Represents a document set. This includes what type of document this and what ingestor and loader would be used
 * It also contains any metadata needed by the loaders and ingestors
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@Entity
public class DocumentSet extends PanacheEntity {
    public String ingestorName;
    public String documentLoaderName;
}
