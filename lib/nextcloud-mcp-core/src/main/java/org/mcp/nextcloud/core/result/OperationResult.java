package org.mcp.nextcloud.core.result;

public record OperationResult<T>(boolean success, T value, ErrorResult error) {
    public static <T> OperationResult<T> ok(T value) {
        return new OperationResult<>(true, value, null);
    }

    public static <T> OperationResult<T> failed(ErrorResult error) {
        if (error == null) {
            throw new IllegalArgumentException("error is required");
        }
        return new OperationResult<>(false, null, error);
    }
}
