package zw.co.afc.orbit.sla.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zw.co.afc.orbit.sla.dto.request.record.CreateRecordRequestDTO;
import zw.co.afc.orbit.sla.enums.Status;
import zw.co.afc.orbit.sla.exception.RecordExistsException;
import zw.co.afc.orbit.sla.handler.AgreementHandler;
import zw.co.afc.orbit.sla.model.*;
import zw.co.afc.orbit.sla.model.Record;
import zw.co.afc.orbit.sla.processor.contract.SequentialCollaborativeContractProcessor;
import zw.co.afc.orbit.sla.repository.AgreementRepository;
import zw.co.afc.orbit.sla.repository.RecordRepository;
import zw.co.afc.orbit.sla.util.RecordDurationCalculatorUtil;
import zw.co.afc.orbit.sla.util.SLACodeUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateRecordProcessor {
    private final AgreementHandler agreementHandler;
    private final RecordRepository recordRepository;
    private final AgreementRepository agreementRepository;

    private final SequentialCollaborativeContractProcessor sequentialCollaborativeContractProcessor;

    public void createRecord(CreateRecordRequestDTO recordRequest) {

// CHECKS -> 1) record does not exist 2) Agreement exists and was not deleted 3) Get agreement

        // 1) Check if the record exists first
        Optional<Record> existingRecord = recordRepository.findByReferenceNumber(recordRequest.referenceNumber());

        // TODO -> CRITICAL -> Error Topic / save to error table
        if (existingRecord.isPresent()) {
            throw new RecordExistsException();
        }

        // 2) check if the agreement exists and was not deleted
        if (agreementHandler.exists(recordRequest.agreementId())) {

            Agreement agreement = agreementHandler.getAgreement(recordRequest.agreementId());
// EXECUTION -> 1) Create and save the new record 2) Process contract creation

            // 1) Create and save the new record
            Record record = new Record();
            record.setReferenceNumber(recordRequest.referenceNumber());
            record.setSlaCode(SLACodeUtil.slaCodeGenerator(agreement.getSla().getSlaType(), agreement.getSla().getExtendedSLAType()));
            record.setDuration(RecordDurationCalculatorUtil.calculateRecordDuration(record.getSlaCode(), agreement.getSla().getContractsRunConcurrently(), agreement.getAgreementDetails()));
            record.setCompletionDeadline(calculateCompletionDeadline(record.getDuration()));
            record.setCompletionTime(null); // this is the actual completion time, is set to null initially
            record.setStatus(Status.OPEN);
            record.setHoldCount(0);
            record.setCreatedBy(recordRequest.actor());
            record.setIsDeleted(false);
            record.setAgreement(agreement);

            log.info("Now saving the record");
            recordRepository.save(record);
            log.info("Record has been saved");

            // TODO set reminders -> in contract processor

            for (AgreementDetails agreementDetail: agreement.getAgreementDetails()) {
                log.info("Here is the stuff: {}", agreementDetail.getAssignedUser());
            }

            // 2) Process contracts
            SLA recordSLA = getSLA(recordRequest.agreementId());
            String slaTypeAndExtendedType = getSlaTypeAndExtendedType(recordSLA);
            log.info("Agreement: {}", agreement);
            processCreateRecordContract(record, agreement, agreement.getAgreementDetails(), slaTypeAndExtendedType);
        }
    }

    private void processCreateRecordContract(Record record, Agreement agreement, List<AgreementDetails> agreementDetails, String slaTypeAndExtendedType){
            switch (slaTypeAndExtendedType) {
                case "SEQUENTIAL_COLLABORATIVE":
                    log.info("Now processing the contract for SEQUENTIAL_COLLABORATIVE");
                    log.info("Agreement details: {}", agreementDetails);
                    sequentialCollaborativeContractProcessor.createContracts(record, agreement, agreementDetails);
                    break;
                case "SEQUENTIAL_APPROVAL_CHAIN":
                    log.info("Sequential approval chain");
                    break;
                default:
                    log.error("Unsupported SLA and Extended SLA Type");
                    break;
            }
    }

    private SLA getSLA(String agreementId) {
        Optional<Agreement> agreement = agreementRepository.findById(agreementId);
        if (agreement.isPresent()) {
            return agreement.get().getSla();
        } else {
            // TODO save this someone to show that it was not processed and can be examined
            log.error("The Agreement does not exist");
        }
        return null;
    }

    public static Date calculateCompletionDeadline(int durationInMinutes) {
        // Get the current date and time
        Date now = new Date();

        // Use Calendar to add the duration to the current time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, durationInMinutes);

        // Return the new Date object
        return calendar.getTime();
    }

    private String getSlaTypeAndExtendedType(SLA sla) {
        return sla.getSlaType() + "_" + sla.getExtendedSLAType();
    }
}
