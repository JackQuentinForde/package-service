package com.example.packageservice.repository;

import com.example.packageservice.model.ProductPackage;

import java.util.List;
import java.util.Optional;

public interface IPackageRepository {
    List<ProductPackage> getAll();
    Optional<ProductPackage> get(String id);
    ProductPackage create(String name, String description, List<String> productIds);
    Optional<ProductPackage> update(String id, String name, String description, List<String> productIds);
    boolean delete(String id);
}
