package nano.project.eshop;

import nano.project.eshop.models.Order;
import nano.project.eshop.models.OrderLine;
import nano.project.eshop.models.User;
import nano.project.eshop.services.OrderService;
import nano.project.eshop.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EshopApplicationTests {
	@Autowired
	UserService userService ;
	@Test
	public void contextLoads() {
		System.out.println("Hello world");
		OrderService orderService;
		Order order = new Order();
		order.getUser().getFirstName();
		for (int i = 0; i < order.getOrderLines().size(); i++) {
			order.getOrderLines().get(i).getProduct().getName();
			order.getOrderLines().get(i).getQuantity();

		}

		List<User> users = userService.findAllUsers();
		for (User user:
			 users) {

			System.out.println(user.getFirstName());

		}
	}

}
