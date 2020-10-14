package uz.pdp.botsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.botsale.entity.Output;
import uz.pdp.botsale.entity.User;
import uz.pdp.botsale.entity.Warehouse;

import java.util.List;
import java.util.UUID;

public interface WarehouseRepository extends JpaRepository<Warehouse,Integer> {
    Warehouse findByUser(User user);

    @Query(value = "select w.id as id, w.name_uz as omborNomi,((select coalesce(sum(pwa.amount * p.income_price), 0) from product_with_amount pwa join product p on pwa.product_id = p.id join output o on pwa.output_id = o.id and o.incomer_id = w.id)-((select coalesce(sum(pwa.amount * p.income_price), 0) from product_with_amount pwa join product p on pwa.product_id = p.id join output o on pwa.output_id = o.id and o.outputer_id = w.id)+(select coalesce(sum(pwa.amount * p.income_price), 0) from product_with_amount pwa join product p on pwa.product_id = p.id join orders ord on pwa.orders_id = ord.id and ord.warehouse_id = w.id))) ostotka from warehouse w", nativeQuery = true)
    List<Object[]> getWarehouseBalance();
}
