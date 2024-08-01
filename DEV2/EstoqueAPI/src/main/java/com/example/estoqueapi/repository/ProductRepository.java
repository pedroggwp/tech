package com.example.estoqueapi.repository;

import com.example.estoqueapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Modifying
    @Query("DELETE FROM Product e WHERE e.id = ?1")
    void deleteById(Integer id);

    List<Product> findByDescriptionContainsIgnoreCase(String description);
}
