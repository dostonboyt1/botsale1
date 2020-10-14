package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ReqBrand;
import uz.pdp.botsale.payload.ReqProductSize;
import uz.pdp.botsale.service.BrandService;
import uz.pdp.botsale.service.ProductSizeService;
import uz.pdp.botsale.utils.AppConstants;

import java.util.UUID;

@RestController
@RequestMapping("/api/productSize")
public class ProductSizeController {
    @Autowired
    ProductSizeService productSizeService;

    @PostMapping
    public HttpEntity<?> saveOrEdit(@RequestBody ReqProductSize reqProductSize){
        ApiResponse response = productSizeService.saveOrEdit(reqProductSize);
        return ResponseEntity.status(response.isSuccess()?response.getMessage().equals("Saved")? HttpStatus.CREATED:HttpStatus.ACCEPTED:HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/byPageable")
    public HttpEntity<?> getByPageable(@RequestParam(value = "page",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                       @RequestParam(value = "size",defaultValue = AppConstants.DEFAULT_PAGE_SIZE)Integer size){
        return ResponseEntity.ok(productSizeService.getByPageable(page,size));
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> removeBrand(@PathVariable UUID id){
        ApiResponse response = productSizeService.removeBrand(id);
        return ResponseEntity.status(response.isSuccess()?HttpStatus.OK:HttpStatus.CONFLICT).body(response);
    }


    @GetMapping("/all")
    public ApiResponseModel getAll(){
        return productSizeService.getAll();
    }
}
