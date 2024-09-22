package com.example.Project3.user;

import com.example.Project3.AuthenticationFacade;
import com.example.Project3.user.UserDetails.CustomUserDetails;
import com.example.Project3.user.dto.UserBusinessRegistrationDto;
import com.example.Project3.user.dto.UserDto;
import com.example.Project3.user.dto.UserEssentialInfoDto;
import com.example.Project3.user.dto.UserRegisterDto;
import com.example.Project3.user.entity.UserBusinessRegistration;
import com.example.Project3.user.entity.UserEntity;
import com.example.Project3.user.entity.UserRole;
import com.example.Project3.user.repo.BusinessRepository;
import com.example.Project3.user.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class JpaUserService implements UserDetailsService {
    private PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    public JpaUserService(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            BusinessRepository businessRepository
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        UserEntity alex = new UserEntity();
        alex.setUsername("alex");
        alex.setPassword(passwordEncoder.encode("password"));
//        alex.setNickname("alexhihi");
//        alex.setAge(30);
//        alex.setEmail("user1@a.a");
//        alex.setPhone("010-asdf-zxcv");
        alex.setRole(UserRole.BUSINESS);
        userRepository.save(alex);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUser =
                userRepository.findByUsername(username);
        UserEntity user = optionalUser.get();
        return new CustomUserDetails(user);
    }

    public void createUser(
            UserRegisterDto registerDto
    ) {
        if (userRepository.existsByUsername(registerDto.getUsername()) ||
                !registerDto.getPassword().equals(registerDto.getPasswordCheck()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        UserEntity newUser = new UserEntity();
        newUser.setUsername(registerDto.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        log.info(newUser.toString());
        userRepository.save(newUser);
    }

    public void updateUser(UserEssentialInfoDto dto) {
        //AuthenticationFacade 활용하여 로그인하고 있는 UserEntity의 username를 검색함
        AuthenticationFacade facade = new AuthenticationFacade();

        UserEntity user = facade.getCurrentUserEntity(userRepository);
        user.setNickname(dto.getNickname());
        user.setAge(dto.getAge());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setRole(UserRole.REGULAR);
        log.info(user.getNickname());
        System.out.println(user.getRole().name());
        userRepository.save(user);
    }

    public UserDto updateProfileImage(
            Long userId,
            MultipartFile profileImage
    ) {
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        //2.파일의 업로드 위치를 결정한다
        //추천: media/{userId}/profile.png|jpeg
        //2.1.프로필 이미지를 업로드할 폴더가 있는지 확인 밎 생성
        String profileDir = "media/" + userId + "/";
        try {
            Files.createDirectories(Path.of(profileDir));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //2.2. 업로드한 파일의 확장자를 추출한다
        String originalFileName = profileImage.getOriginalFilename();
        String[] fileNameSplit = originalFileName.split("\\.");
        String extension = fileNameSplit[fileNameSplit.length - 1];
        //2.3.실제 위치에 파일을 저장한다
        //media/1/profile.png
        //media/2/profile.png
        String uploadPath = profileDir + "profile." + extension;
        try{
            profileImage.transferTo(Path.of(uploadPath));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //3.업로드에 성공하면, 이미지 URL을 Entity에 저장한다
        //http://localhost:8080/static/{userId}/profile.png|jpeg
        String reqPath = "/static/" + userId + "/profile." + extension;
        UserEntity target = optionalUser.get();
        target.setProfileImg(reqPath);
        return UserDto.fromEntity(userRepository.save(target));
    }

    //Regular user -> business
    @Transactional
    public UserBusinessRegistrationDto createBusinessRegistration(Long businessNum) {
        AuthenticationFacade facade = new AuthenticationFacade();
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);

        // Check role of user is INACTIVE or not => if INACTIVE, throw Exception
        String role = currentUser.getRole().name();
        if (role.equals("INACTIVE")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        log.info(currentUser.getRole().name());

        if(businessRepository.existsByBusinessNum(businessNum)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "this businessNum is existed");
        }
        log.info("success1");
        UserBusinessRegistration businessRegistration = UserBusinessRegistration.builder()
                .user(currentUser)
                .businessNum(businessNum)
                .build();
        return UserBusinessRegistrationDto.fromEntity(businessRepository.save(businessRegistration));
    }

    // if user is admin -> Read Business Registrations
    public List<UserBusinessRegistrationDto> readBusinessRegistrations() {
        AuthenticationFacade facade = new AuthenticationFacade();
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);

        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not ADMIN");
        }
        List<UserBusinessRegistration> registrations = businessRepository.findAll();
        List<UserBusinessRegistrationDto> dtoRegistrations = new ArrayList<>();

        for (UserBusinessRegistration registration : registrations) {
            dtoRegistrations.add(UserBusinessRegistrationDto.fromEntity(registration));
        }

        return dtoRegistrations;
    }

    //read one businessRegistration
    public UserBusinessRegistrationDto readOneBusinessRegistration(Long id) {
        AuthenticationFacade facade = new AuthenticationFacade();
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);

        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not ADMIN");
        }

        Optional<UserBusinessRegistration> optionalBusinessRegis = businessRepository.findById(id);

        if(optionalBusinessRegis.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Business registration is not found");

        UserBusinessRegistration regis = optionalBusinessRegis.get();
        return UserBusinessRegistrationDto.fromEntity(businessRepository.save(regis));
    }

    @Transactional
    public void acceptBusinessRegis(Long id) {
        AuthenticationFacade facade = new AuthenticationFacade();
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);

        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not ADMIN");
        }

        Optional<UserBusinessRegistration> optionalBusinessRegis = businessRepository.findById(id);

        if(optionalBusinessRegis.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Business registration is not found");

        UserBusinessRegistration businessRegistration = optionalBusinessRegis.get();
        UserEntity user = businessRegistration.getUser();

        if(!user.getRole().name().equals("BUSINESS")) {
            user.setRole(UserRole.BUSINESS);
            userRepository.save(user);
        }
    }

    @Transactional
    public void declineBusinessRegis(Long id) {
        AuthenticationFacade facade = new AuthenticationFacade();
        UserEntity currentUser = facade.getCurrentUserEntity(userRepository);

        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not ADMIN");
        }

        Optional<UserBusinessRegistration> optionalBusinessRegis = businessRepository.findById(id);

        if(optionalBusinessRegis.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Business registration is not found");

        UserBusinessRegistration businessRegistration = optionalBusinessRegis.get();
        UserEntity user = businessRegistration.getUser();

        businessRepository.deleteById(id);
    }
}
