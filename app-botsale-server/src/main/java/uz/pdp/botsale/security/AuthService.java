package uz.pdp.botsale.security;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.botsale.entity.User;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.payload.ApiResponse;
import uz.pdp.botsale.payload.ReqSignUp;
import uz.pdp.botsale.repository.RoleRepository;
import uz.pdp.botsale.repository.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;

    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AuthService(@Lazy UserRepository userRepository, MessageSource messageSource, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(phoneNumber).orElseThrow(() -> new UsernameNotFoundException(phoneNumber));
    }


    public UserDetails loadUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User id not found: " + userId));
    }


    public ApiResponse register(ReqSignUp reqSignUp) {
        Optional<User> optionalUser = userRepository.findByPhoneNumber(reqSignUp.getPhoneNumber());
        if (optionalUser.isPresent()) {
            return new ApiResponse(messageSource.getMessage("phone.number.exist", null, LocaleContextHolder.getLocale()), false);
        } else {
            User user=new User();
            if (reqSignUp.getId()!=null){
                user=userRepository.findById(reqSignUp.getId()).orElseThrow(() -> new ResourceNotFoundException("user","id",reqSignUp.getId()));
            }
            user.setRoles(roleRepository.findAllByNameIn(Collections.singletonList(reqSignUp.getRoleName())));
            user.setPassword(passwordEncoder.encode(reqSignUp.getPassword()));
            user.setPhoneNumber(reqSignUp.getPhoneNumber());
            user.setFirstName(reqSignUp.getFirstName());
            user.setLastName(reqSignUp.getLastName());
            user.setEnabled(true);
            userRepository.save(user);
            return new ApiResponse(messageSource.getMessage("user.created", null, LocaleContextHolder.getLocale()), true);
        }
    }


}
