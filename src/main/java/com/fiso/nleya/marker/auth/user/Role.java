package com.fiso.nleya.marker.auth.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fiso.nleya.marker.auth.user.Permission.*;


@RequiredArgsConstructor
public enum Role {

  USER(Set.of(
          USER_READ,
          USER_UPDATE,
          USER_CREATE,
          USER_DELETE

  )),
  ADMIN(
          Set.of(
                  ADMIN_READ,
                  ADMIN_UPDATE,
                  ADMIN_DELETE,
                  ADMIN_CREATE,

                  ASSIGNER_READ,
                  ASSIGNER_UPDATE,
                  ASSIGNER_DELETE,
                  ASSIGNER_CREATE,


                  USER_READ,
                  USER_UPDATE,
                  USER_CREATE,
                  USER_DELETE

          )
  ),


  ASSIGNER(
          Set.of(
                  ASSIGNER_READ,
                  ASSIGNER_UPDATE,
                  ASSIGNER_DELETE,
                  ASSIGNER_CREATE,

                  USER_READ,
                  USER_UPDATE,
                  USER_CREATE,
                  USER_DELETE

          )
  );


  @Getter
  private final Set<Permission> permissions;

  public List<SimpleGrantedAuthority> getAuthorities() {
    var authorities = getPermissions()
            .stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getPermissionValue()))
            .collect(Collectors.toList());
    authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
    return authorities;
  }
}
