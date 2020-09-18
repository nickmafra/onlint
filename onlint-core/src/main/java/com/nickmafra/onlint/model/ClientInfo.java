package com.nickmafra.onlint.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName
public class ClientInfo {
    private String clientId;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientInfo && Objects.equals(((ClientInfo) obj).clientId, this.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clientId);
    }
}
