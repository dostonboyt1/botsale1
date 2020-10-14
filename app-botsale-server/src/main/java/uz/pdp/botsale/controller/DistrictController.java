package uz.pdp.botsale.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ReqDistrict;
import uz.pdp.botsale.service.DistrictService;
import uz.pdp.botsale.utils.AppConstants;

@RestController
@RequestMapping("/api/district")
public class DistrictController {
    @Autowired
    DistrictService districtService;

    @PostMapping
    public HttpEntity<?> saveOrEdit(@RequestBody ReqDistrict reqDistrict){
        ApiResponse response=districtService.saveOrEdit(reqDistrict);
        return ResponseEntity.status(response.isSuccess()?response.getMessage().equals("Saved")? HttpStatus.CREATED:HttpStatus.ACCEPTED:HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/getAll")
    public ApiResponseModel getAll(){
        return districtService.getAll();
    }

    @GetMapping("/getPagaeble")
    public HttpEntity<?> getByPageble(@RequestParam(value = "page",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                      @RequestParam(value = "size",defaultValue = AppConstants.DEFAULT_PAGE_SIZE)Integer size){
        return ResponseEntity.ok(districtService.getByPegaeble(page,size));
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteDistrict(@PathVariable Integer id) {
        ApiResponse response = districtService.deleteDistrict(id);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(response);

    }

}
