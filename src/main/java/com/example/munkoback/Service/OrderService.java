package com.example.munkoback.Service;

import com.example.munkoback.Model.FunkoPop.FunkoPop;
import com.example.munkoback.Model.InvalidArgumentsException;
import com.example.munkoback.Model.Order.Order;
import com.example.munkoback.Model.Order.OrderItem;
import com.example.munkoback.Model.Order.Status;
import com.example.munkoback.Model.User.User;
import com.example.munkoback.Repository.OrderItemRepository;
import com.example.munkoback.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repository;
    private final UserService userService;
    private final FunkoPopService funkoPopService;
    private final OrderItemRepository orderItemRepository;

    public OrderItem addItemInBasket(Integer userId, Integer funkoId) {
        if (userId == null) {
            throw new InvalidArgumentsException("User id required");
        }
        Order order = getUserOrder(userId);
        if (order == null) {
            order = createNewOrder(userId);
        }

        FunkoPop funkoPop = funkoPopService.getItem(funkoId);
        OrderItem existingItem = getOrderItemByFunkoId(order, funkoId);

        if (existingItem != null) {
            existingItem.setAmount(existingItem.getAmount() + 1);
            repository.save(order);
            return existingItem;
        } else {
            OrderItem newItem = new OrderItem(order, funkoPop, 1);
            order.getOrderItems().add(newItem);
            repository.save(order);
            return newItem;
        }

    }

    private Order createNewOrder(Integer userId) {
        Order order = new Order();
        order.setUserId(userService.findById(userId));

        order.setOrderItems(new ArrayList<>());
        order.setStatus(Status.PENDING);
        return order;
    }

    private OrderItem getOrderItemByFunkoId(Order order, Integer funkoId) {
        for (OrderItem item : order.getOrderItems()) {
            if (item.getFunkoPop() != null) {
                if (item.getFunkoPop().getId().equals(funkoId)) {
                    return item;
                }
            }
        }
        return null;
    }

    public Boolean deleteItemInBasket(Integer userId, Integer funkoId) {
        Order order = getUserOrder(userId);
        if (order == null) {
            throw new InvalidArgumentsException("Order does not exist");
        }
        if (funkoId != null) {
            order.getOrderItems().remove(orderItemRepository.findById(funkoId).orElse(null));
            repository.save(order);
            if (order.getOrderItems() == null) {
                repository.delete(order);
            }
            return true;
        }
        throw new InvalidArgumentsException("Something goes wrong");
    }

    public List<OrderItem> getOrderItems(Integer userId) {
        Order order = getUserOrder(userId);
        if (order == null) {
            throw new InvalidArgumentsException("Order does not exist");
        }
        return order.getOrderItems();
    }

    public Order getUserOrder(Integer userId) {
        if (userId != null) {
            User user = userService.getAutentificatedUser();
            if (!user.getId().equals(userId)) {
                throw new InvalidArgumentsException("Wrong user");
            }
            return repository.findOrderByUserIdAndStatus(userService.findById(userId), Status.PENDING).orElse(null);
        } else {
            throw new InvalidArgumentsException("At least one argument required");
        }
    }

    public OrderItem updateItemInBasket(Integer userId, Integer orderItemId, Integer amount) {
        Order order = getUserOrder(userId);
        if (orderItemRepository.findById(orderItemId).orElse(null) == null) {
            throw new InvalidArgumentsException("Item is empty");
        }
        OrderItem existing = orderItemRepository.findByOrderAndId(order, orderItemId).orElse(null);
        if (existing == null) {
            throw new InvalidArgumentsException("Item not found");
        }
        if (amount <= 0) {
            throw new InvalidArgumentsException("Wrong amount");
        }
        existing.setAmount(amount);

        return orderItemRepository.save(existing);
    }
}
