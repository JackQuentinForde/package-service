package com.example.packageservice.repository;

import com.example.packageservice.model.ProductPackage;
import com.example.packageservice.repository.jpa.IProductPackageJpaRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        name = "package.repository.inmemory",
        havingValue = "false",
        matchIfMissing = true)
public class PackageRepository implements IPackageRepository {

    private final IProductPackageJpaRepository jpaRepository;

    public PackageRepository(IProductPackageJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<ProductPackage> getAll() {
        return jpaRepository.findAll();
    }

    @Override
    public Optional<ProductPackage> get(String id) {
        return jpaRepository.findById(id);
    }

    @Override
    public ProductPackage create(String name, String description, List<String> productIds) {
        ProductPackage newPackage = new ProductPackage(UUID.randomUUID().toString(), name, description, productIds);
        return jpaRepository.save(newPackage);
    }

    @Override
    public Optional<ProductPackage> update(String id, String name, String description, List<String> productIds) {
        return jpaRepository.findById(id).map(pkg -> {
            pkg.setName(name);
            pkg.setDescription(description);
            pkg.setProductIds(productIds);
            return jpaRepository.save(pkg);
        });
    }

    @Override
    public boolean delete(String id) {
        if (jpaRepository.existsById(id)) {
            jpaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}