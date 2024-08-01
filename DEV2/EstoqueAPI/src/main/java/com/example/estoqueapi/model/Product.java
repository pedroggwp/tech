package com.example.estoqueapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name="produto")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="nome")
    @NotNull(message = "The name should be not null")
    @Size(min = 3, message = "The name should have more than 2 characters")
    private String name;

    @Column(name="descricao")
    private String description;

    @Column(name="preco")
    @NotNull(message = "The price should be not null")
    @Min(value = 0, message = "The price should not be lower than zero")
    private Double price;

    @Column(name="quantidadeestoque")
    @NotNull(message = "The storage number should be not null")
    @Min(value = 0, message = "The storage number should not be lower than zero")
    private Integer number;

    public Product() {}

    public Product(int id, String name, String description, double price, int number) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.number = number;
    }

    public Product(String name, String description, double price, int number) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.number = number;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Double getPrice() {
        return this.price;
    }

    public Integer getNumber() {
        return this.number;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", number=" + number +
                '}';
    }

    public Map<String, Method> fillSettersMap() {
        Map<String, Method> settersMap = new HashMap<>();
        try {
            settersMap.put("name", getClass().getMethod("setName", String.class));
            settersMap.put("description", getClass().getMethod("setDescription", String.class));
            settersMap.put("price", getClass().getMethod("setPrice", double.class));
            settersMap.put("number", getClass().getMethod("setNumber", int.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return settersMap;
    }
}
