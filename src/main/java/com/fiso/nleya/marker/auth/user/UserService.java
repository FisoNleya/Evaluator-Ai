package com.fiso.nleya.marker.auth.user;


import com.fiso.nleya.marker.auth.config.JwtService;
import com.fiso.nleya.marker.auth.user.dtos.AuthenticationUser;
import com.fiso.nleya.marker.auth.user.dtos.ValidUser;
import com.fiso.nleya.marker.auth.user.dtos.ValidateTokenRequest;
import com.fiso.nleya.marker.auth.user.token.TokenService;
import com.fiso.nleya.marker.shared.exceptions.DataNotFoundException;
import com.fiso.nleya.marker.shared.exceptions.TokenAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    private  final JwtService jwtService;

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        userRepository.save(user);
    }





    public Page<User> findAllUsers(Integer pageNumber, Integer pageSize, Sort.Direction direction, String sortBy) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize).withSort(direction,sortBy);
        return userRepository.findAll(pageable);
    }

    public ValidUser findByUserName(String username){

        return userRepository.findByEmail(username)
                .map(this::map)
                .orElseThrow(()-> new UsernameNotFoundException("User not found : "+username ));
    }


    public ValidUser findMe(HttpServletRequest request){

        final String authHeader = request.getHeader("Authorization");
        String  jwt = authHeader.substring(7);

        final  String  userEmail = jwtService.extractUsername(jwt);

        log.info("Checking me : {}", userEmail);
        return userRepository.findByEmail(userEmail)
                .map(this::map)
                .orElseThrow(()-> new UsernameNotFoundException("User not found with email : "+userEmail));
    }


    public ValidUser map(User userDetails){
        return new ValidUser(userDetails.getFirstName(), userDetails.getLastName(), userDetails.getEmail(), userDetails.getRole());
    }


    public User findByEmail(String emailAddress) {
        return userRepository.findByEmail(emailAddress).orElseThrow(()->new DataNotFoundException("User with email "+emailAddress+" was not found"));
    }


}
