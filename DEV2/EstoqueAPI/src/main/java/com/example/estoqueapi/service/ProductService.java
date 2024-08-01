package com.example.estoqueapi.service;

import com.example.estoqueapi.model.Product;
import com.example.estoqueapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findOne(int id) {
        return productRepository.findById(id).orElseThrow(() ->
            new RuntimeException("Product not found.")
        );
    }

    public List<Product> findByDescription(String description) {
        return productRepository.findByDescriptionContainsIgnoreCase(description);
    }

    public Product createOne(Product product) {
        return productRepository.save(product);
    }

    public Product updatePartial(int id, Map<String, Object> updates) throws Exception {
        Optional<Product> existentProduct = productRepository.findById(id);
        if(existentProduct.isPresent()) {
            try {
                Product product = existentProduct.get();
                Map<String, Method> actions = product.fillSettersMap();

                for(String key : updates.keySet()) {
                    Method action = actions.get(key);
                    if(action != null) {
                        action.invoke(product, updates.get(key));
                    }
                }
                return productRepository.save(product);
            } catch (Exception e) {
                throw new Exception(e);
            }
        } else {
            return null;
        }
    }

    public Product update(int id, Product updates) {
        Optional<Product> existentProduct = productRepository.findById(id);
        if(existentProduct.isPresent()) {
            Product product = existentProduct.get();
            product.setName(updates.getName());
            product.setDescription(updates.getDescription());
            product.setPrice(updates.getPrice());
            product.setNumber(updates.getNumber());
            return productRepository.save(product);
        } else {
            return null;
        }
    }

    public Product deleteOne(int id) {
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()) {
            productRepository.deleteById(id);
            return product.get();
        }
        return null;
    }
}
