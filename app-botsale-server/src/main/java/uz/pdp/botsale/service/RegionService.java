package uz.pdp.botsale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.botsale.entity.Region;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.payload.*;
import uz.pdp.botsale.repository.RegionRepository;


@Service
public class RegionService {

    @Autowired
    RegionRepository regionRepository;


    public ApiResponse saveOrEdit(ReqRegion reqRegion) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setMessage("Saved");
            apiResponse.setSuccess(true);
            Region region = new Region();
            if (reqRegion.getId() != null) {
                region = regionRepository.findById(reqRegion.getId()).orElseThrow(() -> new ResourceNotFoundException("region", "id", reqRegion.getId()));
                apiResponse.setMessage("Edited");
            }
            region.setNameUz(reqRegion.getNameUz());
            region.setNameRu(reqRegion.getNameRu());
            regionRepository.save(region);
        } catch (Exception e) {
            apiResponse.setMessage("Error");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponseModel getAll() {
        return new ApiResponseModel(true, "Ok", regionRepository.findAll());

    }

    public ApiResponse deleteRegion(Integer id) {
        try {
            regionRepository.deleteById(id);
            return new ApiResponse("Deleted", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }
}
