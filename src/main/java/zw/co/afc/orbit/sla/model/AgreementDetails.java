package zw.co.afc.orbit.sla.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import zw.co.afc.orbit.sla.enums.EscalationType;
import zw.co.afc.orbit.sla.enums.Role;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class AgreementDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String assignedUser;
    private String escalationUser;
    private String verificationUser;

    private Integer time;
    private Integer index;
    private Role role;

    @Enumerated(EnumType.STRING)
    private EscalationType escalationType;

    private Boolean isDefaultEscalation;

    @ManyToOne // This will map back to Agreement
    @JsonBackReference
    private Agreement agreement; // Add this reference back to Agreement

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;


}
