package zw.co.afc.orbit.sla.dto.request;

import zw.co.afc.orbit.sla.model.AgreementDetails;

import java.util.List;

public record AgreementRequestDTO(
        String name,
        String owner,
        String description,
        String cyclicUser,
        Boolean hasReminder,
        Integer reminderTime,
        Boolean requiresManager,
        Integer managersTime,
        String slaId,
        List<AgreementDetails> agreementDetails
) {
}
