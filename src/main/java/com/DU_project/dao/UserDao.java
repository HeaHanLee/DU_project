package com.DU_project.dao;

import com.DU_project.domain.User;

public interface UserDao {
    User selectUser(String id) throws Exception;
    int deleteUser(String id) throws Exception;
    int insertUser(User user) throws Exception;
    int updateUser(User user) throws Exception;
    int count() throws Exception;
    void deleteAll() throws Exception;

}