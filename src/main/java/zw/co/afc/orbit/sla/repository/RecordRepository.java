package zw.co.afc.orbit.sla.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.afc.orbit.sla.model.Record;

import java.util.List;
import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, String> {
    List<Record> findByIsDeletedTrue();

    List<Record> findByIsDeletedNullOrIsDeletedFalse();

    Optional<Record> findByReferenceNumber(String s);
}
