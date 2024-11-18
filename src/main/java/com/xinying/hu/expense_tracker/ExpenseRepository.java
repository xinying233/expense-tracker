package com.xinying.hu.expense_tracker;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;


public interface ExpenseRepository extends CrudRepository<Expense, Integer> {
//    @Query("FROM Expense e where e.payer.id = :payerId")
    List<Expense> findAllByPayerId(Integer payerId);

    List<Expense> findAllByBorrowerId(Integer borrowerId);

    Optional<Expense> findById(Integer id);
}
