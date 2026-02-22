package com.example.packageservice.repository.jpa;

import com.example.packageservice.model.ProductPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProductPackageJpaRepository extends JpaRepository<ProductPackage, String> {
}