/*
 *
 *      Copyright (C) 2023 Joerg Bayer (SG-O)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.sg_o.lib.tagy.util;

import java.text.SimpleDateFormat;
import java.util.*;

public class Util {

    /**
     * Sanitizes the given string by truncating it to the specified maximum number of characters
     * and removing all special characters except for the allowed ones. Whether special characters
     * are allowed in the beginning or end of the string is determined by the allowSpecialStart and
     * allowSpecialEnd parameters.
     * If the input is null, the method returns null.
     *
     * @param raw the string to sanitize
     * @param allowedSpecialChars the allowed special characters (e.g. ' ', '-', '_')
     * @param allowSpecialStart whether special characters are allowed at the beginning of the string
     * @param allowSpecialEnd whether special characters are allowed at the end of the string
     * @param maxLength the maximum number of characters allowed in the string (truncated before removal of special characters)
     * @return the sanitized string, or null if the input was null
     */
    @SuppressWarnings("unused")
    public static String sanitize(String raw, char[] allowedSpecialChars, boolean allowSpecialStart, boolean allowSpecialEnd, int maxLength) {
        if (raw == null) return null;
        if (allowedSpecialChars == null) allowedSpecialChars = new char[0];
        raw = raw.trim();
        if (raw.length() > maxLength) raw = raw.substring(0, maxLength + 1);
        if (raw.isEmpty()) return "";
        StringBuilder builder = new StringBuilder(raw.length());
        HashMap<Character, Character> catalog = new HashMap<>();
        for (char c : allowedSpecialChars) {
            catalog.put(c, c);
        }
        for (char c : raw.toCharArray()) {
            if (catalog.containsKey(c)) {
                builder.append(c);
            }
            if ((c >= 0x30) && (c <= 0x39)) {
                builder.append(c);
            }
            if (((c >= 0x41) && (c <= 0x5A)) || ((c >= 0x61) && (c <= 0x7A))) {
                builder.append(c);
            }
        }
        raw = builder.toString();
        if (!allowSpecialStart) {
            while (catalog.containsKey(raw.charAt(0))) {
                raw = raw.substring(1);
            }
        }
        if (!allowSpecialEnd) {
            while (catalog.containsKey(raw.charAt(raw.length() - 1))) {
                raw = raw.substring(0, raw.length() - 1);
            }
        }
        return raw.trim();
    }

    @SuppressWarnings("unused")
    public static boolean byteArrayListEquals(List<byte[]> byteArrayList0, List<byte[]> byteArrayList1) {
        if (byteArrayList0 == byteArrayList1) return true;
        if ((byteArrayList0 == null) || (byteArrayList1 == null)) {
            if (byteArrayList0 == null) {
                return byteArrayList1.isEmpty();
            } else {
                return byteArrayList0.isEmpty();
            }
        }
        if (byteArrayList0.size() != byteArrayList1.size()) return false;
        for (int i = 0; i < byteArrayList0.size(); i++) {
            if (!Arrays.equals(byteArrayList0.get(i), byteArrayList1.get(i))) return false;
        }
        return true;
    }
    @SuppressWarnings("unused")
    public static int byteArrayListHash(List<byte[]> byteArrayList0) {
        int hash = 0;
        if (byteArrayList0 == null) return 0;
        if (byteArrayList0.isEmpty()) return 0;
        for (byte[] bytes : byteArrayList0) {
            hash += Arrays.hashCode(bytes);
        }
        return hash;
    }

    @SuppressWarnings("unused")
    public static boolean betterListEquals(List<?> arrayList0, List<?> arrayList1) {
        if (arrayList0 == arrayList1) return true;
        if ((arrayList0 == null) || (arrayList1 == null)) {
            if (arrayList0 == null) {
                return arrayList1.isEmpty();
            } else {
                return arrayList0.isEmpty();
            }
        }
        if (arrayList0.size() != arrayList1.size()) return false;
        for (int i = 0; i < arrayList0.size(); i++) {
            if (!Objects.equals(arrayList0.get(i), arrayList1.get(i))) return false;
        }
        return true;
    }
    @SuppressWarnings("unused")
    public static int betterListHash(List<?> arrayList0) {
        int hash = 0;
        if (arrayList0 == null) return 0;
        if (arrayList0.isEmpty()) return 0;
        for (Object o : arrayList0) {
            hash += Objects.hash(o);
        }
        return hash;
    }

    public static String formatDateToString(Date date) {
        if (date == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }
}