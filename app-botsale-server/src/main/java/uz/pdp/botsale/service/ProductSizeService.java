package uz.pdp.botsale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.pdp.botsale.entity.Brand;
import uz.pdp.botsale.entity.ProductSize;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.payload.*;
import uz.pdp.botsale.repository.BrandRepository;
import uz.pdp.botsale.repository.ProductSizeRepository;
import uz.pdp.botsale.utils.CommonUtils;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProductSizeService {
    @Autowired
    ProductSizeRepository productSizeRepository;


    public ApiResponse saveOrEdit(ReqProductSize reqProductSize) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            if (!productSizeRepository.existsByName(reqProductSize.getName())) {
                apiResponse.setMessage("Saved");
                apiResponse.setSuccess(true);
                ProductSize productSize = new ProductSize();
                if (reqProductSize.getId() != null) {
                    apiResponse.setMessage("Edited");
                    productSize = productSizeRepository.findById(reqProductSize.getId()).orElseThrow(() -> new ResourceNotFoundException("productSize", "id", reqProductSize.getId()));
                }
                productSize.setName(reqProductSize.getName());
                productSizeRepository.save(productSize);
            } else {
                apiResponse.setMessage("Bunday nomli o'lcham mavjud");
                apiResponse.setSuccess(false);
            }
        } catch (Exception e) {
            apiResponse.setMessage("Error");
            apiResponse.setSuccess(false);
        }
        return apiResponse;
    }

    public ResPageable getByPageable(Integer page, Integer size) {
        Page<ProductSize> productSizePage = productSizeRepository.findAll(CommonUtils.getPageableById(page, size));
        return new ResPageable(productSizePage, productSizePage.getTotalElements(), page);
    }

    public ApiResponse removeBrand(UUID id) {
        try {
            productSizeRepository.deleteById(id);
            return new ApiResponse("Deleted", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }

    public ApiResponseModel getAll() {
        return new ApiResponseModel(true, "Ok", productSizeRepository.findAll());
    }
}
