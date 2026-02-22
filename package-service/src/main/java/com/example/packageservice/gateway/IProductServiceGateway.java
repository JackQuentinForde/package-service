package com.example.packageservice.gateway;

import com.example.packageservice.gateway.dto.Product;
import java.util.List;

public interface IProductServiceGateway {
    List<Product> getProductsByIds(List<String> ids);
}