package uz.pdp.botsale.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uz.pdp.botsale.entity.Region;
import uz.pdp.botsale.projection.CustomRegion;

@RepositoryRestResource(path = "region",collectionResourceRel = "list", excerptProjection = CustomRegion.class)
public interface RegionRepository extends JpaRepository<Region, Integer> {

}
