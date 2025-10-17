package com.needbackend_app.needapp.user.util;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiErrorResponse(Boolean success, Object data, String message, @JsonProperty("error_type") ErrorType errorType) {}

