package com.picpay.transactions.API.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name="cliente")
public class Customer {
    @Id
    @Column(name = "cpf")
    @CPF(message = "Should be CPF format")
    @NotNull(message = "The cpf should be not null")
    private String cpf;

    @Column(name = "nome")
    @NotNull(message = "The name should be not null")
    private String name;

    @Column(name = "email")
    @Email(message = "The format should fit email")
    private String email;

    @Column(name = "telefone")
    @NotNull(message = "The phone number should be not null")
    @Size(min = 8, message = "The phone number should have more than 8 characters")
    @Size(max = 15, message = "The phone number should not have more than 15 characters")
    private String phoneNumber;

    @JsonIgnore
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Account> accounts;

    public Customer() {}

    public Customer(String cpf, String name, String email, String phoneNumber) {
        this.cpf = cpf;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getCpf() {
        return this.cpf;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public Map<String, Method> fillSettersMap() {
        Map<String, Method> settersMap = new HashMap<>();
        try {
            settersMap.put("name", getClass().getMethod("setName", String.class));
            settersMap.put("email", getClass().getMethod("setEmail", String.class));
            settersMap.put("phoneNumber", getClass().getMethod("setPhoneNumber", String.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return settersMap;
    }
}
