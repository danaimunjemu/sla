package zw.co.afc.orbit.sla.messaging.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import zw.co.afc.orbit.sla.dto.request.record.CreateRecordRequestDTO;
import zw.co.afc.orbit.sla.dto.request.record.UpdateRecordRequestDTO;
import zw.co.afc.orbit.sla.enums.Action;
import zw.co.afc.orbit.sla.enums.Status;
import zw.co.afc.orbit.sla.model.Agreement;
import zw.co.afc.orbit.sla.model.AgreementDetails;
import zw.co.afc.orbit.sla.model.Record;
import zw.co.afc.orbit.sla.model.SLA;
import zw.co.afc.orbit.sla.processor.CreateRecordProcessor;
import zw.co.afc.orbit.sla.processor.contract.SequentialCollaborativeContractProcessor;
import zw.co.afc.orbit.sla.processor.record.SequentialCollaborativeRecordProcessor;
import zw.co.afc.orbit.sla.repository.RecordRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class RecordConsumer {

    private final CreateRecordProcessor createRecordProcessor;
    private final RecordRepository recordRepository;
    private final SequentialCollaborativeRecordProcessor sequentialCollaborativeRecordProcessor;
    private final SequentialCollaborativeContractProcessor sequentialCollaborativeContractProcessor;

    @KafkaListener(id="createRecordListener", topics = "sla-record-create")
    public void consumeCreateRecordEvent(String createRecord){
        log.info("MESSAGE: Ticket event received: {}", createRecord);
        try {
            CreateRecordRequestDTO createRecordRequest = new ObjectMapper().readValue(createRecord, CreateRecordRequestDTO.class);
            log.info("Now creating the record.");
            createRecordProcessor.createRecord(createRecordRequest);
        } catch (IOException e) {
            log.error("Error deserializing record event message", e);
        }
    }

    @KafkaListener(id="updateRecordListener", topics = "sla-record-update")
    public void consumeUpdateRecordEvent(String updateRecord){
        log.info("MESSAGE: Ticket event received: {}", updateRecord);
        log.info("This is for SLA Record Update");
        try {
            UpdateRecordRequestDTO updateRecordRequest = new ObjectMapper().readValue(updateRecord, UpdateRecordRequestDTO.class);
            // reject action create
            if (updateRecordRequest.action() == Action.CREATE) {
                log.error("You cannot create a record on this topic");
                return;
            }
            Optional<Record> record = recordRepository.findByReferenceNumber(updateRecordRequest.referenceNumber());
            if (record.isPresent()){
                // if the record is closed, no action can be done
                if (record.get().getStatus() == Status.CLOSED) {
                    return;
                }
                String slaTypeAndExtendedType = getSlaTypeAndExtendedType(record.get().getAgreement().getSla());
                processUpdateContract(record.get(), updateRecordRequest,slaTypeAndExtendedType);
            } else {
                log.info("The record you are trying to update does not exist");
            }
        } catch (IOException e) {
            log.error("Error deserializing record event message", e);
        }
    }

    private String getSlaTypeAndExtendedType(SLA sla) {
        return sla.getSlaType() + "_" + sla.getExtendedSLAType();
    }

//    private void processUpdateRecordContract(Record record, Agreement agreement, List<AgreementDetails> agreementDetails, String slaTypeAndExtendedType){
//        switch (slaTypeAndExtendedType) {
//            case "SEQUENTIAL_COLLABORATIVE":
//                log.info("Now processing the contract for SEQUENTIAL_COLLABORATIVE");
//                log.info("Agreement details: {}", agreementDetails);
//                sequentialCollaborativeRecordProcessor.updateRecord(record, agreement, agreementDetails);
//                break;
//            case "SEQUENTIAL_APPROVAL_CHAIN":
//                log.info("Sequential approval chain");
//                break;
//            default:
//                log.error("Unsupported SLA and Extended SLA Type");
//                break;
//        }
//    }


    private void processUpdateContract(Record record, UpdateRecordRequestDTO updateRecordRequest, String slaTypeAndExtendedType) {
        switch (slaTypeAndExtendedType) {
            case "SEQUENTIAL_COLLABORATIVE":
                log.info("Now processing the contract for SEQUENTIAL_COLLABORATIVE");
                sequentialCollaborativeContractProcessor.updateContracts(record, updateRecordRequest);
                break;
            case "SEQUENTIAL_APPROVAL_CHAIN":
                log.info("Sequential approval chain");
                break;
            default:
                log.error("Unsupported SLA and Extended SLA Type");
                break;
        }
    }

}
