package com.crm.settings.service.impl;

import com.crm.settings.domain.User;
import com.crm.settings.mapper.UserMapper;
import com.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> queryAllUsers() {
        return userMapper.selectAllUsers();
    }

    public User queryUserByLoginActAndPwd(Map<String, Object> map) {
        return userMapper.selectUserByLoginActAndPwd(map);
    }
}
