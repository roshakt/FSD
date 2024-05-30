package com.projfinal.usersmanagementsystem.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.projfinal.usersmanagementsystem.entity.OurUsers;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<OurUsers, Integer> {

    Optional<OurUsers> findByEmail(String email);
}
