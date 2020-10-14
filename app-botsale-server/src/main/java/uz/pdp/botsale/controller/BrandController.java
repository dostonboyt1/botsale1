package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ReqBrand;
import uz.pdp.botsale.service.BrandService;
import uz.pdp.botsale.utils.AppConstants;

import java.net.Inet4Address;

@RestController
@RequestMapping("/api/brand")
public class BrandController {
    @Autowired
    BrandService brandService;

    @PostMapping
    public HttpEntity<?> saveOrEdit(@RequestBody ReqBrand reqBrand) {
        ApiResponse response = brandService.saveOrEdit(reqBrand);
        return ResponseEntity.status(response.isSuccess() ? response.getMessage().equals("Saved") ? HttpStatus.CREATED : HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/byPageable")
    public HttpEntity<?> getByPageable(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                       @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        return ResponseEntity.ok(brandService.getByPageable(page, size));
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> removeBrand(@PathVariable Integer id) {
        ApiResponse response = brandService.removeBrand(id);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/changeActive")
    public HttpEntity<?> changeActive(@RequestParam Integer id, @RequestParam boolean active) {
        return ResponseEntity.ok(brandService.changeActive(id, active));
    }

    @GetMapping("/all")
    public ApiResponseModel getAll() {
        return brandService.getAll();
    }
}
