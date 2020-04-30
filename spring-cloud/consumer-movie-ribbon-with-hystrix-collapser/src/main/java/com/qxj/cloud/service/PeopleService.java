package com.qxj.cloud.service;

import java.util.List;
import java.util.concurrent.Future;

import com.qxj.cloud.entity.User;

public interface PeopleService {
    public Future<User> find(Long id);

    public List<User> findAll(List<Long> ids);
}
