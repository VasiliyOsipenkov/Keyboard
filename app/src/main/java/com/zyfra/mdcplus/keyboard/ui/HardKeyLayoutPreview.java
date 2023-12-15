package com.zyfra.mdcplus.keyboard.ui;

public class HardKeyLayoutPreview extends Activity {
    private static final String TAG = HardKeyLayoutPreview.class.getSimpleName();

    private KeyAdapter adapter;

    private KeyCharacterMap androidCharMap = KeyCharacterMap.load(0);

    private HashMap<Integer, String> keyNames = new HashMap<Integer, String>();

    private ArrayList<KeyData> keysList = new ArrayList<KeyData>();

    View.OnClickListener listener = new View.OnClickListener() {
        public void onClick(View param1View) {
            switch (param1View.getId()) {
                default:
                    return;
                case 2131165190:
                    HardKeyLayoutPreview.this.adapter.sort(HardKeyLayoutPreview.this.sKeySymbolComparator);
                    return;
                case 2131165191:
                    HardKeyLayoutPreview.this.adapter.sort(HardKeyLayoutPreview.this.sKeySymEngComparator);
                    return;
                case 2131165192:
                    break;
            }
            HardKeyLayoutPreview.this.adapter.sort(HardKeyLayoutPreview.this.sKeyComparator);
        }
    };

    private ListView lvLayout;

    private final Comparator<KeyData> sKeyComparator = new Comparator<KeyData>() {
        public final int compare(HardKeyLayoutPreview.KeyData param1KeyData1, HardKeyLayoutPreview.KeyData param1KeyData2) {
            return param1KeyData1.codeStr.compareTo(param1KeyData2.codeStr);
        }
    };

    private final Comparator<KeyData> sKeySymEngComparator = new Comparator<KeyData>() {
        public final int compare(HardKeyLayoutPreview.KeyData param1KeyData1, HardKeyLayoutPreview.KeyData param1KeyData2) {
            return param1KeyData1.symbolEng.compareTo(param1KeyData2.symbolEng);
        }
    };

    private final Comparator<KeyData> sKeySymbolComparator = new Comparator<KeyData>() {
        public final int compare(HardKeyLayoutPreview.KeyData param1KeyData1, HardKeyLayoutPreview.KeyData param1KeyData2) {
            return param1KeyData1.symbol.compareTo(param1KeyData2.symbol);
        }
    };

    private SharedPreferences settings;

    private void loadKeysNames() {
        this.keyNames.put(Integer.valueOf(84), "Search");
        this.keyNames.put(Integer.valueOf(19), "dpad up");
        this.keyNames.put(Integer.valueOf(20), "dpad down");
        this.keyNames.put(Integer.valueOf(21), "dpad left");
        this.keyNames.put(Integer.valueOf(22), "dpad right");
    }

    private void loadLayoutList(Key paramKey, String paramString1, String paramString2) {
        if (paramKey.map != null) {
            Iterator<Map.Entry> iterator = paramKey.map.entrySet().iterator();
            while (iterator.hasNext()) {
                String str2;
                Key key = (Key)((Map.Entry)iterator.next()).getValue();
                int i = key.code;
                String str3 = String.valueOf((char)this.androidCharMap.get(i, 0));
                if (paramString2.equals("")) {
                    str2 = String.valueOf(i);
                } else {
                    str2 = paramString2 + ", " + i;
                }
                String str1 = "";
                if (key.getCharDefault() != Key.EMPTY_CHAR) {
                    if (paramString1.equals("")) {
                        str1 = str3;
                    } else {
                        str1 = paramString1 + ", " + str3;
                    }
                    this.keysList.add(new KeyData(str2, String.valueOf(key.getCharDefault()), str1));
                }
                if (key.getCharAltDefault() != Key.EMPTY_CHAR)
                    this.keysList.add(new KeyData(str2, String.valueOf(key.getCharAltDefault()), paramString1 + "ALT + " + str3));
                if (key.getCharShiftDefault() != Key.EMPTY_CHAR)
                    this.keysList.add(new KeyData(str2, String.valueOf(key.getCharShiftDefault()), paramString1 + "SHIFT + " + str3));
                loadLayoutList(key, str1, str2);
            }
        }
    }

    protected void onCreate(Bundle paramBundle) {
        KeyboardLayout keyboardLayout;
        super.onCreate(paramBundle);
        setContentView(2130903041);
        this.settings = PreferenceManager.getDefaultSharedPreferences((Context)this);
        String str = this.settings.getString("pref_hard_layout", getString(2131427328));
        paramBundle = null;
        try {
            KeyboardLayout keyboardLayout1 = (new KeyLayoutLoader((Context)this)).load(str);
            keyboardLayout = keyboardLayout1;
            if (keyboardLayout == null) {
                finish();
                return;
            }
        } catch (XmlPullParserException xmlPullParserException) {
            Log.e(TAG, "Ill-formatted xml file");
            if (keyboardLayout == null) {
                finish();
                return;
            }
        }
        if (keyboardLayout.langToggleKey != -1)
            this.keysList.add(new KeyData(String.valueOf(keyboardLayout.langToggleKey), "LangToggle", ""));
        loadKeysNames();
        loadLayoutList(keyboardLayout.keysMap, "", "");
        this.adapter = new KeyAdapter((Context)this, this.keysList);
        this.adapter.sort(this.sKeyComparator);
        this.lvLayout = (ListView)findViewById(2131165193);
        this.lvLayout.setAdapter((ListAdapter)this.adapter);
        findViewById(2131165190).setOnClickListener(this.listener);
        findViewById(2131165191).setOnClickListener(this.listener);
        findViewById(2131165192).setOnClickListener(this.listener);
    }

    private class KeyAdapter extends BaseAdapter {
        protected final LayoutInflater mInflater;

        protected List<HardKeyLayoutPreview.KeyData> mList;

        public KeyAdapter(Context param1Context, List<HardKeyLayoutPreview.KeyData> param1List) {
            this.mInflater = (LayoutInflater)param1Context.getSystemService("layout_inflater");
            this.mList = param1List;
        }

        public int getCount() {
            return (this.mList != null) ? this.mList.size() : 0;
        }

        public HardKeyLayoutPreview.KeyData getItem(int param1Int) {
            return (this.mList != null) ? this.mList.get(param1Int) : null;
        }

        public long getItemId(int param1Int) {
            return param1Int;
        }

        public View getView(int param1Int, View param1View, ViewGroup param1ViewGroup) {
            View view = param1View;
            if (param1View == null) {
                view = this.mInflater.inflate(2130903047, null);
                HardKeyLayoutPreview.ViewHolder viewHolder1 = new HardKeyLayoutPreview.ViewHolder();
                viewHolder1.symbol = (TextView)view.findViewById(2131165211);
                viewHolder1.symbolEng = (TextView)view.findViewById(2131165212);
                viewHolder1.code = (TextView)view.findViewById(2131165213);
                view.setTag(viewHolder1);
            }
            HardKeyLayoutPreview.ViewHolder viewHolder = (HardKeyLayoutPreview.ViewHolder)view.getTag();
            HardKeyLayoutPreview.KeyData keyData = this.mList.get(param1Int);
            viewHolder.symbol.setText(keyData.symbol);
            viewHolder.symbolEng.setText(keyData.symbolEng);
            viewHolder.code.setText(keyData.codeStr);
            return view;
        }

        public void sort(Comparator<HardKeyLayoutPreview.KeyData> param1Comparator) {
            Collections.sort(this.mList, param1Comparator);
            notifyDataSetChanged();
        }
    }

    class KeyData {
        String codeStr;

        String symbol;

        String symbolEng;

        public KeyData(String param1String1, String param1String2, String param1String3) {
            this.symbol = param1String2;
            this.symbolEng = param1String3;
            this.codeStr = param1String1;
        }
    }

    private class ViewHolder {
        TextView code;

        TextView symbol;

        TextView symbolEng;

        private ViewHolder() {}
    }
}
