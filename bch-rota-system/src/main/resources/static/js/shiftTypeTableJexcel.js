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

var originalData = JSON.parse(shiftTypeData);
var copiedData = JSON.parse(JSON.stringify(originalData));
var selected = [];

var shiftType = null;

var selection = function(instance, y1, x1, y2, x2, origin) {
    shiftType = copiedData[x1]["shiftType"];
    selected = [];
    selected.push(shiftType);
    $.post({
        url: "/shiftTypeSelect",
        data: JSON.stringify(selected),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        cache: false
    });
    document.getElementById("editId").defaultValue = shiftType["shiftTypeId"];
    document.getElementById("editName").defaultValue = shiftType["name"];
    document.getElementById("editDescription").defaultValue = shiftType["description"];

    function setSelectedIndex(s, i) {
        s.options[i].selected = true;
    }
    setSelectedIndex(document.getElementById("editStartTime"),shiftType["startTime"]);
    setSelectedIndex(document.getElementById("editEndTime"),shiftType["endTime"]);

    switch (shiftType["colour"]) {
        case "#FFF":
            setSelectedIndex(document.getElementById("editColour"),0);
            break;
        case "#F00":
            setSelectedIndex(document.getElementById("editColour"),1);
            break;
        case "#F60":
            setSelectedIndex(document.getElementById("editColour"),2);
            break;
        case "#FC0":
            setSelectedIndex(document.getElementById("editColour"),3);
            break;
        case "#9C6":
            setSelectedIndex(document.getElementById("editColour"),4);
            break;
        case "#363":
            setSelectedIndex(document.getElementById("editColour"),5);
            break;
        case "#09F":
            setSelectedIndex(document.getElementById("editColour"),6);
            break;
        case "#009":
            setSelectedIndex(document.getElementById("editColour"),7);
            break;
        case "#609":
            setSelectedIndex(document.getElementById("editColour"),8);
            break;
        case "#F39":
            setSelectedIndex(document.getElementById("editColour"),9);
            break;
        case "#999":
            setSelectedIndex(document.getElementById("editColour"),10);
            break;
        default:
            break;
    }
};
var myTable = new jexcel(document.getElementById('shiftTypeTable'), {
    data:copiedData,
    columns: [
        {"title":"Name", "width":100},
        {"title":"Starts at", "width":100},
        {"title":"Ends at", "width":100},
        {"title":"Description", "width":300},
        {"title":"Colour", "width":80}
    ],
    onselection:selection,
    allowComments:true,
    allowInsertColumn:false,
    allowDeleteColumn:false,
    allowRenameColumn:false,
    columnSorting:false,
    allowInsertRow:false,
    allowDeleteRow:false,
    about:false,
    allowExport:false,
    columnDrag:false,
    rowDrag:false,
    updateTable: function(el, cell, y, x, source, value, id) {


        cell.classList.add('readonly');
    },
    contextMenu: []
});

function getColumnCode(row, col) {
    var cellId = col.toString() + "-" + row.toString();
    return jexcel.getColumnNameFromId(cellId);
}

function initData() {
    for(let row=0; row < copiedData.length; row++) {
        for(let col=0; col < 5; col++) {
            switch (col) {
                case 0:
                    myTable.setValue(getColumnCode(row,col),copiedData[row]["shiftType"]["name"],true);
                    break;
                case 1:
                    myTable.setValue(getColumnCode(row,col),copiedData[row]["shiftType"]["startTime"],true);
                    break;
                case 2:
                    myTable.setValue(getColumnCode(row,col),copiedData[row]["shiftType"]["endTime"],true);
                    break;
                case 3:
                    myTable.setValue(getColumnCode(row,col),copiedData[row]["shiftType"]["description"],true);
                    break;
                case 4:
                    myTable.setValue(getColumnCode(row,col),copiedData[row]["shiftType"]["colour"],true);
                    break;
                default:
                    myTable.setValue(getColumnCode(row,col)," ",true);
                    break;
            }
        }
    }

}

function shiftTypeColourRenderer(row, col) {
    var columnCode = getColumnCode(row,col);
    myTable.setStyle(columnCode,"background-color",myTable.getValue(columnCode));
}

function deleteSelectedShiftType() {
    if(selected.length === 0) {
        alert("No shift type is selected");
    }
    else {
        var ask = confirm("Do you want to delete the selected shift type?\nWARNING: This action cannot be undone!");
        if(ask) {
            $.post({
                url: "/deleteShiftType",
                success: function () {
                    selected = [];
                    alert("The selected shift-type is deleted");
                    location.reload();
                }
            });
        }
    }
}

document.addEventListener("DOMContentLoaded", function(){
    initData();
    for(let row=0; row < copiedData.length; row++) {
        shiftTypeColourRenderer(row,4);
    }
});
