package zw.co.afc.orbit.sla.processor.contract;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import zw.co.afc.orbit.sla.config.ServiceConfig;
import zw.co.afc.orbit.sla.dto.request.record.UpdateRecordRequestDTO;
import zw.co.afc.orbit.sla.enums.Action;
import zw.co.afc.orbit.sla.enums.EscalationType;
import zw.co.afc.orbit.sla.enums.Role;
import zw.co.afc.orbit.sla.enums.Status;
import zw.co.afc.orbit.sla.messaging.producer.RecordUpdateProducer;
import zw.co.afc.orbit.sla.model.Agreement;
import zw.co.afc.orbit.sla.model.AgreementDetails;
import zw.co.afc.orbit.sla.model.Contract;
import zw.co.afc.orbit.sla.model.Record;
import zw.co.afc.orbit.sla.processor.record.SequentialCollaborativeRecordProcessor;
import zw.co.afc.orbit.sla.repository.ContractRepository;
import zw.co.afc.orbit.sla.repository.RecordRepository;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class SequentialCollaborativeContractProcessor  {
    private final ContractRepository contractRepository;
    private final RecordRepository recordRepository;
    private final SequentialCollaborativeRecordProcessor sequentialCollaborativeRecordProcessor;
    private final RecordUpdateProducer recordUpdateProducer;
    private final ServiceConfig config;

    public void createContracts(Record record, Agreement agreement, List<AgreementDetails> agreementDetails) {
        log.info("Now processing create contracts");
        log.info("Agreement Details: {}", agreementDetails.toArray().length);

        String defaultUser = agreement.getSla().getApplication().getDefaultUser();

        // process manager's position
        if (agreement.getRequiresManager()) {
            Contract contract = new Contract();
            contract.setCompletionDeadline(getCompletionDeadline(agreement.getManagersTime()));
            contract.setStatus(1);
            contract.setRole(Role.MANAGER);

            Map<String, String> assignedUser = getUserDetails(record.getCreatedBy());
            // TODO deal with a case where manager is null

            if (assignedUser.get("manager") == null || assignedUser.get("manager").isBlank()) {
                // the document creator does not have a manager
                // set both assigned user and escalation user to the default user
                contract.setAssignedUser(defaultUser);
                contract.setEscalationUser(defaultUser);
            } else {
                // this means that the document creator has a manager
                contract.setAssignedUser(assignedUser.get("manager"));

                // now get that manager's manager
                Map<String, String> assigneeManager = getUserDetails(assignedUser.get("manager"));

                // the manager does not have a manager
                if (assigneeManager.get("manager") == null || assigneeManager.get("manager").isBlank()) {
                    contract.setEscalationUser(defaultUser);
                } else {
                    // the manager has a manager
                    contract.setEscalationUser(assigneeManager.get("manager"));
                }
            }

            contract.setDuration(agreement.getManagersTime());

            contract.setEscalationType(EscalationType.COMPLETION);
            contract.setIsDeleted(false);
            contract.setIndex(-1);
            contract.setRecord(record);
            contract.setCompletionTime(null); // this is the actual time the contract was completed
            contract.setEscalationTrialCount(0); // only when escalating
            contract.setEscalationTime(null); // only when escalating
            contract.setHasEscalated(false); // only when escalating
            contract.setSlaViolation(null); // only when escalating

            if (agreement.getHasReminder()) {
                contract.setReminders(null);
            }

            log.info("Now saving the contract");
            contractRepository.save(contract);
        }


        for (AgreementDetails agreementDetail: agreementDetails) {
            log.info("Agreement detail: {}", agreementDetail);
            Contract contract = new Contract();
            if (agreementDetail.getIndex() == 0 && !agreement.getRequiresManager()) {
                contract.setCompletionDeadline(getCompletionDeadline(agreementDetail.getTime()));
                contract.setStatus(1);
            } else {
                contract.setStatus(0);
            }
            contract.setRole(agreementDetail.getRole());
            contract.setAssignedUser(agreementDetail.getAssignedUser());
            contract.setEscalationUser(agreementDetail.getEscalationUser());
            contract.setDuration(agreementDetail.getTime());

            contract.setEscalationType(agreementDetail.getEscalationType());
            contract.setIsDeleted(false);
            contract.setIndex(agreementDetail.getIndex());
            contract.setRecord(record);

            contract.setCompletionTime(null); // this is the actual time the contract was completed
            contract.setEscalationTrialCount(0); // only when escalating
            contract.setEscalationTime(null); // only when escalating
            contract.setHasEscalated(false); // only when escalating
            contract.setSlaViolation(null); // only when escalating

            if (agreement.getHasReminder()) {
                contract.setReminders(null);
            }

            log.info("Now saving the contract");
            contractRepository.save(contract);

        }

        log.info("Now getting the record");
        log.info(record.getReferenceNumber());

        Record completeRecord = recordRepository.findByReferenceNumber(record.getReferenceNumber()).get();
        log.info("Got the record");
        String application = completeRecord.getAgreement().getSla().getApplication().getName();

        recordUpdateProducer.sendRecordUpdate(completeRecord, application);




    }

    public void updateContracts(Record record, UpdateRecordRequestDTO updateRecordRequest) {
        // check if the actor is authorised to act
        List<Contract> contracts = record.getContracts();
        Boolean isAllowedToAct = isAllowedToAct(contracts, updateRecordRequest);
        if (isAllowedToAct){
            switch (updateRecordRequest.action()){
                case PROGRESS -> progressAction(record, updateRecordRequest);
                case CLOSE -> closeAction(record, updateRecordRequest);
                case HOLD -> holdAction(record, updateRecordRequest);
                case REASSIGN -> reassignAction(record, updateRecordRequest);
                default -> log.error("The action, {}, for SEQUENTIAL_COLLABORATIVE IS INVALID.", updateRecordRequest.action());
            }
        }
    }

    public void closeContracts(Record record) {

    }

    public Map<String, String> getUserDetails(String email) {
        // Prepare request payload
        Map<String, String> enquiryRequest = Collections.singletonMap("email", email);
        log.info("Sending user enquiry request: {}", enquiryRequest);

        // Call the external service
        ResponseEntity<List> response = config.restTemplate().postForEntity(
                config.userSearchUrl(),
                enquiryRequest,
                List.class
        );

        // Check response status
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("User search failed with status: {}", response.getStatusCode());
            return Collections.emptyMap();
        }

        // Process response
        List<?> listResponse = response.getBody();
        if (listResponse == null || listResponse.isEmpty()) {
            log.warn("No users found for email: {}", email);
            return Collections.emptyMap();
        }

        // Extract first user details
        Map<String, Object> item = (Map<String, Object>) listResponse.get(0);

        String name = (String) item.get("name");
        String userEmail = (String) item.get("email");
        String branch = (String) item.get("branch");
        String manager = (String) item.get("manager");

        // Build response map
        Map<String, String> userResponse = new HashMap<>();
        userResponse.put("name", name);
        userResponse.put("email", userEmail);
        userResponse.put("branch", branch);
        userResponse.put("manager", manager);

        log.info("User details found: {}", userResponse);
        return userResponse;
    }


    private Timestamp getCompletionDeadline(Integer time) {
        log.info("Getting completion deadline");
        // Get the current time
        LocalDateTime now = LocalDateTime.now();

        // Add the time from AgreementDetails to the current time
        // Assuming `time` is in minutes
        LocalDateTime completionDeadline = now.plus(Duration.ofMinutes(time));

        // Set the completionDeadline on the contract
        return Timestamp.valueOf(completionDeadline);
    }

    // TODO move this function to a common util
    private Boolean isAllowedToAct(List<Contract> contracts, UpdateRecordRequestDTO updateRecordRequest) {
        if (updateRecordRequest.action().equals(Action.REASSIGN)) {
            return true;
        }

        Boolean isAllowedToAct = contracts.stream()
                .filter(contract -> contract.getStatus() == 1)
                .anyMatch(contract -> updateRecordRequest.actor().equals(contract.getAssignedUser()));
        log.info("Actor: {}", updateRecordRequest.actor());
        log.info("Is allowed to act?: {}", isAllowedToAct);
        log.error("This user is not allowed to action the Record");
        return isAllowedToAct;
    }

    private void progressAction(Record record, UpdateRecordRequestDTO updateRecordRequest){
        List<Contract> contracts = record.getContracts();

        // Find the affected contract
        Integer actionContractIndex = contracts.stream()
                .filter(contract -> updateRecordRequest.actor().equals(contract.getAssignedUser()))
                .findFirst()
                .orElse(null).getIndex();

        contracts.stream()
                .filter(contract -> updateRecordRequest.actor().equals(contract.getAssignedUser()))
                .forEach(contract -> {
                    contract.setCompletionTime(new Date());
                    contract.setStatus(3);
                    contractRepository.save(contract);
                });


        // Update all other contracts with the same index to status 4
        contracts.stream()
                .filter(contract -> contract.getIndex().equals(actionContractIndex) && !contract.getAssignedUser().equals(updateRecordRequest.actor()) && contract.getStatus() != 2)
                .forEach(contract -> {
                    contract.setCompletionTime(new Date());
                    contract.setStatus(4);
                    contractRepository.save(contract);
                });

        contractRepository.saveAll(contracts);
        nextActionerProcessor(record, updateRecordRequest, contracts, actionContractIndex);

    }

    private void nextActionerProcessor(Record record, UpdateRecordRequestDTO updateRecordRequest, List<Contract> contracts, Integer actionContractIndex) {
        // Track if any contracts with index + 1 were found
        Boolean hasNextIndexContract = contracts.stream()
                .filter(contract -> contract.getStatus() == 0)
                .anyMatch(contract -> contract.getIndex().equals(actionContractIndex + 1));
        contracts.stream()
                .filter(contract -> contract.getIndex().equals(actionContractIndex + 1))
                .forEach(nextContract -> {
                    nextContract.setStatus(1);
                    nextContract.setCompletionDeadline(getCompletionDeadline(nextContract.getDuration()));
                    contractRepository.save(nextContract);
                });

        sequentialCollaborativeRecordProcessor.updateRecord(record, updateRecordRequest.action(), hasNextIndexContract);
    }

    private void closeAction(Record record, UpdateRecordRequestDTO updateRecordRequest){
        List<Contract> contracts = record.getContracts();

        // Find the affected contract
        Integer actionContractIndex = contracts.stream()
                .filter(contract -> updateRecordRequest.actor().equals(contract.getAssignedUser()))
                .findFirst()
                .orElse(null).getIndex();

        contracts.stream()
                .filter(contract -> updateRecordRequest.actor().equals(contract.getAssignedUser()))
                .forEach(contract -> {
                    contract.setCompletionTime(new Date());
                    contract.setStatus(3);
                    contractRepository.save(contract);
                });


        // Update all other contracts that haven't been updated to status 4
        contracts.stream()
                .filter(contract -> (contract.getStatus().equals(0) || contract.getStatus().equals(1)) && !contract.getAssignedUser().equals(updateRecordRequest.actor()))
                .forEach(contract -> {
                    contract.setCompletionTime(new Date());
                    contract.setStatus(4);
                    contractRepository.save(contract);
                });

        sequentialCollaborativeRecordProcessor.updateRecord(record, updateRecordRequest.action(), false);
    }
    private void holdAction(Record record, UpdateRecordRequestDTO updateRecordRequest){
        sequentialCollaborativeRecordProcessor.updateRecord(record, updateRecordRequest.action(), false);
    }
    private void reassignAction(Record record, UpdateRecordRequestDTO updateRecordRequest){
        List<Contract> contracts = record.getContracts();

        Contract replacementContract = new Contract();
        Contract oldContract = contracts.stream()
                .filter(contract -> updateRecordRequest.oldUser().equals(contract.getAssignedUser()))
                .findFirst().get();
        // check if old contract is current
        if (oldContract.getStatus() == 1) {
            replacementContract.setStatus(1);
            replacementContract.setCompletionDeadline(getCompletionDeadline(oldContract.getDuration()));
        } else {
            replacementContract.setStatus(0);
        }

        replacementContract.setIndex(oldContract.getIndex());

        replacementContract.setRole(oldContract.getRole());
        replacementContract.setAssignedUser(updateRecordRequest.replacementUser());
        replacementContract.setEscalationUser(oldContract.getEscalationUser());
        replacementContract.setDuration(oldContract.getDuration());

        replacementContract.setEscalationType(oldContract.getEscalationType());
        replacementContract.setIsDeleted(false);
        replacementContract.setRecord(record);

        replacementContract.setCompletionTime(null); // this is the actual time the contract was completed
        replacementContract.setEscalationTrialCount(0); // only when escalating
        replacementContract.setEscalationTime(null); // only when escalating
        replacementContract.setHasEscalated(false); // only when escalating
        replacementContract.setSlaViolation(null); // only when escalating

        contracts.add(replacementContract);

        //update old contract
        oldContract.setCompletionTime(new Date());
        oldContract.setStatus(2);

        contractRepository.saveAll(contracts);
        sequentialCollaborativeRecordProcessor.updateRecord(record, updateRecordRequest.action(), false);
    }

}
