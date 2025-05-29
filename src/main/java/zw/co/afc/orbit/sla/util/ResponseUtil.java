package zw.co.afc.orbit.sla.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import zw.co.afc.orbit.sla.dto.response.APIResponse;

public class ResponseUtil {
    public static ResponseEntity<APIResponse> createResponse(Integer httpStatus, Boolean success, String message, String detail, String help, Object data){
        APIResponse apiResponse = new APIResponse(success, message, detail, help, data);
        HttpStatus status = determineHttpStatus(httpStatus);
        return new ResponseEntity<>(apiResponse, status);
    }

    private static HttpStatus determineHttpStatus(Integer httpStatus) {
        return switch (httpStatus) {
            case 200 -> HttpStatus.OK;
            case 201 -> HttpStatus.CREATED;
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 403 -> HttpStatus.FORBIDDEN;
            case 404 -> HttpStatus.NOT_FOUND;
            case 500 -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
