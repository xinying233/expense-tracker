package com.xinying.hu.expense_tracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class MainService {
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    @Autowired
    private final UserRelationRepository userRelationRepository;

    public MainService(UserRepository userRepository, ExpenseRepository expenseRepository, UserRelationRepository userRelationRepository) {
        this.userRepository = userRepository;
        this.expenseRepository = expenseRepository;
        this.userRelationRepository = userRelationRepository;
    }

    public User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id" + id + "not found"));
    }

    public Optional<UserRelation> findUserRelationByUserIds(User userA, User userB) {
        if(userA.getId() > userB.getId()) {
            return userRelationRepository.findByUserAAndUserB(userB, userA);
        }else {
            return userRelationRepository.findByUserAAndUserB(userA, userB);
        }
    }

    public UserRelation findOrCreateUserRelationByUserIds(User userA, User userB) {
        return this.findUserRelationByUserIds(userA, userB)
                .orElseGet(() -> {
                    return new UserRelation(userA, userB);
                });
    }

//    TODO: create individual expense
    public void createExpense(User payer, User borrower, LocalDate date, float amount, float splitPercent, String category) {
        Expense expense = new Expense(payer, borrower, date, amount, splitPercent, category);
        expenseRepository.save(expense);
        UserRelation relation = this.findOrCreateUserRelationByUserIds(payer, borrower);
        relation.addToAmount(payer, (float)(amount * splitPercent * 0.01));
        userRelationRepository.save(relation);
    }
}