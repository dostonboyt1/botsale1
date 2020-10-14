package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.botsale.entity.User;
import uz.pdp.botsale.entity.enums.OrderStatus;
import uz.pdp.botsale.payload.*;
import uz.pdp.botsale.security.CurrentUser;
import uz.pdp.botsale.service.OrderService;
import uz.pdp.botsale.utils.AppConstants;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping
    public HttpEntity<?> saveOrEdit(@RequestBody ReqOrder reqOrder, @CurrentUser User user) {
        ApiResponse response = orderService.saveOrEdit(reqOrder, user);
        return ResponseEntity.status(response.isSuccess() ? response.getMessage().equals("Saved") ? HttpStatus.CREATED : HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/byORderStatus")
    public HttpEntity<?> getORdersByOrderStatus(@RequestParam(value = "orderStatus") OrderStatus orderStatus,
                                                @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        return ResponseEntity.ok(orderService.getByOrderStatus(orderStatus, page, size));
    }

    @GetMapping("/bySendOrdersByWarehouse")
    public HttpEntity<?> getSendOrdersByWarehouse(@CurrentUser User user,
                                                  @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                                  @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        return ResponseEntity.ok(orderService.getSendOrdersByWarehouse(page, size, user));
    }

    @GetMapping("/changeOrderStatus")
    public HttpEntity<?> changeOrderStatus(@RequestParam Integer id, @RequestParam OrderStatus orderStatus) {
        ApiResponse response = orderService.changeOrderStatus(id, orderStatus);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/changeToSend/{id}")
    public HttpEntity<?> changeToSend(@PathVariable Integer id, @CurrentUser User user) {
        ApiResponse response = orderService.changeToSend(id, user);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(response);
    }

    @GetMapping("/bySearch")
    public ApiResponseModel getBySearch(@RequestParam(value = "search", defaultValue = "all") String search,
                                        @RequestParam(value = "id", defaultValue = "0") Integer id,
                                        @RequestParam(value = "startDate", defaultValue = AppConstants.BEGIN_DATE) Timestamp startDate,
                                        @RequestParam(value = "endDate", defaultValue = AppConstants.END_DATE) Timestamp endDate,
                                        @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                        @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer size) {
        return orderService.getBySearch(search, id, startDate, endDate, page, size);
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> removeOrder(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.removeOrder(id));
    }

    @PostMapping("/cancelOrder")
    public HttpEntity<?> cancelOrder(@RequestBody ReqCancelOrder reqCancelOrder) {
        return ResponseEntity.ok(orderService.cancelOrder(reqCancelOrder));
    }

}
