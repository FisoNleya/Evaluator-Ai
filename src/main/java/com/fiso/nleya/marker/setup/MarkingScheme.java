package com.fiso.nleya.marker.setup;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "marking_scheme")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkingScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ms_id")
    private long id;

    private String fileName;

    private String createdBy;

    @CreationTimestamp
    private LocalDateTime creationDate;

}
