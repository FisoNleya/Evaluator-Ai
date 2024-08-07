package com.fiso.nleya.marker.auth.user.dtos;


import com.fiso.nleya.marker.auth.user.Role;

public record ValidUser(

         String firstname,
         String lastname,
         String email,
         Role role

) {
}
