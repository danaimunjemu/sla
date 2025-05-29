package zw.co.afc.orbit.sla.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import zw.co.afc.orbit.sla.enums.EscalationType;
import zw.co.afc.orbit.sla.enums.Role;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Date completionDeadline;
    private Date completionTime;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String assignedUser;
    private String escalationUser;

    private Integer duration;
    private Integer escalationTrialCount;
    private Date escalationTime;
    private Boolean hasEscalated;
    private Integer status; // 0 means pending 1 means current 2 means reassigned 3 means completed 4 means nullified

    @Enumerated(EnumType.STRING)
    private EscalationType escalationType;

    private Boolean isDeleted;

    private Integer index; // if it is sequential, the level denotes their position in the action

    @ManyToOne
    @JsonBackReference
    private Record record;

    @OneToOne(mappedBy = "contract")
    private SLAViolation slaViolation;

    @OneToMany(mappedBy = "contract", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Reminder> reminders;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

}
