package com.qxj.cloud.service;

import java.util.List;

import com.qxj.cloud.entity.User;

public interface UserService {
    public User find(Long id);

    public List<User> findAll(List<Long> ids);
}
