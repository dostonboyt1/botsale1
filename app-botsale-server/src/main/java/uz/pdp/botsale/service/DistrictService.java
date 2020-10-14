package uz.pdp.botsale.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.pdp.botsale.entity.District;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ReqDistrict;
import uz.pdp.botsale.payload.ResPageable;
import uz.pdp.botsale.repository.DistrictRepository;
import uz.pdp.botsale.repository.RegionRepository;
import uz.pdp.botsale.utils.CommonUtils;

@Service
public class DistrictService {

    private final DistrictRepository districtRepository;

    private final RegionRepository regionRepository;


    public DistrictService(DistrictRepository districtRepository, RegionRepository regionRepository) {
        this.districtRepository = districtRepository;
        this.regionRepository = regionRepository;
    }

    public ApiResponse saveOrEdit(ReqDistrict reqDistrict) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Saved");
            District district = new District();
            if (reqDistrict.getId() != null) {
                district = districtRepository.findById(reqDistrict.getId()).orElseThrow(() -> new ResourceNotFoundException("district", "id", reqDistrict.getId()));
                apiResponse.setMessage("Edited");
            }
            if (reqDistrict.getRegionId() != null) {
                district.setRegion(regionRepository.findById(reqDistrict.getRegionId()).orElseThrow(() -> new ResourceNotFoundException("regionId", "id", reqDistrict.getRegionId())));
            }
            district.setNameUz(reqDistrict.getNameUz());
            district.setNameRu(reqDistrict.getNameRu());
            districtRepository.save(district);
        } catch (Exception e) {
            apiResponse.setMessage("Error");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ApiResponseModel getAll() {
        return new ApiResponseModel(true, "Ok", districtRepository.findAll());
    }

    public ApiResponse deleteDistrict(Integer id) {
        try {
            districtRepository.deleteById(id);
            return new ApiResponse("Deleted", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }

    public ResPageable getByPegaeble(Integer page, Integer size) {
        Page<District> pageDistrict = districtRepository.findAll(CommonUtils.getPageableById(page, size));
        return new ResPageable(pageDistrict.getContent(),pageDistrict.getTotalElements(),page);
    }
}
