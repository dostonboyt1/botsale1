package uz.pdp.botsale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.pdp.botsale.entity.*;
import uz.pdp.botsale.entity.enums.OrderStatus;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.payload.*;
import uz.pdp.botsale.repository.*;
import uz.pdp.botsale.utils.CommonUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PayTypeRepository payTypeRepository;

    @Autowired
    ProductWithAmountRepository productWithAmountRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductSizeRepository productSizeRepository;

    @Autowired
    OutputInputService outputInputService;

    @Autowired
    UserService userService;

    @Autowired
    WarehouseService warehouseService;

    public ApiResponse saveOrEdit(ReqOrder reqOrder, User user) {
        ApiResponse apiResponse = new ApiResponse();
        try {
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Saved");

            Orders orders = new Orders();
            if (reqOrder.getId() != null) {
                orders = ordersRepository.findById(reqOrder.getId()).orElseThrow(() -> new ResourceNotFoundException("order", "id", reqOrder.getId()));
                apiResponse.setMessage("Edited");
            }
            Customer customer = new Customer();
            if (reqOrder.getCustomerId() != null) {
                customer = customerRepository.findById(reqOrder.getCustomerId()).orElseThrow(() -> new ResourceNotFoundException("customer", "id", reqOrder.getCustomerId()));
            }
            Optional<Customer> optionalCustomer = customerRepository.findByPhoneNumberContaining(reqOrder.getCustomerPhoneNumber());
            if (optionalCustomer.isPresent()) {
                customer = optionalCustomer.get();
            }
            customer.setPhoneNumber(reqOrder.getCustomerPhoneNumber());
            customer.setFirsName(reqOrder.getCustomerFirstName());
            customer.setLastName(reqOrder.getCustomerLastName());
            customer.setAddress(reqOrder.getCustomerAddress());
            Customer savedCustomer = customerRepository.save(customer);

            orders.setCustomer(savedCustomer);
            orders.setAddress(reqOrder.getOrderAddress());
            orders.setPayType(payTypeRepository.findById(reqOrder.getPayTypeId()).orElseThrow(() -> new ResourceNotFoundException("payType", "id", reqOrder.getPayTypeId())));
            orders.setOrderStatus(reqOrder.getOrderStatus());
            orders.setPayStatus(reqOrder.getPayStatus());
            orders.setUser(user);
            Orders savedOrder = ordersRepository.save(orders);
            double orderSum = 0;
            for (ReqProductWithAmount reqProductWithAmount : reqOrder.getReqProductWithAmountList()) {
                ProductWithAmount productWithAmount = new ProductWithAmount();
                if (reqProductWithAmount.getId() != null) {
                    productWithAmount = productWithAmountRepository.findById(reqProductWithAmount.getId()).orElseThrow(() -> new ResourceNotFoundException("productWithAmount", "id", reqProductWithAmount.getId()));
                }
                productWithAmount.setOrders(savedOrder);
                Product product = productRepository.findById(reqProductWithAmount.getProductId()).orElseThrow(() -> new ResourceNotFoundException("product", "id", reqProductWithAmount.getProductId()));
                productWithAmount.setProduct(product);
                productWithAmount.setProductSize(productSizeRepository.findById(reqProductWithAmount.getSize()).orElseThrow(() -> new ResourceNotFoundException("size", "id", reqProductWithAmount.getSize())));
                productWithAmount.setAmount(reqProductWithAmount.getAmount());
                productWithAmountRepository.save(productWithAmount);
                orderSum += product.getSalePrice() * reqProductWithAmount.getAmount();
            }
            savedOrder.setOrderSum(orderSum);
            ordersRepository.save(savedOrder);
        } catch (Exception e) {
            apiResponse.setSuccess(false);
            apiResponse.setMessage("Error");
        }
        return apiResponse;
    }

    public ResPageable getByOrderStatus(OrderStatus orderStatus, Integer page, Integer size) {
        Page<Orders> ordersList = ordersRepository.findAllByOrderStatus(orderStatus, CommonUtils.getPageable(page, size));
        return new ResPageable(ordersList.stream().map(this::getResOrder).collect(Collectors.toList()), ordersList.getTotalElements(), page);
    }

    public ResOrder getResOrder(Orders orders) {
        ResOrder resOrder = new ResOrder();
        resOrder.setId(orders.getId());
        resOrder.setCreatedAt(orders.getCreatedAt());
        if (orders.getAddress() != null) {
            resOrder.setOrderAddress(orders.getAddress());
        }
        if (orders.getLan() != null && orders.getLat() != null) {
            resOrder.setLan(orders.getLan());
            resOrder.setLat(orders.getLat());
        }
        resOrder.setCustomer(orders.getCustomer());
        resOrder.setFromBot(orders.isFromBot());
        resOrder.setOrderStatus(orders.getOrderStatus());
        resOrder.setOrderSum(orders.getOrderSum());
        resOrder.setPayStatus(orders.getPayStatus());
        resOrder.setPayType(orders.getPayType());
        resOrder.setResProductWithAmountList(orders.getPRoductWithAmountList().stream().map(item -> outputInputService.getResProductWithAmount(item)).collect(Collectors.toList()));
        if (orders.getUser() != null) {
            resOrder.setResUser(userService.getResUser(orders.getUser()));
        }
        if (orders.getWarehouse() != null) {
            resOrder.setResWarehouse(warehouseService.getResWarehouse(orders.getWarehouse()));
        }
        return resOrder;
    }

    public ApiResponse changeOrderStatus(Integer id, OrderStatus orderStatus) {
        try {
            Orders orders = ordersRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("order", "id", id));
            orders.setOrderStatus(orderStatus);
            ordersRepository.save(orders);
            return new ApiResponse("Changed", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }

    public ApiResponseModel getBySearch(String search, Integer id, Timestamp startDate, Timestamp endDate, Integer page, Integer size) {
        if (search.equals("all") && id == 0) {
            Page<Orders> ordersPage = ordersRepository.findAllByCreatedAtBetween(startDate, endDate, CommonUtils.getPageable(page, size));
            return new ApiResponseModel(true, "all", ordersPage.stream().map(this::getResOrder).collect(Collectors.toList()));
        }
        if (!search.equals("all")) {
            List<Orders> ordersList = ordersRepository.findAllByCustomerPhoneNumber(search);
            return new ApiResponseModel(true, "byPhoneNumber", ordersList.stream().map(this::getResOrder).collect(Collectors.toList()));
        }
            if (id > 0) {
                Optional<Orders> optionalOrders = ordersRepository.findById(id);
                if (optionalOrders.isPresent()) {
                    return new ApiResponseModel(true, "byId", getResOrder(optionalOrders.get()));
                }
            }
        return new ApiResponseModel(false, "Error", null);
    }

    public ApiResponse removeOrder(Integer id) {
        try {
            ordersRepository.deleteById(id);
            return new ApiResponse("Deleted", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }

    @Transactional
    public ApiResponse cancelOrder(ReqCancelOrder reqCancelOrder) {
        try {
            if (reqCancelOrder.isFullCancel()) {
                for (ReqProductWithAmount reqProductWithAmount : reqCancelOrder.getReqProductWithAmountList()) {
                    ProductWithAmount productWithAmount = productWithAmountRepository.findById(reqProductWithAmount.getId()).orElseThrow(() -> new ResourceNotFoundException("productWithAmount", "id", reqProductWithAmount.getId()));
                    Orders orders = productWithAmount.getOrders();
                    orders.setOrderStatus(OrderStatus.CANCELED);
                    ordersRepository.save(orders);
                    productWithAmount.setCanceledAmount(productWithAmount.getAmount());
                    productWithAmountRepository.save(productWithAmount);
                }
            } else {
                for (ReqProductWithAmount reqProductWithAmount : reqCancelOrder.getReqProductWithAmountList()) {
                    ProductWithAmount productWithAmount = productWithAmountRepository.findById(reqProductWithAmount.getId()).orElseThrow(() -> new ResourceNotFoundException("productWithAmount", "id", reqProductWithAmount.getId()));
                    Orders orders = productWithAmount.getOrders();
                    orders.setOrderStatus(OrderStatus.PARTLY_CANCELED);
                    ordersRepository.save(orders);
                    productWithAmount.setCanceledAmount(reqProductWithAmount.getCanceledAmount());
                    productWithAmountRepository.save(productWithAmount);
                }
            }
            return new ApiResponse("Ok", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }

    }

    public ResPageable getSendOrdersByWarehouse(Integer page, Integer size, User user) {
        Page<Orders> orders = ordersRepository.findAllByWarehouseUser(user, CommonUtils.getPageable(page, size));
        return new ResPageable(orders.stream().map(this::getResOrder).collect(Collectors.toList()), orders.getTotalElements(), page);
    }

    public ApiResponse changeToSend(Integer id, User user) {
        try {
            Orders orders = ordersRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("order", "id", id));
            Warehouse warehouse = warehouseRepository.findByUser(user);
            orders.setOrderStatus(OrderStatus.SEND);
            orders.setWarehouse(warehouse);
            ordersRepository.save(orders);
            return new ApiResponse("Sended", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }
}
