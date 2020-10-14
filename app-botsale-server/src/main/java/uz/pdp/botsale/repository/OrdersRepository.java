package uz.pdp.botsale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.botsale.entity.Customer;
import uz.pdp.botsale.entity.Orders;
import uz.pdp.botsale.entity.User;
import uz.pdp.botsale.entity.enums.OrderStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    Page<Orders> findAllByOrderStatus(OrderStatus orderStatus, Pageable pageable);

    Page<Orders> findAllByCreatedAtBetween(Timestamp startDate, Timestamp endDate, Pageable pageable);

    List<Orders> findAllByCustomerPhoneNumber(String phoneNumber);

    Optional<Orders> findByOrderStatusAndCustomer(OrderStatus orderStatus, Customer customer);

    Page<Orders> findAllByWarehouseUser(User warehouse_user, Pageable pageable);
}
