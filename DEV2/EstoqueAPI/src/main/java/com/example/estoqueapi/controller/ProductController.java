package com.example.estoqueapi.controller;

import com.example.estoqueapi.model.Product;
import com.example.estoqueapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
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
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final Validator validator;

    @Autowired
    public ProductController(ProductService productService, Validator validator) {
        this.productService = productService;
        this.validator = validator;
    }

    @GetMapping
    @Operation(
            summary = "List all products",
            description = "Return a list of all available products"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List all products returned with success",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content
            )
    })
    public ResponseEntity<?> getAllProducts() {
        try {
            List<Product> products = productService.findAll();
            return ResponseEntity.status(200).body(products);
        } catch(Exception e) {
            return ResponseEntity.status(404).body("No products found.");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable int id) {
        try {
            Product product = productService.findOne(id);
            return ResponseEntity.status(200).body(product);
        } catch(Exception e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/description")
    public ResponseEntity<?> getProductsByDescription(@RequestParam String description) {
        List<Product> products = this.productService.findByDescription(description);
        return ResponseEntity.status(200).body(products);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product, BindingResult result) {
        if(!result.hasErrors()) {
            Product createdProduct = productService.createOne(product);
            if(createdProduct.getId() != null) {
                return ResponseEntity.status(200).body(createdProduct.toString());
            } else {
                return ResponseEntity.status(500).body("Failed creating the product");
            }
        } else {
            return ResponseEntity.status(400).body(getErrorsMap(result.getFieldErrors()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id) {
        Product removedProduct = productService.deleteOne(id);
        if(removedProduct != null) {
            return ResponseEntity.status(204).body(removedProduct);
        }
        return ResponseEntity.status(404).body("Product not found");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Integer id, @Valid @RequestBody Product updateData, BindingResult result) {
        if(!result.hasErrors()) {
            Product updatedProduct = productService.update(id, updateData);
            if(updatedProduct != null) {
                return ResponseEntity.status(204).body(updatedProduct);
            }
            return ResponseEntity.status(404).body("Product not found");
        } else {
            return ResponseEntity.status(400).body(getErrorsMap(result.getFieldErrors()));
        }
    }

    @PatchMapping("/partial-update/{id}")
    public ResponseEntity<?> updatePartialProduct(@PathVariable Integer id, @RequestBody Map<String, Object> updateData) {
        try {
            Product product = productService.findOne(id);

            Map<String, Method> actions = product.fillSettersMap();
            for (String key : updateData.keySet()) {
                Method action = actions.get(key);
                if (action != null) {
                    action.invoke(product, updateData.get(key));
                }
            }

            DataBinder binder = new DataBinder(product);
            binder.setValidator(validator);
            binder.validate();
            BindingResult result = binder.getBindingResult();

            if (!result.hasErrors()) {
                Product updatedProduct = productService.updatePartial(id, updateData);
                return ResponseEntity.status(204).body(updatedProduct);
            } else {
                return ResponseEntity.status(400).body(getErrorsMap(result.getFieldErrors()));
            }
        } catch(IllegalArgumentException iae) {
            return ResponseEntity.status(400).body(iae.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Product not found.")) {
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
