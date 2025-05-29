package zw.co.afc.orbit.sla.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.afc.orbit.sla.model.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, String> {
}