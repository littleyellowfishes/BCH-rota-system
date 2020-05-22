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

import java.security.SecureRandom;

public class PasswordGenerator {

    private final SecureRandom random = new SecureRandom();
    private static final String charSet = "!@#$%&*()_+-=[]?0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private String password;

    public PasswordGenerator() {
        generatePassword(8);
    }

    public PasswordGenerator(int length) {
        generatePassword(length);
    }

    private void generatePassword(int length) {
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charSet.length());
            char randomChar = charSet.charAt(randomIndex);
            stringBuilder.append(randomChar);

        }
        password = stringBuilder.toString();
    }

    public String getPassword() {
        return password;
    }
}
