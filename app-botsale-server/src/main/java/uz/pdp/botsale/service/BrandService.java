package uz.pdp.botsale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.pdp.botsale.entity.Brand;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ReqBrand;
import uz.pdp.botsale.payload.ResPageable;
import uz.pdp.botsale.repository.BrandRepository;
import uz.pdp.botsale.utils.CommonUtils;

import java.util.Optional;

@Service
public class BrandService {
    @Autowired
    BrandRepository brandRepository;


    public ApiResponse saveOrEdit(ReqBrand reqBrand) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setMessage("Saved");
            apiResponse.setSuccess(true);
            Brand brand = new Brand();
            if (reqBrand.getId() != null) {
                apiResponse.setMessage("Edited");
                brand = brandRepository.findById(reqBrand.getId()).orElseThrow(() -> new ResourceNotFoundException("brand", "id", reqBrand.getId()));
            }
            brand.setNameUz(reqBrand.getNameUz());
            brand.setNameRu(reqBrand.getNameRu());
            brandRepository.save(brand);
        } catch (Exception e) {
            apiResponse.setMessage("Error");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ResPageable getByPageable(Integer page, Integer size) {
        Page<Brand> brandPage = brandRepository.findAll(CommonUtils.getPageableById(page, size));
        return new ResPageable(brandPage, brandPage.getTotalElements(), page);
    }

    public ApiResponse removeBrand(Integer id) {
        try {
            brandRepository.deleteById(id);
            return new ApiResponse("Deleted", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }

    public ApiResponse changeActive(Integer id, boolean active) {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isPresent()) {
            Brand brand = optionalBrand.get();
            brand.setActive(active);
            brandRepository.save(brand);
            return new ApiResponse(active ? "Activated" : "Blocked", true);
        }
        return new ApiResponse("Error", false);
    }

    public ApiResponseModel getAll() {
        return new ApiResponseModel(true, "Ok", brandRepository.findAll());
    }
}
