package com.assignment.fooddelivery.repository;

import com.assignment.fooddelivery.model.LoginDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginDetailsRepository extends JpaRepository<LoginDetails, Long> {
}
