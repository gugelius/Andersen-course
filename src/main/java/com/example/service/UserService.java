package com.example.service;

import com.example.entity.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Loading user by username: " + username);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            System.out.println("User found: " + username);
            return user.get();
        } else {
            System.out.println("User not found: " + username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    public void saveUser(User user) {
        System.out.println("Saving user: " + user.getUsername());
        userRepository.save(user);
        System.out.println("User saved: " + user.getUsername());
    }
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
