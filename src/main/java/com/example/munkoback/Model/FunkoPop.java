package com.example.munkoback.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FunkoPop {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    @ElementCollection
    private List<String> images;
    private Integer price;
    private Integer amount;
    private String description;
    private Boolean sale;
    private String license;
    private String sublicense;
    private String series;
    private String category;
    private String productType;
    private String date;
    private Boolean favorite;
}
