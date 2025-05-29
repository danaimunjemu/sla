package zw.co.afc.orbit.sla.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import zw.co.afc.orbit.sla.dto.request.AgreementRequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;
import zw.co.afc.orbit.sla.model.Agreement;
import zw.co.afc.orbit.sla.model.AgreementDetails;
import zw.co.afc.orbit.sla.model.SLA;
import zw.co.afc.orbit.sla.repository.AgreementRepository;
import zw.co.afc.orbit.sla.repository.SLARepository;
import zw.co.afc.orbit.sla.service.iface.AgreementService;
import zw.co.afc.orbit.sla.util.ResponseUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgreementServiceImpl implements AgreementService {

    private final AgreementRepository agreementRepository;
    private final SLARepository slaRepository;

    @Override
    public ResponseEntity<APIResponse> createAgreement(AgreementRequestDTO agreementRequest) {
        // TODO agreement creation needs to conform to the SLA and Extended SLA type
        if (agreementRepository.existsByNameIgnoreCaseAndSla_Id(agreementRequest.name(), agreementRequest.slaId())) {
            return ResponseUtil.createResponse(
                    400,
                    false,
                    "Agreement Creation Failed",
                    "Agreement with the same name already exists in the specified SLA",
                    null,
                    null
            );
        }

        SLA sla = slaRepository.findById(agreementRequest.slaId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid SLA ID"));

        Agreement agreement = getAgreement(agreementRequest, sla);

        agreement.validateContracts(sla);


        Agreement savedAgreement = agreementRepository.save(agreement);

        return ResponseUtil.createResponse(
                201,
                true,
                "Agreement created successfully",
                "Your Agreement was created successfully",
                null,
                savedAgreement
        );


    }



    @Override
    public ResponseEntity<APIResponse> getAgreements(Boolean isDeleted) {
        List<Agreement> agreements = new ArrayList<>();
        if (isDeleted) {
            agreements = agreementRepository.findByIsDeletedTrue();
        } else {
            agreements = agreementRepository.findByIsDeletedNullOrIsDeletedFalse();
        }

        return ResponseUtil.createResponse(
                200,
                true,
                "Agreements retrieved successfully",
                "We have retrieved " + agreements.size() +" agreements successfully",
                null,
                agreements
        );
    }

    @Override
    public ResponseEntity<APIResponse> getAgreement(String id) {
        Agreement agreement = agreementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The Agreement with id " + id + " was not found."));
        return ResponseUtil.createResponse(
                200,
                true,
                "Agreement retrieved successfully",
                "We have retrieved the Agreement successfully",
                null,
                agreement
        );
    }

    @Override
    public ResponseEntity<APIResponse> updateAgreement(String id, AgreementRequestDTO agreementRequest) {
        Agreement agreement = agreementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The Agreement with id " + id + " was not found."));

        // Check if the updated name and category ID are different from the current values
        boolean isNameOrSLAChanged = !agreement.getName().equalsIgnoreCase(agreementRequest.name())
                || !agreement.getSla().getId().equals(agreementRequest.slaId());

        if (isNameOrSLAChanged && slaRepository.existsByNameIgnoreCaseAndApplication_Id(agreementRequest.name(), agreementRequest.slaId())) {
            return ResponseUtil.createResponse(
                    400,
                    false,
                    "Agreement Update Failed",
                    "Agreement with the same name already exists in the specified SLA",
                    null,
                    null
            );
        }

        SLA sla = slaRepository.findById(agreementRequest.slaId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid SLA ID"));

        agreement.setName(agreementRequest.name());
        agreement.setOwner(agreementRequest.owner());
        agreement.setDescription(agreementRequest.description());
        agreement.setHasReminder(agreementRequest.hasReminder());
        if (agreementRequest.hasReminder()) {
            agreement.setReminderTime(agreementRequest.reminderTime());
        }
        agreement.setSla(sla);

        // TODO Validate and ensure all required fields are provided
        agreement.setAgreementDetails(agreementRequest.agreementDetails());

        agreement.validateContracts(sla);

        Agreement updatedAgreement = agreementRepository.save(agreement);

        return ResponseUtil.createResponse(
                200,
                true,
                "Agreement updated successfully",
                "We have updated the Agreement successfully",
                null,
                updatedAgreement
        );


    }

    @Override
    public ResponseEntity<APIResponse> deleteAgreement(String id) {
        // TODO check if it is not deleted in the first place
        Agreement agreement = agreementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The Agreement with id " + id + " was not found."));

        agreement.setIsDeleted(true);

        Agreement deletedAgreement = agreementRepository.save(agreement);
        return ResponseUtil.createResponse(
                200,
                true,
                "Agreement deleted successfully",
                "We have deleted the agreement successfully",
                null,
                deletedAgreement
        );
    }

    @Override
    public ResponseEntity<APIResponse> restoreAgreement(String id) {

        // TODO check if it was deleted in the first place
        Agreement agreement = agreementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The Agreement with id " + id + " was not found."));

        agreement.setIsDeleted(false);

        Agreement restoredAgreement = agreementRepository.save(agreement);
        return ResponseUtil.createResponse(
                200,
                true,
                "Agreement restored successfully",
                "We have restored the agreement successfully",
                null,
                restoredAgreement
        );
    }


    private static Agreement getAgreement(AgreementRequestDTO agreementRequest, SLA sla) {
        Agreement agreement = new Agreement();
        agreement.setName(agreementRequest.name());
        agreement.setOwner(agreementRequest.owner());
        agreement.setDescription(agreementRequest.description());
        agreement.setCyclicUser(agreementRequest.cyclicUser());
        agreement.setRequiresManager(agreementRequest.requiresManager());
        agreement.setManagersTime(agreementRequest.managersTime());
        agreement.setHasReminder(agreementRequest.hasReminder());
        if (agreementRequest.hasReminder()) {
            agreement.setReminderTime(agreementRequest.reminderTime());
        }
        // TODO a user cannot appear more than once
        agreement.setSla(sla);
        // Initialize agreement details
        List<AgreementDetails> details = agreementRequest.agreementDetails();
        for (AgreementDetails singleDetail : details) {
            if (singleDetail.getIsDefaultEscalation()) {
                singleDetail.setEscalationUser(agreement.getSla().getApplication().getDefaultUser()); // check if default user
            }
            singleDetail.setAgreement(agreement); // Set the bidirectional reference
        }
        agreement.setAgreementDetails(details);
        agreement.setIsDeleted(false);
        return agreement;
    }
}
