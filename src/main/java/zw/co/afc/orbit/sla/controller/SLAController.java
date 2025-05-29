package zw.co.afc.orbit.sla.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.afc.orbit.sla.dto.request.SLARequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;
import zw.co.afc.orbit.sla.service.iface.SLAService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/sla/sla")
@RestController
public class SLAController {

    private final SLAService slaService;

    @PostMapping
    public ResponseEntity<APIResponse> createSLA(@RequestBody SLARequestDTO slaRequest) {
        log.info("MORE REQUEST INFO FIRST: {}", slaRequest);
        return slaService.createSLA(slaRequest);
    }

    @GetMapping
    public ResponseEntity<APIResponse> getSLAs(@RequestParam Boolean isDeleted) {
        return slaService.getSLAs(isDeleted);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> getSLA(@PathVariable String id) {
        return slaService.getSLA(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse> updateSLA(@PathVariable String id, @RequestBody SLARequestDTO slaRequest) {
        return slaService.updateSLA(id, slaRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteSLA(@PathVariable String id) {
        return slaService.deleteSLA(id);
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<APIResponse> restoreSLA(@PathVariable String id) {
        return slaService.restoreSLA(id);
    }

}
