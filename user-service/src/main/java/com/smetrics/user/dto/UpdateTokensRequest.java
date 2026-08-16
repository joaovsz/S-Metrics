package com.smetrics.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateTokensRequest(
        @NotBlank String accessToken,
        String refreshToken
) {
}
