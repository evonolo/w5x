package io.grundner.wallbox.web;

import io.grundner.wallbox.loxone.LoxoneClient;
import io.grundner.wallbox.report.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Fehlerantworten gemäß docs/api/report-api.md (400 / 409 / 502).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	public record ApiError(int status, String error, String message) {
	}

	@ExceptionHandler(ReportService.SettingsIncompleteException.class)
	public ResponseEntity<ApiError> conflict(RuntimeException ex) {
		return build(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(LoxoneClient.LogFetchException.class)
	public ResponseEntity<ApiError> badGateway(RuntimeException ex) {
		return build(HttpStatus.BAD_GATEWAY, ex.getMessage());
	}

	@ExceptionHandler({
			IllegalArgumentException.class,
			MissingServletRequestParameterException.class,
			MethodArgumentTypeMismatchException.class
	})
	public ResponseEntity<ApiError> badRequest(Exception ex) {
		return build(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	private ResponseEntity<ApiError> build(HttpStatus status, String message) {
		return ResponseEntity.status(status)
				.body(new ApiError(status.value(), status.getReasonPhrase(), message));
	}
}
