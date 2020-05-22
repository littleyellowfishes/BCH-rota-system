/*
BCH Rota system. It is a tool for managing rota table in spreadsheet like editing environment
    Copyright (C) 2019 - 2020  Alex Welsh, Seunghun Lee, Xin Ye

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

 */

package bchrotasystem.Service;

import bchrotasystem.entity.User;
import bchrotasystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public List<User> getUserList() {
        List<User> users = new ArrayList<>();
        userRepository.findAll()
                .forEach(users::add);
        return users;
    }

    //tested
    public List<User> getBasicUserList() {
        List<User> users = new ArrayList<>();
        userRepository.findAll()
                .forEach((temp) -> {
                    if (temp.getAccountLevel() == 2) users.add(temp);
                });
        return users;
    }

    //tested
    public void addUser(User user) { userRepository.save(user); }

    public void addUsers(Iterable<User> users) {
        userRepository.saveAll(users);
    }

    public void updateUser(User user) { userRepository.save(user); }

    //tested
    public void deleteUser(User user) {
        userRepository.findAll()
                .forEach((temp) -> {
                    if (usersEqual(temp,user)) userRepository.deleteById(temp.getId());
                });
    }

    //tested
    public void deleteAllUsers() {userRepository.deleteAll(); }

    //tested
    public long countUsers() {return userRepository.count(); }

    //tested
    public User getUser(Integer id) { return userRepository.findById(id).orElse(new User()); }


    public Boolean usersEqual(User user1, User user2) {
        return (user1.getFirstName().equals(user2.getFirstName()) && user1.getLastName().equals(user2.getLastName()) && user1.getAccountLevel().equals(user2.getAccountLevel()) && user1.getEmail().equals(user2.getEmail()) && user1.getRole().equals(user2.getRole()));
    }

    //tested
    public void deleteUsers(List<User> users) {
        userRepository.findAll()
                .forEach((temp) -> {
                    for (User user: users) {
                        if (usersEqual(temp,user)) user.setId(temp.getId());
                    }
                });
        userRepository.deleteAll(users);
    }

    //tested
    public Integer getIDByNames(String firstName, String lastName){
        List<User> users = getUserList();
        for (User temp:users) {
            if (temp.getFirstName().equals(firstName) && temp.getLastName().equals(lastName)) return temp.getId() ;
        }
        return 0;
    }

    //tested
    public List<User> getUsersByAccLevel(Integer Level) {
        List<User> users = new ArrayList<>();
        userRepository.findAll()
                .forEach((temp) -> {
                    if (temp.getAccountLevel().equals(Level)) users.add(temp);
                });
        return users;
    }

    //tested
    public List<User> getUsersByRole(Integer Role) {
        List<User> users = new ArrayList<>();
        userRepository.findAll()
                .forEach((temp) -> {
                    if (temp.getRole().equals(Role)) users.add(temp);
                });
        return users;
    }

    public Optional<User> getUserByEmail(String email) {
        List<User> users = getUserList();
        for (User temp:users) {
            if (temp.getEmail().equals(email)) return Optional.of(temp);
        }
        return Optional.empty();
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findUserByResetToken(String resetToken){
        return userRepository.findByResetToken(resetToken);
    }


}