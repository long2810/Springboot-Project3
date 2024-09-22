package com.example.Project3.user;

import com.example.Project3.AuthenticationFacade;
import com.example.Project3.user.dto.UserBusinessRegistrationDto;
import com.example.Project3.user.dto.UserDto;
import com.example.Project3.user.dto.UserEssentialInfoDto;
import com.example.Project3.user.dto.UserRegisterDto;
import com.example.Project3.user.entity.UserEntity;
import com.example.Project3.user.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
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
    @GetMapping("/login")
    public String loginForm(
    ) {
        return "login-form";
    }

    @GetMapping("/register")
    public String registerFrom() {
        return "register-form";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam("username")
            String username,
            @RequestParam("password")
            String password,
            @RequestParam("password-check")
            String password_check
    ) {
        UserRegisterDto dto = new UserRegisterDto(username, password, password_check);
        service.createUser(dto);
        return "redirect:/users/login";
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

    @PostMapping("/updateInfo")
    public String updateUser(
            @RequestParam("nickname")
            String nickname,
            @RequestParam("age")
            int age,
            @RequestParam("email")
            String email,
            @RequestParam("phone")
            String phone
    ) {
        UserEssentialInfoDto dto = new UserEssentialInfoDto(nickname, age, email, phone);
        service.updateUser(dto);
        return "redirect:/users/home";
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
            @RequestParam("profileImage")
            MultipartFile profileImage,
            Model model
    )throws IOException {
        model.addAttribute("userId",userId);
        System.out.println(profileImage.getOriginalFilename());
        return service.updateProfileImage(userId,profileImage);
    }

    //users/{userId}/business
    @PostMapping("/{userId}/business")
    public UserBusinessRegistrationDto createBusinessRegistration(
            @PathVariable("userId")
            Long userId,
            @RequestParam("businessNum")
            String businessNum
    ) {
        log.info("success");
        return service.createBusinessRegistration(Long.parseLong(businessNum));
    }

    // read list registration
    //("/users/admin/business
    @PostMapping("/admin/business")
    public List<UserBusinessRegistrationDto> readListRegistration() {
        return service.readBusinessRegistrations();
    }

    //read one
    @PostMapping("/admin/business/{id}")
    public UserBusinessRegistrationDto readOneBusinessRegistration(
            @PathVariable("id")
            Long id
    ) {
        log.info("success2");
        return service.readOneBusinessRegistration(id);
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
