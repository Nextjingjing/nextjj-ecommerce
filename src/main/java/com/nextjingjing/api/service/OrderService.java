package com.nextjingjing.api.service;

import com.nextjingjing.api.dto.OrderProductRequestDTO;
import com.nextjingjing.api.dto.OrderProductResponseDTO;
import com.nextjingjing.api.dto.OrderRequestDTO;
import com.nextjingjing.api.dto.OrderResponseDTO;
import com.nextjingjing.api.entity.Order;
import com.nextjingjing.api.entity.OrderProduct;
import com.nextjingjing.api.entity.Product;
import com.nextjingjing.api.entity.User;
import com.nextjingjing.api.repository.OrderRepository;
import com.nextjingjing.api.repository.ProductRepository;
import com.nextjingjing.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public OrderResponseDTO createOrder(Long userId, OrderRequestDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Order order = new Order();
        order.setUser(user);

        List<OrderProduct> orderProducts = new ArrayList<>();
        double total = 0.0;

        for (OrderProductRequestDTO item : dto.getItems()) {
            Product product = productRepository.findByIdForUpdate(item.getProductId())
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

    public Page<OrderResponseDTO> getMyOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findByUserId(userId, pageable);
        return orders.map(this::convertToResponse);
    }

    public OrderResponseDTO getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied to this order");
        }

        return convertToResponse(order);
    }

    public OrderResponseDTO updateOrder(Long userId, Long orderId, OrderRequestDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot edit this order");
        }

        for (OrderProduct oldItem : order.getOrderProducts()) {
            Product product = oldItem.getProduct();
            product.setStock(product.getStock() + oldItem.getQuantity());
            productRepository.save(product);
        }

        order.getOrderProducts().clear();

        double total = 0.0;
        List<OrderProduct> newItems = new ArrayList<>();

        for (OrderProductRequestDTO item : dto.getItems()) {
            Product product = productRepository.findByIdForUpdate(item.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            if (product.getStock() < item.getQuantity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Not enough stock for " + product.getName());
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            OrderProduct op = new OrderProduct();
            op.setOrder(order);
            op.setProduct(product);
            op.setQuantity(item.getQuantity());
            op.setPricePerUnit(product.getPrice());
            newItems.add(op);

            total += product.getPrice() * item.getQuantity();
        }

        order.getOrderProducts().addAll(newItems);
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        return convertToResponse(saved);
    }

    public void deleteOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot delete this order");
        }

        for (OrderProduct op : order.getOrderProducts()) {
            Product product = productRepository.findByIdForUpdate(op.getProduct().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            product.setStock(product.getStock() + op.getQuantity());
            productRepository.save(product);
        }

        orderRepository.delete(order);
    }

    private OrderResponseDTO convertToResponse(Order order) {
        OrderResponseDTO response = new OrderResponseDTO();
        response.setId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setTotalAmount(order.getTotalAmount());
        response.setUserId(order.getUser().getId());
        response.setStatus(order.getStatus());

        List<OrderProductResponseDTO> items = new ArrayList<>();
        for (OrderProduct op : order.getOrderProducts()) {
            OrderProductResponseDTO itemDto = new OrderProductResponseDTO();
            itemDto.setProductId(op.getProduct().getId());
            itemDto.setProductName(op.getProduct().getName());
            itemDto.setQuantity(op.getQuantity());
            itemDto.setPricePerUnit(op.getPricePerUnit());
            items.add(itemDto);
        }

        response.setItems(items);
        return response;
    }

    @Transactional(readOnly = true)
    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
    }

    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + orderId));

        order.setStatus(status);
        orderRepository.save(order);
    }
}
