package com.stu.delivery_system.service;


import com.stu.delivery_system.domain.User;
import com.stu.delivery_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("userService")
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 通过id返回一个User
     *
     * @param id
     * @return
     */
    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    /**
     * 通过username找到User
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public UserDetails loadUserByUuid(String uuid) throws UsernameNotFoundException {
        return userRepository.findByUuid(uuid);
    }

    public User saveUser(User user) {
        String openid = user.getUuid();
        User oldUser = userRepository.findByUuid(openid);
        if (oldUser != null) {  // 已经存在，更新
            oldUser.setAvatar(user.getAvatar());
            oldUser.setGender(user.getGender());
            oldUser.setUsername(user.getUsername());
            oldUser.setProvince(user.getProvince());
            userRepository.save(oldUser);
        } else {  // 新用户，直接保存
            userRepository.save(user);
        }
        return user;
    }


}
