package com.example.Project3.user;

import com.example.Project3.AuthenticationFacade;
import com.example.Project3.user.dto.UserBusinessRegistrationDto;
import com.example.Project3.user.dto.UserDto;
import com.example.Project3.user.dto.UserEssentialInfoDto;
import com.example.Project3.user.dto.UserRegisterDto;
import com.example.Project3.user.entity.UserEntity;
import com.example.Project3.user.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {
    private final JpaUserService service;
    private final UserRepository repository;

    public UserController(JpaUserService service, UserRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @GetMapping("/home")
    public String ShopHome() {
        return "shopHome";
    }

    //user/login -> login-form.html
    @PostMapping("/login")
    public String loginForm(
            @RequestParam("username")
            String username,
            @RequestParam("password")
            String password
    ) {
        service.userLogin(username,password);
        log.info("login success");
        return "login-form";
    }

//    @GetMapping("/register")
//    public String registerFrom() {
//        return "register-form";
//    }

   @PostMapping("/register")
    public String register(
            @RequestParam("username")
            String username,
            @RequestParam("password")
            String password,
            @RequestParam("passwordCheck")
            String passwordCheck
    ) {
        UserRegisterDto dto = new UserRegisterDto(username, password, passwordCheck);
        service.createUser(dto);
        return "redirect:/users/home";
    }

    @GetMapping("/updateInfo")
    public String updateInfo(
            AuthenticationFacade facade
    ) {
        UserEntity user = facade.getCurrentUserEntity(repository);
        if (user.getRole() == null || user.getRole().name().equals("INACTIVE")) {
            return "updateInfo";
        } else
            return "shopHome";
    }

    @PatchMapping("/updateInfo")
    public ResponseEntity<UserDto> updateUser(
            @RequestParam("nickname")
//            @RequestBody
            String nickname,
            @RequestParam("age")
//            @RequestBody
            String age,
            @RequestParam("email")
//            @RequestBody
            String email,
            @RequestParam("phone")
//            @RequestBody
            String phone
    ) {
        UserEssentialInfoDto essentialInfoDto = new UserEssentialInfoDto(nickname, Integer.parseInt(age), email, phone);
        UserDto userDto = service.updateUser(essentialInfoDto);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/{userId}/updateProfileImage")
    public String updateProfileImg(
            @PathVariable("userId")
            Long userId
    ) {
        return "profileImg";
    }

    @PostMapping(
            "/{userId}/updateProfile"
            //consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UserDto updateProfileImage(
            @PathVariable("userId")
            Long userId,
            @RequestBody
            MultipartFile profileImage,
            Model model
    )throws IOException {
        model.addAttribute("userId",userId);
        System.out.println(profileImage.getOriginalFilename());
        return service.updateProfileImage(userId,profileImage);
    }

    //users/businessRegister
    @PostMapping("/businessRegister")
    public ResponseEntity<UserBusinessRegistrationDto> createBusinessRegistration(
//            @PathVariable("userId")
//            Long userId,
            @RequestParam("businessNum")
            String businessNum
    ) {
        log.info("business");
        UserBusinessRegistrationDto dto = service.createBusinessRegistration(Long.parseLong(businessNum));
        return ResponseEntity.ok(dto);
    }

    // read list registration
    //("/users/admin/business
    @PostMapping("/admin/business")
    public List<UserBusinessRegistrationDto> readListRegistration() {
        return service.readBusinessRegistrations();
    }

    //read one
    @PostMapping("/admin/business/{id}")
    public ResponseEntity<UserBusinessRegistrationDto> readOneBusinessRegistration(
            @PathVariable("id")
            Long id
    ) {
        log.info("success2");
        UserBusinessRegistrationDto dto = service.readOneBusinessRegistration(id);
        return ResponseEntity.ok(dto);
    }

    //accept
    //("/users/admin/business/{regisId}/accept")
    @PostMapping("/admin/business/{regisId}/accept")
    public String acceptBusinessRegistration(
            @PathVariable("regisId")
            Long id
    ) {
        service.acceptBusinessRegis(id);
        return "accept";
    }

    //decline
    //("/users/admin/business/{regisId}/decline")
    @DeleteMapping("/admin/business/{regisId}/decline")
    public String declineBusinessRegis(
            @PathVariable("regisId")
            Long id
    ) {
        service.declineBusinessRegis(id);
        return "decline";
    }
}
