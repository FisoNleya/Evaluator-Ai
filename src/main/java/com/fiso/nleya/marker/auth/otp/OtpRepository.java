package com.fiso.nleya.marker.auth.otp;

import com.fiso.nleya.marker.auth.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findOtpByUserAndIsUsedAndExpiresAtGreaterThan(User user, Boolean isUsed, LocalDateTime timeAfter);

    Optional<Otp> findOtpByUser_EmailAndOtpCode(String email, String otp);
}