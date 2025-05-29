package zw.co.afc.orbit.sla.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import zw.co.afc.orbit.sla.dto.request.SLARequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;
import zw.co.afc.orbit.sla.model.Application;
import zw.co.afc.orbit.sla.model.SLA;
import zw.co.afc.orbit.sla.repository.ApplicationRepository;
import zw.co.afc.orbit.sla.repository.SLARepository;
import zw.co.afc.orbit.sla.service.iface.SLAService;
import zw.co.afc.orbit.sla.util.ResponseUtil;
import zw.co.afc.orbit.sla.util.SLAValidatorUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SLAServiceImpl implements SLAService {

    private final SLARepository slaRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    public ResponseEntity<APIResponse> createSLA(SLARequestDTO slaRequest) {
        log.info("SLA Name: {}", slaRequest.name());
        log.info("Application ID: {}", slaRequest.applicationId());
        if (slaRepository.existsByNameIgnoreCaseAndApplication_Id(slaRequest.name(), slaRequest.applicationId())) {
            return ResponseUtil.createResponse(
                    400,
                    false,
                    "SLA Creation Failed",
                    "SLA with the same name already exists in the specified application",
                    null,
                    null
            );
        }

        Application application = applicationRepository.findById(slaRequest.applicationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid application ID"));

        if (!SLAValidatorUtil.isValidSLACombination(slaRequest.slaType(), slaRequest.extendedSLAType())) {
            return ResponseUtil.createResponse(
                    400,
                    false,
                    "SLA Creation Failed",
                    "Invalid SLAType and ExtendedSLAType combination",
                    null,
                    null
            );
        }

        SLA sla = new SLA();
        sla.setName(slaRequest.name());
        sla.setSlaType(slaRequest.slaType());
        sla.setQuota(slaRequest.quota());
        sla.setExtendedSLAType(slaRequest.extendedSLAType());
        sla.setNumberOfContracts(slaRequest.numberOfContracts());
        sla.setContractsRunConcurrently(slaRequest.contractsRunConcurrently());
        sla.setApplication(application);
        sla.setIsDeleted(false);

        SLA savedSLA = slaRepository.save(sla);

        return ResponseUtil.createResponse(
                201,
                true,
                "SLA created successfully",
                "Your SLA was created successfully",
                null,
                savedSLA
        );

    }

    @Override
    public ResponseEntity<APIResponse> getSLAs(Boolean isDeleted) {
        List<SLA> slas = new ArrayList<>();
        if (isDeleted) {
            slas = slaRepository.findByIsDeletedTrue();
        } else {
            slas = slaRepository.findByIsDeletedNullOrIsDeletedFalse();
        }

        return ResponseUtil.createResponse(
                200,
                true,
                "SLAs retrieved successfully",
                "We have retrieved " + slas.size() +" SLAs successfully",
                null,
                slas
        );
    }

    @Override
    public ResponseEntity<APIResponse> getSLA(String id) {
        SLA sla = slaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The SLA with id " + id + " was not found."));
        return ResponseUtil.createResponse(
                200,
                true,
                "SLA retrieved successfully",
                "We have retrieved the SLA successfully",
                null,
                sla
        );
    }

    @Override
    public ResponseEntity<APIResponse> updateSLA(String id, SLARequestDTO slaRequest) {
        // Find the subcategory by ID or throw an exception if not found
        SLA sla = slaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The SLA with id " + id + " was not found."));

        // Check if the updated name and category ID are different from the current values
        boolean isNameOrApplicationChanged = !sla.getName().equalsIgnoreCase(slaRequest.name())
                || !sla.getApplication().getId().equals(slaRequest.applicationId());

        // If the name or category ID is changed, check for uniqueness
        if (isNameOrApplicationChanged && slaRepository.existsByNameIgnoreCaseAndApplication_Id(slaRequest.name(), slaRequest.applicationId())) {
            return ResponseUtil.createResponse(
                    400,
                    false,
                    "SLA Update Failed",
                    "SLA with the same name already exists in the specified application",
                    null,
                    null
            );
        }

        if (!SLAValidatorUtil.isValidSLACombination(slaRequest.slaType(), slaRequest.extendedSLAType())) {
            return ResponseUtil.createResponse(
                    400,
                    false,
                    "SLA Creation Failed",
                    "Invalid SLAType and ExtendedSLAType combination",
                    null,
                    null
            );
        }

        // Find the category by ID or throw an exception if not found
        Application application = applicationRepository.findById(slaRequest.applicationId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid application ID"));

        // Update the subcategory details
        sla.setName(slaRequest.name());
        sla.setSlaType(slaRequest.slaType());
        sla.setExtendedSLAType(slaRequest.extendedSLAType());
        sla.setNumberOfContracts(slaRequest.numberOfContracts());
        sla.setContractsRunConcurrently(slaRequest.contractsRunConcurrently());
        sla.setQuota(slaRequest.quota());
        sla.setApplication(application);

        // Save the updated subcategory
        SLA updatedSLA = slaRepository.save(sla);

        return ResponseUtil.createResponse(
                200,
                true,
                "SLA updated successfully",
                "We have updated the SLA successfully",
                null,
                updatedSLA
        );
    }

    @Override
    public ResponseEntity<APIResponse> deleteSLA(String id) {
        SLA sla = slaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The SLA with id " + id + " was not found."));

        sla.setIsDeleted(true);

        SLA deletedSLA = slaRepository.save(sla);

        return ResponseUtil.createResponse(
                200,
                true,
                "SLA deleted successfully",
                "We have deleted the SLA successfully",
                null,
                deletedSLA
        );
    }

    @Override
    public ResponseEntity<APIResponse> restoreSLA(String id) {
        SLA sla = slaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The SLA with id " + id + " was not found."));

        sla.setIsDeleted(false);

        SLA restoredSLA = slaRepository.save(sla);

        return ResponseUtil.createResponse(
                200,
                true,
                "SLA restored successfully",
                "We have restored the SLA successfully",
                null,
                restoredSLA
        );
    }
}
