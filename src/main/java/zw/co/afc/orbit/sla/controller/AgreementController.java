package zw.co.afc.orbit.sla.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.afc.orbit.sla.dto.request.AgreementRequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;
import zw.co.afc.orbit.sla.service.iface.AgreementService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/sla/agreement")
@RestController
public class AgreementController {

    private final AgreementService agreementService;

    @PostMapping
    public ResponseEntity<APIResponse> createAgreement(@RequestBody AgreementRequestDTO agreementRequest){
        return agreementService.createAgreement(agreementRequest);
    }

    @GetMapping
    public ResponseEntity<APIResponse> getAgreements(@RequestParam Boolean isDeleted){
        return agreementService.getAgreements(isDeleted);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> getAgreement(@PathVariable String id){
        return agreementService.getAgreement(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse> updateAgreement(@PathVariable String id, @RequestBody AgreementRequestDTO agreementRequest){
        return agreementService.updateAgreement(id, agreementRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteAgreement(@PathVariable String id){
        return agreementService.deleteAgreement(id);
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<APIResponse> restoreAgreement(@PathVariable String id){
        return agreementService.restoreAgreement(id);
    }

}
