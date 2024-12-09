package com.example.demo.repository;

import com.example.demo.entity.IncomeDetails;
import com.example.demo.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncomeDetailsRepository extends JpaRepository<IncomeDetails, Long> {
    Optional<IncomeDetails> findByUserId(Long userId);
    Optional<IncomeDetails> findByUser(Users user);
}
