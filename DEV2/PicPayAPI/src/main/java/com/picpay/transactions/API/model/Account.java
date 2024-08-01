package com.picpay.transactions.API.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CPF;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "conta")
public class Account {
    @Id
    @Column(name = "numero_conta")
    private String number;

    @Column(name = "saldo")
    @NotNull(message = "The value should be not null")
    @Min(value = 0, message = "The value should not be lower than zero")
    private Double value;

    @Column(name = "limite_especial")
    @Min(value = 0, message = "The limit should not be lower than zero")
    private Double limit;

    @ManyToOne
    @JoinColumn(name = "cliente_cpf")
    @NotNull(message = "The Account Customer should be not null")
    private Customer customer;

    public Account() {}

    public Account(Double value, Double limit, Customer customer) {
        this.value = value;
        this.limit = limit;
        this.customer = customer;
    }

    public Account(String number, Double value, Double limit, Customer customer) {
        this.number = number;
        this.value = value;
        this.limit = limit;
        this.customer = customer;
    }

    public String getNumber() {
        return this.number;
    }

    public Double getValue() {
        return this.value;
    }

    public Double getLimit() {
        return this.limit;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setLimit(Double limit) {
        this.limit = limit;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Map<String, Method> fillSettersMap() {
        Map<String, Method> settersMap = new HashMap<>();
        try {
            settersMap.put("value", getClass().getMethod("setValue", Double.class));
            settersMap.put("limit", getClass().getMethod("setLimit", Double.class));
            settersMap.put("customer", getClass().getMethod("setCustomer", Customer.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return settersMap;
    }
}
