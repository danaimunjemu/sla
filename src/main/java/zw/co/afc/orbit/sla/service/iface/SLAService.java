package zw.co.afc.orbit.sla.service.iface;

import org.springframework.http.ResponseEntity;
import zw.co.afc.orbit.sla.dto.request.SLARequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;

public interface SLAService {
    ResponseEntity<APIResponse> createSLA(SLARequestDTO slaRequest);

    ResponseEntity<APIResponse> getSLAs(Boolean isDeleted);

    ResponseEntity<APIResponse> getSLA(String id);

    ResponseEntity<APIResponse> updateSLA(String id, SLARequestDTO slaRequest);

    ResponseEntity<APIResponse> deleteSLA(String id);

    ResponseEntity<APIResponse> restoreSLA(String id);
}
