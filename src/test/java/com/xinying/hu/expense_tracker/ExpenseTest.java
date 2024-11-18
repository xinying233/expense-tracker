package com.xinying.hu.expense_tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseTest {
    User payer, borrower;
    Expense expense;

    String payerName = "John Doe";
    String borrowerName = "Jane Doe";
    float amount = 50.0F;
    float splitPercent = 30F;
    String category = "Grocery";
    int id = 5;

    @BeforeEach
    void setUp() {
        payer = new User();
        payer.setName(payerName);
        borrower = new User();
        borrower.setName(borrowerName);
        expense = new Expense(payer, borrower, amount, splitPercent, category);
        expense.setId(id);
    }


    @Test
    void getPayer() {
        User expectedPayer = payer;
        User actualPayer = expense.getPayer();
        assertEquals(expectedPayer.getName(), actualPayer.getName());
    }

    @Test
    void setPayer() {
        expense.setPayer(borrower);
        User expectedPayer = borrower;
        User actualPayer = expense.getPayer();
        assertEquals(expectedPayer.getName(), actualPayer.getName());
    }

    @Test
    void getBorrower() {
        User expectedBorrower = borrower;
        User actualBorrower= expense.getBorrower();
        assertEquals(expectedBorrower.getName(), actualBorrower.getName());
    }

    @Test
    void setBorrower() {
        expense.setBorrower(payer);
        User expectedBorrower = payer;
        User actualBorrower= expense.getBorrower();
        assertEquals(expectedBorrower.getName(), actualBorrower.getName());
    }

    @Test
    void getId() {
        int expectedId = id;
        int actualId = expense.getId();
        assertEquals(expectedId, actualId);
    }

    @Test
    void getSplitPercent() {
        float expectedSplitPercent = splitPercent;
        float actualSplitPercent = expense.getSplitPercent();
        assertEquals(expectedSplitPercent, actualSplitPercent);
    }

    @Test
    void getAmount() {
        float expectedAmount = amount;
        float actualAmount = expense.getAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void setAmount() {
        float expectedAmount = 3333.33F;
        expense.setAmount(expectedAmount);
        float actualAmount = expense.getAmount();
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void getCategory() {
        String expectedCategory = category;
        String actualCategory = expense.getCategory();
        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    void setCategory() {
        String expectedCategory = "GAS";
        expense.setCategory(expectedCategory);
        String actualCategory = expense.getCategory();
        assertEquals(expectedCategory, actualCategory);
    }

    @Test
    void isSettled() {
        boolean expectedIsSettled = false;
        boolean actualIsSettled = expense.isSettled();
        assertEquals(expectedIsSettled, actualIsSettled);
    }

    @Test
    void settleExpense() {
        boolean expectedIsSettled = true;
        expense.settleExpense();
        boolean actualIsSettled = expense.isSettled();
        assertEquals(expectedIsSettled, actualIsSettled);
    }

    @Test
    void unsettle() {
        boolean expectedIsSettled = false;
        expense.unsettle();
        boolean actualIsSettled = expense.isSettled();
        assertEquals(expectedIsSettled, actualIsSettled);
    }
}
