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

import bchrotasystem.entity.ShiftType;
import bchrotasystem.repository.ShiftTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShiftTypeService {

    @Autowired
    private ShiftTypeRepository shiftTypeRepository;


//    private List<ShiftType> ShiftTypeList = Arrays.asList(
//            new ShiftType("Morning", 0,480 ),
//            new ShiftType("Afternoon", 480, 960),
//            new ShiftType("Night", 960, 1440)
//            );

    public List<ShiftType> getShiftTypeList() {
        List<ShiftType> shiftTypes = new ArrayList<>();
        shiftTypeRepository.findAll()
                .forEach(shiftTypes::add);
        return shiftTypes;
    }

    //tested
    public void addShiftType(ShiftType shiftType) { shiftTypeRepository.save(shiftType); }

    public void addShiftTypes(Iterable<ShiftType> shiftTypes) {
        shiftTypeRepository.saveAll(shiftTypes);
    }

    public void updateShiftType(ShiftType shiftType) { shiftTypeRepository.save(shiftType); }

    //tested
    public void deleteShiftType(ShiftType shiftType) {
     //   List<ShiftType> shifts = new ArrayList<>();
        shiftTypeRepository.findAll()
                .forEach((temp) -> {
                    if (shiftTypesEqual(temp,shiftType)) shiftTypeRepository.deleteById(temp.getId());
                });
    }

    //tested
    public void deleteAllShiftTypes() { shiftTypeRepository.deleteAll(); }

    //tested
    public long countShiftTypes() {return shiftTypeRepository.count(); }


    public ShiftType getShiftType(Integer id) { return shiftTypeRepository.findById(id).orElse(new ShiftType()); }


    public boolean shiftTypesEqual(ShiftType shiftType1, ShiftType shiftType2) {
        return (shiftType1.getEndTime() == shiftType2.getEndTime() && shiftType1.getStartTime() == shiftType2.getStartTime() && shiftType1.getName().equals(shiftType2.getName()));
    }

    //tested
    public void deleteShiftTypes(List<ShiftType> shiftTypes) {
        //   List<ShiftType> shifts = new ArrayList<>();
        shiftTypeRepository.findAll()
                .forEach((temp) -> {
                    for (ShiftType shiftType: shiftTypes) {
                        if (shiftTypesEqual(temp, shiftType)) shiftType.setId(temp.getId());
                    }
                });
        shiftTypeRepository.deleteAll(shiftTypes);
    }

    //tested
    public Integer getIDByName(String name){
        int returnID = 0;
        List<ShiftType> shiftTypes = getShiftTypeList();
        for (ShiftType temp : shiftTypes) {
            if (temp.getName().equals(name)) returnID = temp.getId();
        }
        return returnID;
    }
}