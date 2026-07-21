package com.icet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationDTO {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String contact;
    private String nic;
    private Long roleId;
}

