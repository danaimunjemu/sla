package zw.co.afc.orbit.sla.dto.request;

import zw.co.afc.orbit.sla.enums.ExtendedSLAType;
import zw.co.afc.orbit.sla.enums.SLAType;

public record SLARequestDTO(
        String name,
        SLAType slaType,
        ExtendedSLAType extendedSLAType,
        Integer numberOfContracts,
        Boolean contractsRunConcurrently,
        Integer quota,
        String applicationId
) {
}
