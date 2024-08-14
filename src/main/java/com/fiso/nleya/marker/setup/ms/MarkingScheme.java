package com.fiso.nleya.marker.setup.ms;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "marking_scheme")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MarkingScheme {

    @Id
    @Column(name = "ms_id")
    private String id;

    private String fileName;

    private String storageObjectName;

    @CreatedBy
    private String createdBy;

    @CreationTimestamp
    private LocalDateTime creationDate;

}
