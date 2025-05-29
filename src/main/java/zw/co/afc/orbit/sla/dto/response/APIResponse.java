package zw.co.afc.orbit.sla.dto.response;

public record APIResponse(
        Boolean success,
        String message,
        String detail,
        String help,
        Object data
) {
}
