package com.ecom.api.product;

import com.ecom.api.product.dto.ProductDtos.ProductCreateRequest;
import com.ecom.api.product.dto.ProductDtos.ProductResponse;
import com.ecom.api.product.dto.ProductDtos.ProductUpdateRequest;
import com.ecom.application.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * T037 – REST controller for {@code /api/v1/products}.
 */
@Tag(name = "Products", description = "Product management – CRUD")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "List all products")
    @ApiResponse(responseCode = "200", description = "Product list returned")
    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }

    @Operation(summary = "Get product by id")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable String id) {
        return productService.findById(id);
    }

    @Operation(summary = "Create product")
    @ApiResponse(responseCode = "201", description = "Product created")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody ProductCreateRequest req) {
        return productService.create(req);
    }

    @Operation(summary = "Update product (full replacement)")
    @ApiResponse(responseCode = "200", description = "Product updated")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable String id,
                                  @Valid @RequestBody ProductUpdateRequest req) {
        return productService.update(id, req);
    }

    @Operation(summary = "Delete product")
    @ApiResponse(responseCode = "204", description = "Product deleted")
    @ApiResponse(responseCode = "400", description = "Product still referenced by order items")
    @ApiResponse(responseCode = "404", description = "Product not found")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable String id) {
        productService.deleteById(id);
    }
}
