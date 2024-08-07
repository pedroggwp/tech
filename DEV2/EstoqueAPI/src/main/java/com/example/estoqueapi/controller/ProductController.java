package com.example.estoqueapi.controller;

import com.example.estoqueapi.model.Product;
import com.example.estoqueapi.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
            description = "Return a list of all available products",
            tags = {"Products"}
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
            return ResponseEntity.status(HttpStatus.OK).body(products);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found.");
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID",
            description = "Returns a single product by its unique ID",
            tags = {"Products"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the product",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found with the provided ID",
                    content = @Content
            )
    })
    public ResponseEntity<?> getProductById(
            @Parameter(description = "ID of the product to be retrieved", required = true)
            @PathVariable int id) {
        try {
            Product product = productService.findOne(id);
            return ResponseEntity.status(HttpStatus.OK).body(product);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/description")
    @Operation(
            summary = "Get products by description",
            description = "Returns a list of products that match the given description",
            tags = {"Products"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved products by description",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid description provided",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error while fetching products",
                    content = @Content
            )
    })
    public ResponseEntity<?> getProductsByDescription(
            @Parameter(description = "Description to filter products", required = true)
            @RequestParam String description) {
        List<Product> products = this.productService.findByDescription(description);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @PostMapping
    @Operation(summary = "Insert a new product", description = "Add a new product in the stock", tags = {"Products"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product inserted successfully"),
            @ApiResponse(responseCode = "400", description = "Error in request", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error. Failed creating the product", content = @Content)
    })
    public ResponseEntity<?> createProduct(
            @Parameter(description = "Product object to be created", required = true)
            @Valid @RequestBody Product product, BindingResult result) {
        if(!result.hasErrors()) {
            Product createdProduct = productService.createOne(product);
            if(createdProduct.getId() != null) {
                return ResponseEntity.status(HttpStatus.OK).body(createdProduct.toString());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed creating the product");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorsMap(result.getFieldErrors()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product by ID", description = "remove a product from the system by its ID", tags = {"Products"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Product successfully deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404", description = "Product not found", content = @Content
            )
    })
    public ResponseEntity<?> deleteProduct(
            @Parameter(description = "ID of the product to be deleted", required = true)
            @PathVariable int id) {
        Product removedProduct = productService.deleteOne(id);

        if(removedProduct != null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(removedProduct);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update product by ID",
            description = "Updates an existing product by its unique ID",
            tags = {"Products"}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input, object invalid",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found with the provided ID",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error while updating product",
                    content = @Content
            )
    })
    public ResponseEntity<?> updateProduct(
            @Parameter(description = "ID of the product to be updated", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Product object containing updated information", required = true)
            @Valid @RequestBody Product updateData, BindingResult result) {

        if(!result.hasErrors()) {
            Product updatedProduct = productService.update(id, updateData);
            if(updatedProduct != null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updatedProduct);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorsMap(result.getFieldErrors()));
        }
    }

    @PatchMapping("/partial-update/{id}")
    @Operation(summary = "Partially update a product by ID", description = "Update an existing product partially by its ID", tags = {"Products"})
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Product partially updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request error",
                    content = @Content),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found",
                    content = @Content)
    })
    public ResponseEntity<?> updatePartialProduct(
            @Parameter(description = "ID of the product to be partially updated", required = true)
            @PathVariable Integer id,
            @Parameter(description = "Map containing the fields to be updated and their new values", required = true)
            @RequestBody Map<String, Object> updateData) {
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
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updatedProduct);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorsMap(result.getFieldErrors()));
            }
        } catch(IllegalArgumentException iae) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(iae.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Product not found.")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private static Map<String, String> getErrorsMap(List<FieldError> errors) {
        Map<String, String> errorMap = new HashMap<>();
        for (FieldError error : errors) {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errorMap.put(field, message);
        }
        return errorMap.size() > 0 ? errorMap : null;
    }
}
