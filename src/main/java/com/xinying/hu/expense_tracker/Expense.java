package com.xinying.hu.expense_tracker;
import jakarta.persistence.*;

import java.time.LocalDate;

@Table(name="expenses")
@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    private User payer;

    @ManyToOne
    @JoinColumn(nullable = true)
    private User borrower;

    @Column(nullable = false)
    private LocalDate date;

    private float amount;

    // The percentage that payer splits for
    private float splitPercent;

    private float payerAmount;

    private float borrowerAmount;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean settled;

    // TODO: change to Enum
    private String category;

    public Expense() {}

    public Expense(User payer, User borrower, LocalDate date, float amount,  float splitPercent, String category) {
        this.payer = payer;
        this.borrower = borrower;
        this.date = date;
        this.amount = amount;
        this.splitPercent = splitPercent;
        this.borrowerAmount = (float) 0.01 * splitPercent * amount;
        this.payerAmount = amount - this.borrowerAmount;
        this.category = category;
    }

    public Expense(User payer, LocalDate date, float amount, String category) {
        this.payer = payer;
        this.date = date;
        this.amount = amount;
        this.splitPercent = 0;
        this.borrowerAmount = 0;
        this.payerAmount = amount;
        this.category = category;
        this.settled = true;
    }

    public Integer getId() {
        return id;
    }

    public User getPayer() {
        return payer;
    }

    public void setPayer(User payer) {
        this.payer = payer;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public LocalDate getDate() { return date; }

    public float getSplitPercent() {
        return splitPercent;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getPayerAmount() { return payerAmount; }

    public float getBorrowerAmount() { return borrowerAmount; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isSettled() {
        return settled;
    }

    public void settleExpense() {
        this.settled = true;
    }

    public void unsettle() {
        this.settled = false;
    }
}
