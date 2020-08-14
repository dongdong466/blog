package com.dong1990.blog.service.iml;

import com.dong1990.blog.dao.UserRepository;
import com.dong1990.blog.pojo.User;
import com.dong1990.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User checkUser(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, password);
        return user;
    }
    
}
