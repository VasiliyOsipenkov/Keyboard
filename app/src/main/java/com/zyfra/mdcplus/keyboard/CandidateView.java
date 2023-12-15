package com.zyfra.mdcplus.keyboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class CandidateView extends View {
    private static final List<String> EMPTY_LIST = new ArrayList<String>();

    private static final int MAX_SUGGESTIONS = 32;

    private static final int OUT_OF_BOUNDS = -1;

    private static final int SCROLL_PIXELS = 20;

    private static final int X_GAP = 10;

    private Paint awPaint;

    private Paint bPaint;

    private Rect mBgPadding;

    private int mColorBlue;

    private int mColorNormal;

    private int mColorOther;

    private int mColorRecommended;

    private int mColorRed;

    private int mColorWhite;

    private GestureDetector mGestureDetector;

    private Paint mPaint;

    private boolean mScrolled;

    private int mSelectedIndex;

    private Drawable mSelectionHighlight;

    private SoftKeyboard mService;

    private List<String> mSuggestions;

    private int mTargetScrollX;

    private int mTotalWidth;

    private int mTouchX = -1;

    private boolean mTypedWordValid;

    private int mVerticalPadding;

    private int[] mWordWidth = new int[32];

    private int[] mWordX = new int[32];

    private Paint rPaint;

    private Paint wPaint;

    public CandidateView(Context paramContext) {
        super(paramContext);
        this.mSelectionHighlight = paramContext.getResources().getDrawable(17301602);
        this.mSelectionHighlight.setState(new int[] { 16842910, 16842908, 16842909, 16842919 });
        Resources resources = paramContext.getResources();
        setBackgroundColor(resources.getColor(2131230723));
        this.mColorNormal = resources.getColor(2131230720);
        this.mColorRecommended = resources.getColor(2131230721);
        this.mColorOther = resources.getColor(2131230722);
        this.mVerticalPadding = resources.getDimensionPixelSize(2131492867);
        this.mColorWhite = resources.getColor(2131230724);
        this.mColorBlue = resources.getColor(2131230725);
        this.mColorRed = resources.getColor(2131230726);
        this.mPaint = new Paint();
        this.mPaint.setColor(this.mColorNormal);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setTextSize(resources.getDimensionPixelSize(2131492866));
        this.mPaint.setStrokeWidth(0.0F);
        this.wPaint = new Paint();
        this.wPaint.setColor(this.mColorWhite);
        this.wPaint.setStrokeWidth(6.0F);
        this.bPaint = new Paint();
        this.bPaint.setColor(this.mColorBlue);
        this.bPaint.setStrokeWidth(6.0F);
        this.rPaint = new Paint();
        this.rPaint.setColor(this.mColorRed);
        this.rPaint.setStrokeWidth(6.0F);
        this.awPaint = new Paint();
        this.awPaint.setColor(this.mColorWhite);
        this.awPaint.setStrokeWidth(1.0F);
        this.mGestureDetector = new GestureDetector((GestureDetector.OnGestureListener)new GestureDetector.SimpleOnGestureListener() {
            public boolean onScroll(MotionEvent param1MotionEvent1, MotionEvent param1MotionEvent2, float param1Float1, float param1Float2) {
                CandidateView.access$002(CandidateView.this, true);
                int j = (int)(CandidateView.this.getScrollX() + param1Float1);
                int i = j;
                if (j < 0)
                    i = 0;
                j = i;
                if (CandidateView.this.getWidth() + i > CandidateView.this.mTotalWidth)
                    j = (int)(i - param1Float1);
                CandidateView.access$202(CandidateView.this, j);
                CandidateView.this.scrollTo(j, CandidateView.this.getScrollY());
                CandidateView.this.invalidate();
                return true;
            }
        });
        setHorizontalFadingEdgeEnabled(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
    }

    private void removeHighlight() {
        this.mTouchX = -1;
        invalidate();
    }

    private void scrollToTarget() {
        int i = getScrollX();
        if (this.mTargetScrollX > i) {
            int j = i + 20;
            i = j;
            if (j >= this.mTargetScrollX) {
                i = this.mTargetScrollX;
                requestLayout();
            }
        } else {
            int j = i - 20;
            i = j;
            if (j <= this.mTargetScrollX) {
                i = this.mTargetScrollX;
                requestLayout();
            }
        }
        scrollTo(i, getScrollY());
        invalidate();
    }

    public void clear() {
        this.mSuggestions = EMPTY_LIST;
        this.mTouchX = -1;
        this.mSelectedIndex = -1;
        invalidate();
    }

    public int computeHorizontalScrollRange() {
        return this.mTotalWidth;
    }

    protected void onDraw(Canvas paramCanvas) {
        if (paramCanvas != null)
            super.onDraw(paramCanvas);
        this.mTotalWidth = 0;
        if (this.mSuggestions != null) {
            if (this.mBgPadding == null) {
                this.mBgPadding = new Rect(0, 0, 0, 0);
                if (getBackground() != null)
                    getBackground().getPadding(this.mBgPadding);
            }
            int j = 30;
            int k = this.mSuggestions.size();
            int m = getHeight();
            Rect rect = this.mBgPadding;
            Paint paint = this.mPaint;
            int n = this.mTouchX;
            int i1 = getScrollX();
            boolean bool1 = this.mScrolled;
            boolean bool2 = this.mTypedWordValid;
            int i2 = (int)((m - this.mPaint.getTextSize()) / 2.0F - this.mPaint.ascent());
            for (int i = 0; i < k; i++) {
                String str = this.mSuggestions.get(i);
                int i3 = (int)paint.measureText(str) + 20;
                this.mWordX[i] = j;
                this.mWordWidth[i] = i3;
                paint.setColor(this.mColorNormal);
                if (n + i1 >= j && n + i1 < j + i3 && !bool1) {
                    if (paramCanvas != null) {
                        paramCanvas.translate(j, 0.0F);
                        this.mSelectionHighlight.setBounds(0, rect.top, i3, m);
                        this.mSelectionHighlight.draw(paramCanvas);
                        paramCanvas.translate(-j, 0.0F);
                    }
                    this.mSelectedIndex = i;
                }
                if (paramCanvas != null) {
                    if ((i == 1 && !bool2) || (i == 0 && bool2)) {
                        paint.setFakeBoldText(true);
                        paint.setColor(this.mColorRecommended);
                    } else if (i != 0) {
                        paint.setColor(this.mColorOther);
                    }
                    paramCanvas.drawText(str, (j + 10), i2, paint);
                    paint.setColor(this.mColorOther);
                    paramCanvas.drawLine(0.5F + (j + i3), rect.top, 0.5F + (j + i3), (m + 1), paint);
                    paint.setFakeBoldText(false);
                }
                j += i3;
            }
            this.mTotalWidth = j;
            if (this.mTargetScrollX != getScrollX()) {
                scrollToTarget();
                return;
            }
        }
    }

    protected void onMeasure(int paramInt1, int paramInt2) {
        paramInt1 = resolveSize(50, paramInt1);
        Rect rect = new Rect();
        this.mSelectionHighlight.getPadding(rect);
        setMeasuredDimension(paramInt1, resolveSize((int)this.mPaint.getTextSize() + this.mVerticalPadding + rect.top + rect.bottom, paramInt2));
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        if (this.mGestureDetector.onTouchEvent(paramMotionEvent))
            return true;
        int i = paramMotionEvent.getAction();
        int j = (int)paramMotionEvent.getX();
        int k = (int)paramMotionEvent.getY();
        this.mTouchX = j;
        switch (i) {
            default:
                return true;
            case 0:
                this.mScrolled = false;
                invalidate();
                return true;
            case 2:
                if (k <= 0 && this.mSelectedIndex >= 0) {
                    this.mService.pickSuggestionManually(this.mSelectedIndex);
                    this.mSelectedIndex = -1;
                }
                invalidate();
                return true;
            case 1:
                break;
        }
        if (!this.mScrolled && this.mSelectedIndex >= 0)
            this.mService.pickSuggestionManually(this.mSelectedIndex);
        this.mSelectedIndex = -1;
        removeHighlight();
        requestLayout();
        return true;
    }

    public void setService(SoftKeyboard paramSoftKeyboard) {
        this.mService = paramSoftKeyboard;
    }

    public void setSuggestions(List<String> paramList, boolean paramBoolean1, boolean paramBoolean2) {
        clear();
        if (paramList != null)
            this.mSuggestions = new ArrayList<String>(paramList);
        this.mTypedWordValid = paramBoolean2;
        scrollTo(0, 0);
        this.mTargetScrollX = 0;
        onDraw((Canvas)null);
        invalidate();
        requestLayout();
    }

    public void takeSuggestionAt(float paramFloat) {
        this.mTouchX = (int)paramFloat;
        onDraw((Canvas)null);
        if (this.mSelectedIndex >= 0)
            this.mService.pickSuggestionManually(this.mSelectedIndex);
        invalidate();
    }
}

