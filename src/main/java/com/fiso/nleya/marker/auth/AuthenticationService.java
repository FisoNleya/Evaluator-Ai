package com.fiso.nleya.marker.auth;


import com.fiso.nleya.marker.auth.config.JwtService;
import com.fiso.nleya.marker.auth.otp.Otp;
import com.fiso.nleya.marker.auth.otp.OtpEmailDto;
import com.fiso.nleya.marker.auth.otp.OtpRepository;
import com.fiso.nleya.marker.auth.user.User;
import com.fiso.nleya.marker.auth.user.UserRepository;
import com.fiso.nleya.marker.auth.user.dtos.ResetPasswordRequest;
import com.fiso.nleya.marker.auth.user.dtos.ValidateOtpRequest;
import com.fiso.nleya.marker.auth.user.token.TokenService;
import com.fiso.nleya.marker.shared.exceptions.DataNotFoundException;
import com.fiso.nleya.marker.shared.exceptions.DuplicateRecordException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final int OTP_LENGTH = 8; // Change this value for desired OTP length

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpRepository otpRepository;
    private final TokenService tokenService;

    private final ApplicationEventPublisher publisher;



    @Value("${application.otp.expire.after}")
    private Long otpTimeLength;


    public String register(RegisterRequest request) {
        var existUser =  userRepository.findByEmail(request.getEmail());
        if (existUser.isPresent()){
            var user = existUser.get();
            if (user.getEmailVerified())
                throw new DuplicateRecordException("User with email address already exists");
            else{
                generateOtpAndSend(user);
                throw new DuplicateRecordException("User with email address already exist, please check your email to verify account");
            }
        }

        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .emailVerified(false)
                .build();

        user = userRepository.save(user);
        generateOtpAndSend(user);

        return "OTP sent to user email";
    }


    private void generateOtpAndSend(User user){

        var otpOptional = otpRepository.findOtpByUserAndIsUsedAndExpiresAtGreaterThan(user,false, LocalDateTime.now());

        if (otpOptional.isPresent()){
            sendOtpEmail(otpOptional.get());
            return;
        }

        Otp otp = otpRepository.save(Otp.builder()
                .otpCode(generateOTP())
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(otpTimeLength))
                .isUsed(false)
                .build());


        sendOtpEmail(otp);

    }



    public void sendOtpEmail(Otp otp){
        log.info("Sending OTP email to user email");

        User user = otp.getUser();

        publisher.publishEvent(new OtpEmailDto(user.getFirstName(), user.getLastName(), user.getEmail(), otp.getOtpCode()));
        log.info("Sent otp email to : {}", otp.getOtpCode());
    }




    public AuthenticationResponse confirmWithOtp(ValidateOtpRequest validateOtpRequest){

        var otpOptional  = otpRepository.findOtpByUser_EmailAndOtpCode(validateOtpRequest.email(), validateOtpRequest.otp());
        if (otpOptional.isEmpty())
            throw new AccessDeniedException("OTP invalid");
        validateOtp(otpOptional.get());
        User user =  otpOptional.get().getUser();
        user.setEmailVerified(true);
        user =  userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        tokenService.saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }


    private void validateOtp(Otp otp) {

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())){
            generateOtpAndSend(otp.getUser());
            throw new AccessDeniedException("OTP provided has already expired, please check your email for the most recent one");
        }

        if (Boolean.TRUE.equals(otp.getIsUsed())){
            generateOtpAndSend(otp.getUser());
            throw new AccessDeniedException("OTP provided has already been used, please check your email for the most recent one");
        }

        otp.setIsUsed(true);
        otpRepository.save(otp);
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UsernameNotFoundException("User not found with email : "+ request.getEmail()));


        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        tokenService.revokeAllUserTokens(user);
        tokenService.saveUserToken(user, jwtToken);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }





    public static String generateOTP() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder otp = new StringBuilder();
        Set<Character> generatedCharacters = new HashSet<>();

        // Initialize secure random number generator
        SecureRandom random = new SecureRandom();

        // Generate OTP characters
        while (otp.length() < OTP_LENGTH) {
            int index = random.nextInt(characters.length());
            char character = characters.charAt(index);

            // Ensure uniqueness of the character
            if (!generatedCharacters.contains(character)) {
                otp.append(character);
                generatedCharacters.add(character);
            }
        }

        return otp.toString();
    }


    public void requestReset(String emailAddress) {
        User user = userRepository.findByEmail(emailAddress).orElseThrow(
                ()->new DataNotFoundException("User with email "+emailAddress+" was not found"));
        generateOtpAndSend(user);

    }


    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user  = userRepository.findByEmail(resetPasswordRequest.emailAddress())
                .orElseThrow(()->new DataNotFoundException("User with email "+resetPasswordRequest.emailAddress()+" was not found"));

        var otpOptional  = otpRepository.findOtpByUser_EmailAndOtpCode(resetPasswordRequest.emailAddress(), resetPasswordRequest.otp());
        if (otpOptional.isEmpty())
            throw new AccessDeniedException("OTP provided for user is not valid");
        validateOtp(otpOptional.get());
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.newPassword()));
        userRepository.save(user);
        
    }




}
