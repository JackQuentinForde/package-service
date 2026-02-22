package com.example.packageservice.service;

import com.example.packageservice.dto.CreatePackageRequest;
import com.example.packageservice.dto.PackageResponse;

import java.util.List;

public interface IPackageService {
    List<PackageResponse> getAll(String currency);
    PackageResponse get(String id, String currency);
    PackageResponse create(CreatePackageRequest request);
    PackageResponse update(String id, CreatePackageRequest request);
    void delete(String id);
}
