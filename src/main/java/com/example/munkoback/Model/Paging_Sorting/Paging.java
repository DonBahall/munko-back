package com.example.munkoback.Model.Paging_Sorting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
@Getter
@Setter
public class Paging {
    private int page;
    private int perPage;
    private int pageCount;
    private int totalCount;
}
