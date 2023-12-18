package com.zyfra.mdcplus.keyboard.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.zyfra.mdcplus.keyboard.Api5;

public class KeyboardView extends View implements View.OnClickListener {
    private static final boolean DEBUG = false;

    private static final int DELAY_AFTER_PREVIEW = 70;

    private static final int DELAY_BEFORE_PREVIEW = 0;

    private static final int[] KEY_DELETE = new int[] { -5 };

    private static final int LONGPRESS_TIMEOUT = 800;

    private static final int[] LONG_PRESSABLE_STATE_SET = new int[] { 2130772000 };

    private static int MAX_NEARBY_KEYS = 12;

    private static final int MSG_LONGPRESS = 4;

    private static final int MSG_REMOVE_PREVIEW = 2;

    private static final int MSG_REPEAT = 3;

    private static final int MSG_SHOW_PREVIEW = 1;

    private static final int MULTITAP_INTERVAL = 800;

    private static final int NOT_A_KEY = -1;

    private static final int REPEAT_INTERVAL = 50;

    private static final int REPEAT_START_DELAY = 400;

    private boolean mAbortKey;

    private float mBackgroundDimAmount;

    private Bitmap mBuffer;

    private Canvas mCanvas;

    private Rect mClipRegion = new Rect(0, 0, 0, 0);

    private Context mContext;

    private int mCurrentKey = -1;

    private int mCurrentKeyIndex = -1;

    private long mCurrentKeyTime;

    private Rect mDirtyRect = new Rect();

    private boolean mDisambiguateSwipe;

    private int[] mDistances = new int[MAX_NEARBY_KEYS];

    private int mDownKey = -1;

    private long mDownTime;

    private boolean mDrawPending;

    private GestureDetector mGestureDetector;

    Handler mHandler = new Handler() {
        public void handleMessage(Message param1Message) {
            switch (param1Message.what) {
                default:
                    return;
                case 1:
                    KeyboardView.this.showKey(param1Message.arg1);
                    return;
                case 2:
                    KeyboardView.this.mPreviewText.setVisibility(4);
                    return;
                case 3:
                    if (KeyboardView.this.repeatKey()) {
                        sendMessageDelayed(Message.obtain(this, 3), 50L);
                        return;
                    }
                case 4:
                    break;
            }
            KeyboardView.this.openPopupIfRequired((MotionEvent)param1Message.obj);
        }
    };

    private boolean mInMultiTap;

    private Keyboard.Key mInvalidatedKey;

    private Drawable mKeyBackground;

    private int[] mKeyIndices = new int[12];

    private int mKeyTextColor;

    private int mKeyTextSize;

    private Keyboard mKeyboard;

    private OnKeyboardActionListener mKeyboardActionListener;

    private boolean mKeyboardChanged;

    private Keyboard.Key[] mKeys;

    private int mLabelTextSize;

    private int mLastCodeX;

    private int mLastCodeY;

    private int mLastKey;

    private long mLastKeyTime;

    private long mLastMoveTime;

    private int mLastSentIndex;

    private long mLastTapTime;

    private int mLastX;

    private int mLastY;

    private KeyboardView mMiniKeyboard;

    private Map<Keyboard.Key, View> mMiniKeyboardCache;

    private View mMiniKeyboardContainer;

    private int mMiniKeyboardOffsetX;

    private int mMiniKeyboardOffsetY;

    private boolean mMiniKeyboardOnScreen;

    private int[] mOffsetInWindow;

    private long mOldEventTime;

    private int mOldPointerCount = 1;

    private float mOldPointerX;

    private float mOldPointerY;

    private Rect mPadding;

    private int mPaddingBottom;

    private int mPaddingLeft;

    private int mPaddingRight;

    private int mPaddingTop;

    private Paint mPaint;

    private PopupWindow mPopupKeyboard;

    private int mPopupLayout;

    private View mPopupParent;

    private int mPopupPreviewX;

    private int mPopupPreviewY;

    private int mPopupX;

    private int mPopupY;

    private boolean mPossiblePoly;

    private boolean mPreviewCentered = false;

    private int mPreviewHeight;

    private StringBuilder mPreviewLabel = new StringBuilder(1);

    private int mPreviewOffset;

    private PopupWindow mPreviewPopup;

    private TextView mPreviewText;

    private int mPreviewTextSizeLarge;

    private boolean mProximityCorrectOn;

    private int mProximityThreshold;

    private int mRepeatKeyIndex = -1;

    private int mShadowColor;

    private float mShadowRadius;

    private boolean mShowPreview = true;

    private boolean mShowTouchPoints = true;

    private int mStartX;

    private int mStartY;

    private int mSwipeThreshold;

    private SwipeTracker mSwipeTracker = new SwipeTracker();

    private int mTapCount;

    private boolean mUsedVelocity;

    private int mVerticalCorrection;

    private int[] mWindowOffset;

    public KeyboardView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 2130771970);
    }

    public KeyboardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        this.mContext = paramContext;
        TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.KeyboardView, paramInt, 0);
        LayoutInflater layoutInflater = (LayoutInflater)paramContext.getSystemService("layout_inflater");
        this.mKeyBackground = typedArray.getDrawable(1);
        this.mVerticalCorrection = typedArray.getDimensionPixelOffset(9, 0);
        paramInt = typedArray.getResourceId(6, 0);
        this.mPreviewOffset = typedArray.getDimensionPixelOffset(7, 0);
        this.mPreviewHeight = typedArray.getDimensionPixelSize(8, 80);
        this.mKeyTextSize = typedArray.getDimensionPixelSize(3, 18);
        this.mKeyTextColor = typedArray.getColor(5, -16777216);
        this.mLabelTextSize = typedArray.getDimensionPixelSize(4, 14);
        this.mPopupLayout = typedArray.getResourceId(10, 0);
        this.mShadowColor = typedArray.getColor(11, 0);
        this.mShadowRadius = typedArray.getFloat(12, 0.0F);
        this.mBackgroundDimAmount = this.mContext.obtainStyledAttributes(R.styleable.Theme).getFloat(1, 0.5F);
        this.mPreviewPopup = new PopupWindow(paramContext);
        if (paramInt != 0) {
            this.mPreviewText = (TextView)layoutInflater.inflate(paramInt, null);
            this.mPreviewTextSizeLarge = (int)this.mPreviewText.getTextSize();
            this.mPreviewPopup.setContentView((View)this.mPreviewText);
            this.mPreviewPopup.setBackgroundDrawable(null);
        } else {
            this.mShowPreview = false;
        }
        this.mPreviewPopup.setTouchable(false);
        this.mPopupKeyboard = new PopupWindow(paramContext);
        this.mPopupKeyboard.setBackgroundDrawable(null);
        this.mPopupParent = this;
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTextSize(false);
        this.mPaint.setTextAlign(Paint.Align.CENTER);
        this.mPaint.setAlpha(255);
        this.mPadding = new Rect(0, 0, 0, 0);
        this.mMiniKeyboardCache = new HashMap<Keyboard.Key, View>();
        this.mKeyBackground.getPadding(this.mPadding);
        this.mSwipeThreshold = (int)(500.0F * (getResources().getDisplayMetrics()).density);
        this.mDisambiguateSwipe = true;
        resetMultiTap();
        initGestureDetector();
    }

    private CharSequence adjustCase(CharSequence paramCharSequence) {
        CharSequence charSequence = paramCharSequence;
        if (this.mKeyboard.isShifted()) {
            charSequence = paramCharSequence;
            if (paramCharSequence != null) {
                charSequence = paramCharSequence;
                if (paramCharSequence.length() < 3) {
                    charSequence = paramCharSequence;
                    if (Character.isLowerCase(paramCharSequence.charAt(0)))
                        charSequence = paramCharSequence.toString().toUpperCase();
                }
            }
        }
        return charSequence;
    }

    private void checkMultiTap(long paramLong, int paramInt) {
        if (paramInt != -1) {
            Keyboard.Key key = this.mKeys[paramInt];
            if (key.codes.length > 1) {
                this.mInMultiTap = true;
                if (paramLong < this.mLastTapTime + 800L && paramInt == this.mLastSentIndex) {
                    this.mTapCount = (this.mTapCount + 1) % key.codes.length;
                    return;
                }
                this.mTapCount = -1;
                return;
            }
            if (paramLong > this.mLastTapTime + 800L || paramInt != this.mLastSentIndex) {
                resetMultiTap();
                return;
            }
        }
    }

    private void computeProximityThreshold(Keyboard paramKeyboard) {
        if (paramKeyboard != null) {
            Keyboard.Key[] arrayOfKey = this.mKeys;
            if (arrayOfKey != null) {
                int k = arrayOfKey.length;
                int j = 0;
                for (int i = 0; i < k; i++) {
                    Keyboard.Key key = arrayOfKey[i];
                    j += Math.min(key.width, key.height) + key.gap;
                }
                if (j >= 0 && k != 0) {
                    this.mProximityThreshold = (int)(j * 1.4F / k);
                    this.mProximityThreshold *= this.mProximityThreshold;
                    return;
                }
            }
        }
    }

    private void detectAndSendKey(int paramInt1, int paramInt2, int paramInt3, long paramLong) {
        if (paramInt1 != -1 && paramInt1 < this.mKeys.length) {
            Keyboard.Key key = this.mKeys[paramInt1];
            if (key.text != null) {
                this.mKeyboardActionListener.onText(key.text);
                this.mKeyboardActionListener.onRelease(-1);
            } else {
                int i = key.codes[0];
                int[] arrayOfInt = new int[MAX_NEARBY_KEYS];
                Arrays.fill(arrayOfInt, -1);
                getKeyIndices(paramInt2, paramInt3, arrayOfInt);
                paramInt2 = i;
                if (this.mInMultiTap) {
                    if (this.mTapCount != -1) {
                        this.mKeyboardActionListener.onKey(-5, KEY_DELETE);
                    } else {
                        this.mTapCount = 0;
                    }
                    paramInt2 = key.codes[this.mTapCount];
                }
                this.mKeyboardActionListener.onKey(paramInt2, arrayOfInt);
                this.mKeyboardActionListener.onRelease(paramInt2);
            }
            this.mLastSentIndex = paramInt1;
            this.mLastTapTime = paramLong;
        }
    }

    private void dismissPopupKeyboard() {
        if (this.mPopupKeyboard.isShowing()) {
            this.mPopupKeyboard.dismiss();
            this.mMiniKeyboardOnScreen = false;
            invalidateAllKeys();
        }
    }

    private int getKeyIndices(int paramInt1, int paramInt2, int[] paramArrayOfint) {
        // Byte code:
        //   0: aload_0
        //   1: getfield mKeys : [Landroid/inputmethodservice/Keyboard$Key;
        //   4: astore #16
        //   6: iconst_m1
        //   7: istore #7
        //   9: iconst_m1
        //   10: istore #4
        //   12: aload_0
        //   13: getfield mProximityThreshold : I
        //   16: iconst_1
        //   17: iadd
        //   18: istore #6
        //   20: aload_0
        //   21: getfield mDistances : [I
        //   24: ldc_w 2147483647
        //   27: invokestatic fill : ([II)V
        //   30: aload_0
        //   31: getfield mKeyboard : Landroid/inputmethodservice/Keyboard;
        //   34: iload_1
        //   35: iload_2
        //   36: invokevirtual getNearestKeys : (II)[I
        //   39: astore #17
        //   41: aload #17
        //   43: arraylength
        //   44: istore #13
        //   46: iconst_0
        //   47: istore #11
        //   49: iload #11
        //   51: iload #13
        //   53: if_icmpge -> 367
        //   56: aload #16
        //   58: aload #17
        //   60: iload #11
        //   62: iaload
        //   63: aaload
        //   64: astore #18
        //   66: iconst_0
        //   67: istore #8
        //   69: aload #18
        //   71: iload_1
        //   72: iload_2
        //   73: invokevirtual isInside : (II)Z
        //   76: istore #15
        //   78: aload_0
        //   79: getfield mProximityCorrectOn : Z
        //   82: ifeq -> 111
        //   85: aload #18
        //   87: iload_1
        //   88: iload_2
        //   89: invokevirtual squaredDistanceFrom : (II)I
        //   92: istore #5
        //   94: iload #5
        //   96: istore #8
        //   98: iload #5
        //   100: aload_0
        //   101: getfield mProximityThreshold : I
        //   104: if_icmplt -> 124
        //   107: iload #5
        //   109: istore #8
        //   111: iload #4
        //   113: istore #9
        //   115: iload #6
        //   117: istore #10
        //   119: iload #15
        //   121: ifeq -> 336
        //   124: iload #4
        //   126: istore #9
        //   128: iload #6
        //   130: istore #10
        //   132: aload #18
        //   134: getfield codes : [I
        //   137: iconst_0
        //   138: iaload
        //   139: bipush #32
        //   141: if_icmple -> 336
        //   144: aload #18
        //   146: getfield codes : [I
        //   149: arraylength
        //   150: istore #14
        //   152: iload #6
        //   154: istore #5
        //   156: iload #8
        //   158: iload #6
        //   160: if_icmpge -> 174
        //   163: iload #8
        //   165: istore #5
        //   167: aload #17
        //   169: iload #11
        //   171: iaload
        //   172: istore #4
        //   174: aload_3
        //   175: ifnonnull -> 191
        //   178: iload #11
        //   180: iconst_1
        //   181: iadd
        //   182: istore #11
        //   184: iload #5
        //   186: istore #6
        //   188: goto -> 49
        //   191: iconst_0
        //   192: istore #6
        //   194: iload #4
        //   196: istore #9
        //   198: iload #5
        //   200: istore #10
        //   202: iload #6
        //   204: aload_0
        //   205: getfield mDistances : [I
        //   208: arraylength
        //   209: if_icmpge -> 336
        //   212: aload_0
        //   213: getfield mDistances : [I
        //   216: iload #6
        //   218: iaload
        //   219: iload #8
        //   221: if_icmple -> 327
        //   224: aload_0
        //   225: getfield mDistances : [I
        //   228: iload #6
        //   230: aload_0
        //   231: getfield mDistances : [I
        //   234: iload #6
        //   236: iload #14
        //   238: iadd
        //   239: aload_0
        //   240: getfield mDistances : [I
        //   243: arraylength
        //   244: iload #6
        //   246: isub
        //   247: iload #14
        //   249: isub
        //   250: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
        //   253: aload_3
        //   254: iload #6
        //   256: aload_3
        //   257: iload #6
        //   259: iload #14
        //   261: iadd
        //   262: aload_3
        //   263: arraylength
        //   264: iload #6
        //   266: isub
        //   267: iload #14
        //   269: isub
        //   270: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
        //   273: iconst_0
        //   274: istore #12
        //   276: iload #4
        //   278: istore #9
        //   280: iload #5
        //   282: istore #10
        //   284: iload #12
        //   286: iload #14
        //   288: if_icmpge -> 336
        //   291: aload_3
        //   292: iload #6
        //   294: iload #12
        //   296: iadd
        //   297: aload #18
        //   299: getfield codes : [I
        //   302: iload #12
        //   304: iaload
        //   305: iastore
        //   306: aload_0
        //   307: getfield mDistances : [I
        //   310: iload #6
        //   312: iload #12
        //   314: iadd
        //   315: iload #8
        //   317: iastore
        //   318: iload #12
        //   320: iconst_1
        //   321: iadd
        //   322: istore #12
        //   324: goto -> 276
        //   327: iload #6
        //   329: iconst_1
        //   330: iadd
        //   331: istore #6
        //   333: goto -> 194
        //   336: iload #9
        //   338: istore #4
        //   340: iload #10
        //   342: istore #5
        //   344: iload #15
        //   346: ifeq -> 178
        //   349: aload #17
        //   351: iload #11
        //   353: iaload
        //   354: istore #7
        //   356: iload #9
        //   358: istore #4
        //   360: iload #10
        //   362: istore #5
        //   364: goto -> 178
        //   367: iload #7
        //   369: istore_1
        //   370: iload #7
        //   372: iconst_m1
        //   373: if_icmpne -> 379
        //   376: iload #4
        //   378: istore_1
        //   379: iload_1
        //   380: ireturn
    }

    private CharSequence getPreviewText(Keyboard.Key paramKey) {
        int[] arrayOfInt;
        int i = 0;
        if (this.mInMultiTap) {
            this.mPreviewLabel.setLength(0);
            StringBuilder stringBuilder = this.mPreviewLabel;
            arrayOfInt = paramKey.codes;
            if (this.mTapCount >= 0)
                i = this.mTapCount;
            stringBuilder.append((char)arrayOfInt[i]);
            return adjustCase(this.mPreviewLabel);
        }
        return adjustCase(((Keyboard.Key)arrayOfInt).label);
    }

    private static int getSDKVersion() {
        return Integer.valueOf(Build.VERSION.SDK).intValue();
    }

    private void initGestureDetector() {
        this.mGestureDetector = new GestureDetector(getContext(), (GestureDetector.OnGestureListener)new GestureDetector.SimpleOnGestureListener() {
            public boolean onFling(MotionEvent param1MotionEvent1, MotionEvent param1MotionEvent2, float param1Float1, float param1Float2) {
                if (KeyboardView.this.mPossiblePoly)
                    return false;
                float f1 = Math.abs(param1Float1);
                float f2 = Math.abs(param1Float2);
                float f3 = param1MotionEvent2.getX() - param1MotionEvent1.getX();
                float f4 = param1MotionEvent2.getY() - param1MotionEvent1.getY();
                int i = KeyboardView.this.getWidth() / 4;
                int j = KeyboardView.this.getHeight() / 5;
                KeyboardView.this.mSwipeTracker.computeCurrentVelocity(1000);
                float f5 = KeyboardView.this.mSwipeTracker.getXVelocity();
                float f6 = KeyboardView.this.mSwipeTracker.getYVelocity();
                boolean bool = false;
                if (param1Float1 > KeyboardView.this.mSwipeThreshold && f2 < f1 && f3 > i) {
                    if (KeyboardView.this.mDisambiguateSwipe && f5 < param1Float1 / 4.0F) {
                        i = 1;
                    } else {
                        KeyboardView.this.swipeRight();
                        return true;
                    }
                } else if (param1Float1 < -KeyboardView.this.mSwipeThreshold && f2 < f1 && f3 < -i) {
                    if (KeyboardView.this.mDisambiguateSwipe && f5 > param1Float1 / 4.0F) {
                        i = 1;
                    } else {
                        KeyboardView.this.swipeLeft();
                        return true;
                    }
                } else if (param1Float2 < -KeyboardView.this.mSwipeThreshold && f1 < f2 && f4 < -j) {
                    if (KeyboardView.this.mDisambiguateSwipe && f6 > param1Float2 / 4.0F) {
                        i = 1;
                    } else {
                        KeyboardView.this.swipeUp();
                        return true;
                    }
                } else {
                    i = bool;
                    if (param1Float2 > KeyboardView.this.mSwipeThreshold) {
                        i = bool;
                        if (f1 < f2 / 2.0F) {
                            i = bool;
                            if (f4 > j)
                                if (KeyboardView.this.mDisambiguateSwipe && f6 < param1Float2 / 4.0F) {
                                    i = 1;
                                } else {
                                    KeyboardView.this.swipeDown();
                                    return true;
                                }
                        }
                    }
                }
                if (i != 0)
                    KeyboardView.this.detectAndSendKey(KeyboardView.this.mDownKey, KeyboardView.this.mStartX, KeyboardView.this.mStartY, param1MotionEvent1.getEventTime());
                return false;
            }
        });
        this.mGestureDetector.setIsLongpressEnabled(false);
    }

    private void onBufferDraw() {
        if (this.mBuffer == null || this.mKeyboardChanged) {
            if (this.mBuffer == null || (this.mKeyboardChanged && (this.mBuffer.getWidth() != getWidth() || this.mBuffer.getHeight() != getHeight()))) {
                this.mBuffer = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                this.mCanvas = new Canvas(this.mBuffer);
            }
            invalidateAllKeys();
            this.mKeyboardChanged = false;
        }
        Canvas canvas = this.mCanvas;
        canvas.clipRect(this.mDirtyRect, Region.Op.REPLACE);
        if (this.mKeyboard == null)
            return;
        Paint paint = this.mPaint;
        Drawable drawable = this.mKeyBackground;
        Rect rect1 = this.mClipRegion;
        Rect rect2 = this.mPadding;
        int k = this.mPaddingLeft;
        int m = this.mPaddingTop;
        Keyboard.Key[] arrayOfKey = this.mKeys;
        Keyboard.Key key = this.mInvalidatedKey;
        paint.setColor(this.mKeyTextColor);
        int j = 0;
        int i = j;
        if (key != null) {
            i = j;
            if (canvas.getClipBounds(rect1)) {
                i = j;
                if (key.x + k - 1 <= rect1.left) {
                    i = j;
                    if (key.y + m - 1 <= rect1.top) {
                        i = j;
                        if (key.x + key.width + k + 1 >= rect1.right) {
                            i = j;
                            if (key.y + key.height + m + 1 >= rect1.bottom)
                                i = 1;
                        }
                    }
                }
            }
        }
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        int n = arrayOfKey.length;
        for (j = 0; j < n; j++) {
            Keyboard.Key key1 = arrayOfKey[j];
            if (i == 0 || key == key1) {
                String str;
                drawable.setState(key1.getCurrentDrawableState());
                if (key1.label == null) {
                    rect1 = null;
                } else {
                    str = adjustCase(key1.label).toString();
                }
                Rect rect = drawable.getBounds();
                if (key1.width != rect.right || key1.height != rect.bottom)
                    drawable.setBounds(0, 0, key1.width, key1.height);
                canvas.translate((key1.x + k), (key1.y + m));
                drawable.draw(canvas);
                if (str != null) {
                    if (str.length() > 1 && key1.codes.length < 2) {
                        paint.setTextSize(this.mLabelTextSize);
                        paint.setTypeface(Typeface.DEFAULT_BOLD);
                    } else {
                        paint.setTextSize(this.mKeyTextSize);
                        paint.setTypeface(Typeface.DEFAULT);
                    }
                    paint.setShadowLayer(this.mShadowRadius, 0.0F, 0.0F, this.mShadowColor);
                    canvas.drawText(str, ((key1.width - rect2.left - rect2.right) / 2 + rect2.left), ((key1.height - rect2.top - rect2.bottom) / 2) + (paint.getTextSize() - paint.descent()) / 2.0F + rect2.top, paint);
                    paint.setShadowLayer(0.0F, 0.0F, 0.0F, 0);
                } else if (key1.icon != null) {
                    int i1 = (key1.width - rect2.left - rect2.right - key1.icon.getIntrinsicWidth()) / 2 + rect2.left;
                    int i2 = (key1.height - rect2.top - rect2.bottom - key1.icon.getIntrinsicHeight()) / 2 + rect2.top;
                    canvas.translate(i1, i2);
                    key1.icon.setBounds(0, 0, key1.icon.getIntrinsicWidth(), key1.icon.getIntrinsicHeight());
                    key1.icon.draw(canvas);
                    canvas.translate(-i1, -i2);
                }
                canvas.translate((-key1.x - k), (-key1.y - m));
            }
        }
        this.mInvalidatedKey = null;
        if (this.mMiniKeyboardOnScreen) {
            paint.setColor((int)(this.mBackgroundDimAmount * 255.0F) << 24);
            canvas.drawRect(0.0F, 0.0F, getWidth(), getHeight(), paint);
        }
        this.mDrawPending = false;
        this.mDirtyRect.setEmpty();
    }

    private boolean onModifiedTouchEvent(MotionEvent paramMotionEvent, boolean paramBoolean) {
        Message message;
        OnKeyboardActionListener onKeyboardActionListener;
        int k = (int)paramMotionEvent.getX() - this.mPaddingLeft;
        int m = (int)paramMotionEvent.getY() + this.mVerticalCorrection - this.mPaddingTop;
        int i = paramMotionEvent.getAction();
        long l = paramMotionEvent.getEventTime();
        this.mOldEventTime = l;
        int n = getKeyIndices(k, m, (int[])null);
        this.mPossiblePoly = paramBoolean;
        if (i == 0)
            this.mSwipeTracker.clear();
        this.mSwipeTracker.addMovement(paramMotionEvent);
        if (this.mGestureDetector.onTouchEvent(paramMotionEvent)) {
            showPreview(-1);
            this.mHandler.removeMessages(3);
            this.mHandler.removeMessages(4);
            return true;
        }
        if (this.mMiniKeyboardOnScreen)
            return true;
        switch (i) {
            default:
                i = m;
                j = k;
                this.mLastX = j;
                this.mLastY = i;
                return true;
            case 0:
                this.mAbortKey = false;
                this.mStartX = k;
                this.mStartY = m;
                this.mLastCodeX = k;
                this.mLastCodeY = m;
                this.mLastKeyTime = 0L;
                this.mCurrentKeyTime = 0L;
                this.mLastKey = -1;
                this.mCurrentKey = n;
                this.mDownKey = n;
                this.mDownTime = paramMotionEvent.getEventTime();
                this.mLastMoveTime = this.mDownTime;
                checkMultiTap(l, n);
                onKeyboardActionListener = this.mKeyboardActionListener;
                if (n != -1) {
                    i = (this.mKeys[n]).codes[0];
                } else {
                    i = 0;
                }
                onKeyboardActionListener.onPress(i);
                if (this.mCurrentKey >= 0 && (this.mKeys[this.mCurrentKey]).repeatable) {
                    this.mRepeatKeyIndex = this.mCurrentKey;
                    repeatKey();
                    Message message1 = this.mHandler.obtainMessage(3);
                    this.mHandler.sendMessageDelayed(message1, 400L);
                }
                if (this.mCurrentKey != -1) {
                    message = this.mHandler.obtainMessage(4, paramMotionEvent);
                    this.mHandler.sendMessageDelayed(message, 800L);
                }
                showPreview(n);
                j = k;
                i = m;
                this.mLastX = j;
                this.mLastY = i;
                return true;
            case 2:
                j = 0;
                i = j;
                if (n != -1)
                    if (this.mCurrentKey == -1) {
                        this.mCurrentKey = n;
                        this.mCurrentKeyTime = l - this.mDownTime;
                        i = j;
                    } else if (n == this.mCurrentKey) {
                        this.mCurrentKeyTime += l - this.mLastMoveTime;
                        i = 1;
                    } else {
                        i = j;
                        if (this.mRepeatKeyIndex == -1) {
                            resetMultiTap();
                            this.mLastKey = this.mCurrentKey;
                            this.mLastCodeX = this.mLastX;
                            this.mLastCodeY = this.mLastY;
                            this.mLastKeyTime = this.mCurrentKeyTime + l - this.mLastMoveTime;
                            this.mCurrentKey = n;
                            this.mCurrentKeyTime = 0L;
                            i = j;
                        }
                    }
                if (i == 0) {
                    this.mHandler.removeMessages(4);
                    if (n != -1) {
                        message = this.mHandler.obtainMessage(4, message);
                        this.mHandler.sendMessageDelayed(message, 800L);
                    }
                }
                showPreview(this.mCurrentKey);
                j = k;
                i = m;
                this.mLastX = j;
                this.mLastY = i;
                return true;
            case 1:
                removeMessages();
                if (n == this.mCurrentKey) {
                    this.mCurrentKeyTime += l - this.mLastMoveTime;
                } else {
                    resetMultiTap();
                    this.mLastKey = this.mCurrentKey;
                    this.mLastKeyTime = this.mCurrentKeyTime + l - this.mLastMoveTime;
                    this.mCurrentKey = n;
                    this.mCurrentKeyTime = 0L;
                }
                j = k;
                i = m;
                if (this.mCurrentKeyTime < this.mLastKeyTime) {
                    j = k;
                    i = m;
                    if (this.mLastKey != -1) {
                        this.mCurrentKey = this.mLastKey;
                        j = this.mLastCodeX;
                        i = this.mLastCodeY;
                    }
                }
                showPreview(-1);
                Arrays.fill(this.mKeyIndices, -1);
                if (this.mRepeatKeyIndex == -1 && !this.mMiniKeyboardOnScreen && !this.mAbortKey)
                    detectAndSendKey(this.mCurrentKey, j, i, l);
                invalidateKey(n);
                this.mRepeatKeyIndex = -1;
                this.mLastX = j;
                this.mLastY = i;
                return true;
            case 3:
                break;
        }
        removeMessages();
        this.mAbortKey = true;
        showPreview(-1);
        invalidateKey(this.mCurrentKey);
        int j = k;
        i = m;
        this.mLastX = j;
        this.mLastY = i;
        return true;
    }

    private boolean openPopupIfRequired(MotionEvent paramMotionEvent) {
        boolean bool2 = false;
        if (this.mPopupLayout == 0)
            return bool2;
        boolean bool1 = bool2;
        if (this.mCurrentKey >= 0) {
            bool1 = bool2;
            if (this.mCurrentKey < this.mKeys.length) {
                bool2 = onLongPress(this.mKeys[this.mCurrentKey]);
                bool1 = bool2;
                if (bool2) {
                    this.mAbortKey = true;
                    showPreview(-1);
                    return bool2;
                }
            }
        }
        return bool1;
    }

    private void removeMessages() {
        this.mHandler.removeMessages(3);
        this.mHandler.removeMessages(4);
        this.mHandler.removeMessages(1);
    }

    private boolean repeatKey() {
        Keyboard.Key key = this.mKeys[this.mRepeatKeyIndex];
        detectAndSendKey(this.mCurrentKey, key.x, key.y, this.mLastTapTime);
        return true;
    }

    private void resetMultiTap() {
        this.mLastSentIndex = -1;
        this.mTapCount = 0;
        this.mLastTapTime = -1L;
        this.mInMultiTap = false;
    }

    private void showKey(int paramInt) {
        int[] arrayOfInt;
        PopupWindow popupWindow = this.mPreviewPopup;
        Keyboard.Key[] arrayOfKey = this.mKeys;
        if (paramInt < 0 || paramInt >= this.mKeys.length)
            return;
        Keyboard.Key key = arrayOfKey[paramInt];
        if (key.icon != null) {
            Drawable drawable1;
            TextView textView = this.mPreviewText;
            if (key.iconPreview != null) {
                drawable1 = key.iconPreview;
            } else {
                drawable1 = key.icon;
            }
            textView.setCompoundDrawables(null, null, null, drawable1);
            this.mPreviewText.setText(null);
        } else {
            this.mPreviewText.setCompoundDrawables(null, null, null, null);
            this.mPreviewText.setText(getPreviewText(key));
            if (key.label.length() > 1 && key.codes.length < 2) {
                this.mPreviewText.setTextSize(0, this.mKeyTextSize);
                this.mPreviewText.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                this.mPreviewText.setTextSize(0, this.mPreviewTextSizeLarge);
                this.mPreviewText.setTypeface(Typeface.DEFAULT);
            }
        }
        this.mPreviewText.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        paramInt = Math.max(this.mPreviewText.getMeasuredWidth(), key.width + this.mPreviewText.getPaddingLeft() + this.mPreviewText.getPaddingRight());
        int i = this.mPreviewHeight;
        ViewGroup.LayoutParams layoutParams = this.mPreviewText.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.width = paramInt;
            layoutParams.height = i;
        }
        if (!this.mPreviewCentered) {
            this.mPopupPreviewX = key.x - this.mPreviewText.getPaddingLeft() + this.mPaddingLeft;
            this.mPopupPreviewY = key.y - i + this.mPreviewOffset;
        } else {
            this.mPopupPreviewX = 160 - this.mPreviewText.getMeasuredWidth() / 2;
            this.mPopupPreviewY = -this.mPreviewText.getMeasuredHeight();
        }
        this.mHandler.removeMessages(2);
        if (this.mOffsetInWindow == null) {
            this.mOffsetInWindow = new int[2];
            getLocationInWindow(this.mOffsetInWindow);
            arrayOfInt = this.mOffsetInWindow;
            arrayOfInt[0] = arrayOfInt[0] + this.mMiniKeyboardOffsetX;
            arrayOfInt = this.mOffsetInWindow;
            arrayOfInt[1] = arrayOfInt[1] + this.mMiniKeyboardOffsetY;
        }
        Drawable drawable = this.mPreviewText.getBackground();
        if (key.popupResId != 0) {
            arrayOfInt = LONG_PRESSABLE_STATE_SET;
        } else {
            arrayOfInt = EMPTY_STATE_SET;
        }
        drawable.setState(arrayOfInt);
        if (popupWindow.isShowing()) {
            popupWindow.update(this.mPopupPreviewX + this.mOffsetInWindow[0], this.mPopupPreviewY + this.mOffsetInWindow[1], paramInt, i);
        } else {
            popupWindow.setWidth(paramInt);
            popupWindow.setHeight(i);
            popupWindow.showAtLocation(this.mPopupParent, 0, this.mPopupPreviewX + this.mOffsetInWindow[0], this.mPopupPreviewY + this.mOffsetInWindow[1]);
        }
        this.mPreviewText.setVisibility(0);
    }

    private void showPreview(int paramInt) {
        int i = this.mCurrentKeyIndex;
        PopupWindow popupWindow = this.mPreviewPopup;
        this.mCurrentKeyIndex = paramInt;
        Keyboard.Key[] arrayOfKey = this.mKeys;
        if (i != this.mCurrentKeyIndex) {
            if (i != -1 && arrayOfKey.length > i) {
                boolean bool;
                Keyboard.Key key = arrayOfKey[i];
                if (this.mCurrentKeyIndex == -1) {
                    bool = true;
                } else {
                    bool = false;
                }
                key.onReleased(bool);
                invalidateKey(i);
            }
            if (this.mCurrentKeyIndex != -1 && arrayOfKey.length > this.mCurrentKeyIndex) {
                arrayOfKey[this.mCurrentKeyIndex].onPressed();
                invalidateKey(this.mCurrentKeyIndex);
            }
        }
        if (i != this.mCurrentKeyIndex && this.mShowPreview) {
            this.mHandler.removeMessages(1);
            if (popupWindow.isShowing() && paramInt == -1)
                this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(2), 70L);
            if (paramInt != -1) {
                if (popupWindow.isShowing() && this.mPreviewText.getVisibility() == 0) {
                    showKey(paramInt);
                    return;
                }
            } else {
                return;
            }
        } else {
            return;
        }
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1, paramInt, 0), 0L);
    }

    public void closing() {
        if (this.mPreviewPopup.isShowing())
            this.mPreviewPopup.dismiss();
        removeMessages();
        dismissPopupKeyboard();
        this.mBuffer = null;
        this.mCanvas = null;
        this.mMiniKeyboardCache.clear();
    }

    public Keyboard getKeyboard() {
        return this.mKeyboard;
    }

    protected OnKeyboardActionListener getOnKeyboardActionListener() {
        return this.mKeyboardActionListener;
    }

    public boolean handleBack() {
        if (this.mPopupKeyboard.isShowing()) {
            dismissPopupKeyboard();
            return true;
        }
        return false;
    }

    public void invalidateAllKeys() {
        this.mDirtyRect.union(0, 0, getWidth(), getHeight());
        this.mDrawPending = true;
        invalidate();
    }

    public void invalidateKey(int paramInt) {
        if (this.mKeys != null && paramInt >= 0 && paramInt < this.mKeys.length) {
            Keyboard.Key key = this.mKeys[paramInt];
            this.mInvalidatedKey = key;
            this.mDirtyRect.union(key.x + this.mPaddingLeft, key.y + this.mPaddingTop, key.x + key.width + this.mPaddingLeft, key.y + key.height + this.mPaddingTop);
            onBufferDraw();
            invalidate(key.x + this.mPaddingLeft, key.y + this.mPaddingTop, key.x + key.width + this.mPaddingLeft, key.y + key.height + this.mPaddingTop);
            return;
        }
    }

    public boolean isPreviewEnabled() {
        return this.mShowPreview;
    }

    public boolean isProximityCorrectionEnabled() {
        return this.mProximityCorrectOn;
    }

    public boolean isShifted() {
        return (this.mKeyboard != null) ? this.mKeyboard.isShifted() : false;
    }

    public void onClick(View paramView) {
        dismissPopupKeyboard();
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        closing();
    }

    public void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        if (this.mDrawPending || this.mBuffer == null || this.mKeyboardChanged)
            onBufferDraw();
        paramCanvas.drawBitmap(this.mBuffer, 0.0F, 0.0F, null);
    }

    protected boolean onLongPress(Keyboard.Key paramKey) {
        boolean bool = false;
        int i = paramKey.popupResId;
        if (i != 0) {
            this.mMiniKeyboardContainer = this.mMiniKeyboardCache.get(paramKey);
            if (this.mMiniKeyboardContainer == null) {
                Keyboard keyboard;
                this.mMiniKeyboardContainer = ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(this.mPopupLayout, null);
                this.mMiniKeyboard = (KeyboardView)this.mMiniKeyboardContainer.findViewById(2131165216);
                View view = this.mMiniKeyboardContainer.findViewById(2131165217);
                if (view != null)
                    view.setOnClickListener(this);
                this.mMiniKeyboard.setOnKeyboardActionListener(new OnKeyboardActionListener() {
                    public void onKey(int param1Int, int[] param1ArrayOfint) {
                        KeyboardView.this.mKeyboardActionListener.onKey(param1Int, param1ArrayOfint);
                        KeyboardView.this.dismissPopupKeyboard();
                    }

                    public void onPress(int param1Int) {
                        KeyboardView.this.mKeyboardActionListener.onPress(param1Int);
                    }

                    public void onRelease(int param1Int) {
                        KeyboardView.this.mKeyboardActionListener.onRelease(param1Int);
                    }

                    public void onText(CharSequence param1CharSequence) {
                        KeyboardView.this.mKeyboardActionListener.onText(param1CharSequence);
                        KeyboardView.this.dismissPopupKeyboard();
                    }

                    public void swipeDown() {}

                    public void swipeLeft() {}

                    public void swipeRight() {}

                    public void swipeUp() {}
                });
                if (paramKey.popupCharacters != null) {
                    keyboard = new Keyboard(getContext(), i, paramKey.popupCharacters, -1, getPaddingLeft() + getPaddingRight());
                } else {
                    keyboard = new Keyboard(getContext(), i);
                }
                this.mMiniKeyboard.setKeyboard(keyboard);
                this.mMiniKeyboard.setPopupParent(this);
                this.mMiniKeyboardContainer.measure(View.MeasureSpec.makeMeasureSpec(getWidth(), -2147483648), View.MeasureSpec.makeMeasureSpec(getHeight(), -2147483648));
                this.mMiniKeyboardCache.put(paramKey, this.mMiniKeyboardContainer);
            } else {
                this.mMiniKeyboard = (KeyboardView)this.mMiniKeyboardContainer.findViewById(2131165216);
            }
            if (this.mWindowOffset == null) {
                this.mWindowOffset = new int[2];
                getLocationInWindow(this.mWindowOffset);
            }
            this.mPopupX = paramKey.x + this.mPaddingLeft;
            this.mPopupY = paramKey.y + this.mPaddingTop;
            this.mPopupX = this.mPopupX + paramKey.width - this.mMiniKeyboardContainer.getMeasuredWidth();
            this.mPopupY -= this.mMiniKeyboardContainer.getMeasuredHeight();
            int j = this.mPopupX + this.mMiniKeyboardContainer.getPaddingRight() + this.mWindowOffset[0];
            int k = this.mPopupY + this.mMiniKeyboardContainer.getPaddingBottom() + this.mWindowOffset[1];
            KeyboardView keyboardView = this.mMiniKeyboard;
            if (j < 0) {
                i = 0;
            } else {
                i = j;
            }
            keyboardView.setPopupOffset(i, k);
            this.mMiniKeyboard.setShifted(isShifted());
            this.mPopupKeyboard.setContentView(this.mMiniKeyboardContainer);
            this.mPopupKeyboard.setWidth(this.mMiniKeyboardContainer.getMeasuredWidth());
            this.mPopupKeyboard.setHeight(this.mMiniKeyboardContainer.getMeasuredHeight());
            this.mPopupKeyboard.showAtLocation(this, 0, j, k);
            this.mMiniKeyboardOnScreen = true;
            invalidateAllKeys();
            bool = true;
        }
        return bool;
    }

    public void onMeasure(int paramInt1, int paramInt2) {
        if (this.mKeyboard == null) {
            setMeasuredDimension(this.mPaddingLeft + this.mPaddingRight, this.mPaddingTop + this.mPaddingBottom);
            return;
        }
        int i = this.mKeyboard.getMinWidth() + this.mPaddingLeft + this.mPaddingRight;
        paramInt2 = i;
        if (View.MeasureSpec.getSize(paramInt1) < i + 10)
            paramInt2 = View.MeasureSpec.getSize(paramInt1);
        setMeasuredDimension(paramInt2, this.mKeyboard.getHeight() + this.mPaddingTop + this.mPaddingBottom);
    }

    public void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
        this.mBuffer = null;
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        boolean bool1;
        if (getSDKVersion() >= 5) {
            bool1 = Api5.getPointerCount(paramMotionEvent);
        } else {
            bool1 = true;
        }
        int i = paramMotionEvent.getAction();
        long l = paramMotionEvent.getEventTime();
        if (bool1 != this.mOldPointerCount) {
            if (bool1 == true) {
                MotionEvent motionEvent = MotionEvent.obtain(l, l, 0, paramMotionEvent.getX(), paramMotionEvent.getY(), paramMotionEvent.getMetaState());
                boolean bool3 = onModifiedTouchEvent(motionEvent, false);
                motionEvent.recycle();
                if (i == 1)
                    bool3 = onModifiedTouchEvent(paramMotionEvent, true);
                this.mOldPointerCount = bool1;
                return bool3;
            }
            paramMotionEvent = MotionEvent.obtain(l, l, 1, this.mOldPointerX, this.mOldPointerY, paramMotionEvent.getMetaState());
            boolean bool = onModifiedTouchEvent(paramMotionEvent, true);
            paramMotionEvent.recycle();
            this.mOldPointerCount = bool1;
            return bool;
        }
        if (bool1 == true) {
            boolean bool = onModifiedTouchEvent(paramMotionEvent, false);
            this.mOldPointerX = paramMotionEvent.getX();
            this.mOldPointerY = paramMotionEvent.getY();
            this.mOldPointerCount = bool1;
            return bool;
        }
        boolean bool2 = true;
        this.mOldPointerCount = bool1;
        return bool2;
    }

    public void setKeyboard(Keyboard paramKeyboard) {
        if (this.mKeyboard != null)
            showPreview(-1);
        removeMessages();
        this.mKeyboard = paramKeyboard;
        List list = this.mKeyboard.getKeys();
        this.mKeys = (Keyboard.Key[])list.toArray((Object[])new Keyboard.Key[list.size()]);
        requestLayout();
        this.mKeyboardChanged = true;
        invalidateAllKeys();
        computeProximityThreshold(paramKeyboard);
        this.mMiniKeyboardCache.clear();
        this.mAbortKey = true;
    }

    public void setOnKeyboardActionListener(OnKeyboardActionListener paramOnKeyboardActionListener) {
        this.mKeyboardActionListener = paramOnKeyboardActionListener;
    }

    public void setPopupOffset(int paramInt1, int paramInt2) {
        this.mMiniKeyboardOffsetX = paramInt1;
        this.mMiniKeyboardOffsetY = paramInt2;
        if (this.mPreviewPopup.isShowing())
            this.mPreviewPopup.dismiss();
    }

    public void setPopupParent(View paramView) {
        this.mPopupParent = paramView;
    }

    public void setPreviewEnabled(boolean paramBoolean) {
        this.mShowPreview = paramBoolean;
    }

    public void setProximityCorrectionEnabled(boolean paramBoolean) {
        this.mProximityCorrectOn = paramBoolean;
    }

    public boolean setShifted(boolean paramBoolean) {
        if (this.mKeyboard != null && this.mKeyboard.setShifted(paramBoolean)) {
            invalidateAllKeys();
            return true;
        }
        return false;
    }

    public void setVerticalCorrection(int paramInt) {}

    protected void swipeDown() {
        this.mKeyboardActionListener.swipeDown();
    }

    protected void swipeLeft() {
        this.mKeyboardActionListener.swipeLeft();
    }

    protected void swipeRight() {
        this.mKeyboardActionListener.swipeRight();
    }

    protected void swipeUp() {
        this.mKeyboardActionListener.swipeUp();
    }

    public static interface OnKeyboardActionListener {
        void onKey(int param1Int, int[] param1ArrayOfint);

        void onPress(int param1Int);

        void onRelease(int param1Int);

        void onText(CharSequence param1CharSequence);

        void swipeDown();

        void swipeLeft();

        void swipeRight();

        void swipeUp();
    }

    private static class SwipeTracker {
        static final int LONGEST_PAST_TIME = 200;

        static final int NUM_PAST = 4;

        final long[] mPastTime = new long[4];

        final float[] mPastX = new float[4];

        final float[] mPastY = new float[4];

        float mXVelocity;

        float mYVelocity;

        private SwipeTracker() {}

        private void addPoint(float param1Float1, float param1Float2, long param1Long) {
            int j = -1;
            long[] arrayOfLong = this.mPastTime;
            int i;
            for (i = 0;; i++) {
                if (i >= 4 || arrayOfLong[i] == 0L) {
                    int k = j;
                    if (i == 4) {
                        k = j;
                        if (j < 0)
                            k = 0;
                    }
                    j = k;
                    if (k == i)
                        j = k - 1;
                    float[] arrayOfFloat1 = this.mPastX;
                    float[] arrayOfFloat2 = this.mPastY;
                    k = i;
                    if (j >= 0) {
                        k = j + 1;
                        int m = 4 - j - 1;
                        System.arraycopy(arrayOfFloat1, k, arrayOfFloat1, 0, m);
                        System.arraycopy(arrayOfFloat2, k, arrayOfFloat2, 0, m);
                        System.arraycopy(arrayOfLong, k, arrayOfLong, 0, m);
                        k = i - j + 1;
                    }
                    arrayOfFloat1[k] = param1Float1;
                    arrayOfFloat2[k] = param1Float2;
                    arrayOfLong[k] = param1Long;
                    i = k + 1;
                    if (i < 4)
                        arrayOfLong[i] = 0L;
                    return;
                }
                if (arrayOfLong[i] < param1Long - 200L)
                    j = i;
            }
        }

        public void addMovement(MotionEvent param1MotionEvent) {
            long l = param1MotionEvent.getEventTime();
            int j = param1MotionEvent.getHistorySize();
            for (int i = 0; i < j; i++)
                addPoint(param1MotionEvent.getHistoricalX(i), param1MotionEvent.getHistoricalY(i), param1MotionEvent.getHistoricalEventTime(i));
            addPoint(param1MotionEvent.getX(), param1MotionEvent.getY(), l);
        }

        public void clear() {
            this.mPastTime[0] = 0L;
        }

        public void computeCurrentVelocity(int param1Int) {
            computeCurrentVelocity(param1Int, Float.MAX_VALUE);
        }

        public void computeCurrentVelocity(int param1Int, float param1Float) {
            float[] arrayOfFloat1 = this.mPastX;
            float[] arrayOfFloat2 = this.mPastY;
            long[] arrayOfLong = this.mPastTime;
            float f3 = arrayOfFloat1[0];
            float f4 = arrayOfFloat2[0];
            long l = arrayOfLong[0];
            float f2 = 0.0F;
            float f1 = 0.0F;
            int i = 0;
            while (true) {
                float f;
                if (i >= 4 || arrayOfLong[i] == 0L) {
                    int j = 1;
                    while (j < i) {
                        int k = (int)(arrayOfLong[j] - l);
                        if (k == 0) {
                            f = f1;
                        } else {
                            f = (arrayOfFloat1[j] - f3) / k * param1Int;
                            if (f2 != 0.0F)
                                f = (f2 + f) * 0.5F;
                            f2 = (arrayOfFloat2[j] - f4) / k * param1Int;
                            if (f1 == 0.0F) {
                                f1 = f2;
                                f2 = f;
                                f = f1;
                            } else {
                                f1 = (f1 + f2) * 0.5F;
                                f2 = f;
                                f = f1;
                            }
                        }
                        j++;
                        f1 = f;
                    }
                } else {
                    i++;
                    continue;
                }
                if (f2 < 0.0F) {
                    f = Math.max(f2, -param1Float);
                } else {
                    f = Math.min(f2, param1Float);
                }
                this.mXVelocity = f;
                if (f1 < 0.0F) {
                    param1Float = Math.max(f1, -param1Float);
                } else {
                    param1Float = Math.min(f1, param1Float);
                }
                this.mYVelocity = param1Float;
                return;
            }
        }

        public float getXVelocity() {
            return this.mXVelocity;
        }

        public float getYVelocity() {
            return this.mYVelocity;
        }
    }
}

