package com.fiso.nleya.marker.auth.otp;



public record OtpEmailDto(
        String firstName,
        String lastName,
        String email,
        String otpCode
) {

}
