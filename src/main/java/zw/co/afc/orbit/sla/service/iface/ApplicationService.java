package zw.co.afc.orbit.sla.service.iface;

import org.springframework.http.ResponseEntity;
import zw.co.afc.orbit.sla.dto.request.ApplicationRequestDTO;
import zw.co.afc.orbit.sla.dto.response.APIResponse;

public interface ApplicationService {
    ResponseEntity<APIResponse> createService(ApplicationRequestDTO serviceRequest);

    ResponseEntity<APIResponse> getServices(Boolean isDeleted);

    ResponseEntity<APIResponse> getService(String id);

    ResponseEntity<APIResponse> updateService(String id, ApplicationRequestDTO serviceRequest);

    ResponseEntity<APIResponse> deleteService(String id);

    ResponseEntity<APIResponse> restoreService(String id);
}
