package nano.project.eshop.services;

import nano.project.eshop.models.Order;
import nano.project.eshop.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("orderService")
public class OrderService {
    private OrderRepository orderRepository;

    @Autowired
    public OrderService(@Qualifier("orderRepository") OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findAllOrders(){
        return (List<Order>) orderRepository.findAll();
    }

}
