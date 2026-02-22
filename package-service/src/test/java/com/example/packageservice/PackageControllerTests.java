package com.example.packageservice;

import com.example.packageservice.dto.CreatePackageRequest;
import com.example.packageservice.dto.PackageResponse;
import com.example.packageservice.gateway.dto.Product;
import com.example.packageservice.service.IPackageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PackageControllerTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private IPackageService packageService;

    private PackageResponse samplePackage;

    @BeforeEach
    void setup() {
        samplePackage = new PackageResponse(
                "pkg-1",
                "Test Name",
                "Test Desc",
                List.of(new Product("prod1", "Product 1", BigDecimal.TEN)),
                BigDecimal.TEN,
                "USD"
        );

        when(packageService.create(any(CreatePackageRequest.class))).thenReturn(samplePackage);
        when(packageService.get(any(String.class), any(String.class))).thenReturn(samplePackage);
        when(packageService.getAll(any(String.class))).thenReturn(List.of(samplePackage));
        when(packageService.update(any(String.class), any(CreatePackageRequest.class))).thenReturn(samplePackage);
    }

    @Test
    void createPackage() {
        CreatePackageRequest request = new CreatePackageRequest("Test Name", "Test Desc", List.of("prod1"));

        ResponseEntity<PackageResponse> created = restTemplate.postForEntity("/api/packages", request, PackageResponse.class);

        assertEquals(HttpStatus.CREATED, created.getStatusCode());
        assertNotNull(created.getBody());
        assertEquals("Test Name", created.getBody().name());
        assertEquals("Test Desc", created.getBody().description());
        assertEquals(List.of("prod1"), created.getBody().products().stream().map(Product::id).toList());
    }

    @Test
    void getPackage() {
        ResponseEntity<PackageResponse> fetched =
                restTemplate.getForEntity("/api/packages/{id}?currency=USD", PackageResponse.class, "pkg-1");

        assertEquals(HttpStatus.OK, fetched.getStatusCode());
        assertNotNull(fetched.getBody());
        assertEquals("pkg-1", fetched.getBody().id());
    }

    @Test
    void listPackages() {
        ResponseEntity<PackageResponse[]> response =
                restTemplate.getForEntity("/api/packages?currency=USD", PackageResponse[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PackageResponse[] packages = response.getBody();
        assertNotNull(packages);
        assertEquals(1, packages.length);
        assertEquals("pkg-1", packages[0].id());
    }

    @Test
    void updatePackage() {
        CreatePackageRequest request = new CreatePackageRequest("Updated Name", "Updated Desc", List.of("prod1"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreatePackageRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<PackageResponse> updated =
                restTemplate.exchange("/api/packages/{id}", HttpMethod.PUT, entity, PackageResponse.class, "pkg-1");

        assertEquals(HttpStatus.OK, updated.getStatusCode());
        assertNotNull(updated.getBody());
        assertEquals("Test Name", updated.getBody().name());
    }

    @Test
    void deletePackage() {
        ResponseEntity<Void> response =
                restTemplate.exchange("/api/packages/{id}", HttpMethod.DELETE, null, Void.class, "pkg-1");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}