package com.fiso.nleya.marker.setup.assessment;

import com.fiso.nleya.marker.auth.user.User;
import com.fiso.nleya.marker.setup.ms.MarkingScheme;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.proxy.HibernateProxy;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id",
            nullable = false,
            referencedColumnName = "user_id",
            foreignKey = @ForeignKey(name = "user_id_fk"))
    private User assessor;


    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "ms_id",
            nullable = false,
            referencedColumnName = "ms_id",
            foreignKey = @ForeignKey(name = "ms_id_fk"))
    private MarkingScheme markingScheme;


    @Column(name = "assessment_name")
    private String name;

    @ManyToMany
    @JoinTable(
            name = "assessment_assessee",
            joinColumns = @JoinColumn(name = "assessement_id"),
            inverseJoinColumns = @JoinColumn(name = "assesee_id"))
    @ToString.Exclude
    private Set<Assessee> assesees;

    private Integer maxAttempts;

    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private int durationInMinutes;


    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        Class<?> oEffectiveClass = object instanceof HibernateProxy ? ((HibernateProxy) object).getHibernateLazyInitializer().getPersistentClass() : object.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Assessment that = (Assessment) object;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
