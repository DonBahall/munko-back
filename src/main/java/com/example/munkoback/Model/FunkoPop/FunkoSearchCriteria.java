package com.example.munkoback.Model.FunkoPop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FunkoSearchCriteria {
    private String name;
    private PriceSearch price;
    private List<String> collection;
    private List<String> series;
    private List<String> category;
    private Boolean inStock;
    private Boolean sale;
}