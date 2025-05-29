package zw.co.afc.orbit.sla.messaging.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import zw.co.afc.orbit.sla.model.Record;

@Component
@RequiredArgsConstructor
public class RecordUpdateProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public void sendRecordUpdate(Record record, String application) {

        String recordUpdateString = null;

        try {
            recordUpdateString = objectMapper.writeValueAsString(record);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        kafkaTemplate.send("sla-record-status-update-" + application.toLowerCase(), recordUpdateString).whenComplete(((sendResult, throwable) -> {
            if (throwable ==null) {
                System.out.println(sendResult);
            } else {
                throwable.printStackTrace();
            }
        } ));
    }


}
