package org.chappiebot.chappie.product;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

/**
 * A product is something like Quarkus or Lit or Keycloak. Something that has it's own set of documentation.
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 */
@Entity
public class Product extends PanacheEntity {

    @Column(unique = true)
    public String name;

    public String webpage;
    public String scope;
    public String systemMessage;
    public String userRole;
    public String repoUrl;
    public String repoOwner;
    public String repoName;
    public String repoAuthToken;
    
    @OneToOne(cascade = CascadeType.ALL)
    public DocumentSet documentSet; // TODO: Support multiple
    
    public String alias;
    public String greeting;
}
