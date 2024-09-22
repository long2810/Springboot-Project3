package com.example.Project3.user.dto;

import com.example.Project3.user.entity.UserBusinessRegistration;
import com.example.Project3.user.entity.UserEntity;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBusinessRegistrationDto {
    private Long id;
    private Long businessNum;
    private UserEntity user;

    public static UserBusinessRegistrationDto fromEntity(UserBusinessRegistration entity) {
        return UserBusinessRegistrationDto.builder()
                .id(entity.getId())
                .businessNum(entity.getBusinessNum())
                .user(entity.getUser())
                .build();
    }
}
