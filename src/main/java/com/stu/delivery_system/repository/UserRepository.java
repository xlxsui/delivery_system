package com.stu.delivery_system.repository;

import com.stu.delivery_system.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

    User findByUuid(String uuid);
}
