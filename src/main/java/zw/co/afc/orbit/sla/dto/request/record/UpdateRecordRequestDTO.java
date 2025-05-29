package zw.co.afc.orbit.sla.dto.request.record;

import zw.co.afc.orbit.sla.enums.Action;

public record UpdateRecordRequestDTO(
        String referenceNumber,
        Action action,
        String actor,
        String message, //optional
        String oldUser, //optional
        String replacementUser //optional
) {
}
