package com.zyfra.mdcplus.keyboard;

import android.content.Context;
import android.util.Log;

import com.zyfra.mdcplus.keyboard.model.Key;
import com.zyfra.mdcplus.keyboard.model.KeyboardLayout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class KeyLayoutLoader {
    public static final String ATTR_ALT_CHAR = "altChar";

    public static final String ATTR_AUTHOR_EMAIL = "authorEmail";

    public static final String ATTR_AUTHOR_NAME = "authorName";

    public static final String ATTR_AUTHOR_WEBSITE = "authorWebSite";

    public static final String ATTR_CHAR = "char";

    public static final String ATTR_CODE = "code";

    public static final String ATTR_FLAG_ICON = "flagIcon";

    public static final String ATTR_IS_GLOBAL_KEY = "isGlobalKey";

    public static final String ATTR_IS_LANG_TOGGLE = "isLangToggle";

    public static final String ATTR_IS_ON_KEY_DOWN = "isOnKeyDown";

    public static final String ATTR_IS_ON_KEY_UP = "isOnKeyUp";

    public static final String ATTR_LAYOUT_DESC = "layoutDescription";

    public static final String ATTR_LAYOUT_NAME = "layoutName";

    public static final String ATTR_PARENT_ID = "parentId";

    public static final String ATTR_SHIFT_CHAR = "shiftChar";

    public static final String ATTR_SYSTEM = "system";

    public static final String ATTR_VERSION_CODE = "versionCode";

    public static final String ATTR_VERSION_NAME = "versionName";

    private static boolean DEBUG = false;

    public static final String NAMESPACE;

    private static final String TAG = KeyLayoutLoader.class.getSimpleName();

    public static final String XMLTAG_ALT_CHAR = "altChar";

    public static final String XMLTAG_KEY = "key";

    public static final String XMLTAG_KEYLAYOUT = "keyboardLayout";

    public static final String XMLTAG_KEYS = "keys";

    public static final String XMLTAG_SHIFT_CHAR = "shiftChar";

    private KeyboardLayout keyboardLayout;

    private Key mCharMap;

    private Context mContext;

    static {
        NAMESPACE = null;
    }

    public KeyLayoutLoader(Context paramContext) {
        this.mContext = paramContext;
        this.keyboardLayout = new KeyboardLayout();
        this.mCharMap = this.keyboardLayout.keysMap;
    }

    private int digit(char paramChar) {
        if (paramChar >= '0' && paramChar <= '9')
            return paramChar - 48;
        if (paramChar >= 'A' && paramChar <= 'F')
            return paramChar - 65 + 10;
        if (paramChar >= 'a' && paramChar <= 'f')
            return paramChar - 97 + 10;
        throw new RuntimeException("not a hex digit = " + paramChar);
    }

    private char getCharacterFromString(String paramString) {
        int i = paramString.length();
        return (i == 1) ? paramString.charAt(0) : ((i == 6 && paramString.charAt(0) == '\\') ? (char)(digit(paramString.charAt(2)) << 12 | digit(paramString.charAt(3)) << 8 | digit(paramString.charAt(4)) << 4 | digit(paramString.charAt(5))) : Key.EMPTY_CHAR);
    }

    private char makeUnicode(String paramString) {
        return (paramString.length() != 4) ? Key.EMPTY_CHAR : (char)(digit(paramString.charAt(0)) << 12 | digit(paramString.charAt(1)) << 8 | digit(paramString.charAt(2)) << 4 | digit(paramString.charAt(3)));
    }

    private Key parseKey(XmlPullParser paramXmlPullParser, Key paramKey) throws IOException, XmlPullParserException {
        int i = -1;
        char c1 = Key.EMPTY_CHAR;
        char c2 = Key.EMPTY_CHAR;
        char c4 = Key.EMPTY_CHAR;
        char c3 = Key.EMPTY_CHAR;
        boolean bool = false;
        int m = paramXmlPullParser.getAttributeCount();
        int j = 0;
        while (j < m) {
            String str1 = paramXmlPullParser.getAttributeName(j);
            String str2 = paramXmlPullParser.getAttributeValue(j);
            char c5 = c4;
            int n = i;
            char c6 = c1;
            boolean bool1 = bool;
            char c7 = c3;
            char c8 = c2;
            if (str1 != null)
                if (str2 == null) {
                    c8 = c2;
                    c7 = c3;
                    bool1 = bool;
                    c6 = c1;
                    n = i;
                    c5 = c4;
                } else if (str1.equals("code")) {
                    n = Integer.parseInt(str2);
                    c5 = c4;
                    c6 = c1;
                    bool1 = bool;
                    c7 = c3;
                    c8 = c2;
                } else if (str1.equals("char")) {
                    c6 = getCharacterFromString(str2);
                    c5 = c4;
                    n = i;
                    bool1 = bool;
                    c7 = c3;
                    c8 = c2;
                } else if (str1.equals("system")) {
                    c8 = getCharacterFromString(str2);
                    c5 = c4;
                    n = i;
                    c6 = c1;
                    bool1 = bool;
                    c7 = c3;
                } else if (str1.equals("altChar")) {
                    c5 = getCharacterFromString(str2);
                    n = i;
                    c6 = c1;
                    bool1 = bool;
                    c7 = c3;
                    c8 = c2;
                } else if (str1.equals("shiftChar")) {
                    c7 = getCharacterFromString(str2);
                    c5 = c4;
                    n = i;
                    c6 = c1;
                    bool1 = bool;
                    c8 = c2;
                } else {
                    c5 = c4;
                    n = i;
                    c6 = c1;
                    bool1 = bool;
                    c7 = c3;
                    c8 = c2;
                    if (str1.equals("isLangToggle")) {
                        bool1 = Boolean.parseBoolean(str2);
                        c5 = c4;
                        n = i;
                        c6 = c1;
                        c7 = c3;
                        c8 = c2;
                    }
                }
            j++;
            c4 = c5;
            i = n;
            c1 = c6;
            bool = bool1;
            c3 = c7;
            c2 = c8;
        }
        char c = c1;
        if (c1 == Key.EMPTY_CHAR) {
            c = c1;
            if (paramXmlPullParser.next() == 4) {
                c1 = getCharacterFromString(paramXmlPullParser.getText());
                c = c1;
                if (c1 == '\n')
                    c = Key.EMPTY_CHAR;
            }
        }
        int k = i;
        if (bool) {
            j = i;
            if (i == -1)
                try {
                    j = Integer.valueOf(paramXmlPullParser.getText()).intValue();
                } catch (NumberFormatException numberFormatException) {
                    Log.e(TAG, numberFormatException.getMessage());
                    j = i;
                }
            this.keyboardLayout.langToggleKey = j;
            k = j;
            if (DEBUG) {
                Log.d(TAG, "code: " + j + ", isLangToggle: " + bool);
                k = j;
            }
        }
        if (DEBUG) {
            StringBuilder stringBuilder = new StringBuilder(50);
            stringBuilder.append("code: ").append(k);
            if (c != Key.EMPTY_CHAR)
                stringBuilder.append(", char: ").append(c);
            if (c4 != Key.EMPTY_CHAR)
                stringBuilder.append(", altChar: ").append(c4);
            if (c3 != Key.EMPTY_CHAR)
                stringBuilder.append(", shiftChar: ").append(c3);
            Log.d(TAG, stringBuilder.toString());
        }
        Key key = paramKey.getOrCreateChildByKeyCode(k);
        key.setChar(c, c2);
        key.setAltChar(c4, Key.EMPTY_CHAR);
        key.setShiftChar(c3, Key.EMPTY_CHAR);
        return key;
    }

    private void parseKeyAlt(XmlPullParser paramXmlPullParser, Key paramKey) {
        char c2 = Key.EMPTY_CHAR;
        char c1 = Key.EMPTY_CHAR;
        int j = paramXmlPullParser.getAttributeCount();
        int i = 0;
        while (i < j) {
            String str1 = paramXmlPullParser.getAttributeName(i);
            String str2 = paramXmlPullParser.getAttributeValue(i);
            char c3 = c2;
            char c4 = c1;
            if (str1 != null)
                if (str2 == null) {
                    c4 = c1;
                    c3 = c2;
                } else if (str1.equals("char")) {
                    c3 = str2.charAt(0);
                    c4 = c1;
                } else {
                    c3 = c2;
                    c4 = c1;
                    if (str1.equals("system")) {
                        c4 = str2.charAt(0);
                        c3 = c2;
                    }
                }
            i++;
            c2 = c3;
            c1 = c4;
        }
        paramKey.setAltChar(c2, c1);
    }

    private void parseKeyShift(XmlPullParser paramXmlPullParser, Key paramKey) {
        char c2 = Key.EMPTY_CHAR;
        char c1 = Key.EMPTY_CHAR;
        int j = paramXmlPullParser.getAttributeCount();
        int i = 0;
        while (i < j) {
            String str1 = paramXmlPullParser.getAttributeName(i);
            String str2 = paramXmlPullParser.getAttributeValue(i);
            char c3 = c2;
            char c4 = c1;
            if (str1 != null)
                if (str2 == null) {
                    c4 = c1;
                    c3 = c2;
                } else if (str1.equals("char")) {
                    c3 = str2.charAt(0);
                    c4 = c1;
                } else {
                    c3 = c2;
                    c4 = c1;
                    if (str1.equals("system")) {
                        c4 = str2.charAt(0);
                        c3 = c2;
                    }
                }
            i++;
            c2 = c3;
            c1 = c4;
        }
        paramKey.setShiftChar(c2, c1);
    }

    public KeyboardLayout load(int paramInt) throws IOException, XmlPullParserException {
        return load(this.mContext.getResources().openRawResource(paramInt));
    }

    public KeyboardLayout load(InputStream paramInputStream) throws XmlPullParserException, IOException {
        XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
        xmlPullParserFactory.setNamespaceAware(true);
        XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
        xmlPullParser.setInput(new InputStreamReader(paramInputStream));
        int i = xmlPullParser.getEventType();
        boolean bool = false;
        xmlPullParserFactory = null;
        label43: while (i != 1) {
            String str2;
            if (i == 2) {
                String str = xmlPullParser.getName();
                if (str.equals("key")) {
                    if (bool) {
                        Key key1 = parseKey(xmlPullParser, (Key)xmlPullParserFactory);
                        boolean bool4 = bool;
                        continue;
                    }
                    Key key = parseKey(xmlPullParser, this.mCharMap);
                    boolean bool3 = bool;
                    continue;
                }
                if (str.equals("altChar")) {
                    parseKeyAlt(xmlPullParser, (Key)xmlPullParserFactory);
                    boolean bool3 = bool;
                    XmlPullParserFactory xmlPullParserFactory2 = xmlPullParserFactory;
                    continue;
                }
                if (str.equals("shiftChar")) {
                    parseKeyShift(xmlPullParser, (Key)xmlPullParserFactory);
                    boolean bool3 = bool;
                    XmlPullParserFactory xmlPullParserFactory2 = xmlPullParserFactory;
                    continue;
                }
                if (str.equals("keys")) {
                    boolean bool3 = true;
                    Key key = this.mCharMap;
                    continue;
                }
                boolean bool2 = bool;
                XmlPullParserFactory xmlPullParserFactory1 = xmlPullParserFactory;
                if (str.equals("keyboardLayout")) {
                    int j = xmlPullParser.getAttributeCount();
                    i = 0;
                    while (true) {
                        String str3;
                        bool2 = bool;
                        xmlPullParserFactory1 = xmlPullParserFactory;
                        if (i < j) {
                            str3 = xmlPullParser.getAttributeName(i);
                            str = xmlPullParser.getAttributeValue(i);
                            if (str3 != null && str != null && str3.equals("flagIcon"))
                                this.keyboardLayout.flag = str;
                            i++;
                            continue;
                        }
                        xmlPullParser.next();
                        i = xmlPullParser.getEventType();
                        bool = bool2;
                        str2 = str3;
                        continue label43;
                    }
                }
                continue;
            }
            boolean bool1 = bool;
            String str1 = str2;
            if (i == 3) {
                bool1 = bool;
                str1 = str2;
                if (xmlPullParser.getName().equals("keys")) {
                    bool1 = false;
                    str1 = null;
                    continue;
                }
                continue;
            }
            continue;
        }
        this.keyboardLayout.updateCounts();
        return this.keyboardLayout;
    }

    public KeyboardLayout load(String paramString) throws XmlPullParserException {
        InputStream inputStream4 = null;
        InputStream inputStream5 = null;
        FileInputStream fileInputStream = null;
        IOException iOException = null;
        InputStream inputStream2 = fileInputStream;
        InputStream inputStream3 = inputStream4;
        InputStream inputStream1 = inputStream5;
        try {
            InputStream inputStream;
            if (paramString.startsWith("/")) {
                inputStream2 = fileInputStream;
                inputStream3 = inputStream4;
                inputStream1 = inputStream5;
                fileInputStream = new FileInputStream(paramString);
            } else {
                inputStream2 = fileInputStream;
                inputStream3 = inputStream4;
                inputStream1 = inputStream5;
                inputStream = this.mContext.getAssets().open("hard/" + paramString);
            }
            inputStream2 = inputStream;
            inputStream3 = inputStream;
            inputStream1 = inputStream;
            KeyboardLayout keyboardLayout3 = load(inputStream);
            KeyboardLayout keyboardLayout2 = keyboardLayout3;
            KeyboardLayout keyboardLayout1 = keyboardLayout2;
            return (KeyboardLayout)null;
        } catch (FileNotFoundException fileNotFoundException) {
            inputStream1 = inputStream2;
            Log.e(TAG, "Unable to find xml file: " + null);
            null = iOException;
            return (KeyboardLayout)null;
        } catch (IOException iOException2) {
            inputStream1 = inputStream3;
            Log.e(TAG, "Unable to read xml file: " + null);
            null = iOException;
            return (KeyboardLayout)iOException1;
        } finally {
            if (inputStream1 != null)
                try {
                    inputStream1.close();
                } catch (IOException iOException1) {
                    iOException1.printStackTrace();
                }
        }
    }
}
