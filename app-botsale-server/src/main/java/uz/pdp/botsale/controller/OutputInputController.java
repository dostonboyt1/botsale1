package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.botsale.entity.User;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ReqInput;
import uz.pdp.botsale.security.CurrentUser;
import uz.pdp.botsale.service.OutputInputService;
import uz.pdp.botsale.utils.AppConstants;

import java.sql.Timestamp;
import java.util.UUID;

@RestController
@RequestMapping("/api/output")
public class OutputInputController {
    @Autowired
    OutputInputService outputInputService;

    @PostMapping("/inputOrOutput")
    public HttpEntity<?> inputToWarehouse(@RequestBody ReqInput reqInput, @CurrentUser User user) {
        ApiResponse response = outputInputService.inputToWarehouse(reqInput, user);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/byConfirmedAndIsIncome")
    public HttpEntity<?> byConfirmedAndIsIncome(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
                                                @RequestParam(value = "confirmed") boolean confirmed,
                                                @RequestParam(value = "isIncome") boolean isIncome,
                                                @RequestParam(value = "startDate",defaultValue = AppConstants.BEGIN_DATE)Timestamp startDate,
                                                @RequestParam(value = "endDate",defaultValue = AppConstants.END_DATE)Timestamp endDate,
                                                @CurrentUser User user) {
        return ResponseEntity.ok(outputInputService.byConfirmedAndIsIncome(page, size, confirmed, isIncome,startDate,endDate,user));
    }

    @GetMapping("/confirm/{id}")
    public HttpEntity<?> confirmIncome(@PathVariable UUID id){
        return ResponseEntity.ok(outputInputService.confirmIncome(id));
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> removeOutput(@PathVariable UUID id){
        return ResponseEntity.ok(outputInputService.removeOutput(id));
    }
}
