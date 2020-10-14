package uz.pdp.botsale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.botsale.entity.PayType;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ReqPayType;
import uz.pdp.botsale.repository.PayTypeRepository;

import java.util.List;

@Service
public class PayTypeService {
    @Autowired
    PayTypeRepository payTypeRepository;


    public ApiResponse saveOrEdit(ReqPayType reqPayType) {
        ApiResponse apiResponse=new ApiResponse();
        try {
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Saved");
            PayType payType=new PayType();
            if (reqPayType.getId()!=null){
                payType=payTypeRepository.findById(reqPayType.getId()).orElseThrow(() -> new ResourceNotFoundException("payType","id",reqPayType.getId()));
                apiResponse.setMessage("Edited");
            }
            payType.setNameUz(reqPayType.getNameUz());
            payType.setNameRu(reqPayType.getNameRu());
            payTypeRepository.save(payType);
        }catch (Exception e){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error");
        }
        return apiResponse;
    }

    public ApiResponse removePayType(Integer id) {
        try {
            payTypeRepository.deleteById(id);
            return new ApiResponse("Deleted",true);
        }catch (Exception e){
            return new ApiResponse("Error",false);
        }
    }

    public ApiResponse blockOrActivate(Integer id, boolean ketmon) {
        try {
            PayType payType = payTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("payType", "id", id));
            payType.setActive(ketmon);
            payTypeRepository.save(payType);
            return new ApiResponse(ketmon?"Activated":"Blocked",true);
        }catch (Exception e){
            return new ApiResponse("Error",false);
        }
    }

    public ApiResponseModel getAll(Boolean active) {
        List<PayType> all = payTypeRepository.findAll();
        if (active!=null){
           all=payTypeRepository.findAllByActive(active);
        }
        return new ApiResponseModel(true,"Ok",all);
    }
}
