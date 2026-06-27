package org.mcp.nextcloud.server;

import java.util.Map;

import org.mcp.nextcloud.core.error.ConfigurationException;
import org.mcp.nextcloud.core.error.NextcloudApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class NextcloudMcpExceptionHandler {
    @ExceptionHandler(ServerRequestException.class)
    ResponseEntity<Map<String, Object>> requestError(ServerRequestException ex) {
        HttpStatus status = switch (ex.code()) {
            case "config.not_found", "account.not_found", "admin.account_not_found" -> HttpStatus.NOT_FOUND;
            case "config.invalid", "account.disabled", "account.exists", "admin.disabled",
                    "admin.account_disabled", "admin.account_not_marked_admin", "request.invalid" -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
        return ResponseEntity.status(status).body(Map.of(
                "success", false,
                "error", Map.of(
                        "code", ex.code(),
                        "message", ex.getMessage())));
    }

    @ExceptionHandler(NextcloudApiException.class)
    ResponseEntity<Map<String, Object>> nextcloudApiError(NextcloudApiException ex) {
        HttpStatus status = switch (ex.statusCode()) {
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 403 -> HttpStatus.FORBIDDEN;
            case 404 -> HttpStatus.NOT_FOUND;
            case 502, 503, 504 -> HttpStatus.BAD_GATEWAY;
            default -> HttpStatus.BAD_GATEWAY;
        };
        return ResponseEntity.status(status).body(Map.of(
                "success", false,
                "error", Map.of(
                        "code", ex.code(),
                        "message", ex.getMessage(),
                        "nextcloudStatus", ex.statusCode())));
    }

    @ExceptionHandler(ConfigurationException.class)
    ResponseEntity<Map<String, Object>> configurationError(ConfigurationException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", Map.of(
                        "code", ex.code(),
                        "message", ex.getMessage())));
    }
}
