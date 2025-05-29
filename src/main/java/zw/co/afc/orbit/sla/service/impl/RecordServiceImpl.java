package zw.co.afc.orbit.sla.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import zw.co.afc.orbit.sla.dto.request.record.CreateRecordRequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;
import zw.co.afc.orbit.sla.model.Record;
import zw.co.afc.orbit.sla.repository.RecordRepository;
import zw.co.afc.orbit.sla.service.iface.RecordService;
import zw.co.afc.orbit.sla.util.ResponseUtil;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;

    @Override
    public ResponseEntity<APIResponse> createRecord(CreateRecordRequestDTO recordRequest) {
        // TODO this should follow the same flow as the topic
        return null;
    }

    @Override
    public ResponseEntity<APIResponse> getRecords(Boolean isDeleted) {
        List<Record> records = new ArrayList<>();
        if (isDeleted){
            records = recordRepository.findByIsDeletedTrue();
        } else {
            records = recordRepository.findByIsDeletedNullOrIsDeletedFalse();
        }
        return ResponseUtil.createResponse(
                200,
                true,
                "Records retrieved successfully",
                "We have retrieved " + records.size() +" records successfully",
                null,
                records
        );
    }

    @Override
    public ResponseEntity<APIResponse> getRecord(String id) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The Record with id " + id + " was not found."));
        return ResponseUtil.createResponse(
                200,
                true,
                "Record retrieved successfully",
                "We have retrieved the record successfully",
                null,
                record
        );

    }

    @Override
    public ResponseEntity<APIResponse> updateRecord(String id, CreateRecordRequestDTO recordRequest) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The Record with id " + id + " was not found."));

        boolean isAgreementIdChanged = !record.getAgreement().getId().equals(recordRequest.agreementId());

        if (isAgreementIdChanged) {
            return ResponseUtil.createResponse(
                    400,
                    false,
                    "Record Update Failed",
                    "Cannot change the agreement of a record that is already being tracked",
                    null,
                    null
            );
        }

        record.setReferenceNumber(recordRequest.referenceNumber());
//        record.setStatus(recordRequest.action());

        Record updatedRecord = recordRepository.save(record);
        return ResponseUtil.createResponse(
                200,
                true,
                "Record updated successfully",
                "We have updated the Record successfully",
                null,
                updatedRecord
        );
    }

    @Override
    public ResponseEntity<APIResponse> deleteRecord(String id) {

        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The Record with id " + id + " was not found."));

        record.setIsDeleted(true);

        Record deletedRecord = recordRepository.save(record);
        return ResponseUtil.createResponse(
                200,
                true,
                "Record deleted successfully",
                "We have deleted the Record successfully",
                null,
                deletedRecord
        );
    }

    @Override
    public ResponseEntity<APIResponse> restoreRecord(String id) {

        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "The Record with id " + id + " was not found."));

        record.setIsDeleted(false);

        Record restoredRecord = recordRepository.save(record);
        return ResponseUtil.createResponse(
                200,
                true,
                "Record restored successfully",
                "We have restored the Record successfully",
                null,
                restoredRecord
        );
    }
}
