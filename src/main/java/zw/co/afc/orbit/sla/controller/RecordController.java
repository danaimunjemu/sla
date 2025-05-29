package zw.co.afc.orbit.sla.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.afc.orbit.sla.dto.request.record.CreateRecordRequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;
import zw.co.afc.orbit.sla.service.iface.RecordService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/sla/record")
@RestController
public class RecordController {

    private final RecordService recordService;


    @PostMapping
    public ResponseEntity<APIResponse> createRecord(@RequestBody CreateRecordRequestDTO recordRequest){
        return recordService.createRecord(recordRequest);
    }

    @GetMapping
    public ResponseEntity<APIResponse> getRecords(@RequestParam Boolean isDeleted){
        return recordService.getRecords(isDeleted);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> getRecord(@PathVariable String id){
        return recordService.getRecord(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse> updateRecord(@PathVariable String id, @RequestBody CreateRecordRequestDTO recordRequest){
        return recordService.updateRecord(id, recordRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteRecord(@PathVariable String id){
        return recordService.deleteRecord(id);
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<APIResponse> restoreRecord(@PathVariable String id){
        return recordService.restoreRecord(id);
    }

}
