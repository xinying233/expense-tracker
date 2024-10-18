package com.xinying.hu.expense_tracker.repositories;

import com.xinying.hu.expense_tracker.entities.Candy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandyRepository extends JpaRepository<Candy, Long> {
}
