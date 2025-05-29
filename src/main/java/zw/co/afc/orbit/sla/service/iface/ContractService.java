package zw.co.afc.orbit.sla.service.iface;

import org.springframework.http.ResponseEntity;
import zw.co.afc.orbit.sla.dto.request.ContractRequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;

public interface ContractService {
    ResponseEntity<APIResponse> createContract(ContractRequestDTO contractRequest);

    ResponseEntity<APIResponse> getContracts(Boolean isDeleted);

    ResponseEntity<APIResponse> getContract(String id);

    ResponseEntity<APIResponse> updateContract(String id, ContractRequestDTO contractRequest);

    ResponseEntity<APIResponse> deleteContract(String id);

    ResponseEntity<APIResponse> restoreContract(String id);
}
