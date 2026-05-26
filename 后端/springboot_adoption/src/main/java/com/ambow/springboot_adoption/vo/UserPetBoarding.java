package com.ambow.springboot_adoption.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPetBoarding {
    private Integer userPetId;
    private Integer userId;
    private String petName;
    private String petType;
    private String petSize;
    private Integer petAge;
    private String petGender;
    private String healthStatus;
    private String specialHabit;
    private String petImage;
}
