package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ReqCustomer;
import uz.pdp.botsale.service.CustomerService;
import uz.pdp.botsale.utils.AppConstants;

import java.util.UUID;

@RestController
@RequestMapping("/api/customer")

public class CustomerController {
    @Autowired
    CustomerService customerService;

    @PostMapping
    public HttpEntity<?> saveOrEdit(@RequestBody ReqCustomer reqCustomer) {
        ApiResponse response = customerService.saveOrEdit(reqCustomer);
        return ResponseEntity.status(response.isSuccess() ? response.getMessage().equals("Saved") ? HttpStatus.CREATED : HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> removeCustomer(@PathVariable UUID id) {
        return ResponseEntity.ok(customerService.removeCustomer(id));
    }

    @GetMapping("/activateOrBlock/{id}")
    public HttpEntity<?> activateOrBlock(@PathVariable UUID id, @RequestParam boolean active) {
        return ResponseEntity.ok(customerService.activateOrBlock(id, active));
    }

    @GetMapping("/byPageable")
    public HttpEntity<?> getByPageable(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                       @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size,
                                       @RequestParam Boolean active,
                                       @RequestParam(value = "search", defaultValue = "all") String search) {
        return ResponseEntity.ok(customerService.getAllByPageable(page, size, active, search));
    }
}
