package com.xinying.hu.expense_tracker;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRelationRepository extends CrudRepository<UserRelation, Integer> {
    public Optional<UserRelation> findByUserAAndUserB(User userA, User userB);

    List<UserRelation> findAllByUserA(User userA);

    List<UserRelation> findAllByUserB(User userB);
}
