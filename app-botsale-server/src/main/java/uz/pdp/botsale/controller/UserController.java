package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.pdp.botsale.entity.User;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ApiResponseModel;
import uz.pdp.botsale.payload.ReqSignUp;
import uz.pdp.botsale.repository.UserRepository;
import uz.pdp.botsale.security.AuthService;
import uz.pdp.botsale.security.CurrentUser;
import uz.pdp.botsale.service.UserService;
import uz.pdp.botsale.utils.AppConstants;

import javax.ws.rs.Path;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AuthService authService;


    @GetMapping("/me")
    public HttpEntity<?> getUser(@CurrentUser User user) {
        return ResponseEntity.ok(new ApiResponseModel(user!=null?true:false, user!=null?"Mana user":"Error", user));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DIRECTOR')")
    @PostMapping
    public HttpEntity<?> createUser(@RequestBody ReqSignUp reqUser) {
        ApiResponse response = userService.addUser(reqUser);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(response.getMessage(), true));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(response.getMessage(), false));
    }

    @GetMapping("/byPageable")
    public HttpEntity<?> getByPageable(@RequestParam(value = "page",defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer page,
                                       @RequestParam(value = "size",defaultValue = AppConstants.DEFAULT_PAGE_SIZE)Integer size){
        return ResponseEntity.ok(userService.getByPageable(page,size));
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_DIRECTOR')")
    @GetMapping
    public HttpEntity<?> getUsers() {
        return ResponseEntity.ok(new ApiResponseModel(true, "Mana userlar", userService.getUsers()));
    }

    @GetMapping("/changeEnable")
    public HttpEntity<?> changeEnabled(@RequestParam UUID id,@RequestParam boolean status){
        return ResponseEntity.ok(userService.changeEnabled(id,status));
    }

    @DeleteMapping("/{id}")
    public HttpEntity<?> changeEnabled(@PathVariable UUID id){
        return ResponseEntity.ok(userService.removeEmployee(id));
    }

    @GetMapping("/all")
    public HttpEntity<?> getAll(){
        return ResponseEntity.ok(userService.getAll());
    }


}
