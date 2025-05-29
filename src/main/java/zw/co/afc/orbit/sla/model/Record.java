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
import zw.co.afc.orbit.sla.enums.Status;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true)
    private String referenceNumber;

    private String slaCode;

    private Integer duration;

    private Date completionDeadline;

    private Date completionTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String createdBy;

    private Boolean isDeleted;

    private Integer holdCount;

    @ManyToOne
    @JsonBackReference
    private Agreement agreement;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @OneToMany(mappedBy = "record", fetch = FetchType.EAGER)
    private List<Contract> contracts;
}
