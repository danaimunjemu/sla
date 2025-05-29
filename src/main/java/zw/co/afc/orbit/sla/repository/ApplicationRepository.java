package zw.co.afc.orbit.sla.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zw.co.afc.orbit.sla.model.Application;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, String> {
    boolean existsByNameIgnoreCase(String name);

    List<Application> findByIsDeletedTrue();
    List<Application> findByIsDeletedNullOrIsDeletedFalse();
}