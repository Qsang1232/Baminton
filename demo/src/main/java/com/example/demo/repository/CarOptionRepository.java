package com.example.demo.repository;

import com.example.demo.model.CarOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarOptionRepository extends JpaRepository<CarOption, Long> {
}