package com.codewithmosh.store.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RefreshRequest {
    @NotNull
    private String refreshToken;
}
