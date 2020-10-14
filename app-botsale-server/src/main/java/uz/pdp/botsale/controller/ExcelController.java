package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.service.ExcelService;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {
    @Autowired
    ExcelService excelService;

    @GetMapping("/{id}")
    public HttpEntity<?>  downloadExcel(@PathVariable Integer id){

        return excelService.downloadProductAmountInfoByWarehouse(id);
    }
}
