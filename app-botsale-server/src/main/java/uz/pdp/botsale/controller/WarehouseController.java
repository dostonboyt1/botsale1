package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.botsale.entity.User;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ReqWarehouse;
import uz.pdp.botsale.repository.DistrictRepository;
import uz.pdp.botsale.repository.RegionRepository;
import uz.pdp.botsale.security.CurrentUser;
import uz.pdp.botsale.service.WarehouseService;
import uz.pdp.botsale.utils.AppConstants;

@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {
    @Autowired
    WarehouseService warehouseService;

    @Autowired
    RegionRepository regionRepository;

    @Autowired
    DistrictRepository districtRepository;

    @PostMapping
    public HttpEntity<?> saveOrEdit(@RequestBody ReqWarehouse reqWarehouse){
        ApiResponse response = warehouseService.saveOrEdit(reqWarehouse);
        return ResponseEntity.status(response.isSuccess()?response.getMessage().equals("Saved")? HttpStatus.CREATED:HttpStatus.ACCEPTED:HttpStatus.CONFLICT).body(response);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> removeWarehouse(@PathVariable Integer id){
        ApiResponse response = warehouseService.removeWarehouse(id);
        return ResponseEntity.status(response.isSuccess()?HttpStatus.OK:HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/changeActive")
    public HttpEntity<?> changeActive(@RequestParam Integer id,@RequestParam boolean active){
        return ResponseEntity.ok(warehouseService.changeActive(id,active));
    }

    @GetMapping("/all")
    public HttpEntity<?> getAll() {
        return ResponseEntity.ok(warehouseService.getAll());
    }

    @GetMapping("/allRegion")
    public HttpEntity<?> getAllRegion() {
        return ResponseEntity.ok(regionRepository.findAll());
    }

    @GetMapping("/allDistrict")
    public HttpEntity<?> getAllDistrict(@RequestParam Integer id) {
        return ResponseEntity.ok(districtRepository.findAllByRegionId(id));
    }

    @GetMapping("/getAmount")
    public HttpEntity<?> getAmount(@RequestParam(value = "page",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                   @RequestParam(value = "size",defaultValue = AppConstants.DEFAULT_PAGE_SIZE)Integer size,
                                   @RequestParam(value="search",defaultValue = "all")String search,
                                   @CurrentUser User user){
        return ResponseEntity.ok(warehouseService.getAmount(page,size,search,user));
    }

    @GetMapping("/getAllWarehouseWithoutOutputer")
    public ApiResponseModel getAllWarehouseWithoutOutputer(@CurrentUser User user){
        return warehouseService.getAllWarehouseWithoutOutputer(user);
    }
}
