package com.example.cleonoraadmin.model.admin;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class AdminUserProfileResponse implements Serializable {
    Long id;
    String name;
    String surname;
    String avatar;
    String email;
    String phoneNumber;
    Boolean isActive;
    String role;
}
