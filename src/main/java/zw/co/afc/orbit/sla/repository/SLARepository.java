package zw.co.afc.orbit.sla.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.afc.orbit.sla.model.SLA;

import java.util.List;

public interface SLARepository extends JpaRepository<SLA, String> {
    boolean existsByNameIgnoreCaseAndApplication_Id(String name, String id);

    List<SLA> findByIsDeletedTrue();

    List<SLA> findByIsDeletedNullOrIsDeletedFalse();
}