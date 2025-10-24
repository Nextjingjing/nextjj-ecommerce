package com.nextjingjing.api.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextjingjing.api.dto.ProductDTO;
import com.nextjingjing.api.dto.ProductResponseDTO;
import com.nextjingjing.api.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ProductDTO productDTO = mapper.readValue(productJson, ProductDTO.class);
        ProductResponseDTO created = productService.createProduct(productDTO, imageFile);
        return ResponseEntity.ok(created);
    }
}
