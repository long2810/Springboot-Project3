package com.example.Project3.user.dto;

import com.example.Project3.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    private String username;
    private String password;
    private String nickname;
    private Integer age;
    private String email;
    private String phone;
    private String profileImagePath;
    private String role;

    public static UserDto fromEntity(UserEntity entity) {
        return UserDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .nickname(entity.getNickname())
                .age(entity.getAge())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .profileImagePath(entity.getProfileImg())
                .role(entity.getRole().name()).build();
    }
}
