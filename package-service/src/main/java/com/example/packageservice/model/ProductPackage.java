package com.example.packageservice.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "product_packages")
public class ProductPackage {
    @Id
    private String id;

    private String name;

    private String description;

    @ElementCollection
    @CollectionTable(name = "product_package_products", joinColumns = @JoinColumn(name = "package_id"))
    @Column(name = "product_id")
    private List<String> productIds;

    public ProductPackage() {}

    public ProductPackage(String id, String name, String description, List<String> productIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.productIds = productIds;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getProductIds() { return productIds; }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }
}
