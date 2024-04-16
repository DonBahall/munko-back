package com.example.munkoback.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoRequest {
    public Integer funkoId;
    public Integer amount;
}
