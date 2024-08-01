package com.picpay.transactions.API.service;

import com.picpay.transactions.API.model.Customer;
import com.picpay.transactions.API.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findOne(String cpf) {
        return customerRepository.findById(cpf).orElseThrow(() ->
                new RuntimeException("Customer not found.")
        );
    }

    public Customer createOne(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updatePartial(String cpf, Map<String, Object> updates) throws Exception {
        Optional<Customer> existentProduct = customerRepository.findById(cpf);
        if(existentProduct.isPresent()) {
            try {
                Customer customer = existentProduct.get();
                Map<String, Method> actions = customer.fillSettersMap();

                for(String key : updates.keySet()) {
                    Method action = actions.get(key);
                    if(action != null) {
                        action.invoke(customer, updates.get(key));
                    }
                }
                return customerRepository.save(customer);
            } catch (Exception e) {
                throw new Exception(e);
            }
        } else {
            return null;
        }
    }

    public Customer update(String cpf, Customer updates) {
        Optional<Customer> existentCustomer = customerRepository.findById(cpf);
        if(existentCustomer.isPresent()) {
            Customer customer = existentCustomer.get();
            customer.setName(updates.getName());
            customer.setEmail(updates.getEmail());
            customer.setPhoneNumber(updates.getPhoneNumber());
            return customerRepository.save(customer);
        } else {
            return null;
        }
    }

    public Customer deleteOne(String cpf) {
        Optional<Customer> customer = customerRepository.findById(cpf);
        if(customer.isPresent()) {
            customerRepository.deleteById(cpf);
            return customer.get();
        }
        return null;
    }
}
