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

import bchrotasystem.Service.UserService;
import bchrotasystem.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class settingController {

    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @RequestMapping("/settings")
    public String settings() {
        return "settings";
    }

    @PostMapping("/changePassword")
    public ModelAndView changePassword(HttpServletRequest request, @RequestParam Map<String, String> param) {
        ModelAndView modelAndView = new ModelAndView("settings");
        String email = request.getRemoteUser();
        if(userService.getUserByEmail(email).isPresent()) {
            User user = userService.getUserByEmail(email).get();
            if(passwordEncoder.matches(param.get("currentPassword"),user.getPassword())) {
                user.setPassword(passwordEncoder.encode(param.get("newPassword")));
                userService.updateUser(user);
                System.out.println("Password is changed");
                modelAndView.addObject("successMessage", "New password is saved");
            }
            else {
                //current password doesn't match
                System.out.println("current password doesn't match");
                modelAndView.addObject("errorMessage","The current password doesn't match");
            }
        }
        else {
            //somehow the user does not exist
            System.out.println("The user doesn't exist");
            modelAndView.addObject("errorMessage","Illegal action");
        }

        return modelAndView;
    }

}
