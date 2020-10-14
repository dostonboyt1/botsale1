package uz.pdp.botsale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.pdp.botsale.entity.Customer;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ReqCustomer;
import uz.pdp.botsale.payload.ResPageable;
import uz.pdp.botsale.repository.CustomerRepository;
import uz.pdp.botsale.utils.CommonUtils;

import java.util.UUID;

@Service
public class CustomerService {
    @Autowired
    CustomerRepository customerRepository;


    public ApiResponse saveOrEdit(ReqCustomer reqCustomer) {
        ApiResponse apiResponse=new ApiResponse();
        try {
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Saved");
            Customer customer=new Customer();
            if (reqCustomer.getId()!=null){
                customer=customerRepository.findById(reqCustomer.getId()).orElseThrow(() -> new ResourceNotFoundException("customer","id",reqCustomer.getId()));
                apiResponse.setMessage("Edited");
            }
            customer.setFirsName(reqCustomer.getFirsName());
            customer.setLastName(reqCustomer.getLastName());
            customer.setPhoneNumber(reqCustomer.getPhoneNumber());
            if (reqCustomer.getAddress()!=null){
                customer.setAddress(reqCustomer.getAddress());
            }
            customerRepository.save(customer);
        }catch (Exception e){
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error");
        }
        return apiResponse;
    }


    public ApiResponse removeCustomer(UUID id) {
        try {
            customerRepository.deleteById(id);
            return new ApiResponse("Deleted",true);
        }catch (Exception e){
            return new ApiResponse("Error",false);
        }
    }

    public ApiResponse activateOrBlock(UUID id, boolean active) {
        try {
            Customer customer = customerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("customer", "id", id));
            customer.setActive(active);
            customerRepository.save(customer);
            return new ApiResponse(active?"Activated":"Blocked",true);
        }catch (Exception e){
            return new ApiResponse("Error",false);
        }
    }

    public ResPageable getAllByPageable(Integer page, Integer size, Boolean active, String search) {
        Page<Customer> customerPage = customerRepository.findAll(CommonUtils.getPageable(page, size));
        if (!search.equals("all")){
            customerPage=customerRepository.findAllByPhoneNumberContainingIgnoreCase(search,CommonUtils.getPageable(page,size));
        }
        if (active!=null){
            customerPage=customerRepository.findAllByActive(active,CommonUtils.getPageable(page,size));
        }
        return new  ResPageable(customerPage,customerPage.getTotalElements(),page);
    }
}
