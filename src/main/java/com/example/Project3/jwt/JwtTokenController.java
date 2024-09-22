package com.example.Project3.jwt;

import com.example.Project3.jwt.dto.JwtRequestDto;
import com.example.Project3.jwt.dto.JwtResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/token")
public class JwtTokenController {
    private final JwtTokenUtils tokenUtils;
    private final UserDetailsService userService;
    private final PasswordEncoder passwordEncoder;

    public JwtTokenController(
            PasswordEncoder passwordEncoder,
            JwtTokenUtils tokenUtils,
            UserDetailsService userService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.tokenUtils = tokenUtils;
        this.userService = userService;
    }
    @PostMapping("/issue")
    public JwtResponseDto issueJwt (
            @RequestBody
            JwtRequestDto dto
    ) {
        UserDetails userDetails;
        // check username is founded or not
        try {
            log.info(dto.getUsername());
            log.info(dto.getPassword());
            userDetails = userService.loadUserByUsername(dto.getUsername());
        } catch (UsernameNotFoundException ignored) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        //if found username => check password is equals with password of username or not
        if (!passwordEncoder.matches(
                dto.getPassword(), userDetails.getPassword())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        String jwt = tokenUtils.generateToken(userDetails);
        JwtResponseDto response = new JwtResponseDto();
        response.setToken(jwt);
        return response;
    }

    @GetMapping("/test")
    public String test() {
        UserDetails userDetails = userService.loadUserByUsername("alex");
        String token = tokenUtils.generateToken(userDetails);
        log.info(token);
        log.info(tokenUtils.parseClaims(token).toString());
        return token;
    }
}
