package zw.co.afc.orbit.sla.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zw.co.afc.orbit.sla.exception.AgreementDoesNotExistException;
import zw.co.afc.orbit.sla.model.Agreement;
import zw.co.afc.orbit.sla.repository.AgreementRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgreementHandler {

    private final AgreementRepository agreementRepository;

    /**
     * Checks if an agreement exists in the database
     *
     * @param id String containing the agreement ID.
     */
    public boolean exists(String id) {
        // An agreement exists if it is present and deleted is false
        Optional<Agreement> agreementOptional = agreementRepository.findById(id);
        return agreementOptional.isPresent() && !agreementOptional.get().getIsDeleted();
    }

    /**
     * Fetches an agreement if it exists in the database
     *
     * @param id String containing the agreement ID.
     */
    public Agreement getAgreement(String id) {
        log.info("Now in handler");
        // Retrieve the agreement from the repository
        Agreement agreement = agreementRepository.findById(id)
                .orElseThrow(() -> new AgreementDoesNotExistException("Agreement with id " + id + " does not exist"));

        // Check if the agreement is marked as deleted
        if (agreement.getIsDeleted()) {
            throw new AgreementDoesNotExistException("Agreement with id " + id + " was deleted");
        }
        log.info("Agreement in handler: {}", agreement.getName());

        return agreement;
    }

    /**
     * Saves a new agreement or updates an already existing agreement
     *
     * @param agreement Agreement object.
     */
    public void saveAgreement(Agreement agreement) {
        agreementRepository.save(agreement);
    }

    /**
     * Delete an agreement by ID
     *
     * @param id Agreement object.
     */
    public void deleteAgreement(String id) {
        if (exists(id)) {
            agreementRepository.deleteById(id);
        } else {
            throw new AgreementDoesNotExistException("The agreement was deleted");
        }
    }

    /**
     * Get all agreements
     *
     *
     */
    public List<Agreement> getAllAgreements() {
        return agreementRepository.findAll();
    }

    /**
     * Check if an agreement meets certain criteria
     *
     * @param agreement Agreement Data Transfer Object
     */
    public boolean isValidAgreement(Agreement agreement) {
        // Implement your validation logic here
        return agreement != null && agreement.getAgreementDetails() != null; // Example condition
    }


}
