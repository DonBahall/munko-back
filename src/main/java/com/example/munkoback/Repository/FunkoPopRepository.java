package com.example.munkoback.Repository;

import com.example.munkoback.Model.FunkoPop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunkoPopRepository extends JpaRepository<FunkoPop, Integer> { }
