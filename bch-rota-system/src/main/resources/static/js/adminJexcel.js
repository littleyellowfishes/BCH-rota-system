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

var changedCells = [];
var oldNotes = [];

var shiftTypeNamesLoad = JSON.parse(shiftTypeNames);
var shiftTypeColoursLoad = JSON.parse(shiftTypeColoursFromBack);
var shiftTypes = [null];
var shiftTypeColours = ["#FFF"];

var changed = function(instance, cell, y, x, value) {
    if(afterInit) {
        var cellName = jexcel.getColumnNameFromId([x,y]);
        changedCells.push({x,y,value});
        sendChanges();
        shiftRenderer(x,y);
    }
};


function buildJson() {
    var comments = myTable.getComments();
    for(let i=0; i < Object.keys(comments).length; i++) {
        var columnCode = Object.keys(comments)[i];
        if(oldNotes.includes(columnCode)) {
            continue;
        }
        var coordinates = jexcel.getIdFromColumnName(columnCode, true);
        var x = coordinates[1];
        var y = coordinates[0];
        var comment = JSON.parse(JSON.stringify(comments[columnCode]));
        changedCells.push({x,y,comment});
    }
    for(let i=0; i < Object.keys(oldNotes).length; i++) {
        var oldColumnCode = oldNotes[i];
        if(!Object.keys(comments).includes(oldColumnCode)) {
            var rmCoordinates = jexcel.getIdFromColumnName(oldColumnCode, true);
            var rmX = rmCoordinates[1];
            var rmY = rmCoordinates[0];
            var rmComment = null;
            changedCells.push({"x":rmX,"y":rmY,"comment":rmComment});
        }
    }

    var shiftJsons = [];
    for(let i=0; i < changedCells.length; i++) {

        var row = changedCells[i]["x"];
        var col = changedCells[i]["y"];
        var value = changedCells[i]["value"];
        var cmt = changedCells[i]["comment"];
        var shift = copiedData[row][col];

        if(shift === undefined) {
            shift = {"shift":{}};
        }

        if(shift["shift"]["id"] == null) {
            shift["shift"]["firstName"] = copiedData[0][col]["shift"]["shiftType"].substring(0,copiedData[0][col]["shift"]["shiftType"].length - 2);
            shift["shift"]["lastName"] = copiedData[0][col]["shift"]["lastName"];
            shift["shift"]["note"] = null;
        }
        if(value === undefined || value === null) {
            shift["shift"]["shiftType"] = myTable.getData()[row][col];
        }
        else {
            shift["shift"]["shiftType"] = value;
        }

        if(cmt !== undefined) {
            shift["shift"]["note"] = cmt;
        }

        shift["shift"]["date"] = copiedData[row][0];
        //console.log(shift);
        if(!shiftJsons.includes(shift)) {
            shiftJsons.push(shift);
        }
    }
    return shiftJsons;
}

function sendChanges() {
    var savingJsons = buildJson();
    //console.log(savingJsons);
    $.post({
        url: "/change",
        data: JSON.stringify(savingJsons),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        cache: false
    });
}

async function saveChanges() {
    sendChanges();
    await new Promise(r => setTimeout(r, 50));
    $.post({
        url: "/save",
        success: function (res) {
            changedCells = [];
            oldNotes = [];
            alert("Data saved");
            //Force web page to reload when user saves the changes
            location.reload();
        },

    });
}

var myTable = new jexcel(document.getElementById('rota'), {
    data:copiedData,
    columns: cols1,
    onchange: changed,
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
        if (y === 0 || x === 0) {
            cell.classList.add('readonly');
        }
        if(y !== 0 && copiedData[0][y]["shift"]["id"] === -1) {
            cell.classList.add('readonly');
        }
    },
    contextMenu: function(obj, x, y, e) {
        var items = [];

        if (y == null) {

        } else {

            if (x) {
                if (obj.options.allowComments === true) {
                    items.push({ type:'line' });

                    var title = obj.records[y][x].getAttribute('title') || '';

                    items.push({
                        title: title ? obj.options.text.editComments : obj.options.text.addComments,
                        onclick:function() {
                            obj.setComments([ x, y ], prompt(obj.options.text.comments, title));
                        }
                    });

                    if (title) {
                        items.push({
                            title:obj.options.text.clearComments,
                            onclick:function() {
                                obj.setComments([ x, y ], '');
                            }
                        });
                    }
                }
            }
        }

        // Line
        items.push({ type:'line' });


        return items;
    }
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

function displayComments() {
    for(let row=1; row < copiedData.length; row++) {
        for(let col=1; col <= Math.max(... Object.keys(copiedData[row])); col++) {
            if(copiedData[row][col] !== undefined && copiedData[row][col] !== null) {
                if(copiedData[row][col]["shift"]["note"] !== undefined && copiedData[row][col]["shift"]["note"] !== null) {
                    var columnCode = getColumnCode(row, col);
                    myTable.setComments(columnCode,copiedData[row][col]["shift"]["note"]);
                    oldNotes.push(columnCode);
                }
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
    displayComments();
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