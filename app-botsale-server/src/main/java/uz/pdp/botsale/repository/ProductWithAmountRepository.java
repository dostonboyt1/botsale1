package uz.pdp.botsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.pdp.botsale.entity.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductWithAmountRepository extends JpaRepository<ProductWithAmount, UUID> {
    @Query(value="select ((select coalesce(sum(pwa.amount), 0) from product_with_amount pwa join output o on pwa.output_id = o.id join warehouse w on o.incomer_id = w.id join users u on w.user_id = u.id and u.id = :userId where pwa.product_id = :productId and pwa.product_size_id=:sizeId) - (select coalesce(sum(pwa.amount), 0) from product_with_amount pwa join output o on pwa.output_id = o.id join warehouse w on o.outputer_id = w.id join users u on w.user_id = u.id and u.id = :userId where pwa.product_id = :productId and pwa.product_size_id=:sizeId)-(select coalesce(sum(pwa.amount-pwa.canceled_amount), 0) from product_with_amount pwa join orders o on pwa.orders_id = o.id join warehouse w on o.warehouse_id = w.id join users u on w.user_id = u.id and u.id = :userId where pwa.product_id = :productId and pwa.product_size_id=:sizeId))",nativeQuery=true)
    Integer getAmountByWarehouseAndProduct(@Param(value="userId")UUID userId,@Param(value="productId")UUID productId,@Param(value="sizeId")UUID sizeId);

    Optional<ProductWithAmount> findByOrdersAndProductSizeAndProduct(Orders orders, ProductSize productSize, Product product);

    List<ProductWithAmount> findAllByOutput(Output output);

    @Query(value="select ((select coalesce(sum(pwa.amount), 0) from product_with_amount pwa join output o on pwa.output_id = o.id join warehouse w on o.incomer_id = w.id and w.id=:warehouseId where pwa.product_id = :productId and pwa.product_size_id=:sizeId) - (select coalesce(sum(pwa.amount), 0) from product_with_amount pwa join output o on pwa.output_id = o.id join warehouse w on o.outputer_id = w.id and w.id=:warehouseId where pwa.product_id = :productId and pwa.product_size_id=:sizeId)-(select coalesce(sum(pwa.amount-pwa.canceled_amount), 0) from product_with_amount pwa join orders o on pwa.orders_id = o.id join warehouse w on o.warehouse_id = w.id and w.id=:warehouseId where pwa.product_id = :productId and pwa.product_size_id=:sizeId))",nativeQuery=true)
    Integer getProductAmountByWarehouseIdAndProductIdAndSizeId(@Param(value="warehouseId")Integer warehouseId,@Param(value="productId")UUID productId,@Param(value="sizeId")UUID sizeId);

}
