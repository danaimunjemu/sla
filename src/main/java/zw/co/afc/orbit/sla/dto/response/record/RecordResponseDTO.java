package zw.co.afc.orbit.sla.dto.response.record;

import zw.co.afc.orbit.sla.enums.Action;

public record RecordResponseDTO(
        String referenceNumber,
        Action action,
        String actor,
        String details
) {
}
