package com.fiso.nleya.marker.auth.user.dtos;

public record ResetPasswordRequest(String otp, String emailAddress, String newPassword) {
}
