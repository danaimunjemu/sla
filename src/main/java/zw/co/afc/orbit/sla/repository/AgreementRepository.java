package zw.co.afc.orbit.sla.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.afc.orbit.sla.model.Agreement;

import java.util.List;

public interface AgreementRepository extends JpaRepository<Agreement, String> {
    boolean existsByNameIgnoreCaseAndSla_Id(String name, String id);

    List<Agreement> findByIsDeletedTrue();

    List<Agreement> findByIsDeletedNullOrIsDeletedFalse();
}