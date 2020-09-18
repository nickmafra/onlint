package com.nickmafra.onlint.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName
public class ReadResponse {
    private int x;
    private int y;
    private boolean arrastando;
}
