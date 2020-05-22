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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
public class DataController {

    @Autowired
    ShiftService shiftService;
    @Autowired
    UserService userService;
    @Autowired
    ShiftTypeService shiftTypeService;

    @Autowired
    PasswordEncoder passwordEncoder;

    private JSONArray changedRowJson;
    private JSONArray selectedUserJson;
    private JSONArray selectedShiftTypeJson;

    @RequestMapping(value = "/change", method = RequestMethod.POST, consumes = "application/json")
    public void getAfterChangePostRequest(@RequestBody String payload) throws Exception {
        this.changedRowJson = new JSONArray(payload);
        System.out.println(payload);
        System.out.println(changedRowJson);
        System.out.println("Data incoming");
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public void saveChanges() throws Exception {
        List<Shift> shifts = convertToShifts(this.changedRowJson);
        List<Shift> shiftsFinal = new ArrayList<>();
        boolean isDupe = false;
        List<Shift> removes = new ArrayList<>();

        for(Shift shift : shifts) {

            for (Shift shift2 : shiftsFinal) {
                if (shift.equals(shift2)) {
                    isDupe = true;
                    break;
                }
                if(shift.getDate().equals(shift2.getDate()) && shift.getUserID().equals(shift2.getUserID())) {
                    removes.add(shift2);
                }
            }
            for(Shift removeShift : removes) {
                shiftsFinal.remove(removeShift);
            }

            if (isDupe) {
                isDupe = false;
            } else {
                shiftsFinal.add(shift);
            }
        }
        shiftService.addShifts(shiftsFinal);
    }

    @RequestMapping(value = "/userSelect", method = RequestMethod.POST, consumes = "application/json")
    public void getSelectedUserId(@RequestBody String payload) throws JSONException {
        System.out.println(payload);
        selectedUserJson = new JSONArray(payload);
        System.out.println("Selected User data incoming");
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public void deleteSelectedUserFromUserList() throws JSONException {
        Integer userId = selectedUserJson.getJSONObject(0).getInt("userId");
        userService.deleteUser(userService.getUser(userId));
    }

    @RequestMapping(value = "/shiftTypeSelect", method = RequestMethod.POST, consumes = "application/json")
    public void getSelectedShiftTypeId(@RequestBody String payload) throws JSONException {
        System.out.println(payload);
        selectedShiftTypeJson = new JSONArray(payload);
        System.out.println("Selected shiftType data incoming");
    }

    @RequestMapping(value = "/deleteShiftType", method = RequestMethod.POST)
    public void deleteSelectedShiftType() throws JSONException {
        Integer shiftTypeId = selectedShiftTypeJson.getJSONObject(0).getInt("shiftTypeId");
        ShiftType deprecatedShiftType = shiftTypeService.getShiftType(shiftTypeId);
        deprecatedShiftType.setDeprecated(true);
        shiftTypeService.updateShiftType(deprecatedShiftType);
    }


    private List<Shift> convertToShifts(JSONArray shiftJsonArray) throws JSONException {
        List<User> basicUsers = userService.getBasicUserList();
        List<ShiftType> shiftTypes = shiftTypeService.getShiftTypeList();
        List<Shift> shifts = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for(int j = 0; j < shiftJsonArray.length(); j++) {
            JSONObject jsonRow = shiftJsonArray.getJSONObject(j);
            if(!jsonRow.isNull("shift")) {  //Ensures shift is not null
                JSONObject shiftJson = jsonRow.getJSONObject("shift");
                LocalDate date = LocalDate.parse(shiftJson.getJSONObject("date").getString("date"), formatter);
                Integer userId = -1;
                for(User user : basicUsers) {
                    if(user.getFirstName().equals(shiftJson.getString("firstName")) && user.getLastName().equals(shiftJson.getString("lastName"))) {
                        userId = user.getId();
                        break;
                    }
                }
                Shift shift = new Shift();
                shift.setDate(date);
                shift.setUserID(userId);

                if(jsonRow.getJSONObject("shift").isNull("id")) {  //The shift is new shift
                    if(shiftJson.isNull("shiftType")) {  //The cell is empty in frontend
                        shift.setShiftTypeID(-1);
                    }
                    else {  //The cell have shiftType in frontend
                        Integer shiftTypeId = -1;
                        for(ShiftType shiftType : shiftTypes) {
                            if(shiftType.getName().equals(shiftJson.getString("shiftType"))) {
                                shiftTypeId = shiftType.getId();
                                break;
                            }
                        }
                        shift.setShiftTypeID(shiftTypeId);
                    }
                    if(!shiftJson.isNull("note")) {  //The shift has note
                        String note = shiftJson.getString("note");
                        shift.setNote(note);
                    }
                }
                else {  //The shift is existing shift
                    Integer id = shiftJson.getInt("id");
                    shift.setId(id);
                    if(shiftJson.isNull("shiftType") || shiftJson.getString("shiftType").equals("")) {  //The cell is empty in frontend
                        if(shiftJson.isNull("note")) {  //No note
                            shiftService.deleteById(id);  //Empty without note, so delete the shift from DB then break
                            continue;
                        }
                        else {  //note
                            shift.setShiftTypeID(-1);  //Empty with note, so null shiftType id -1
                            String note = shiftJson.getString("note");
                            shift.setNote(note);
                        }
                    }
                    else {  //The cell have shiftType in frontend
                        Integer shiftTypeId = -1;
                        for(ShiftType shiftType : shiftTypes) {
                            if(shiftType.getName().equals(shiftJson.getString("shiftType"))) {
                                shiftTypeId = shiftType.getId();
                                break;
                            }
                        }
                        shift.setShiftTypeID(shiftTypeId);
                        if(!shiftJson.isNull("note")) {  //The shift has note
                            String note = shiftJson.getString("note");
                            shift.setNote(note);
                        }
                    }
                }
                shifts.add(shift);
            }
        }
        return shifts;
    }

    @RequestMapping(value = "/load")
    public String loadEverything() {
        loadUser();
        loadShiftType();
        loadShift();
        return "Test data is loaded";
    }

    private void loadShift() {
        Integer fredId = userService.getIDByNames("Fred", "Love");
        Integer thomasId = userService.getIDByNames("Thomas", "Cook");
        Integer amyId = userService.getIDByNames("Amy", "November");
        Integer maxId = userService.getIDByNames("Max", "Bloom");
        Integer natashaId = userService.getIDByNames("Natasha", "Burns");

        Integer L_id = shiftTypeService.getIDByName("L");
        Integer O_id = shiftTypeService.getIDByName("O");
        Integer F_id = shiftTypeService.getIDByName("F");
        Integer E1_id = shiftTypeService.getIDByName("E1");
        Integer SL_id = shiftTypeService.getIDByName("SL");
        Integer AL_id = shiftTypeService.getIDByName("AL");
        Integer CG_id = shiftTypeService.getIDByName("CG");
        
        List<Shift> testShifts = new ArrayList<>();
        
        testShifts.add(new Shift( LocalDate.of(2020,5,10), fredId, L_id));
        testShifts.add(new Shift( LocalDate.of(2020, 5, 11), fredId, L_id));
        testShifts.add(new Shift( LocalDate.of(2020,5,12), fredId, SL_id));
        testShifts.add(new Shift( LocalDate.of(2020, 5, 13), fredId, O_id));
        testShifts.add(new Shift( LocalDate.of(2020,5,10), thomasId, F_id));
        testShifts.add(new Shift( LocalDate.of(2020,5,11), thomasId, AL_id));
        testShifts.add(new Shift( LocalDate.of(2020,5,12), thomasId, CG_id));
        testShifts.add(new Shift( LocalDate.of(2020,5,10), amyId, E1_id));
        testShifts.add(new Shift( LocalDate.of(2020,5,11), amyId, E1_id));
        testShifts.add(new Shift( LocalDate.of(2020,5,12), amyId, F_id));
        testShifts.add(new Shift( LocalDate.of(2020,5,14), amyId, AL_id));
        testShifts.add(new Shift(LocalDate.of(2020, 5, 20), maxId, SL_id));
        testShifts.add(new Shift(LocalDate.of(2020, 5, 24), natashaId, O_id));
        shiftService.deleteAllShifts();
        shiftService.addShifts(testShifts);
    }

    private void loadShiftType() {
        List<ShiftType> testShiftTypes = new ArrayList<>();
        testShiftTypes.add(new ShiftType("O", 9,0, "","#FFF"));
        testShiftTypes.add(new ShiftType("F", 0, 0, "", "#FFF"));
        testShiftTypes.add(new ShiftType("E2", 0,0,"", "#FFF"));
        testShiftTypes.add(new ShiftType("EW", 0,0, "Early weekend", "#FFF"));
        testShiftTypes.add(new ShiftType("M",0,0, "", "#FFF"));
        testShiftTypes.add(new ShiftType("MW",0,0, "Morning weekend", "#FFF"));
        testShiftTypes.add(new ShiftType("L", 0,0, "Late", "#FFF"));
        testShiftTypes.add(new ShiftType("LW", 0,0, "Late weekend", "#FFF"));
        testShiftTypes.add(new ShiftType("N",0,0,"Night", "#FFF"));
        testShiftTypes.add(new ShiftType("CG", 0,0, "", "#FC0"));
        testShiftTypes.add(new ShiftType("E1",0,0,"","#FFF"));
        testShiftTypes.add(new ShiftType("SL",0,0,"Study leave", "#09F"));
        testShiftTypes.add(new ShiftType("AL",0,0,"Annual leave", "#9C6"));
        testShiftTypes.add(new ShiftType("ML", 0,0,"","#363"));
        testShiftTypes.add(new ShiftType("Sec",0,0,"", "#FFF"));
        testShiftTypes.add(new ShiftType("Comm", 0,0,"","#999"));
        testShiftTypes.add(new ShiftType("GPSI",0,0,"","#F39"));
        shiftTypeService.deleteAllShiftTypes();
        shiftTypeService.addShiftTypes(testShiftTypes);
    }

    private void loadUser() {
        // 0: admin, 1: spectator, 2: user
        List<User> testUsers = new ArrayList<>();
        testUsers.add( new User("Fred", "Love", 2, 2,"flove@mail.com",passwordEncoder.encode("pass")));
        testUsers.add( new User("Thomas", "Cook", 2, 2, "tcook@mail.com",passwordEncoder.encode("pass")));
        testUsers.add( new User("Amy", "November", 2, 2, "anovember@mail.com",passwordEncoder.encode("pass")));
        testUsers.add( new User("Max", "Bloom", 2, 3, "mbloom@mail.com",passwordEncoder.encode("pass")));
        testUsers.add( new User("Natasha", "Burns", 2, 3, "nburns@mail.com",passwordEncoder.encode("pass")));
        testUsers.add( new User("Dummy", "Admin", 0, 0, "admin@mail.com",passwordEncoder.encode("admin")));
        testUsers.add( new User("Dummy", "Spectator", 1, 1, "spectator@mail.com",passwordEncoder.encode("pass")));
        userService.deleteAllUsers();
        userService.addUsers(testUsers);
    }

}
