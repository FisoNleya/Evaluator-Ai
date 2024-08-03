package com.fiso.nleya.marker.testing;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "_user")
@NoArgsConstructor
@AllArgsConstructor
public class User {


    @Id
    @Column(name = "user_id")
    private String  email;

    private String firstName;

    private String lastName;


}
