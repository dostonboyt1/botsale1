package uz.pdp.botsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import uz.pdp.botsale.entity.District;
import uz.pdp.botsale.projection.CustomDistrict;

import java.util.List;

@RepositoryRestResource(path = "district",collectionResourceRel = "list", excerptProjection = CustomDistrict.class)
public interface DistrictRepository extends JpaRepository<District, Integer> {
    List<District> findAllByRegionId(Integer region_id);


}
