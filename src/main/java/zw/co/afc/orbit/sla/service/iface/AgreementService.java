package zw.co.afc.orbit.sla.service.iface;

import org.springframework.http.ResponseEntity;
import zw.co.afc.orbit.sla.dto.request.AgreementRequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;

public interface AgreementService {
    ResponseEntity<APIResponse> createAgreement(AgreementRequestDTO agreementRequest);

    ResponseEntity<APIResponse> getAgreements(Boolean isDeleted);

    ResponseEntity<APIResponse> getAgreement(String id);

    ResponseEntity<APIResponse> updateAgreement(String id, AgreementRequestDTO agreementRequest);

    ResponseEntity<APIResponse> deleteAgreement(String id);

    ResponseEntity<APIResponse> restoreAgreement(String id);
}
