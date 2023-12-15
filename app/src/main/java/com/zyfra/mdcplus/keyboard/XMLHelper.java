package com.zyfra.mdcplus.keyboard;

import android.content.Context;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import com.zyfra.mdcplus.keyboard.model.KeyLayoutInfo;

public class XMLHelper {
    private static final String TAG = XMLHelper.class.getSimpleName();

    private static InputStream checkForUtf8BOM(InputStream paramInputStream) throws IOException {
        paramInputStream = new PushbackInputStream(new BufferedInputStream(paramInputStream), 3);
        byte[] arrayOfByte = new byte[3];
        if (paramInputStream.read(arrayOfByte) != -1 && (arrayOfByte[0] != -17 || arrayOfByte[1] != -69 || arrayOfByte[2] != -65))
            paramInputStream.unread(arrayOfByte);
        return paramInputStream;
    }

    public static KeyLayoutInfo getLayoutInfo(Context paramContext, String paramString) {
        InputStream inputStream5 = null;
        InputStream inputStream6 = null;
        InputStream inputStream4 = null;
        String str = null;
        InputStream inputStream2 = inputStream4;
        InputStream inputStream3 = inputStream5;
        InputStream inputStream1 = inputStream6;
        try {
            InputStream inputStream;
            if (paramString.startsWith("/")) {
                inputStream2 = inputStream4;
                inputStream3 = inputStream5;
                inputStream1 = inputStream6;
                inputStream = new FileInputStream(paramString);
            } else {
                inputStream2 = inputStream4;
                inputStream3 = inputStream5;
                inputStream1 = inputStream6;
                inputStream = inputStream.getAssets().open("hard/" + paramString);
            }
            inputStream2 = inputStream;
            inputStream3 = inputStream;
            inputStream1 = inputStream;
            KeyLayoutInfo keyLayoutInfo3 = getLayoutInfo(paramString, inputStream);
            KeyLayoutInfo keyLayoutInfo2 = keyLayoutInfo3;
            KeyLayoutInfo keyLayoutInfo1 = keyLayoutInfo2;
            return keyLayoutInfo1;
        } catch (FileNotFoundException fileNotFoundException) {
            inputStream1 = inputStream2;
            Log.e(TAG, "Unable to find xml file: " + paramString);
            paramString = str;
            return (KeyLayoutInfo)paramString;
        } catch (IOException iOException) {
            inputStream1 = inputStream3;
            Log.e(TAG, "Unable to read xml file: " + paramString);
            paramString = str;
            return (KeyLayoutInfo)paramString;
        } finally {
            if (inputStream1 != null)
                try {
                    inputStream1.close();
                } catch (IOException iOException) {
                    iOException.printStackTrace();
                }
        }
    }

    public static KeyLayoutInfo getLayoutInfo(String paramString, InputStream paramInputStream) {
        KeyLayoutInfo keyLayoutInfo = new KeyLayoutInfo();
        keyLayoutInfo.layoutID = paramString;
        try {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            xmlPullParserFactory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
            xmlPullParser.setInput(new InputStreamReader(checkForUtf8BOM(paramInputStream)));
            int i = xmlPullParser.getEventType();
            label76: while (true) {
                if (i != 1) {
                    String str = xmlPullParser.getName();
                    if (i == 2) {
                        if (str.equals("keyboardLayout")) {
                            int j = xmlPullParser.getAttributeCount();
                            for (i = 0;; i++) {
                                if (i < j) {
                                    str = xmlPullParser.getAttributeName(i);
                                    String str1 = xmlPullParser.getAttributeValue(i);
                                    if (str != null && str1 != null)
                                        if (str.equals("layoutName")) {
                                            keyLayoutInfo.layoutName = str1;
                                        } else if (str.equals("layoutDescription")) {
                                            keyLayoutInfo.layoutDescription = str1;
                                        } else if (str.equals("authorName")) {
                                            keyLayoutInfo.authorName = str1;
                                        } else if (str.equals("authorEmail")) {
                                            keyLayoutInfo.authorEmail = str1;
                                        } else if (str.equals("authorWebSite")) {
                                            keyLayoutInfo.authorWebSite = str1;
                                        } else if (str.equals("versionName")) {
                                            keyLayoutInfo.versionName = str1;
                                        } else if (str.equals("versionCode")) {
                                            keyLayoutInfo.versionCode = Integer.parseInt(str1);
                                        } else if (str.equals("flagIcon")) {
                                            keyLayoutInfo.flag = str1;
                                        }
                                } else {
                                    xmlPullParser.next();
                                    i = xmlPullParser.getEventType();
                                    continue label76;
                                }
                            }
                            break;
                        }
                        continue;
                    }
                    if (i == 3) {
                        boolean bool = str.equals("keyboardLayout");
                        if (bool)
                            return keyLayoutInfo;
                        continue;
                    }
                    continue;
                }
                return keyLayoutInfo;
            }
        } catch (XmlPullParserException xmlPullParserException) {
            Log.e(TAG, "Ill-formatted xml file");
            return keyLayoutInfo;
        } catch (IOException iOException) {
            Log.e(TAG, "Unable to read xml file");
            return keyLayoutInfo;
        } finally {
            if (paramInputStream != null)
                try {
                    paramInputStream.close();
                } catch (IOException iOException) {
                    iOException.printStackTrace();
                }
        }
    }
}

