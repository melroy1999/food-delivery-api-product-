package com.assignment.fooddelivery.repository;

import com.assignment.fooddelivery.enums.UserTypes;
import com.assignment.fooddelivery.model.LoginDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginDetailsRepository extends JpaRepository<LoginDetails, Long> {
    LoginDetails findByUsernameAndIsDeletedFalse(String username);

    LoginDetails findByUsernameAndUserRoleAndIsDeletedFalse(String username, UserTypes userRole);

    LoginDetails findByUsernameAndUserRoleAndIsDeletedTrue(String username, UserTypes userRole);
}
