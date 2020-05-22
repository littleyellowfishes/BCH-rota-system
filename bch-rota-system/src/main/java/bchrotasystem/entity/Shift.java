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

package bchrotasystem.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.Objects;



@Entity
public class Shift {

    @Id @GeneratedValue
    private Integer id;
    private LocalDate date;
    private Integer shiftTypeID;
    private Integer userID;
    private String note;

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public LocalDate getDate() { return date; }

    public void setDate(LocalDate date) { this.date = date; }

    public Integer getShiftTypeID() { return shiftTypeID; }

    public void setShiftTypeID(Integer shiftTypeID) { this.shiftTypeID = shiftTypeID; }

    public Integer getUserID() { return userID; }

    public void setUserID(Integer userID) { this.userID = userID; }


    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shift shift = (Shift) o;
        return date.equals(shift.date) &&
                Objects.equals(shiftTypeID, shift.shiftTypeID) &&
                userID.equals(shift.userID) &&
                Objects.equals(note, shift.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, shiftTypeID, userID, note);
    }

    public Shift() {
        this.date = LocalDate.now().plusDays(1);
        this.userID = null;
        this.shiftTypeID = null;
        this.note = null;
    }

    public Shift(LocalDate date, Integer userID, Integer shiftTypeID) {
        this.date = date;
        this.userID = userID;
        this.shiftTypeID = shiftTypeID;
        this.note = null;
    }
    public Shift(LocalDate date, Integer userID, Integer shiftTypeID, String note) {
        this.date = date;
        this.userID = userID;
        this.shiftTypeID = shiftTypeID;
        this.note = note;
    }

    public Shift(Integer id, LocalDate date, Integer userID, Integer shiftTypeID, String note) {
        this.id = id;
        this.date = date;
        this.userID = userID;
        this.shiftTypeID = shiftTypeID;
        this.note = note;
    }
}
