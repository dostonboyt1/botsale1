package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ReqRegion;
import uz.pdp.botsale.service.RegionService;

@RestController
@RequestMapping("/api/region")
public class RegionController {

    @Autowired
    RegionService regionService;

    @PostMapping
    public HttpEntity<?> saveOrEdit(@RequestBody ReqRegion reqRegion) {
        ApiResponse response = regionService.saveOrEdit(reqRegion);
        return ResponseEntity.status(response.isSuccess() ? response.getMessage().equals("Saved") ? HttpStatus.CREATED : HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/all")
    public ApiResponseModel getAll() {
        return regionService.getAll();
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> deleteRegion(@PathVariable Integer id) {
        ApiResponse response = regionService.deleteRegion(id);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.CONFLICT).body(response);

    }

}
