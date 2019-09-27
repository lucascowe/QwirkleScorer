package keepitsimple.store.qwirklescorer;

import android.content.Context;
import android.widget.LinearLayout;

public class SquareLinearLayout extends LinearLayout {
    public SquareLinearLayout(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int squareLen = Math.min(width, height);

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(squareLen, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(squareLen, MeasureSpec.EXACTLY));
    }

}
