package com.xinying.hu.expense_tracker;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface UserRelationRepository extends CrudRepository<UserRelation, Integer> {
    public Optional<UserRelation> findByUserAAndUserB(User userA, User userB);
}
