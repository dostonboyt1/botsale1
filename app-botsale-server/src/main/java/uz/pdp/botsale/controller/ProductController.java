package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ReqProduct;
import uz.pdp.botsale.service.ProductService;
import uz.pdp.botsale.utils.AppConstants;

import java.util.UUID;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping
    public HttpEntity<?> saveOrEdit(@RequestBody ReqProduct reqProduct){
        ApiResponse response = productService.saveOrEdit(reqProduct);
        return ResponseEntity.status(response.isSuccess()?response.getMessage().equals("Saved")? HttpStatus.CREATED:HttpStatus.ACCEPTED:HttpStatus.CONFLICT).body(response);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> removeProduct(@PathVariable UUID id){
        ApiResponse response = productService.removeProduct(id);
        return ResponseEntity.status(response.isSuccess()?HttpStatus.OK:HttpStatus.CONFLICT).body(response);
    }



    @GetMapping("/changeActive")
    public HttpEntity<?> changeActive(@RequestParam UUID id,@RequestParam boolean active){
        return ResponseEntity.ok(productService.changeActive(id,active));
    }

    @GetMapping("/search")
    public HttpEntity<?> getBySearch(@RequestParam(value = "page",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                       @RequestParam(value = "size",defaultValue = AppConstants.DEFAULT_PAGE_SIZE)Integer size,
                                       @RequestParam(value = "search",defaultValue = "all")String search){
        return ResponseEntity.ok(productService.getBySearch(page,size,search));
    }

    @GetMapping("/byCatIdOrBrandId")
    public ApiResponseModel getByCatIdOrBrandId(@RequestParam(value = "catId")Integer catId,
                                                @RequestParam(value = "brandId")Integer brandId){
        return productService.getByCatIdOrBrandId(catId,brandId);
    }
    @GetMapping("/sizeListByProduct/{id}")
    public ApiResponseModel sizeListByProduct(@PathVariable UUID id){
        return productService.sizeListByProduct(id);
    }


}
