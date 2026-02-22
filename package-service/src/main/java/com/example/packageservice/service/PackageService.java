package com.example.packageservice.service;

import com.example.packageservice.dto.CreatePackageRequest;
import com.example.packageservice.dto.PackageResponse;
import com.example.packageservice.exception.PackageNotFoundException;
import com.example.packageservice.gateway.IProductServiceGateway;
import com.example.packageservice.gateway.dto.Product;
import com.example.packageservice.model.ProductPackage;
import com.example.packageservice.repository.IPackageRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PackageService implements IPackageService {

    private final IPackageRepository packageRepository;
    private final IProductServiceGateway productServiceGateway;
    private final ICurrencyService currencyService;
    private static final Logger logger = LoggerFactory.getLogger(PackageService.class);

    public PackageService(IPackageRepository packageRepository, IProductServiceGateway productServiceGateway, ICurrencyService currencyService) {
        this.packageRepository = packageRepository;
        this.productServiceGateway = productServiceGateway;
        this.currencyService = currencyService;
    }

    public List<PackageResponse> getAll(String currency) {
        List<ProductPackage> packages = packageRepository.getAll();

        logger.info("Fetching all packages, count: {}", packages.size());
        return packages.stream()
                .map(pkg -> createPackageResponse(pkg, currency))
                .toList();
    }

    public PackageResponse get(String id, String currency) {
        logger.debug("Fetching package with id: {}", id);
        ProductPackage pkg = packageRepository.get(id)
                .orElseThrow(() -> {
                    logger.error("Package with id {} not found", id);
                    return new PackageNotFoundException(id);
                });

        logger.debug("Fetching Package with id: {}", id);
        return createPackageResponse(pkg, currency);
    }

    public PackageResponse create(CreatePackageRequest pkgRequest) {
        logger.info("Creating package with name: {}", pkgRequest.name());
        ProductPackage createdPkg = packageRepository.create(pkgRequest.name(), pkgRequest.description(), pkgRequest.productIds());

        logger.info("Package created with id: {}", createdPkg.getId());
        return createPackageResponse(createdPkg, null);
    }

    public PackageResponse update(String id, CreatePackageRequest pkgRequest) {
        logger.info("Updating package with id: {}", id);
        ProductPackage updatedPkg = packageRepository.update(id, pkgRequest.name(), pkgRequest.description(), pkgRequest.productIds())
                .orElseThrow(() -> new PackageNotFoundException(id));

        logger.info("Package updated with id: {}", id);
        return createPackageResponse(updatedPkg, null);
    }

    public void delete(String id) {
        logger.info("Deleting package with id: {}", id);
        boolean deleted = packageRepository.delete(id);

        if (!deleted) {
            logger.error("Failed to delete package with id: {}", id);
            throw new PackageNotFoundException(id);
        }

        logger.info("Package deleted successfully: {}", id);
    }

    private PackageResponse createPackageResponse(ProductPackage pkg, String currency) {
        List<Product> products = getProducts(pkg.getProductIds());

        BigDecimal totalUsd = calculateTotalUsd(products);

        String desiredCurrency = currency != null ? currency : "USD";
        BigDecimal convertedTotalPrice = currencyService.convert(totalUsd, desiredCurrency);

        return new PackageResponse(pkg.getId(), pkg.getName(), pkg.getDescription(), products, convertedTotalPrice, desiredCurrency);
    }

    private List<Product> getProducts(List<String> productIds) {
        return productServiceGateway.getProductsByIds(productIds);
    }

    private BigDecimal calculateTotalUsd(List<Product> products) {
        return products.stream()
                .map(Product::usdPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
