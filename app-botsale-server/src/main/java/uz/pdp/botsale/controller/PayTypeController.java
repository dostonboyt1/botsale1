package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ReqPayType;
import uz.pdp.botsale.service.PayTypeService;

@RestController
@RequestMapping("/api/payType")
public class PayTypeController {
    @Autowired
    PayTypeService payTypeService;

    @PostMapping
    public HttpEntity<?> saveOrEdit(@RequestBody ReqPayType reqPayType){
        ApiResponse response = payTypeService.saveOrEdit(reqPayType);
        return ResponseEntity.status(response.isSuccess()?response.getMessage().equals("Saved")? HttpStatus.CREATED:HttpStatus.ACCEPTED:HttpStatus.CONFLICT).body(response);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> removePayType(@PathVariable Integer id){
        return ResponseEntity.ok(payTypeService.removePayType(id));
    }

    @GetMapping("/blockOrActivate/{id}")
    public HttpEntity<?> blockOrActivate(@PathVariable Integer id,@RequestParam boolean active){
        return ResponseEntity.ok(payTypeService.blockOrActivate(id,active));
    }

    @GetMapping
    public ApiResponseModel getAll(@RequestParam Boolean active){
        return payTypeService.getAll(active);
    }

}
