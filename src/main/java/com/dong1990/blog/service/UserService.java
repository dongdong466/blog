package com.dong1990.blog.service;

import com.dong1990.blog.pojo.User;

public interface UserService {

    User checkUser(String username, String password);

}
