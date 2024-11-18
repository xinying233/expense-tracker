package com.xinying.hu.expense_tracker;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

@Entity
@Check(constraints = "userA_id != userB_id")
@Table(uniqueConstraints = {
        @UniqueConstraint( columnNames = "{ userA_id, userB_id }"),
        @UniqueConstraint( columnNames = "{ userB_id, userA_id }"),
})
public class UserRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userA_id", nullable = false)
    private User userA;

    @ManyToOne
    @JoinColumn(name = "userB_id", nullable = false)
    private User userB;

    // the amount that userA lends to userB;
    private float amount;

    public UserRelation() {};

    public UserRelation(User userA, User userB) {
        if (userA.getId() < userB.getId()) {
            this.userA = userA;
            this.userB = userB;
        } else {
            this.userA = userB;
            this.userB = userA;
        }
    }

    public Integer getId() { return id; }

    public User getUserA() { return userA; }

    public User getUserB() { return userB; }

    public  float getAmount() { return amount; }

    public void setAmount(float amount) { this.amount = amount; }

    public void addToAmount( User payer, float addOnAmount) {
        if(payer.getId() == userA.getId()) {
            this.amount += addOnAmount;
        } else {
            this.amount -= addOnAmount;
        }

    }

}
