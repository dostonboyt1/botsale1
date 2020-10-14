package uz.pdp.botsale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.botsale.entity.*;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.payload.*;
import uz.pdp.botsale.repository.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OutputInputService {
    @Autowired
    OutputRepository outputRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    ProductWithAmountRepository productWithAmountRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductSizeRepository productSizeRepository;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    ProductService productService;

    @Autowired
    WarehouseService warehouseService;

    public ApiResponse inputToWarehouse(ReqInput reqInput, User user) {
       try {
           Output output=new Output();
           if (reqInput.getId()!=null){
               output=outputRepository.findById(reqInput.getId()).orElseThrow(() -> new ResourceNotFoundException("output","id",reqInput.getId()));
               List<ProductWithAmount> allByOutput = productWithAmountRepository.findAllByOutput(output);
               for (ProductWithAmount productWithAmount : allByOutput) {
                   productWithAmountRepository.delete(productWithAmount);
               }
           }
           if (reqInput.getIncomerWarehouseId()==null){
               output.setIncomer(warehouseRepository.findByUser(user));
               output.setConfirmed(true);
           }else {
               output.setOutputer(warehouseRepository.findByUser(user));
               output.setConfirmed(false);
               output.setIncomer(warehouseRepository.findById(reqInput.getIncomerWarehouseId()).orElseThrow(() -> new ResourceNotFoundException("incomerWarehouse","id",reqInput.getIncomerWarehouseId())));
           }
           Output savedOutput = outputRepository.save(output);
           for (ReqProductWithAmount reqProductWithAmount : reqInput.getReqProductWithAmountList()) {
               ProductWithAmount productWithAmount=new ProductWithAmount();
               productWithAmount.setOutput(savedOutput);
               productWithAmount.setProduct(productRepository.findById(reqProductWithAmount.getProductId()).orElseThrow(() -> new ResourceNotFoundException("product","id",reqProductWithAmount.getProductId())));
               productWithAmount.setProductSize(productSizeRepository.findById(reqProductWithAmount.getSize()).orElseThrow(() -> new ResourceNotFoundException("size","id",reqProductWithAmount.getSize())));
               productWithAmount.setAmount(reqProductWithAmount.getAmount());
               productWithAmountRepository.save(productWithAmount);
           }
           return new ApiResponse("Ok",true);
       }catch (Exception e){
           return new ApiResponse("Error",false);
       }
    }

    public ResProductWithAmount getResProductWithAmount(ProductWithAmount productWithAmount){
        ResProductWithAmount resProductWithAmount=new ResProductWithAmount();
        resProductWithAmount.setId(productWithAmount.getId());
        resProductWithAmount.setResProduct(productService.getResProduct(productWithAmount.getProduct()));
        resProductWithAmount.setProductSize(productWithAmount.getProductSize());
        resProductWithAmount.setProductListByCategoryAndBrand(productRepository.findAllByCategoryIdAndBrandId(productWithAmount.getProduct().getCategory().getId(), productWithAmount.getProduct().getBrand().getId()).stream().map(item -> productService.getResProduct(item)).collect(Collectors.toList()));
        resProductWithAmount.setAmount(productWithAmount.getAmount());
        return resProductWithAmount;
    }

    public ResOutput getResOutput(Output output){
        ResOutput resOutput=new ResOutput();
        resOutput.setId(output.getId());
        resOutput.setCreatedAt(output.getCreatedAt());
        resOutput.setConfirmed(output.isConfirmed());
        if (output.getIncomer()!=null){
            resOutput.setIncomer(warehouseService.getResWarehouse(output.getIncomer()));
        }
        if (output.getOutputer()==null){
            resOutput.setIncome(true);
        }else {
            resOutput.setOutputer(warehouseService.getResWarehouse(output.getOutputer()));
        }
        List<ProductWithAmount> productWithAmounts = productWithAmountRepository.findAllByOutput(output);
        if (productWithAmounts.size()>0){
            resOutput.setResProductWithAmountList(productWithAmounts.stream().map(this::getResProductWithAmount).collect(Collectors.toList()));
        }
        return resOutput;
    }

    public ResPageable byConfirmedAndIsIncome(Integer page, Integer size, boolean confirmed, boolean isIncome, Timestamp startDate, Timestamp endDate,User user) {
        long totalEmount=0;
        List<ResOutput> resOutputList=new ArrayList<>();
        Warehouse byUser = warehouseRepository.findByUser(user);
        List<Output> outputList=new ArrayList<>();
        if (isIncome){
           outputList = outputRepository.getAllIncomesByConfirmAndDate(confirmed, startDate, endDate, byUser.getId(), page, size);
             totalEmount = outputRepository.countAllIncomesByConfirmAndDate(confirmed, startDate, endDate, byUser.getId());
        }else {
            outputList=outputRepository.getAllOutputsByConfirmAndDate(confirmed,startDate,endDate,byUser.getId(),page,size);
            totalEmount=outputRepository.countAllOutputsByConfirmAndDate(confirmed,startDate,endDate,byUser.getId());
        }
        for (Output output : outputList) {
            resOutputList.add(getResOutput(output));
        }
        return new ResPageable(resOutputList,totalEmount,page);
    }


    public ApiResponse confirmIncome(UUID id) {
        try {
            Output output = outputRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("output", "id", id));
            output.setConfirmed(true);
            outputRepository.save(output);
            return new ApiResponse("Ok" , true);
        }catch (Exception e){
            return new ApiResponse("Error",false);
        }
    }


    public ApiResponse removeOutput(UUID id) {
        try {
            Output output = outputRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("output", "id", id));
            List<ProductWithAmount> allProductWitAmountByOutput = productWithAmountRepository.findAllByOutput(output);
            for (ProductWithAmount productWithAmount : allProductWitAmountByOutput) {
                productWithAmountRepository.delete(productWithAmount);
            }
            outputRepository.removeInput(id);
            return new ApiResponse("Ok",true);
        }catch (Exception e){
            return new ApiResponse("Error",false);
        }
    }
}
