package zw.co.afc.orbit.sla.dto.request.record;

import zw.co.afc.orbit.sla.enums.Action;

public record CreateRecordRequestDTO(
        String referenceNumber,
        Action action,
        String actor,
        String agreementId

) {
}
