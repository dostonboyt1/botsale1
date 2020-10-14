package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardCoontroller {
    @Autowired
    DashboardService dashboardService;

    @GetMapping("/warehouseBalance")
    public ApiResponseModel getWarehouseBalance(){
        return dashboardService.getWarehouseBalance();
    }
}
