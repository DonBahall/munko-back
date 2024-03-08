package com.example.munkoback.Service;

import com.example.munkoback.Model.FunkoPop;
import com.example.munkoback.Repository.FunkoPopRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FunkoPopService {
    private final FunkoPopRepository repository;

    public FunkoPopService(FunkoPopRepository repository) {
        this.repository = repository;
    }
    @PreAuthorize("permitAll()")
    public List<FunkoPop> getAllItems(){
        return repository.findAll();
    }
    public FunkoPop getItem(Integer id){
        return repository.findById(id).orElse(null);
    }
}
