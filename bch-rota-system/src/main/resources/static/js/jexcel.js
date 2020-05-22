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

var originalData = JSON.parse(serverData);
var copiedData = JSON.parse(JSON.stringify(originalData));
var cols1 = JSON.parse(cols);
var afterInit = false;

var shiftTypeNamesLoad = JSON.parse(shiftTypeNames);
var shiftTypeColoursLoad = JSON.parse(shiftTypeColoursFromBack);
var shiftTypes = [null];
var shiftTypeColours = ["#FFF"];

var myTable = new jexcel(document.getElementById('rota'), {
    data:copiedData,
    columns: cols1,
    allowComments:true,
    allowInsertColumn:false,
    allowDeleteColumn:false,
    allowRenameColumn:false,
    columnSorting:false,
    allowInsertRow:false,
    allowDeleteRow:false,
    about:false,
    allowExport:false,
    freezeColumns:1,
    columnDrag:false,
    rowDrag:false,
    updateTable: function(el, cell, y, x, source, value, id) {
        cell.classList.add('readonly');
    },
    contextMenu: []
});

function displayData() {
    for(let col=0; col <= Object.keys(copiedData[0]).length; col++) {
        var columnCode = getColumnCode(0, col);
        myTable.setValue(columnCode,"",true);
    }
    for(let row=1; row < copiedData.length; row++) {
        var columnCode1 = getColumnCode(row, 0);

        myTable.setValue(columnCode1,copiedData[row][0]["date"],true);
        for(let col=1; col <= Math.max(... Object.keys(copiedData[row])); col++) {
            var columnCode2 = getColumnCode(row, col);
            if(copiedData[row][col] !== undefined && copiedData[row][col] !== null && copiedData[row][col]["shift"]["shiftType"] !== undefined
                && copiedData[row][col]["shift"]["shiftType"] !== null)
            {
                myTable.setValue(columnCode2,copiedData[row][col]["shift"]["shiftType"],true);
            }
            else {
                myTable.setValue(columnCode2,"",true);
            }

        }
    }
}

function getColumnCode(row, col) {
    var cellId = col.toString() + "-" + row.toString();
    return jexcel.getColumnNameFromId(cellId);
}

function firstRowRenderer() {
    for(let col=0; col <= Object.keys(copiedData[0]).length; col++) {
        var cellId = col.toString() + "-0";
        var columnCode = jexcel.getColumnNameFromId(cellId);
        myTable.setStyle(columnCode, 'background-color', "#000");
    }
}

function shiftRenderer(row,col) {
    var cellId = col.toString() + "-" + row.toString();
    var columnCode = jexcel.getColumnNameFromId(cellId);
    var index = shiftTypes.indexOf(myTable.getValue(columnCode));
    if(index === -1) {
        myTable.setStyle(columnCode, "background-color","#FFF");
    }
    else {
        myTable.setStyle(columnCode, "background-color",shiftTypeColours[index]);
    }
}
function dateRenderer() {
    for(let row = 1; row < copiedData.length; row++) {
        var columnCode = getColumnCode(row,0);
        if(originalData[row][0]["weekend"]) {
            myTable.setStyle(columnCode, "background-color", "#FF0");
        }
    }
}

function prepareShiftTypes() {
    shiftTypes = shiftTypes.concat(shiftTypeNamesLoad);
    shiftTypeColours = shiftTypeColours.concat(shiftTypeColoursLoad);
    //console.log("colours are added");
}



document.addEventListener("DOMContentLoaded", function(){
    displayData();
    firstRowRenderer();
    dateRenderer();
    prepareShiftTypes();
    for(let row=1; row < copiedData.length; row++) {
        for(let col=1; col <= Math.max(... Object.keys(copiedData[row])); col++) {
            shiftRenderer(row,col);
        }
    }

    afterInit = true;
});