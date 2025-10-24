package com.nextjingjing.api.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nextjingjing.api.dto.ProductDTO;
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

    public ProductResponseDTO createProduct(ProductDTO dto, MultipartFile imageFile) throws IOException {
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
}
