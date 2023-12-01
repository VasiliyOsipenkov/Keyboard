package com.zyfra.mdcplus.keyboard.model;

import java.util.HashMap;

public class Key {
    public static char EMPTY_CHAR = Character.MIN_VALUE;

    public static final int TYPE_ALT = 1;

    public static final int TYPE_ALT_SHIFT = 3;

    private static final int TYPE_COUNT = 4;

    public static final int TYPE_REGULAR = 0;

    public static final int TYPE_SHIFT = 2;

    public int code;

    public KeyBase[] keys = new KeyBase[4];

    public HashMap<Integer, Key> map;

    public Key(int paramInt) {
        this.code = paramInt;
    }

    public static Key createEmptyKey() {
        Key key = new Key(0);
        key.map = new HashMap<Integer, Key>();
        return key;
    }

    private char getCharByType(int paramInt, boolean paramBoolean) {
        KeyBase keyBase = this.keys[paramInt];
        return (keyBase == null) ? EMPTY_CHAR : (paramBoolean ? keyBase.defaultChar : keyBase.systemChar);
    }

    private void setCharByType(int paramInt, char paramChar1, char paramChar2) {
        KeyBase keyBase2 = this.keys[paramInt];
        KeyBase keyBase1 = keyBase2;
        if (keyBase2 == null) {
            keyBase1 = new KeyBase();
            this.keys[paramInt] = keyBase1;
        }
        keyBase1.defaultChar = paramChar1;
        keyBase1.systemChar = paramChar2;
    }

    public Key addToMap(Integer paramInteger, Key paramKey) {
        if (this.map == null)
            this.map = new HashMap<Integer, Key>();
        this.map.put(paramInteger, paramKey);
        return this.map.get(paramInteger);
    }

    public char getCharAlt(boolean paramBoolean) {
        return getCharByType(1, paramBoolean);
    }

    public char getCharAltDefault() {
        return getCharByType(1, true);
    }

    public char getCharAltSystem() {
        return getCharByType(1, false);
    }

    public char getCharDefault() {
        return getCharByType(0, true);
    }

    public char getCharDefault(boolean paramBoolean) {
        return getCharByType(0, paramBoolean);
    }

    public char getCharShift(boolean paramBoolean) {
        return getCharByType(2, paramBoolean);
    }

    public char getCharShiftDefault() {
        return getCharByType(2, true);
    }

    public char getCharShiftSystem() {
        return getCharByType(2, false);
    }

    public char getCharSystem() {
        return getCharByType(0, false);
    }

    public Key getOrCreateChildByKeyCode(int paramInt) {
        return (this.map == null || this.map.get(Integer.valueOf(paramInt)) == null) ? addToMap(Integer.valueOf(paramInt), new Key(paramInt)) : this.map.get(Integer.valueOf(paramInt));
    }

    public void setAltChar(char paramChar1, char paramChar2) {
        setCharByType(1, paramChar1, paramChar2);
    }

    public void setChar(char paramChar1, char paramChar2) {
        setCharByType(0, paramChar1, paramChar2);
    }

    public void setShiftChar(char paramChar1, char paramChar2) {
        setCharByType(2, paramChar1, paramChar2);
    }
}
