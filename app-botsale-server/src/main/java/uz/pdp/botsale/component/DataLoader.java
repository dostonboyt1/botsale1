package uz.pdp.botsale.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.pdp.botsale.entity.User;
import uz.pdp.botsale.entity.enums.RoleName;
import uz.pdp.botsale.repository.RoleRepository;
import uz.pdp.botsale.repository.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {

    @Value("${spring.datasource.initialization-mode}")
    private String initialMode;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (initialMode.equals("always")) {
            userRepository.save(new User(

                    "123",

                    passwordEncoder.encode("123"),
                    "Alisher",
                    "Atadjanov",
                    roleRepository.findAllByNameIn(
                            Arrays.asList(RoleName.ROLE_ADMIN,
                                    RoleName.ROLE_DIRECTOR)

                    )));
        }
//        else{
//            Optional<User> optionalUser = userRepository.findById(UUID.fromString("a0830781-f2cf-45b9-b64b-6c243040c13d"));
//            if (optionalUser.isPresent()){
//                User user = optionalUser.get();
//                user.setRoles(roleRepository.findAllByNameIn(
//                        Arrays.asList(RoleName.ROLE_ADMIN)));
//                userRepository.save(user);
//            }
//        }

    }
}
