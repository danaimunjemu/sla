package zw.co.afc.orbit.sla.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.afc.orbit.sla.model.Contract;

import java.util.Date;
import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, String> {
    List<Contract> findByIsDeletedTrue();

    List<Contract> findByIsDeletedNullOrIsDeletedFalse();

    List<Contract> findByCompletionDeadlineBeforeAndHasEscalatedFalseAndStatus(Date completionDeadline, Integer status);


}