package com.example.elasticsearchdemo.service;

import com.example.elasticsearchdemo.model.Product;
import com.example.elasticsearchdemo.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductSearchService {

    private final ProductRepository productRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public ProductSearchService(ProductRepository productRepository,
                               ElasticsearchOperations elasticsearchOperations) {
        this.productRepository = productRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    public List<Product> findAll() {
        return productRepository.findAll().stream().toList();
    }

    public List<Product> searchByName(String queryText) {
        Criteria criteria = new Criteria("name").matches(queryText);
        CriteriaQuery query = new CriteriaQuery(criteria);
        return execute(query);
    }

    public List<Product> findByBrand(String brand) {
        Criteria criteria = new Criteria("brand.keyword").is(brand);
        CriteriaQuery query = new CriteriaQuery(criteria);
        return execute(query);
    }

    public List<Product> findByMaxPrice(Double maxPrice) {
        Criteria criteria = new Criteria("price").lessThanEqual(maxPrice);
        CriteriaQuery query = new CriteriaQuery(criteria, Sort.by(Sort.Direction.ASC, "price"));
        return execute(query);
    }

    private List<Product> execute(CriteriaQuery query) {
        SearchHits<Product> hits = elasticsearchOperations.search(query, Product.class);

        if (hits == null) {
            return List.of();
        }

        return hits.stream()
                .map(SearchHit::getContent)
                .toList();
    }
}
