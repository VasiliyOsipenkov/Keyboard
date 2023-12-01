package com.zyfra.mdcplus.keyboard.model;

import java.util.HashMap;

public class CharMap {
    private static char EMPTY_CHAR = Character.MIN_VALUE;

    public char altchr = EMPTY_CHAR;

    public char chr = EMPTY_CHAR;

    public int code = 0;

    public HashMap<Integer, CharMap> map;

    public char shiftchr = EMPTY_CHAR;

    public CharMap(int paramInt) {
        this.code = paramInt;
    }

    public CharMap(int paramInt, char paramChar, HashMap<Integer, CharMap> paramHashMap) {
        this.code = paramInt;
        this.chr = paramChar;
        this.map = paramHashMap;
    }

    public CharMap(int paramInt, char paramChar1, HashMap<Integer, CharMap> paramHashMap, char paramChar2) {
        this.code = paramInt;
        this.chr = paramChar1;
        this.map = paramHashMap;
        this.altchr = paramChar2;
    }

    public CharMap(int paramInt, char paramChar1, HashMap<Integer, CharMap> paramHashMap, char paramChar2, char paramChar3) {
        this.code = paramInt;
        this.chr = paramChar1;
        this.map = paramHashMap;
        this.altchr = paramChar2;
        this.shiftchr = paramChar3;
    }

    public CharMap addToMap(Integer paramInteger, CharMap paramCharMap) {
        if (this.map == null)
            this.map = new HashMap<Integer, CharMap>();
        this.map.put(paramInteger, paramCharMap);
        return this.map.get(paramInteger);
    }

    public CharMap getFromMap(Integer paramInteger) {
        return (this.map != null) ? this.map.get(paramInteger) : null;
    }

    public String toString() {
        StringBuilder stringBuilder = (new StringBuilder()).append("code: ").append(this.code).append(", char: ").append(this.chr);
        if (this.altchr != EMPTY_CHAR) {
            String str1 = ", altchr: " + this.altchr;
            return stringBuilder.append(str1).toString();
        }
        String str = "";
        return stringBuilder.append(str).toString();
    }
}
