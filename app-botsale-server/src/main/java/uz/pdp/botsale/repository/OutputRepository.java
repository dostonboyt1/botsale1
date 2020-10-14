package uz.pdp.botsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.pdp.botsale.entity.Output;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface OutputRepository extends JpaRepository<Output, UUID> {
    @Query(value="select output.* from output join warehouse w on output.incomer_id = w.id and w.id=:warehouseId where output.confirmed = :confirmed and output.created_at between :startDate and :endDate order by output.created_at desc limit :size offset :page*:size",nativeQuery=true)
    List<Output> getAllIncomesByConfirmAndDate(@Param(value="confirmed")boolean confirmed, @Param(value="startDate")Timestamp startDate,@Param(value="endDate")Timestamp endDate,@Param(value="warehouseId")Integer warehouseId,@Param(value="page")Integer page,@Param(value="size")Integer size);

    @Query(value="select count(output.*) from output join warehouse w on output.incomer_id = w.id and w.id=:warehouseId where confirmed = :confirmed and output.created_at between :startDate and :endDate",nativeQuery=true)
   int countAllIncomesByConfirmAndDate(@Param(value="confirmed")boolean confirmed, @Param(value="startDate")Timestamp startDate,@Param(value="endDate")Timestamp endDate,@Param(value="warehouseId")Integer warehouseId);


    @Query(value="select output.* from output join warehouse w on output.outputer_id = w.id and w.id=:warehouseId where confirmed = :confirmed and output.created_at between :startDate and :endDate order by output.created_at desc limit :size offset :page*:size",nativeQuery=true)
    List<Output> getAllOutputsByConfirmAndDate(@Param(value="confirmed")boolean confirmed, @Param(value="startDate")Timestamp startDate,@Param(value="endDate")Timestamp endDate,@Param(value="warehouseId")Integer warehouseId,@Param(value="page")Integer page,@Param(value="size")Integer size);


    @Query(value="select  count(output.*) from output join warehouse w on output.outputer_id = w.id and w.id=:warehouseId where confirmed = :confirmed and output.created_at between :startDate and :endDate",nativeQuery=true)
    int countAllOutputsByConfirmAndDate(@Param(value="confirmed")boolean confirmed, @Param(value="startDate")Timestamp startDate,@Param(value="endDate")Timestamp endDate,@Param(value="warehouseId")Integer warehouseId);

    @Query(value="delete from output where id=:outputId",nativeQuery=true)
    void removeInput(@Param(value="outputId")UUID outputId);
}
