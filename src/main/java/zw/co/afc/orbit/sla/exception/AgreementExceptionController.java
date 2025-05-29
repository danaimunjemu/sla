package zw.co.afc.orbit.sla.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import zw.co.afc.orbit.sla.dto.response.APIResponse;
import zw.co.afc.orbit.sla.util.ResponseUtil;

@ControllerAdvice
public class AgreementExceptionController {
    @ExceptionHandler(value = AgreementDoesNotExistException.class)
    public ResponseEntity<APIResponse> handleAgreementDoesNotExistException(AgreementDoesNotExistException exception) {
        return ResponseUtil.createResponse(
                HttpStatus.NOT_FOUND.value(),
                false,
                "Agreement Does Not Exist",
                AgreementExceptionString.entityDoesNotExist,
                "Create a record with an existing agreement.",
                null
        );
    }
}
