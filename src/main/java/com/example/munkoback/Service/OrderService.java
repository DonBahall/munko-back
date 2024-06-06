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

    public Order saveOrder(String sessionID, Integer userId, Integer funkoId) {
        Order order = getUserOrder(sessionID, userId);
        if (order == null) {
            order = new Order();
            if (sessionID == null) {
                order.setUserId(userService.findById(userId));
            } else if(userId == null ){
                order.setSessionID(sessionID);
            }else {
                throw new InvalidArgumentsException("User id or session id required");
            }
            order.setOrderItems(new ArrayList<>());
            order.setStatus(Status.PENDING);
        }

        FunkoPop funkoPop = funkoPopService.getItem(funkoId);

        OrderItem item = new OrderItem(order, funkoPop.getImages().get(0), funkoPop.getName(), funkoPop.getAmount(), funkoPop.getPrice());
        order.getOrderItems().add(item);
        repository.save(order);
        return order;
    }

    public Boolean deleteItemInBasket(String sessionID, Integer userId, Integer itemId) {
        Order order = getUserOrder(sessionID, userId);
        if (order == null) {
            throw new InvalidArgumentsException("Order does not exist");
        }
        if (itemId != null) {
            order.getOrderItems().remove(orderItemRepository.findById(itemId).orElse(null));
            repository.save(order);
            if (order.getOrderItems() == null) {
                repository.delete(order);
            }
            return true;
        }
        throw new InvalidArgumentsException("Something goes wrong");
    }

    public List<OrderItem> getOrderItems(String sessionID, Integer userId) {
        Order order = getUserOrder(sessionID, userId);
        if (order == null) {
            throw new InvalidArgumentsException("Order does not exist");
        }
        return order.getOrderItems();
    }

    public Order getUserOrder(String sessionID, Integer userId) {
        if (sessionID == null) {
            User user = userService.getAutentificatedUser();
            if (!user.getId().equals(userId)) {
                throw new InvalidArgumentsException("Wrong user");
            }
            return repository.findOrderByUserIdAndStatus(userService.findById(userId), Status.PENDING).orElse(null);
        } else if (userId == null) {
            return repository.findOrderBySessionIDAndStatus(sessionID, Status.PENDING).orElse(null);
        } else {
            throw new InvalidArgumentsException("At least one argument required");
        }
    }

    public OrderItem updateItemInBasket(String sessionID, Integer userId, OrderItem newItem) {
        Order order = getUserOrder(sessionID, userId);
        if (newItem == null) {
            throw new InvalidArgumentsException("Item is empty");
        }
        OrderItem existing = orderItemRepository.findByOrderAndId(order, newItem.getId()).orElse(null);
        if (existing == null) {
            throw new InvalidArgumentsException("Item not found");
        }

        existing.setAmount(newItem.getAmount());

        return orderItemRepository.save(existing);
    }
}
