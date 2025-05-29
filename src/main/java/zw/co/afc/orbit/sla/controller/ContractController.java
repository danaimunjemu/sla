package zw.co.afc.orbit.sla.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.afc.orbit.sla.dto.request.ContractRequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;
import zw.co.afc.orbit.sla.service.iface.ContractService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/sla/contract")
@RestController
public class ContractController {

    private final ContractService contractService;

    @PostMapping
    public ResponseEntity<APIResponse> createContract(@RequestBody ContractRequestDTO contractRequest){
        return contractService.createContract(contractRequest);
    }

    @GetMapping
    public ResponseEntity<APIResponse> getContracts(@RequestParam Boolean isDeleted){
        return contractService.getContracts(isDeleted);
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse> getContract(@PathVariable String id){
        return contractService.getContract(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse> updateContract(@PathVariable String id, @RequestBody ContractRequestDTO contractRequest){
        return contractService.updateContract(id, contractRequest);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse> deleteContract(@PathVariable String id){
        return contractService.deleteContract(id);
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<APIResponse> restoreContract(@PathVariable String id){
        return contractService.restoreContract(id);
    }

}
