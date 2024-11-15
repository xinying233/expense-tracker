package com.xinying.hu.expense_tracker;
import jakarta.persistence.*;

@Table(name="expenses")
@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    private User user;

    private float amount;

    // TODO: change to Enum
    private String category;

    public Expense(User user, float amount, String category) {
        this.user = user;
        this.amount = amount;
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
