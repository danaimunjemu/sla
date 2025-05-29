package zw.co.afc.orbit.sla.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import zw.co.afc.orbit.sla.dto.request.NotificationRequest;
import zw.co.afc.orbit.sla.model.Contract;
import zw.co.afc.orbit.sla.repository.ContractRepository;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class EscalatorScheduler {

    private final ContractRepository contractRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 60000)
    public void runEscalationChecks(){
        Date currentTime = new Date();

        log.info("\n \n \uD83D\uDD14 ESCALATOR STARTED AT {} \uD83D\uDD14 \n", currentTime );

        // Fetch contracts where the current time is greater than the dueDateTime
        List<Contract> contracts = contractRepository.findByCompletionDeadlineBeforeAndHasEscalatedFalseAndStatus(currentTime, 1);

        if (contracts.isEmpty()) {
            log.info("\uD83D\uDD14 NO CONTRACTS TO ESCALATE" );
        } else {
            // Process the fetched contracts as needed
            for (Contract contract : contracts) {
                log.info("=== PROCESSING CONTRACT START: {} ===", contract.getRecord().getReferenceNumber());
                log.info("=== ESCALATING: {} ===", contract.getAssignedUser());
                log.info("=== ESCALATING TO: {} ===", contract.getEscalationUser());
                // Perform the necessary escalation actions
                // For example, send an email, update status, etc.
                log.info("Escalating contract: {} and type {}", contract.getRecord().getReferenceNumber(), contract.getEscalationType()  );


                NotificationRequest notificationRequest = new NotificationRequest(
                        "DEFAULT",
                        List.of(contract.getEscalationUser()),
                        contract.getRecord().getReferenceNumber() + " - Escalation Notice: Pending Action on Document",
                        "This is an automated notification regarding an escalation for the document assigned to " + contract.getAssignedUser() + " which has not been actioned within the required timeframe.",
                        "SLA",
                        null,
                        null
                );

                String notificationRequestString = null;
                try {
                    notificationRequestString = objectMapper.writeValueAsString(notificationRequest);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }

                kafkaTemplate.send("notification-notify", notificationRequestString).whenComplete(((sendResult, throwable) -> {
                    if (throwable ==null) {
                        System.out.println(sendResult);
                    } else {
                        throwable.printStackTrace();
                    }
                } ));



                contract.setEscalationTrialCount(contract.getEscalationTrialCount() + 1);
                contract.setHasEscalated(true);
//                if (escalationResponse) {
//                    contract.setHasEscalated(true);
//                    contract.setEscalationTime(LocalToDateConverterUtil.convertLocalToDate(LocalDateTime.now()));
//                    slaBreachLogService.addBreach(contract);
//                }
                contractRepository.save(contract);

                // TODO Put back for when the fixes have been applied
//                if (!contract.getHasEscalated()) {
//                    ISupportTicketUpdateTopicDTO iSupportTicketUpdateTopic = new ISupportTicketUpdateTopicDTO();
//                    iSupportTicketUpdateTopic.setStatus(ISupportRecordStatus.OVERDUE);
//                    iSupportTicketUpdateTopic.setDocId(contract.getDocId());
//                    ticketUpdateProducer.sendTicketUpdate(iSupportTicketUpdateTopic);
//                }
                log.info("=== PROCESSING CONTRACT END: {} ===", contract.getRecord().getReferenceNumber());
            }
        }



        log.info("\n \n \uD83D\uDD14 ESCALATOR ENDED AT {} \uD83D\uDD14 \n", currentTime );
    }

}
