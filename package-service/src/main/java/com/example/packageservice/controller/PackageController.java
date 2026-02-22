package com.example.packageservice.controller;

import com.example.packageservice.dto.CreatePackageRequest;
import com.example.packageservice.dto.PackageResponse;
import com.example.packageservice.service.IPackageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
public class PackageController {

    private final IPackageService packageService;

    public PackageController(IPackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping
    public ResponseEntity<List<PackageResponse>> getAll(@RequestParam(defaultValue = "USD") String currency) {
        List<PackageResponse> response = packageService.getAll(currency);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackageResponse> getById(@PathVariable String id, @RequestParam(defaultValue = "USD") String currency) {
        PackageResponse response = packageService.get(id, currency);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PackageResponse> create(@Valid @RequestBody CreatePackageRequest request) {
        PackageResponse response = packageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PackageResponse> update(@PathVariable String id, @Valid @RequestBody CreatePackageRequest request) {
        PackageResponse response = packageService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        packageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
