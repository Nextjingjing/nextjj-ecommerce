package com.nextjingjing.api.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nextjingjing.api.dto.ProductRequestDTO;
import com.nextjingjing.api.dto.ProductResponseDTO;
import com.nextjingjing.api.entity.Category;
import com.nextjingjing.api.entity.Product;
import com.nextjingjing.api.repository.CategoryRepository;
import com.nextjingjing.api.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    CategoryRepository categoryRepository;

    public ProductResponseDTO createProduct(ProductRequestDTO dto, MultipartFile imageFile) throws IOException {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = cloudinaryService.uploadImage(imageFile);
            product.setImageUrl(imageUrl);
        }
        Product saved = productRepository.save(product);

        ProductResponseDTO response = new ProductResponseDTO();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setDescription(saved.getDescription());
        response.setPrice(saved.getPrice());
        response.setStock(saved.getStock());
        response.setImageUrl(saved.getImageUrl());
        if (saved.getCategory() != null) {
            response.setCategoryId(saved.getCategory().getId());
        }

        return response;
    }

    public Page<ProductResponseDTO> getAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        return productPage.map(this::convertToResponseDTO);
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return convertToResponseDTO(product);
    }

    private ProductResponseDTO convertToResponseDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setImageUrl(product.getImageUrl());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
        }
        return dto;
    }

    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto, MultipartFile imageFile) throws IOException {
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));

    product.setName(dto.getName());
    product.setDescription(dto.getDescription());
    product.setPrice(dto.getPrice());
    product.setStock(dto.getStock());

    if (dto.getCategoryId() != null) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setCategory(category);
    }

    if (imageFile != null && !imageFile.isEmpty()) {
        String imageUrl = cloudinaryService.uploadImage(imageFile);
        product.setImageUrl(imageUrl);
    }

    Product updated = productRepository.save(product);
    return convertToResponseDTO(updated);
    }
}
