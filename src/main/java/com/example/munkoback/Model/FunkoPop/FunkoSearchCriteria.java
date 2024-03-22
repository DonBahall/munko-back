package com.example.munkoback.Model.FunkoPop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FunkoSearchCriteria {
    private String name;
    private PriceSearch price;
    private String series;
    private String category;
    private Boolean inStock;
    private Boolean sale;
}