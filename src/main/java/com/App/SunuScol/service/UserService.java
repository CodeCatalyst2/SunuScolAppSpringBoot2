package com.App.SunuScol.service;

import com.App.SunuScol.model.User;
import com.App.SunuScol.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository){this.userRepository = userRepository;}

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            users.add(user);
        });
        return users;
    }

    //Utilisation procédures stockées - add user
//    public User getUser(long id) {return userRepository.getUserWithProcedure(id);}
    public void addUser(User user) {
        userRepository.addUserWithProcedure(user.getLastName(), user.getFirstName(), user.getPassword(),
            user.getEmail(), user.getBirthDay(), user.getBirthPlace(), user.getAdress());
    }
    public void updateUser(User user) {
        userRepository.updateUserWithProcedure(user.getUserId(), user.getLastName(), user.getFirstName(), user.getPassword(),
                user.getEmail(), user.getBirthDay(), user.getBirthPlace(), user.getAdress());
    }
    public void deleteUser(long id) {userRepository.deleteUserWithProcedure(id);}

        public User getUser(long id) { return userRepository.findById(id).orElse(null); }
    //    public void addUser(User user) {userRepository.save(user);}
    //    public void updateUser(User user) { userRepository.save(user); }
    //    public void deleteUser(long id) { userRepository.deleteById(id);  }






}
