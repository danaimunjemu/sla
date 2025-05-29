package zw.co.afc.orbit.sla.processor.record;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import zw.co.afc.orbit.sla.dto.request.record.UpdateRecordRequestDTO;
import zw.co.afc.orbit.sla.enums.Action;
import zw.co.afc.orbit.sla.enums.Status;
import zw.co.afc.orbit.sla.model.Agreement;
import zw.co.afc.orbit.sla.model.AgreementDetails;
import zw.co.afc.orbit.sla.model.Contract;
import zw.co.afc.orbit.sla.model.Record;
import zw.co.afc.orbit.sla.repository.RecordRepository;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SequentialCollaborativeRecordProcessor {
    private final RecordRepository recordRepository;
    public void updateRecord(Record record, Action action, Boolean hasNextIndexContract) {
        switch (action) {
            case PROGRESS -> {
                if (hasNextIndexContract) {
                    record.setStatus(Status.IN_PROGRESS); // Assuming `Status` is an enum with IN_PROGRESS value
                } else {
                    record.setCompletionTime(new Date());
                    record.setStatus(Status.CLOSED); // Assuming `Status` is an enum with CLOSED value
                }
            }
            case CLOSE -> {
                record.setCompletionTime(new Date());
                record.setStatus(Status.CLOSED);
            }
            case HOLD -> {
                record.setStatus(Status.ON_HOLD);
                record.setHoldCount(record.getHoldCount() + 1);
            }
            case REASSIGN -> {
                return;
            }
        }
        recordRepository.save(record); // Save the updated record status
    }

    public void closeRecord(UpdateRecordRequestDTO recordRequest) {

    }
}
