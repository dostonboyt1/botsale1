package uz.pdp.botsale.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.pdp.botsale.entity.Product;
import uz.pdp.botsale.entity.enums.Gender;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findAllByNameUzStartingWithIgnoreCaseOrNameRuStartingWithIgnoreCaseOrBrandNameUzStartingWithIgnoreCaseOrBrandNameRuStartingWithIgnoreCaseOrCategoryNameUzStartingWithIgnoreCaseOrCategoryNameRuStartingWithIgnoreCase(String nameUz, String nameRu, String brand_nameUz, String brand_nameRu, String category_nameUz, String category_nameRu);

    Page<Product> findAllByCategoryIdAndBrandIdAndGenderAndActive(Integer category_id, Integer brand_id, Gender gender, boolean active, Pageable pageable);

    @Query(value="select w.id as warehouseId,  ((select coalesce(sum(pwa.amount), 0) from product_with_amount pwa join output o on pwa.output_id = o.id and o.incomer_id=w.id where pwa.product_id = :productId) - (select coalesce(sum(pwa.amount), 0) from product_with_amount pwa join output o on pwa.output_id = o.id and o.outputer_id=w.id where pwa.product_id = :productId) - (select coalesce(sum(pwa.amount - pwa.canceled_amount), 0) from product_with_amount pwa join orders o on pwa.orders_id = o.id and o.warehouse_id=w.id where pwa.product_id = :productId)) as productAmount from warehouse w",nativeQuery=true)
    List<Object[]> getAmountByAllWarehouseAndProductId(@Param(value="productId") UUID productId);

    @Query(value="select w.id as warehouseId,  ((select coalesce(sum(pwa.amount), 0) from product_with_amount pwa join output o on pwa.output_id = o.id and o.incomer_id=w.id where pwa.product_id = :productId and pwa.product_size_id=:size) - (select coalesce(sum(pwa.amount), 0) from product_with_amount pwa join output o on pwa.output_id = o.id and o.outputer_id=w.id where pwa.product_id = :productId and pwa.product_size_id=:size) - (select coalesce(sum(pwa.amount - pwa.canceled_amount), 0) from product_with_amount pwa join orders o on pwa.orders_id = o.id where pwa.product_id = :productId and pwa.product_size_id=:size)) as productAmount from warehouse w",nativeQuery=true)
    List<Object[]> getAmountByAllWarehouseAndProductIdAndSize(@Param(value="productId") UUID productId,@Param(value="size") UUID size);


    List<Product> findAllByCategoryIdAndBrandId(Integer category_id, Integer brand_id);
    List<Product> findAllByCategoryId(Integer category_id);
    List<Product> findAllByBrandId(Integer brand_id);
}
