package com.example.elasticsearchdemo.controller;

import com.example.elasticsearchdemo.model.Product;
import com.example.elasticsearchdemo.service.ProductSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductSearchService productSearchService;

    public ProductController(ProductSearchService productSearchService) {
        this.productSearchService = productSearchService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productSearchService.findAll());
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchByName(@RequestParam String query) {
        return ResponseEntity.ok(productSearchService.searchByName(query));
    }

    @GetMapping("/products/brand")
    public ResponseEntity<List<Product>> getByBrand(@RequestParam String brand) {
        return ResponseEntity.ok(productSearchService.findByBrand(brand));
    }

    @GetMapping("/products/price")
    public ResponseEntity<List<Product>> getByPrice(@RequestParam Double maxPrice) {
        return ResponseEntity.ok(productSearchService.findByMaxPrice(maxPrice));
    }
}
