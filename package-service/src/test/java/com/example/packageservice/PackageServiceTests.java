package com.example.packageservice;

import com.example.packageservice.dto.CreatePackageRequest;
import com.example.packageservice.dto.PackageResponse;
import com.example.packageservice.exception.PackageNotFoundException;
import com.example.packageservice.gateway.IProductServiceGateway;
import com.example.packageservice.gateway.dto.Product;
import com.example.packageservice.model.ProductPackage;
import com.example.packageservice.repository.IPackageRepository;
import com.example.packageservice.service.ICurrencyService;
import com.example.packageservice.service.PackageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PackageServiceTests {

    private PackageService packageService;

    private IPackageRepository packageRepository;
    private IProductServiceGateway productServiceGateway;
    private ICurrencyService currencyService;

    @BeforeEach
    void setup() {
        packageRepository = mock(IPackageRepository.class);
        productServiceGateway = mock(IProductServiceGateway.class);
        currencyService = mock(ICurrencyService.class);

        packageService = new PackageService(packageRepository, productServiceGateway, currencyService);
    }

    @Test
    void createPackage_shouldReturnPackageResponse() {
        CreatePackageRequest request = new CreatePackageRequest("My Package", "Description", List.of("prod1", "prod2"));

        ProductPackage savedPackage = new ProductPackage("pkg-1", "My Package", "Description", List.of("prod1", "prod2"));
        when(packageRepository.create(anyString(), anyString(), anyList())).thenReturn(savedPackage);

        List<Product> products = List.of(
                new Product("prod1", "Product 1", BigDecimal.TEN),
                new Product("prod2", "Product 2", BigDecimal.valueOf(20))
        );
        when(productServiceGateway.getProductsByIds(anyList())).thenReturn(products);
        when(currencyService.convert(any(), anyString())).thenAnswer(invocation -> invocation.getArgument(0)); // USD->USD

        PackageResponse response = packageService.create(request);

        assertNotNull(response);
        assertEquals("pkg-1", response.id());
        assertEquals("My Package", response.name());
        assertEquals("Description", response.description());
        assertEquals(2, response.products().size());
        assertEquals(BigDecimal.valueOf(30), response.totalPrice());

        verify(packageRepository).create("My Package", "Description", List.of("prod1", "prod2"));
    }

    @Test
    void getPackage_shouldReturnPackageResponse() {
        ProductPackage pkg = new ProductPackage("pkg-1", "Name", "Desc", List.of("prod1"));
        when(packageRepository.get("pkg-1")).thenReturn(Optional.of(pkg));

        List<Product> products = List.of(new Product("prod1", "Product 1", BigDecimal.TEN));
        when(productServiceGateway.getProductsByIds(List.of("prod1"))).thenReturn(products);
        when(currencyService.convert(BigDecimal.TEN, "USD")).thenReturn(BigDecimal.TEN);

        PackageResponse response = packageService.get("pkg-1", "USD");

        assertNotNull(response);
        assertEquals("pkg-1", response.id());
        assertEquals(BigDecimal.TEN, response.totalPrice());
    }

    @Test
    void getPackage_nonExistent_shouldThrow() {
        when(packageRepository.get("missing")).thenReturn(Optional.empty());
        assertThrows(PackageNotFoundException.class, () -> packageService.get("missing", "USD"));
    }

    @Test
    void deletePackage_success() {
        when(packageRepository.delete("pkg-1")).thenReturn(true);
        assertDoesNotThrow(() -> packageService.delete("pkg-1"));
        verify(packageRepository).delete("pkg-1");
    }

    @Test
    void deletePackage_notFound_shouldThrow() {
        when(packageRepository.delete("pkg-1")).thenReturn(false);
        assertThrows(PackageNotFoundException.class, () -> packageService.delete("pkg-1"));
    }

    @Test
    void updatePackage_shouldReturnUpdatedResponse() {
        CreatePackageRequest request = new CreatePackageRequest("Updated Name", "Updated Desc", List.of("prod1"));
        ProductPackage updatedPkg = new ProductPackage("pkg-1", "Updated Name", "Updated Desc", List.of("prod1"));
        when(packageRepository.update(eq("pkg-1"), anyString(), anyString(), anyList())).thenReturn(Optional.of(updatedPkg));

        List<Product> products = List.of(new Product("prod1", "Product 1", BigDecimal.TEN));
        when(productServiceGateway.getProductsByIds(List.of("prod1"))).thenReturn(products);
        when(currencyService.convert(BigDecimal.TEN, "USD")).thenReturn(BigDecimal.TEN);

        PackageResponse response = packageService.update("pkg-1", request);

        assertNotNull(response);
        assertEquals("Updated Name", response.name());
        assertEquals(BigDecimal.TEN, response.totalPrice());
    }

    @Test
    void updatePackage_nonExistent_shouldThrow() {
        CreatePackageRequest request = new CreatePackageRequest("Name", "Desc", List.of("prod1"));
        when(packageRepository.update(eq("pkg-1"), anyString(), anyString(), anyList())).thenReturn(Optional.empty());

        assertThrows(PackageNotFoundException.class, () -> packageService.update("pkg-1", request));
    }
}