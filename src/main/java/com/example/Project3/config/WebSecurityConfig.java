package com.example.Project3.config;

import com.example.Project3.jwt.JwtTokenFilter;
import com.example.Project3.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenUtils jwtTokenUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/users/register", "/users/home", "/token/issue").permitAll();
                    auth.requestMatchers("/users/updateInfo").authenticated();
                    auth.requestMatchers("/users/{userId}/updateProfileImage").authenticated();
                    auth.requestMatchers("/users/{userId}/updateProfile").authenticated();
                    auth.requestMatchers("/users/businessRegister").authenticated();
                    auth.requestMatchers("/users/admin/business", "/users/admin/business/{id}").authenticated();
                    auth.requestMatchers("/users/admin/business/{regisId}/accept").authenticated();
                    auth.requestMatchers("/users/{userName}/shops/registerLogin").authenticated();
                    auth.requestMatchers("/users/{userName}/shops/register").authenticated();
                    auth.requestMatchers(
                            "/shops/register",
                            "/shops/shopWaitingList",
                            "/shops/shopList",
                            "/shops/{shopId}/accept",
                            "/shops/{shopId}/decline",
                            "/shops/{shopId}/close",
                            "/shops/{shopId}/read"
                    ).authenticated();
                    auth.requestMatchers(
                            "/shops/searchName",
                            "/shops/searchByCategory",
                            "/shops/searchByDate"
                    );
                })

                .formLogin(formLogin -> formLogin
                        .loginPage("/users/login")
                        .defaultSuccessUrl("/users/updateInfo")
                        .failureUrl("/users/login?fail")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessUrl("/users/login")
                )
                .addFilterBefore(
                        new JwtTokenFilter(jwtTokenUtils),
                        AuthorizationFilter.class
                );
        ;
        return http.build();
    }

    //@Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user1 = User.withUsername("user1")
                .password(passwordEncoder.encode("password"))
                .build();
        return new InMemoryUserDetailsManager(user1);
    }

    //비밀번호의 안호화를 담당하는 객체
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}