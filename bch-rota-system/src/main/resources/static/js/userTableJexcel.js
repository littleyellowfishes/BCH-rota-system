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

var originalData = JSON.parse(userData);
var copiedData = JSON.parse(JSON.stringify(originalData));
var selected = [];

var user = null;

var selection = function(instance, y1, x1, y2, x2, origin) {

    user = copiedData[x1]["user"];
    selected = [];
    selected.push(user);
    $.post({
        url: "/userSelect",
        data: JSON.stringify(selected),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        cache: false
    });
    document.getElementById("editId").defaultValue = user["userId"];
    document.getElementById("editEmail").defaultValue = user["email"];
    document.getElementById("editFirstName").defaultValue = user["firstName"];
    document.getElementById("editLastName").defaultValue = user["lastName"];

    function setSelectedIndex(s, i) {
        s.options[i].selected = true;
    }
    switch (user["accountLevel"]) {
        case "User":
            setSelectedIndex(document.getElementById("editAccountLevel"),0);
            break;
        case "Spectator":
            setSelectedIndex(document.getElementById("editAccountLevel"),1);
            break;
        case "Admin":
            setSelectedIndex(document.getElementById("editAccountLevel"),2);
            break;
        default:
            break;
    }
    switch (user["role"]) {
        case "SHO":
            setSelectedIndex(document.getElementById("editRole"),0);
            break;
        case "Reg":
            setSelectedIndex(document.getElementById("editRole"),1);
            break;
        case "Supervisor":
            setSelectedIndex(document.getElementById("editRole"),2);
            break;
        case "Administrator":
            setSelectedIndex(document.getElementById("editRole"),3);
            break;
        default:
            break;
    }

};
var myTable = new jexcel(document.getElementById('userTable'), {
    data:copiedData,
    columns: [
        {"title":"First name", "width":150},
        {"title":"Last name", "width":150},
        {"title":"Email", "width":300},
        {"title":"Account level", "width":150},
        {"title":"Role", "width":150}
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
                    myTable.setValue(getColumnCode(row,col),copiedData[row]["user"]["firstName"],true);
                    break;
                case 1:
                    myTable.setValue(getColumnCode(row,col),copiedData[row]["user"]["lastName"],true);
                    break;
                case 2:
                    myTable.setValue(getColumnCode(row,col),copiedData[row]["user"]["email"],true);
                    break;
                case 3:
                    myTable.setValue(getColumnCode(row,col),copiedData[row]["user"]["accountLevel"],true);
                    break;
                case 4:
                    myTable.setValue(getColumnCode(row,col),copiedData[row]["user"]["role"],true);
                    break;
                default:
                    myTable.setValue(getColumnCode(row,col)," ",true);
                    break;
            }
        }
    }

}

function deleteSelectedUser() {
    if(selected.length === 0) {
        alert("No user is selected");
    }
    else {
        var ask = confirm("Do you want to delete the selected user?\nWARNING: This action cannot be undone!");
        if(ask) {
            $.post({
                url: "/deleteUser",
                success: function () {
                    selected = [];
                    alert("The selected user is deleted");
                    location.reload();
                }
            });
        }
    }
}

document.addEventListener("DOMContentLoaded", function(){
    initData();
});
