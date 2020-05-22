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

package bchrotasystem.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User {

    private @Id @GeneratedValue Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Integer accountLevel;   //0: Admin, 1: Spectator 2: User
    private Integer role;   //0:Administrator, 1: Supervisor, 2: SHO, 3: Reg  ??
    private String resetToken;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAccountLevel() {
        return accountLevel;
    }

    public void setAccountLevel(Integer role) {
        this.accountLevel = role;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getResetToken() { return resetToken; }

    public void setResetToken(String resetToken) { this.resetToken = resetToken;}


    public User() {
        this.firstName = "emptyFirstName";
        this.email = "emptyEmail";
        this.password = "emptyPassword";
        this.lastName = "emptyLastName";
        this.email = "emptyEmail";
        this.password = "emptyPassword";
        this.accountLevel = 999;
    }

    public User(String firstName, String lastName, Integer accountLevel) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountLevel = accountLevel;
    }

    public User(String firstName, String lastName, Integer accountLevel, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountLevel = accountLevel;
        this.email = email;
    }

    public User(String firstName, String lastName, Integer accountLevel, Integer role, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountLevel = accountLevel;
        this.role = role;
        this.email = email;
    }

    public User(String firstName, String lastName, Integer accountLevel, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountLevel = accountLevel;
        this.email = email;
        this.password = password;
    }

    public User(String firstName, String lastName, Integer accountLevel, Integer role, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.accountLevel = accountLevel;
        this.role = role;
        this.email = email;
        this.password = password;
    }


}
