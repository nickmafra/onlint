package com.nickmafra.onlint.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName
public class UpdateRequest {
    private int mouseX;
    private int mouseY;
    private boolean arrastando;
}
