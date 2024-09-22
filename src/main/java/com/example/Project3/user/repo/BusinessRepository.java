package com.example.Project3.user.repo;

import com.example.Project3.user.entity.UserBusinessRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRepository extends JpaRepository<UserBusinessRegistration,Long> {
    boolean existsByBusinessNum(Long businessNum);
}
