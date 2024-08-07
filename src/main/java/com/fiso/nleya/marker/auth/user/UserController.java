package com.fiso.nleya.marker.auth.user;


import com.fiso.nleya.marker.auth.user.dtos.ValidUser;
import com.fiso.nleya.marker.auth.user.dtos.ValidateTokenRequest;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }



    @GetMapping
    public ResponseEntity<Page<User>> findAllUsers(@RequestParam(defaultValue = "10") Integer pageSize,
                                                   @RequestParam(defaultValue = "createdAt") String sortBy,
                                                   @RequestParam(defaultValue = "DESC") Sort.Direction direction,
                                                   @RequestParam(defaultValue = "0") Integer pageNumber ){
        return new ResponseEntity<>(userService.findAllUsers(pageNumber,pageSize,direction,sortBy), HttpStatus.OK);

    }

    @GetMapping("/me")
    public ResponseEntity<ValidUser> find(
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(userService.findMe(request));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> find(
            @PathVariable(required = false) @Parameter(example = "tariro@gmail.com", description = "User email") String email,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

}
