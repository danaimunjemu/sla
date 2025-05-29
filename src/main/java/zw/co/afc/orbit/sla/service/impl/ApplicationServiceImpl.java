package zw.co.afc.orbit.sla.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import zw.co.afc.orbit.sla.dto.request.ApplicationRequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;
import zw.co.afc.orbit.sla.model.Application;
import zw.co.afc.orbit.sla.repository.ApplicationRepository;
import zw.co.afc.orbit.sla.service.iface.ApplicationService;
import zw.co.afc.orbit.sla.util.ResponseUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;

    @Override
    public ResponseEntity<APIResponse> createService(ApplicationRequestDTO serviceRequest) {
        if (applicationRepository.existsByNameIgnoreCase(serviceRequest.name())){
            return ResponseUtil.createResponse(
                    400,
                    false,
                    "Service Creation Failed",
                    "Service name must be unique",
                    null,
                    null
            );
        }

        Application service = new Application();
        service.setName(serviceRequest.name());
        service.setBaseUrl(serviceRequest.baseUrl());
        service.setIsDeleted(false);
        Application savedService = applicationRepository.save(service);
        return ResponseUtil.createResponse(
                201,
                true,
                "Service created successfully",
                "Your service was created successfully",
                null,
                savedService
        );
    }

    @Override
    public ResponseEntity<APIResponse> getServices(Boolean isDeleted) {
        List<Application> services = new ArrayList<>();
        if (isDeleted) {
            services = applicationRepository.findByIsDeletedTrue();
        } else {
            services = applicationRepository.findByIsDeletedNullOrIsDeletedFalse();
        }
        return ResponseUtil.createResponse(
                200,
                true,
                "Services retrieved successfully",
                "We have retrieved " + services.size() +" services successfully",
                null,
                services
        );
    }

    @Override
    public ResponseEntity<APIResponse> getService(String id) {
        Application service = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The service with id " + id + " was not found."));
        return ResponseUtil.createResponse(
                200,
                true,
                "Service retrieved successfully",
                "We have retrieved the service successfully",
                null,
                service
        );
    }

    @Override
    public ResponseEntity<APIResponse> updateService(String id, ApplicationRequestDTO serviceRequest) {
        Application service = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The service with id " + id + " was not found."));

        Boolean isNameChanged = !service.getName().equalsIgnoreCase(serviceRequest.name());

        if (isNameChanged && applicationRepository.existsByNameIgnoreCase(serviceRequest.name())){
            return ResponseUtil.createResponse(
                    400,
                    false,
                    "Service update Failed",
                    "Service name must be unique",
                    null,
                    null
            );
        }

        service.setName(serviceRequest.name());
        service.setBaseUrl(serviceRequest.baseUrl());
        Application updatedService = applicationRepository.save(service);

        return ResponseUtil.createResponse(
                200,
                true,
                "Service updated successfully",
                "We have updated the service successfully",
                null,
                updatedService
        );
    }

    @Override
    public ResponseEntity<APIResponse> deleteService(String id) {
        Application service = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The service with id " + id + " was not found."));

        service.setIsDeleted(true);

        Application deletedService = applicationRepository.save(service);
        return ResponseUtil.createResponse(
                200,
                true,
                "Service deleted successfully",
                "We have deleted the service successfully",
                null,
                deletedService
        );
    }

    @Override
    public ResponseEntity<APIResponse> restoreService(String id) {
        Application service = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The service with id " + id + " was not found."));

        service.setIsDeleted(false);

        Application restoredService = applicationRepository.save(service);
        return ResponseUtil.createResponse(
                200,
                true,
                "Service restored successfully",
                "We have restored the service successfully",
                null,
                restoredService
        );
    }
}
