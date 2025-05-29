package zw.co.afc.orbit.sla.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.afc.orbit.sla.dto.request.ApplicationRequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;
import zw.co.afc.orbit.sla.service.iface.ApplicationService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/sla/application")
@RestController
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<APIResponse> createService(@RequestBody ApplicationRequestDTO serviceRequest) {
        log.info("MORE REQUEST INFO FIRST: {}", serviceRequest);
        return applicationService.createService(serviceRequest);
    }

    @GetMapping
    public ResponseEntity<APIResponse> getServices(@RequestParam Boolean isDeleted) {
        return applicationService.getServices(isDeleted);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> getService(@PathVariable String id) {
        return applicationService.getService(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse> updateService(@PathVariable String id, @RequestBody ApplicationRequestDTO serviceRequest) {
        return applicationService.updateService(id, serviceRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteService(@PathVariable String id) {
        return applicationService.deleteService(id);
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<APIResponse> restoreService(@PathVariable String id) {
        return applicationService.restoreService(id);
    }

}
