package com.nextjingjing.api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.nextjingjing.api.dto.OrderProductRequestDTO;
import com.nextjingjing.api.dto.OrderProductResponseDTO;
import com.nextjingjing.api.dto.OrderRequestDTO;
import com.nextjingjing.api.dto.OrderResponseDTO;
import com.nextjingjing.api.entity.Order;
import com.nextjingjing.api.entity.OrderProduct;
import com.nextjingjing.api.entity.Product;
import com.nextjingjing.api.entity.User;
import com.nextjingjing.api.repository.OrderProductRepository;
import com.nextjingjing.api.repository.OrderRepository;
import com.nextjingjing.api.repository.ProductRepository;
import com.nextjingjing.api.repository.UserRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

     @Autowired
    private ProductRepository productRepository;

     @Autowired
    private UserRepository userRepository;

     @Autowired
    private OrderProductRepository orderProductRepository;

    public OrderResponseDTO createOrder(Long userId, OrderRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Order order = new Order();
        order.setUser(user);

        List<OrderProduct> orderProducts = new ArrayList<>();
        double total = 0.0;

        for (OrderProductRequestDTO item : dto.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            if (product.getStock() < item.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Not enough stock for product: " + product.getName());
            }

            OrderProduct op = new OrderProduct();
            op.setOrder(order);
            op.setProduct(product);
            op.setQuantity(item.getQuantity());
            op.setPricePerUnit(product.getPrice());

            total += product.getPrice() * item.getQuantity();

            orderProducts.add(op);

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }

        order.setTotalAmount(total);
        order.setOrderProducts(orderProducts);

        Order savedOrder = orderRepository.save(order);

        return convertToResponse(savedOrder);
    }

    private OrderResponseDTO convertToResponse(Order order) {
        OrderResponseDTO response = new OrderResponseDTO();
        response.setId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setTotalAmount(order.getTotalAmount());
        response.setUserId(order.getUser().getId());

        List<OrderProductResponseDTO> items = new ArrayList<>();
        for (OrderProduct op : order.getOrderProducts()) {
            OrderProductResponseDTO itemDto = new OrderProductResponseDTO();
            itemDto.setProductId(op.getProduct().getId());
            itemDto.setProductName(op.getProduct().getName());
            itemDto.setQuantity(op.getQuantity());
            itemDto.setPrice(op.getPricePerUnit());
            items.add(itemDto);
        }
        response.setItems(items);
        return response;
    }

    public Page<OrderResponseDTO> getMyOrders(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findByUserId(user.getId(), pageable);
        return orders.map(this::convertToResponse);
    }

    public OrderResponseDTO getOrderById(String username, Long orderId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this order");
        }
        return convertToResponse(order);
    }

}
