package zw.co.afc.orbit.sla.dto.request;

import zw.co.afc.orbit.sla.enums.EscalationType;
import zw.co.afc.orbit.sla.enums.Role;

import java.util.Date;

public record ContractRequestDTO(
        Date completionDeadline,
        Date completionTime,
        Role role,
        String assignedUser,
        String escalationUser,
        Integer duration,
        Boolean hasEscalated,
        Integer status,
        Integer index,
        EscalationType escalationType
) {
}
