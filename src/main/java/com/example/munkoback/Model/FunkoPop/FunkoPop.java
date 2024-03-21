package com.example.munkoback.Model.FunkoPop;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.GenerationType;

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
}
