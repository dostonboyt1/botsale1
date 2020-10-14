package uz.pdp.botsale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ResWarehouse;
import uz.pdp.botsale.payload.ResWarehouseBalance;
import uz.pdp.botsale.repository.WarehouseRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {
    @Autowired
    WarehouseRepository warehouseRepository;
    public ApiResponseModel getWarehouseBalance() {
        List<ResWarehouseBalance> resWarehouseBalanceList=new ArrayList<>();
        List<Object[]> objects= warehouseRepository.getWarehouseBalance();
        for (Object[] object : objects) {
            ResWarehouseBalance resWarehouseBalance=new ResWarehouseBalance();
            resWarehouseBalance.setWarehouseId(Integer.parseInt(object[0].toString()));
            resWarehouseBalance.setWarehouseName(object[1].toString());
            resWarehouseBalance.setWarehouseBalance(Double.parseDouble(object[2].toString()));
            resWarehouseBalanceList.add(resWarehouseBalance);
        }
       return new ApiResponseModel(true,"Ok",resWarehouseBalanceList);
    }

}
