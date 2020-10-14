package uz.pdp.botsale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.pdp.botsale.entity.Product;
import uz.pdp.botsale.entity.ProductSize;
import uz.pdp.botsale.entity.User;
import uz.pdp.botsale.entity.Warehouse;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.payload.*;
import uz.pdp.botsale.repository.*;
import uz.pdp.botsale.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WarehouseService {
    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DistrictRepository districtRepository;

    @Autowired
    UserService userService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @Autowired
    ProductWithAmountRepository productWithAmountRepository;


    public ApiResponse saveOrEdit(ReqWarehouse reqWarehouse) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Saved");
            Warehouse warehouse = new Warehouse();
            if (reqWarehouse.getId() != null) {
                warehouse = warehouseRepository.findById(reqWarehouse.getId()).orElseThrow(() -> new ResourceNotFoundException("warehouse", "id", reqWarehouse.getId()));
                apiResponse.setMessage("Edited");
            }
            warehouse.setNameUz(reqWarehouse.getName());
            warehouse.setNameRu(reqWarehouse.getName());
            warehouse.setAddress(reqWarehouse.getAddress());
            warehouse.setDistrict(districtRepository.findById(reqWarehouse.getDistrictId()).orElseThrow(() -> new ResourceNotFoundException("district", "id", reqWarehouse.getDistrictId())));
            warehouse.setUser(userRepository.findById(reqWarehouse.getUserId()).orElseThrow(() -> new ResourceNotFoundException("user", "id", reqWarehouse.getUserId())));
            warehouse.setWarehouseStatus(reqWarehouse.getWarehouseStatus());
            warehouseRepository.save(warehouse);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error");
        }
        return apiResponse;
    }

    public ApiResponse removeWarehouse(Integer id) {
        try {
            warehouseRepository.deleteById(id);
            return new ApiResponse("Deleted", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }

    public ApiResponse changeActive(Integer id, boolean active) {
        Optional<Warehouse> optionalWarehouse = warehouseRepository.findById(id);
        if (optionalWarehouse.isPresent()) {
            Warehouse warehouse = optionalWarehouse.get();
            warehouse.setActive(active);
            return new ApiResponse(active ? "Activated" : "Blocked", true);
        }
        return new ApiResponse("Error", false);
    }

    public ResWarehouse getResWarehouse(Warehouse warehouse) {
        ResWarehouse resWarehouse = new ResWarehouse();
        resWarehouse.setId(warehouse.getId());
        resWarehouse.setName(warehouse.getNameUz());
        resWarehouse.setAddress(warehouse.getAddress());
        resWarehouse.setWarehouseStatus(warehouse.getWarehouseStatus());
        resWarehouse.setDistrictId(warehouse.getDistrict().getId());
        resWarehouse.setRegionId(warehouse.getDistrict().getRegion().getId());
        resWarehouse.setDistrictName(warehouse.getDistrict().getNameUz());
        resWarehouse.setRegionName(warehouse.getDistrict().getRegion().getNameUz());
        resWarehouse.setResUser(userService.getResUser(warehouse.getUser()));
        return resWarehouse;
    }


    public ResPageable getAmount(Integer page, Integer size, String search, User user) {
        List<ResAmount> resAmountList = new ArrayList<>();
        List<ResProduct> resProductList = new ArrayList<>();
        long totalElement = 0;
        if (search.equals("all")) {
            Page<Product> productPage = productRepository.findAll(CommonUtils.getPageable(page, size));
            totalElement = productPage.getTotalElements();
            for (Product product : productPage) {
                resProductList.add(productService.getResProduct(product));
            }
        } else {
            List<Product> bySearch = productRepository.findAllByNameUzStartingWithIgnoreCaseOrNameRuStartingWithIgnoreCaseOrBrandNameUzStartingWithIgnoreCaseOrBrandNameRuStartingWithIgnoreCaseOrCategoryNameUzStartingWithIgnoreCaseOrCategoryNameRuStartingWithIgnoreCase(search, search, search, search, search, search);
            totalElement = bySearch.size();
            for (Product product : bySearch) {
                resProductList.add(productService.getResProduct(product));
            }
        }
        for (ResProduct resProduct : resProductList) {
            for (ProductSize productSize : resProduct.getProductSizeList()) {
                ResAmount resAmount = new ResAmount();
                resAmount.setResProduct(resProduct);
                resAmount.setAmount(productWithAmountRepository.getAmountByWarehouseAndProduct(user.getId(), resProduct.getId(), productSize.getId()));
                resAmount.setProductSize(productSize);
                resAmountList.add(resAmount);
            }
        }
        return new ResPageable(resAmountList,totalElement,page);
    }

    public List<ResWarehouse> getAll() {
        return warehouseRepository.findAll().stream().map(this::getResWarehouse).collect(Collectors.toList());
    }

    public ApiResponseModel getAllWarehouseWithoutOutputer(User user) {
        Warehouse byUser = warehouseRepository.findByUser(user);
        List<Warehouse> all = warehouseRepository.findAll();
        return new ApiResponseModel(true,"Ok",all.stream().filter(item -> !item.getId().equals(byUser.getId())).map(this::getResWarehouse).collect(Collectors.toList()));
    }
}
