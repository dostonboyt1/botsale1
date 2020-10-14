package uz.pdp.botsale.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.JwtResponse;
import uz.pdp.botsale.payload.ReqSignIn;
import uz.pdp.botsale.payload.ReqSignUp;
import uz.pdp.botsale.security.AuthService;
import uz.pdp.botsale.security.JwtTokenProvider;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    AuthenticationManager authenticate;

    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public HttpEntity<?> register(@Valid @RequestBody ReqSignUp reqSignUp) {
        ApiResponse response = authService.register(reqSignUp);
        if (response.isSuccess()) {
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(getApiToken(reqSignUp.getPhoneNumber(), reqSignUp.getPassword()));
        }
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response.getMessage());
    }


    @PostMapping("/login")
    public HttpEntity<?> login(@Valid @RequestBody ReqSignIn reqSignIn){
        return ResponseEntity.ok(getApiToken(reqSignIn.getPhoneNumber(),reqSignIn.getPassword()));
    }


    public HttpEntity<?> getApiToken(String phoneNumber, String password){
        Authentication authentication = authenticate.authenticate(
                new UsernamePasswordAuthenticationToken(phoneNumber, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }


}
