package com.example.elasticsearchdemo.config;

import com.example.elasticsearchdemo.model.Product;
import com.example.elasticsearchdemo.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(ProductRepository productRepository, ElasticsearchOperations elasticsearchOperations) {
        return args -> {
            elasticsearchOperations.indexOps(Product.class).create();

            productRepository.deleteAll();

            List<Product> products = List.of(
                    new Product("1", "Nike Air Max", "Nike", "black", 4500.0),
                    new Product("2", "Adidas Running Shoes", "Adidas", "white", 3500.0),
                    new Product("3", "Nike Revolution 7", "Nike", "black", 3200.0),
                    new Product("4", "Puma Sport Sneakers", "Puma", "red", 2800.0)
            );

            productRepository.saveAll(products);
            System.out.println("Seeded Elasticsearch with sample products.");
        };
    }
}
