package com.ridelink.account_service.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String phoneNumber;
    private String password;
}
