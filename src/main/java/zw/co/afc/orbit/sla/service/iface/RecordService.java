package zw.co.afc.orbit.sla.service.iface;

import org.springframework.http.ResponseEntity;
import zw.co.afc.orbit.sla.dto.request.record.CreateRecordRequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;

public interface RecordService {
    ResponseEntity<APIResponse> createRecord(CreateRecordRequestDTO recordRequest);

    ResponseEntity<APIResponse> getRecords(Boolean isDeleted);

    ResponseEntity<APIResponse> getRecord(String id);

    ResponseEntity<APIResponse> updateRecord(String id, CreateRecordRequestDTO recordRequest);

    ResponseEntity<APIResponse> deleteRecord(String id);

    ResponseEntity<APIResponse> restoreRecord(String id);
}
