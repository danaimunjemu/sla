package zw.co.afc.orbit.sla.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import zw.co.afc.orbit.sla.dto.response.APIResponse;
import zw.co.afc.orbit.sla.util.ResponseUtil;

@ControllerAdvice
public class RecordExceptionController {
    @ExceptionHandler(value = RecordExistsException.class)
    public ResponseEntity<APIResponse> handleRecordExistsException(RecordExistsException exception) {
        return ResponseUtil.createResponse(
                HttpStatus.NOT_FOUND.value(),
                false,
                "Record Exists",
                RecordExceptionString.entityExists,
                "Create a record with a new reference number.",
                null
        );
    }
}
