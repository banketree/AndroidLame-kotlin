/* LameUtils.java
   Copyright (c) 2011 Ethan Chen
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2 of the License, or (at your option) any later version.
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.naman14.androidlame;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

class LameUtils {
    public static short byteToShortLE(byte b1, byte b2) {
        return (short) (b1 & 0xFF | ((b2 & 0xFF) << 8));
    }

    public static int readUnsignedInt(BufferedInputStream in) throws IOException {
        int ret;
        byte[] buf = new byte[4];
        ret = in.read(buf);
        if (ret == -1) {
            return -1;
        } else {
            return (((buf[0] & 0xFF) << 24)
                    | ((buf[1] & 0xFF) << 16)
                    | ((buf[2] & 0xFF) << 8)
                    | (buf[3] & 0xFF));
        }
    }

    public static int readUnsignedIntLE(BufferedInputStream in) throws IOException {
        int ret;
        byte[] buf = new byte[4];
        ret = in.read(buf);
        if (ret == -1) {
            return -1;
        } else {
            return (buf[0] & 0xFF
                    | ((buf[1] & 0xFF) << 8)
                    | ((buf[2] & 0xFF) << 16)
                    | ((buf[3] & 0xFF) << 24));
        }
    }

    public static short readUnsignedShortLE(BufferedInputStream in) throws IOException {
        int ret;
        byte[] buf = new byte[2];
        ret = in.read(buf, 0, 2);
        if (ret == -1) {
            return -1;
        } else {
            return byteToShortLE(buf[0], buf[1]);
        }
    }
}