package com.example.packageservice.repository;

import com.example.packageservice.model.ProductPackage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "package.repository.inmemory",
        havingValue = "true"
)
public class InMemoryPackageRepository implements IPackageRepository {

    private final List<ProductPackage> productPackages = new ArrayList<>();

    public List<ProductPackage> getAll() {
        return List.copyOf(productPackages);
    }

    public Optional<ProductPackage> get(String id) {
        return productPackages.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public ProductPackage create(String name, String description, List<String> productIds) {
        ProductPackage newProductPackage = new ProductPackage(UUID.randomUUID().toString(), name, description, productIds);
        productPackages.add(newProductPackage);
        return newProductPackage;
    }

    public Optional<ProductPackage> update(String id, String name, String description, List<String> productIds) {
        Optional<ProductPackage> productPackage = get(id);
        productPackage.ifPresent(pkg -> {
            pkg.setName(name);
            pkg.setDescription(description);
            pkg.setProductIds(productIds);
        });
        return productPackage;
    }

    public boolean delete(String id) {
        return productPackages.removeIf(pkg -> pkg.getId().equals(id));
    }
}
