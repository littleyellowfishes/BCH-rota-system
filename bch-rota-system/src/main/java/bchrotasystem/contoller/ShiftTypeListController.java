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

import bchrotasystem.Service.ShiftTypeService;
import bchrotasystem.entity.ShiftType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;
import java.util.List;

@Controller
public class ShiftTypeListController {

    @Autowired
    ShiftTypeService shiftTypeService;

    boolean addSuccess = false;
    boolean editSuccess = false;
    boolean editIllegal = false;

    @RequestMapping(value = "/admin/shift_types")
    public String listOfShiftTypes(Model model) throws JSONException {
        JSONArray shiftTypeList = getAllShiftTypeJsonArray();
        String jsonString = shiftTypeList.toString();
        if(addSuccess) {
            model.addAttribute("successMessage","The shift-type is added");
            this.addSuccess = false;
        }
        if(editSuccess) {
            model.addAttribute("successMessage","The shift-type is edited");
            this.editSuccess = false;
        }
        if(editIllegal) {
            model.addAttribute("errorMessage","The shift-type does not exist");
            this.editIllegal = false;
        }
        model.addAttribute("shiftTypeData", jsonString);
        return "shift_types";
    }

    @RequestMapping(value = "/addShiftType", method = RequestMethod.POST)
    public RedirectView getNewShiftTypeInfo(String name, Integer startTime, Integer endTime, Integer colour, String description) {
        String colourHexCode = getColourHexCode(colour);
        ShiftType shiftType = new ShiftType(name, startTime, endTime, colourHexCode);
        if(description != null) {
            shiftType.setDescription(description);
        }
        System.out.println(shiftType.getName()+"/"+shiftType.getStartTime()+"/"+shiftType.getEndTime()+"/"+shiftType.getColour()+"/"+shiftType.getDescription());
        shiftTypeService.addShiftType(shiftType);
        this.addSuccess = true;
        return new RedirectView("/admin/shift_types");
    }

    @PostMapping("/editShiftType")
    public RedirectView editShiftTypeInfo(Integer id, String name, Integer startTime, Integer endTime, Integer colour, String description) {
        String colourHexCode = getColourHexCode(colour);
        if(id == null) {
            //no shiftType was selected before
            this.editIllegal = true;
            this.editSuccess = false;
            return new RedirectView("/admin/shift_types");
        }
        ShiftType updatedShiftType = new ShiftType(id,name,startTime,endTime,description,colourHexCode);
        shiftTypeService.updateShiftType(updatedShiftType);
        this.editSuccess = true;
        this.editIllegal = false;
        return new RedirectView("/admin/shift_types");
    }

    private JSONObject convertShiftTypeToJson(ShiftType shiftType) throws JSONException {
        JSONObject item = new JSONObject();
        JSONObject subItem = new JSONObject();
        subItem.put("shiftTypeId", shiftType.getId());
        subItem.put("name", shiftType.getName());
        subItem.put("startTime", shiftType.getStartTime());
        subItem.put("endTime", shiftType.getEndTime());
        subItem.put("description", shiftType.getDescription());
        subItem.put("colour", shiftType.getColour());
        item.put("shiftType", subItem);

        return item;
    }

    private JSONArray getAllShiftTypeJsonArray() throws JSONException {
        JSONArray result = new JSONArray();
        List<ShiftType> shiftTypes = shiftTypeService.getShiftTypeList();
        for(ShiftType shiftType : shiftTypes) {
            if(!shiftType.isDeprecated()) {
                JSONObject shiftTypeJsonItem = convertShiftTypeToJson(shiftType);
                result.put(shiftTypeJsonItem);
            }
        }
        return result;
    }

    private String getColourHexCode(Integer colour) {
        switch (colour) {
            case 1:
                return "#F00";
            case 2:
                return "#F60";
            case 3:
                return "#FC0";
            case 4:
                return "#9C6";
            case 5:
                return "#363";
            case 6:
                return "#09F";
            case 7:
                return "#009";
            case 8:
                return "#609";
            case 9:
                return "#F39";
            case 10:
                return "#999";
            default:
                return "#FFF";
        }
    }
}
