package com.xinying.hu.expense_tracker;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ExpenseRepository extends CrudRepository<Expense, Integer> {
//    @Query("FROM Expense e where e.user.id = :userId")
    List<Expense> findAllByUserId(Integer userId);
}
