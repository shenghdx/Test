package com.qxj.cloud.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qxj.cloud.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{

}
