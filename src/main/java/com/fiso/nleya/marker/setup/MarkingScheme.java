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
    @Column(name = "ms_id")
    private String id;

    private String fileName;

    private String storageObjectName;

    private String createdBy;


    @CreationTimestamp
    private LocalDateTime creationDate;

}
