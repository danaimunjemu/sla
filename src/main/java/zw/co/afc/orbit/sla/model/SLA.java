package zw.co.afc.orbit.sla.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import zw.co.afc.orbit.sla.enums.ExtendedSLAType;
import zw.co.afc.orbit.sla.enums.SLAType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SLA {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    @Enumerated(EnumType.STRING)
    private SLAType slaType;

    @Enumerated(EnumType.STRING)
    private ExtendedSLAType extendedSLAType;

    private Integer numberOfContracts;
    private Boolean contractsRunConcurrently;
    private Integer quota;

    private Boolean isDeleted;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;


    // Relationships
    @ManyToOne
    @JsonBackReference
    private Application application;

    @OneToMany(mappedBy = "sla", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Agreement> agreements = new ArrayList<>();

    public void validateAgreements() {
        for (Agreement agreement : agreements) {
            agreement.validateContracts(this);
        }
    }

}
