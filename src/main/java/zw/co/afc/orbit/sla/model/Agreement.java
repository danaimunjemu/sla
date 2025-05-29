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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Agreement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String owner;
    private String description;

    private String cyclicUser;

    private Boolean hasReminder;
    private Integer reminderTime;

    private Boolean requiresManager;
    private Integer managersTime;
    // if this is set to true then agreement details doesn't change, it will create the contract with managersTime and index -1

    private Boolean isDeleted;

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @ManyToOne // This maps the Many side of the relationship
    @JsonBackReference
    private SLA sla;

    @OneToMany(mappedBy = "agreement", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<AgreementDetails> agreementDetails;

    @OneToMany(mappedBy = "agreement")
    @JsonManagedReference
    private List<Record> records;

    public void validateContracts(SLA sla) {
        // TODO fix this function, it is not working the way it is meant to
//        if (agreementDetails.size() != sla.getNumberOfContracts()) {
//            throw new IllegalArgumentException("Number of contracts in agreement must match the number specified in the parent SLA");
//        }
    }

}
