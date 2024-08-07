package com.fiso.nleya.marker.auth;


import com.fiso.nleya.marker.auth.user.UserService;
import com.fiso.nleya.marker.auth.user.dtos.ResetPasswordRequest;
import com.fiso.nleya.marker.auth.user.dtos.ValidUser;
import com.fiso.nleya.marker.auth.user.dtos.ValidateOtpRequest;
import com.fiso.nleya.marker.auth.user.dtos.ValidateTokenRequest;
import com.fiso.nleya.marker.auth.user.token.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;

    @Operation(
            description = "Get endpoint for creating user",
            summary = "This allows you to create users of all sorts",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "403"
                    )
            }

    )
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }


    @PostMapping("/otp")
    public ResponseEntity<AuthenticationResponse> confirmOtp(
            @RequestBody ValidateOtpRequest request
    ) {
        return ResponseEntity.ok(authenticationService.confirmWithOtp(request));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestPasswordReset(
            @RequestParam String emailAddress
            ) {
        authenticationService.requestReset(emailAddress);
        return ResponseEntity.ok("OTP sent to user email");
    }


    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
           @RequestBody ResetPasswordRequest resetPasswordRequest
            ) {
        authenticationService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok("Password Reset successful");
    }


    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }


    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        tokenService.refreshToken(request, response);
    }

    @PostMapping("/valid")
    public ResponseEntity<ValidUser> validateToken(
            @RequestBody ValidateTokenRequest request
    ) {
        return ResponseEntity.ok(tokenService.validateToken(request));
    }


}
