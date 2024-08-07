package com.fiso.nleya.marker.auth.user.token;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiso.nleya.marker.auth.AuthenticationResponse;
import com.fiso.nleya.marker.auth.config.JwtService;
import com.fiso.nleya.marker.auth.user.User;
import com.fiso.nleya.marker.auth.user.UserRepository;
import com.fiso.nleya.marker.auth.user.UserService;
import com.fiso.nleya.marker.auth.user.dtos.ValidUser;
import com.fiso.nleya.marker.auth.user.dtos.ValidateTokenRequest;
import com.fiso.nleya.marker.shared.exceptions.TokenAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {


    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final UserService userService;

    public Optional<Token> findOptionalToken(String tokenValue){
        return tokenRepository.findByToken(tokenValue);
    }


    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }


    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }



    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            var user = userService.findByEmail(userEmail);

            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }


    public ValidUser validateToken(ValidateTokenRequest request) {


        final  String  userEmail = jwtService.extractUsername(request.accessToken());

        if (userEmail != null ) {

            var userDetails = userService.findByEmail(userEmail);

            var isTokenValid = findOptionalToken(request.accessToken())
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if (jwtService.isTokenValid(request.accessToken(), userDetails) && Boolean.TRUE.equals(isTokenValid)){
                log.info("Token validated : "+ userEmail);
                return new ValidUser(userDetails.getFirstName(), userDetails.getLastName(), userDetails.getEmail(), userDetails.getRole());
            }
        }

        throw new TokenAuthenticationException("User token is invalid");

    }


}
