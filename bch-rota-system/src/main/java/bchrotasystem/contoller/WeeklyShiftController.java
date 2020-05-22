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

import bchrotasystem.Service.ShiftService;
import bchrotasystem.Service.ShiftTypeService;
import bchrotasystem.Service.UserService;
import bchrotasystem.entity.Shift;
import bchrotasystem.entity.ShiftType;
import bchrotasystem.entity.User;
import bchrotasystem.entity.WeeklyShift;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WeeklyShiftController {

    @Autowired
    UserService userService;
    @Autowired
    ShiftService shiftService;
    @Autowired
    ShiftTypeService shiftTypeService;

    @RequestMapping(value = "/print")
    public String adminPrint(Model model) {
        LocalDate startOfWeek = getCurrentMonday();
        String formattedDate = startOfWeek.format(DateTimeFormatter.ofPattern("cccc dd LLLL yyyy"));
        List<ShiftType> shiftTypes = shiftTypeService.getShiftTypeList();
        List<Shift> weekly = getWeeklyShiftList(startOfWeek);
        List<Shift> clinics = new ArrayList<>();
        List<Shift> early1 = new ArrayList<>();
        List<Shift> early2 = new ArrayList<>();
        List<Shift> mid = new ArrayList<>();
        List<Shift> late = new ArrayList<>();
        List<Shift> night = new ArrayList<>();
        List<Shift> floatShift = new ArrayList<>();
        Integer CG_id = -1;
        Integer E1_id = -1;
        Integer E2_id = -1;
        Integer M_id = -1;
        Integer L_id = -1;
        Integer N_id = -1;
        Integer F_id = -1;
        Integer EW_id = -1;
        Integer MW_id = -1;
        Integer LW_id = -1;
        for(ShiftType shiftType : shiftTypes) {
            switch (shiftType.getName()) {
                case "CG":
                    CG_id = shiftType.getId();
                    break;
                case "E1":
                    E1_id = shiftType.getId();
                    break;
                case "E2":
                    E2_id = shiftType.getId();
                    break;
                case "M":
                    M_id = shiftType.getId();
                    break;
                case "L":
                    L_id = shiftType.getId();
                    break;
                case "N":
                    N_id = shiftType.getId();
                    break;
                case "F":
                    F_id = shiftType.getId();
                    break;
                case "EW":
                    EW_id = shiftType.getId();
                    break;
                case "MW":
                    MW_id = shiftType.getId();
                    break;
                case "LW":
                    LW_id = shiftType.getId();
                    break;
                default:
                    break;
            }
        }

        for(Shift shift : weekly) {
            if(shift.getShiftTypeID().equals(CG_id)) {
                clinics.add(shift);
            }
            else if(shift.getShiftTypeID().equals(E1_id) || shift.getShiftTypeID().equals(EW_id)) {
                early1.add(shift);
            }
            else if(shift.getShiftTypeID().equals(E2_id)) {
                early2.add(shift);
            }
            else if(shift.getShiftTypeID().equals(M_id) || shift.getShiftTypeID().equals(MW_id)) {
                mid.add(shift);
            }
            else if(shift.getShiftTypeID().equals(L_id) || shift.getShiftTypeID().equals(LW_id)) {
                late.add(shift);
            }
            else if(shift.getShiftTypeID().equals(N_id)) {
                night.add(shift);
            }
            else if(shift.getShiftTypeID().equals(F_id)) {
                floatShift.add(shift);
            }
        }

        WeeklyShift clinicUsers = writeWeekRow(clinics);
        WeeklyShift early1Users = writeWeekRow(early1);
        WeeklyShift early2Users = writeWeekRow(early2);
        WeeklyShift midUser = writeWeekRow(mid);
        WeeklyShift lateUser = writeWeekRow(late);
        WeeklyShift nightUser = writeWeekRow(night);
        WeeklyShift floatUser = writeWeekRow(floatShift);

        model.addAttribute("date", formattedDate);
        model.addAttribute("clinic", clinicUsers);
        model.addAttribute("early1", early1Users);
        model.addAttribute("early2", early2Users);
        model.addAttribute("mid",midUser);
        model.addAttribute("late", lateUser);
        model.addAttribute("night", nightUser);
        model.addAttribute("float", floatUser);
        return "print";
    }

    private WeeklyShift writeWeekRow(List<Shift> shifts) {
        WeeklyShift result = new WeeklyShift();
        List<User> users = userService.getBasicUserList();
        for(Shift shift: shifts) {
            Integer userID = shift.getUserID();
            User user = new User();
            for(User user1 : users) {
                if(user1.getId().equals(userID)) {
                    user = user1;
                    break;
                }
            }
            String userName = user.getFirstName() + " " + user.getLastName().substring(0,1);
            switch (shift.getDate().getDayOfWeek()) {
                case MONDAY:
                    result.mon.add(userName);
                    break;
                case TUESDAY:
                    result.tue.add(userName);
                    break;
                case WEDNESDAY:
                    result.wed.add(userName);
                    break;
                case THURSDAY:
                    result.thr.add(userName);
                    break;
                case FRIDAY:
                    result.fri.add(userName);
                    break;
                case SATURDAY:
                    result.sat.add(userName);
                    break;
                case SUNDAY:
                    result.sun.add(userName);
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private LocalDate getCurrentMonday() {
        LocalDate date = LocalDate.now();
        System.out.println(date.with(DayOfWeek.MONDAY));
        return date.with(DayOfWeek.MONDAY);
    }

    private List<Shift> getWeeklyShiftList(LocalDate monday) {
        return shiftService.getShiftsBetweenDates(monday, monday.plusWeeks(1).minusDays(1));
    }
}
