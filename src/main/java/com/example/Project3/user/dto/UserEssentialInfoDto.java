package com.example.Project3.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class UserEssentialInfoDto {
    private String nickname;
    private Integer age;
    private String email;
    private String phone;
}
