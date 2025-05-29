package zw.co.afc.orbit.sla.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String message;
    private Date reminderTime;
    private Boolean isSent;

    @ManyToOne
    @JsonBackReference
    private Contract contract;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}
