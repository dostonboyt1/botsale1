package uz.pdp.botsale.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.botsale.entity.Role;
import uz.pdp.botsale.entity.User;
import uz.pdp.botsale.entity.enums.RoleName;
import uz.pdp.botsale.payload.*;
import uz.pdp.botsale.repository.RoleRepository;
import uz.pdp.botsale.repository.UserRepository;
import uz.pdp.botsale.utils.CommonUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MessageSource messageSource;

    public ApiResponse addUser(ReqSignUp request) {
        if (request.getId()==null) {
            User user = new User(
                    request.getPhoneNumber(),
                    passwordEncoder.encode(request.getPassword()),
                    request.getFirstName(),
                    request.getLastName(),
                    roleRepository.findAllByNameIn(
                            Collections.singletonList(request.getRoleName())
            ));
            userRepository.save(user);
            return new ApiResponse("Foydalanuvchi muvoffaqiyatli ro'yxatga olindi", true);
        }else {
            Optional<User> optionalUser = userRepository.findById(request.getId());
            if (optionalUser.isPresent()){
                User user = optionalUser.get();
                user.setFirstName(request.getFirstName());
                user.setLastName(request.getLastName());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setPhoneNumber(request.getPhoneNumber());
                user.setRoles(roleRepository.findAllByNameIn(Collections.singletonList(request.getRoleName())));
                userRepository.save(user);
                return new ApiResponse("Foydalanuvchi malumotlari muvofaqqiyatli o'zgartirildi.", true);
            }else {
                return new ApiResponse("Bunday telefon raqamli foydalanuvchi mavjud", false);
            }
        }
    }


    public ResponseEntity changePassword(ReqPassword request, User user) {
        if (request.getPassword().equals(request.getPrePassword())) {
            if (checkPassword(request.getOldPassword(), user)) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepository.save(user);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse("Parol o'zgartirildi", true));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Hozirgi parol xato", false));
            }
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Yangi va tasdiqlovchi parol mos emas", false));
        }
    }


    private Boolean checkPassword(String oldPassword, User user) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }


    public HttpEntity<?> editUser(ReqSignUp reqUser, User user) {
        ApiResponse response = new ApiResponse();
        response.setSuccess(true);
        user.setFirstName(reqUser.getFirstName());
        user.setLastName(reqUser.getLastName());

        if (!user.getPhoneNumber().equals(reqUser.getPhoneNumber())) {
            if (!userRepository.findByPhoneNumber(reqUser.getPhoneNumber()).isPresent()) {
                user.setPhoneNumber(reqUser.getPhoneNumber());
            } else {
                response.setSuccess(false);
                response.setMessage("Phone number is already exist");
            }
        }
        if (response.isSuccess()) {
            response.setMessage(messageSource.getMessage("user.edited", null, LocaleContextHolder.getLocale()));
        } else {
            response.setMessage(messageSource.getMessage("error", null, LocaleContextHolder.getLocale()));
        }
        userRepository.save(user);
        return ResponseEntity.ok(response);
    }

    public List<ResUser> getUsers() {
        return userRepository.findAll().stream().map(this::getResUser).collect(Collectors.toList());
    }

    public ResUser getResUser(User user){
        ResUser resUser=new ResUser();
        resUser.setId(user.getId());
        resUser.setFirstName(user.getFirstName());
        resUser.setLastName(user.getLastName());
        resUser.setPhoneNumber(user.getPhoneNumber());
        resUser.setEnabled(user.isEnabled());
        List<String> stringList=new ArrayList<>();
        for (Role role : user.getRoles()) {
            stringList.add(role.getName().name());
        }
        resUser.setRoleNameList(stringList);
        return resUser;
    }

    public ResPageable getByPageable(Integer page, Integer size) {
        Page<User> userPage = userRepository.findAll(CommonUtils.getPageable(page, size));
        return new ResPageable(userPage.stream().map(this::getResUser).collect(Collectors.toList()),userPage.getTotalElements(),page);
    }

    public ApiResponse changeEnabled(UUID id, boolean status) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setEnabled(status);
            userRepository.save(user);
            return new ApiResponse(status?"Aktivlashtirildi":"Bloklandi",true);
        }
        return new ApiResponse("Error",false);
    }

    public ApiResponse removeEmployee(UUID id) {
        try {
            userRepository.deleteById(id);
            return new ApiResponse("Deleted",true);
        }catch (Exception e){
            return new ApiResponse("Error",false);
        }
    }

    public List<ResUser> getAll() {
        return userRepository.findAll().stream().map(this::getResUser).collect(Collectors.toList());
    }
}
