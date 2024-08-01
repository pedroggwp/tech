package com.picpay.transactions.API.controller;

import com.picpay.transactions.API.model.Customer;
import com.picpay.transactions.API.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {
    private final CustomerService customerService;
    private final Validator validator;

    @Autowired
    public CustomerController(CustomerService customerService, Validator validator) {
        this.customerService = customerService;
        this.validator = validator;
    }

    @GetMapping("list")
    public ResponseEntity<?> listCustomers() {
        try {
            List<Customer> customer = customerService.findAll();
            return ResponseEntity.status(200).body(customer);
        } catch(Exception e) {
            return ResponseEntity.status(404).body("No customer found.");
        }
    }

    @GetMapping("get/{cpf}")
    public ResponseEntity<?> getCustomer(@PathVariable String cpf) {
        try {
            Customer customer = customerService.findOne(cpf);
            return ResponseEntity.status(200).body(customer);
        } catch(Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("create")
    public ResponseEntity<?> insertCustomer(@Valid @RequestBody Customer customer, BindingResult result) {
        if(!result.hasErrors()) {
            Customer createdCustomer = customerService.createOne(customer);
            if(createdCustomer.getCpf() != null) {
                return ResponseEntity.status(200).body(createdCustomer.toString());
            } else {
                return ResponseEntity.status(500).body("Failed creating the customer");
            }
        } else {
            return ResponseEntity.status(400).body(getErrorsMap(result.getFieldErrors()));
        }
    }

    @DeleteMapping("remove/{cpf}")
    public ResponseEntity<?> removeCustomer(@PathVariable String cpf) {
        Customer removedCustomer = customerService.deleteOne(cpf);
        if(removedCustomer != null) {
            return ResponseEntity.status(204).body(removedCustomer);
        }
        return ResponseEntity.status(404).body("Customer not found");
    }

    @PutMapping("/update/{cpf}")
    public ResponseEntity<?> updateCustomer(@PathVariable String cpf, @Valid @RequestBody Customer updateData, BindingResult result) {
        if(!result.hasErrors()) {
            Customer updatedCustomer = customerService.update(cpf, updateData);
            if(updatedCustomer != null) {
                return ResponseEntity.status(204).body(updatedCustomer);
            }
            return ResponseEntity.status(404).body("Customer not found");
        } else {
            return ResponseEntity.status(400).body(getErrorsMap(result.getFieldErrors()));
        }
    }

    @PatchMapping("/partial-update/{cpf}")
    public ResponseEntity<?> updatePartialCustomer(@PathVariable String cpf, @RequestBody Map<String, Object> updateData) {
        try {
            Customer customer = customerService.findOne(cpf);

            Map<String, Method> actions = customer.fillSettersMap();
            for (String key : updateData.keySet()) {
                Method action = actions.get(key);
                if (action != null) {
                    action.invoke(customer, updateData.get(key));
                }
            }

            DataBinder binder = new DataBinder(customer);
            binder.setValidator(validator);
            binder.validate();
            BindingResult result = binder.getBindingResult();

            if (!result.hasErrors()) {
                Customer updatedCustomer = customerService.updatePartial(cpf, updateData);
                return ResponseEntity.status(204).body(updatedCustomer);
            } else {
                return ResponseEntity.status(400).body(getErrorsMap(result.getFieldErrors()));
            }
        } catch(IllegalArgumentException iae) {
            return ResponseEntity.status(400).body(iae.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Customer not found.")) {
                return ResponseEntity.status(404).body(e.getMessage());
            } else {
                return ResponseEntity.status(500).body(e.getMessage());
            }
        } catch(Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    private static Map<String, String> getErrorsMap(List<FieldError> errors) {
        Map<String, String> errorMap = new HashMap<String, String>();
        for (FieldError error : errors) {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errorMap.put(field, message);
        }
        return errorMap.size() > 0 ? errorMap : null;
    }
}
