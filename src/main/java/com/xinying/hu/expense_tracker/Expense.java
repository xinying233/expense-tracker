package com.xinying.hu.expense_tracker;
import jakarta.persistence.*;

@Table(name="expenses")
@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    private User payer;

    @ManyToOne
    private User borrower;

    private float amount;

    // The percentage that payer splits for
    private float splitPercent;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean settled;

    // TODO: change to Enum
    private String category;

    public Expense() {}

    public Expense(User payer, User borrower, float amount, float splitPercent, String category) {
        this.payer = payer;
        this.borrower = borrower;
        this.amount = amount;
        this.splitPercent = splitPercent;
        this.category = category;
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

    public Integer getId() {
        return id;
    }

    public float getSplitPercent() {
        return splitPercent;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

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
