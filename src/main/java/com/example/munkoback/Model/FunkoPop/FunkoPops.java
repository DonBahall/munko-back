package com.example.munkoback.Model.FunkoPop;

import com.example.munkoback.Model.Paging_Sorting.Paging;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FunkoPops {
    private List<FunkoPop> items;
    private Paging paging;
}