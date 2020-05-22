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

package bchrotasystem.contoller;

import bchrotasystem.Service.EmailService;
import bchrotasystem.Service.UserService;
import bchrotasystem.entity.PasswordGenerator;
import bchrotasystem.entity.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class UserListController {

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    boolean addSuccessful = false;
    boolean addFailed = false;
    boolean editSuccessful = false;
    boolean editFailed = false;
    boolean editIllegal = false;

    @RequestMapping(value = "/admin/users")
    public String listOfUsers(Model model) throws JSONException {
        JSONArray userList = getAllUserJsonArray();
        String jsonString = userList.toString();
        if(addSuccessful) {
            model.addAttribute("successMessage","New user is created");
            this.addSuccessful = false;
        }
        if(addFailed) {
            model.addAttribute("errorMessage","This email is already used");
            this.addFailed = false;
        }
        if(editSuccessful) {
            model.addAttribute("successMessage","The user is edited");
            this.editSuccessful = false;
        }
        if(editFailed) {
            model.addAttribute("errorMessage","This email is already in use");
            this.editFailed = false;
        }
        if(editIllegal) {
            model.addAttribute("errorMessage","The user does not exist");
            this.editIllegal = false;
        }
        model.addAttribute("userData", jsonString);
        return "users";
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public RedirectView getNewUserInfo(HttpServletRequest request, User user, Integer accountLevel, Integer role) {
        user.setEmail(user.getEmail().toLowerCase());
        if(userService.getUserByEmail(user.getEmail()).isPresent()) {
            this.addFailed = true;
            this.addSuccessful = false;
        }
        else {
            user.setAccountLevel(accountLevel);
            String initialPassword = new PasswordGenerator().getPassword();
            user.setPassword(passwordEncoder.encode(initialPassword));
            user.setRole(role);
            userService.addUser(user);
            this.addSuccessful = true;
            this.addFailed = false;
            String url = request.getScheme() + "://" + request.getServerName();
            SimpleMailMessage newAccountEmail = new SimpleMailMessage();
            newAccountEmail.setFrom("xinye0007@gmail.com");
            newAccountEmail.setTo(user.getEmail());
            newAccountEmail.setSubject("[BCH Rota System] Your account is opened");
            newAccountEmail.setText("Your initial password is\n" + initialPassword + "\nPlease sign in at  " + url +"  and change the password immediately");
            emailService.sendEmail(newAccountEmail);
        }

        return new RedirectView("/admin/users");
    }

    @PostMapping("/editUser")
    public RedirectView editUserInfo(User user, Integer accountLevel, Integer role) {
        user.setEmail(user.getEmail().toLowerCase());
        if(user.getId() == null) {
            //error, user does not exist
            this.editIllegal = true;
            this.editSuccessful = false;
            this.editFailed = false;
            return new RedirectView("/admin/users");
        }
        User updatedUser = userService.getUser(user.getId());
        if(updatedUser.getId() == null) {
            //error, user does not exist
            this.editIllegal = true;
            this.editSuccessful = false;
            this.editFailed = false;
            return new RedirectView("/admin/users");
        }
        //when the edited email is already used by other user
        if(userService.getUserByEmail(user.getEmail()).isPresent() && !userService.getUserByEmail(user.getEmail()).get().getId().equals(user.getId())) {
            //error, the email is already in use
            this.editFailed = true;
            this.editSuccessful = false;
            this.editIllegal = false;
            return new RedirectView("/admin/users");
        }
        updatedUser.setEmail(user.getEmail());
        updatedUser.setFirstName(user.getFirstName());
        updatedUser.setLastName(user.getLastName());
        updatedUser.setAccountLevel(accountLevel);
        updatedUser.setRole(role);
        userService.updateUser(updatedUser);
        this.editSuccessful = true;
        this.editFailed = false;
        this.editIllegal = false;
        return new RedirectView("/admin/users");
    }

    private JSONObject convertUserToJson(User user) throws JSONException {
        String accountLevelString = getAccountLevelString(user.getAccountLevel());
        String roleString = getRoleString(user.getRole());
        JSONObject item = new JSONObject();
        JSONObject subItem = new JSONObject();
        subItem.put("userId", user.getId());
        subItem.put("firstName", user.getFirstName());
        subItem.put("lastName", user.getLastName());
        subItem.put("email", user.getEmail());
        subItem.put("accountLevel", accountLevelString);
        subItem.put("role", roleString);
        item.put("user", subItem);

        return item;
    }

    private String getAccountLevelString (Integer accountLevelCode) {
        switch (accountLevelCode) {
            case 0:
                return "Admin";
            case 1:
                return "Spectator";
            case 2:
                return "User";
            default:
                return "Unknown account level";
        }
    }

    private String getRoleString (Integer roleCode) {
        switch (roleCode) {
            case 0:
                return "Administrator";
            case 1:
                return "Supervisor";
            case 2:
                return "SHO";
            case 3:
                return "Reg";
            default:
                return "Unknown";
        }
    }

    private JSONArray getAllUserJsonArray() throws JSONException{
        JSONArray result = new JSONArray();
        List<User> users = userService.getUserList();
        for(User user : users) {
            JSONObject userJsonItem = convertUserToJson(user);
            result.put(userJsonItem);
        }
        return result;
    }
}
