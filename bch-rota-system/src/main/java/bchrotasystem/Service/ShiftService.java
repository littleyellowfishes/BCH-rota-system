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

import bchrotasystem.entity.Shift;
import bchrotasystem.entity.ShiftType;
import bchrotasystem.entity.User;
import bchrotasystem.repository.ShiftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ShiftTypeService shiftTypeService;

//    private List<Shift> ShiftList = Arrays.asList(
//            new Shift(LocalDate.of(2020,1,5),1,1,"Ill"),
//            new Shift(LocalDate.of(2020,1,5),2,2),
//            new Shift(LocalDate.of(2020,1,5),3,3)
//            );

    //tested
    public List<Shift> getShiftList() {
        List<Shift> shifts = new ArrayList<>();
        shiftRepository.findAll()
                .forEach(shifts::add);
        return shifts;
    }

    //tested
    public void addShift(Shift shift) {
        Shift shift2 = new Shift(shift.getDate().plusDays(1),shift.getUserID(),shift.getShiftTypeID(),shift.getNote());
        if (shift.getId() != null) {
            shift2.setId(shift.getId());
        }
        shiftRepository.save(shift2);
    }

    //tested
    public void updateShift(Shift shift) { addShift(shift); }

    public void deleteById(Integer id) {
        if (shiftRepository.existsById(id)){shiftRepository.deleteById(id);}
    }

    //tested
    public void deleteShift(Shift shift) {
        shiftRepository.findAll()
                .forEach((temp) -> {
                    if (shiftsEqual(temp,shift)) shiftRepository.deleteById(temp.getId());
                });
    }

    public void deleteAllShifts() { shiftRepository.deleteAll(); }

    public Shift getShift(Integer id) { return shiftRepository.findById(id).orElse(new Shift()); }

    //tested
    public long countShifts() {return shiftRepository.count(); }

    //tested
    public List<Shift> getShiftsByDate(LocalDate date) {
        List<Shift> shifts = new ArrayList<>();
        shiftRepository.findAll()
                .forEach((temp) -> {
                    if (temp.getDate().compareTo(date) == 0) shifts.add(temp);
                });
        return shifts;
    }

    public List<Shift> getShiftsBetweenDates(LocalDate date1, LocalDate date2) {
        LocalDate dateTemp;
        if (date2.isBefore(date1)){
            dateTemp = date1;
            date1 = date2;
            date2 = dateTemp;
        }
        List<Shift> returnList = new ArrayList<>();
        List<Shift> shifts = getShiftList();
        for (Shift temp: shifts) {
            if ((temp.getDate().isBefore(date2) && (temp.getDate().isAfter(date1)) ) || temp.getDate().isEqual(date1) || temp.getDate().isEqual(date2) ) returnList.add(temp);
        }
        return returnList;
    }


    //tested
    public List<Shift> getShiftsByName(String firstName, String lastName) {
        Integer userID = userService.getIDByNames(firstName,lastName);
        List<Shift> returnList = new ArrayList<>();
        List<Shift> shifts = getShiftList();
        for (Shift temp:shifts) {
            if (temp.getUserID().equals(userID)) {returnList.add(temp);}
        }
        return returnList;
    }


    public Boolean shiftsEqual(Shift shift1, Shift shift2) {
        return (shift1.getUserID().equals(shift2.getUserID()) && shift1.getShiftTypeID().equals(shift2.getShiftTypeID()) && shift1.getDate().isEqual(shift2.getDate()) && shift1.getNote().equals(shift2.getNote()));
    }

    //tested
    public void deleteShifts(List<Shift> shifts) {
        shiftRepository.findAll()
                .forEach((temp) -> {
                    for (Shift shift : shifts) {
                        if (shiftsEqual(temp, shift)) shift.setId(temp.getId());
                    }
                });
        shiftRepository.deleteAll(shifts);
    }

    //Tested
    public void addShifts(List<Shift> shifts) {
        List<Shift> shifts2 = new ArrayList<>();
        for (Shift shift : shifts) {
            Shift shift2 = new Shift(shift.getDate().plusDays(1),shift.getUserID(),shift.getShiftTypeID(),shift.getNote());
            if (shift.getId() != null) {
                shift2.setId(shift.getId());
            }
            shifts2.add(shift2);
        }
        shiftRepository.saveAll(shifts2);
    }

    //tested
    public User getUser(Shift shift) {
        return userService.getUser(shift.getUserID());
    }

    //tested
    public ShiftType getShiftType(Shift shift) {
        return shiftTypeService.getShiftType(shift.getShiftTypeID());
    }

    //tested
    public String getUserFirstName(Shift shift) {
        return userService.getUser(shift.getUserID()).getFirstName();
    }

    //tested
    public String getUserLastName(Shift shift) {
        return userService.getUser(shift.getUserID()).getLastName();
    }

    //tested
    public String getShiftTypeName(Shift shift){
        return shiftTypeService.getShiftType(shift.getShiftTypeID()).getName();
    }







    //public void updateShift(Shift shift) { addShift(shift); }
}
