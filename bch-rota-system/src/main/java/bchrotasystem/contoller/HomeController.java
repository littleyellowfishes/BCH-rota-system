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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



@Controller
public class HomeController {

    @Autowired
    ShiftService shiftService;
    @Autowired
    UserService userService;
    @Autowired
    ShiftTypeService shiftTypeService;
    private int noOfCol;


    @RequestMapping(value = {"/admin","/rota"})
    public String showRotaWithRange(HttpServletRequest request, Model model,
                                    @RequestParam(value = "y", required = false) Integer year,
                                    @RequestParam(value = "m", required = false) Integer month,
                                    @RequestParam(value = "r", required = false) Integer range) throws JSONException {
        List<ShiftType> shiftTypes = shiftTypeService.getShiftTypeList();
        JSONArray shiftTypeNames = getShiftTypeNames(shiftTypes);
        JSONArray shiftTypeColours = getShiftTypeColours(shiftTypes);

        boolean admin = request.isUserInRole("ADMIN");
        if(year == null) {
            year = LocalDate.now().getYear();
        }
        if(month == null) {
            month = LocalDate.now().getMonth().getValue();
        }
        if(range == null) {
            range = 6;
        }

        model.addAttribute("shiftTypeNames", shiftTypeNames.toString());
        model.addAttribute("shiftTypeColours",shiftTypeColours.toString());
        JSONArray wrap = getRangeOfShiftJson(
                LocalDate.of(year,month,1),
                LocalDate.of(year,month,1).plusMonths(range).minusDays(1),
                admin
        );
        JSONArray cols = getColumnHeader(wrap, shiftTypeNames);
        model.addAttribute("serverData", wrap.toString());
        model.addAttribute("cols",cols.toString());

        if(admin) {
            return "admin";
        }
        return "rota";
    }

    JSONArray getColumnHeader(JSONArray data, JSONArray shiftTypes) throws JSONException {
        JSONArray result = new JSONArray();
        JSONObject dateCol = new JSONObject();
        dateCol.put("width",110);
        dateCol.put("title","Date");
        result.put(dateCol);
        for(int i=1; i <= this.noOfCol; i++) {
            JSONObject normalCol = new JSONObject();
            normalCol.put("type","dropdown");
            if(!data.getJSONObject(0).getJSONObject(String.valueOf(i)).isNull("shift")
                    && !data.getJSONObject(0).getJSONObject(String.valueOf(i)).getJSONObject("shift").isNull("shiftType"))
            {
                normalCol.put("title",data.getJSONObject(0).getJSONObject(String.valueOf(i)).getJSONObject("shift").getString("shiftType"));
            }
            else {
                normalCol.put("title"," ");
            }
            normalCol.put("width",100);
            normalCol.put("source", shiftTypes);
            result.put(normalCol);
        }
        return result;
    }


    @GetMapping(value = "/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/select")
    public String selectRange(Model model) {
        LocalDate now = LocalDate.now();
        Integer year = now.getYear();
        Integer month = now.getMonthValue();
        model.addAttribute("year",year);
        model.addAttribute("month",month);
        return "select";
    }

    @RequestMapping(value = "/")
    public RedirectView frontPage(HttpServletRequest request) {
        if(request.isUserInRole("ADMIN")) {
            return new RedirectView("/admin");
        }
        else if(request.isUserInRole("SPECTATOR") || request.isUserInRole("USER")) {
            return new RedirectView("/rota");
        }
        return new RedirectView("/login");
    }



    private JSONObject getUserJsonRow(List<User> SHOs, List<User> Regs) throws JSONException {
        JSONObject result = new JSONObject();
        result.put(String.valueOf(0), null);
        int i = 1;
        for(User user : SHOs) {
            JSONObject item = new JSONObject();
            JSONObject subItem = new JSONObject();
            subItem.put("id", user.getId());
            subItem.put("shiftType", user.getFirstName() + " " + user.getLastName().substring(0,1));
            subItem.put("lastName",user.getLastName());
            subItem.put("role",user.getRole());
            item.put("shift",subItem);
            result.put(String.valueOf(i), item);
            i++;
        }
        JSONObject empty = new JSONObject();
        JSONObject emptySubItem = new JSONObject();
        emptySubItem.put("id", -1);
        empty.put("shift",emptySubItem);
        result.put(String.valueOf(i), empty);
        i++;
        for(User user : Regs) {
            JSONObject item = new JSONObject();
            JSONObject subItem = new JSONObject();
            subItem.put("id", user.getId());
            subItem.put("shiftType", user.getFirstName() + " " + user.getLastName().substring(0,1));
            subItem.put("lastName",user.getLastName());
            subItem.put("role",user.getRole());
            item.put("shift",subItem);
            result.put(String.valueOf(i), item);
            i++;
        }
        return result;
    }

    private JSONObject getShiftJsonRowWithDate(LocalDate date, List<Shift> shifts, List<User> SHOs, List<User> Regs, List<ShiftType> shiftTypes, boolean admin) throws JSONException {
        JSONObject result = new JSONObject();
        JSONObject dateSubItem = new JSONObject();

        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        dateSubItem.put("date", formattedDate);
        switch (date.getDayOfWeek()) {
            case SATURDAY:
            case SUNDAY:
                dateSubItem.put("weekend",true);
                break;
            default:
                dateSubItem.put("weekend",false);
        }
        result.put(String.valueOf(0),dateSubItem);
        int index;
        for(Shift shift : shifts) {
            int j = 0;
            for(User user : SHOs) {
                if(shift.getUserID().equals(user.getId())) {
                    index = j;
                    JSONObject item = shiftToJson(shift,user,shiftTypes,formattedDate,admin);
                    result.put(String.valueOf(index+1),item);
                }
                j++;
            }
            j++;  //gap between SHOs and Regs
            for(User user : Regs) {
                if(shift.getUserID().equals(user.getId())) {
                    index = j;
                    JSONObject item = shiftToJson(shift,user,shiftTypes,formattedDate,admin);
                    result.put(String.valueOf(index+1),item);
                }
                j++;
            }
            this.noOfCol = j;
        }

        return result;
    }

    private JSONObject shiftToJson(Shift shift, User user, List<ShiftType> shiftTypes, String formattedDate, boolean admin) throws JSONException {
        JSONObject item = new JSONObject();
        JSONObject subItem = new JSONObject();

        subItem.put("id", shift.getId());
        subItem.put("date",formattedDate);
        subItem.put("firstName", user.getFirstName());
        subItem.put("lastName", user.getLastName());

        if(shift.getShiftTypeID() != null) {
            String shiftTypeName = null;
            for(ShiftType shiftType : shiftTypes) {
                if(shiftType.getId().equals(shift.getShiftTypeID())) {
                    shiftTypeName = shiftType.getName();
                    break;
                }
            }
            subItem.put("shiftType",shiftTypeName);
        }

        if(admin) {
            subItem.put("note",shift.getNote());
        }
        item.put("shift", subItem);
        return item;
    }

    private JSONArray getRangeOfShiftJson(LocalDate startDate, LocalDate endDate, boolean admin) throws JSONException {
        JSONArray result = new JSONArray();
        List<Shift> rangeShifts = shiftService.getShiftsBetweenDates(startDate, endDate);
        List<User> allBasicUsers = userService.getBasicUserList();
        List<User> SHOs = new ArrayList<>();
        List<User> Regs = new ArrayList<>();
        List<ShiftType> shiftTypes = shiftTypeService.getShiftTypeList();
        for(User user : allBasicUsers) {
            switch (user.getRole()) {
                case 2:
                    SHOs.add(user);
                    break;
                case 3:
                    Regs.add(user);
                default:
                    break;
            }
        }
        JSONObject names = getUserJsonRow(SHOs, Regs);
        result.put(names);

        for(LocalDate i = startDate; i.isBefore(endDate) || i.isEqual(endDate); i = i.plusDays(1)) {
            List<Shift> dateShifts = new ArrayList<>();
            for(Shift shift : rangeShifts) {
                if(shift.getDate().isEqual(i)) {
                    dateShifts.add(shift);
                    //rangeShifts.remove(shift);
                }
            }
            result.put(getShiftJsonRowWithDate(i, dateShifts, SHOs, Regs, shiftTypes, admin));
        }
        return result;
    }

    private JSONArray getShiftTypeData() throws JSONException {
        List<ShiftType> shiftTypes = shiftTypeService.getShiftTypeList();
        JSONArray jsonArray = new JSONArray();
        for(ShiftType shiftType : shiftTypes) {
            if(!shiftType.isDeprecated()) {
                JSONObject item = new JSONObject();
                JSONObject subItem = new JSONObject();
                subItem.put("shiftTypeId", shiftType.getId());
                subItem.put("name", shiftType.getName());
                subItem.put("colour", shiftType.getColour());
                item.put("shiftType", subItem);
                jsonArray.put(item);
            }
        }
        return jsonArray;
    }

    private JSONArray getShiftTypeNames(List<ShiftType> shiftTypes) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(ShiftType shiftType : shiftTypes) {
            if(!shiftType.isDeprecated()) {
                jsonArray.put(shiftType.getName());
            }
        }
        return jsonArray;
    }
    private JSONArray getShiftTypeColours(List<ShiftType> shiftTypes) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(ShiftType shiftType : shiftTypes) {
            if(!shiftType.isDeprecated()) {
                jsonArray.put(shiftType.getColour());
            }
        }
        return jsonArray;
    }

}