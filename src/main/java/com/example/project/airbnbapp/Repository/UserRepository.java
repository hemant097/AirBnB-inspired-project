package com.example.project.airbnbapp.Repository;

import com.example.project.airbnbapp.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
