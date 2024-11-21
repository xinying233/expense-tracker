package com.xinying.hu.expense_tracker;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRelationRepository extends CrudRepository<UserRelation, Integer> {
    Optional<UserRelation> findByUserAAndUserB(User userA, User userB);

    List<UserRelation> findAllByUserA(User userA);

    List<UserRelation> findAllByUserB(User userB);
}
