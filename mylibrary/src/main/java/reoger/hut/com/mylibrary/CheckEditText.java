package reoger.hut.com.mylibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 24540 on 2017/3/8.
 *
 */

public class CheckEditText extends EditText implements TextWatcher, View.OnFocusChangeListener {

    //数据是否合法
    public static boolean dataLegality = false;

    //text的文本内容
    private String msg = "";
    private Context mContext;
    /**
     * 是否获取焦点，默认没有焦点
     */
    private boolean mIsFoucse = false;

    //匹配的正则表达式
    private String mMatchString = "";

    //提示的错误信息
    private String mEeorMsg = "";

    /**
     * 左右两侧图片资源
     */
    private Drawable left, right;

    /**
     * 手指抬起时的X坐标
     */
    private int xUp = 0;

    //是否打开震动提示错误
    private boolean isOpenShake = false;


    public CheckEditText(Context context) {
        super(context, null);
    }

    public CheckEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initWedgits();
        initData(attrs);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    public CheckEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }


    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        msg = text.toString();
        if (mIsFoucse) {
            if (TextUtils.isEmpty(text)) {
                // 如果为空，则不显示删除图标
                setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
            } else {
                // 如果非空，则要显示删除图标
                if (null == right) {
                    right = getCompoundDrawables()[2];
                }
                setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);
            }
        }

    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.mIsFoucse = hasFocus;
        if (!mIsFoucse) {
            if (!"".equals(mMatchString)) {
                Pattern p = Pattern
                        .compile(mMatchString);
                Matcher m = p.matcher(msg);
                dataLegality = m.matches();
                if (!dataLegality) {
                    if (!"".equals(mEeorMsg)) {
                        setError(mEeorMsg);
                        if (isOpenShake)
                            setAnimation(shakeAnimation(5));
                    }

                }
            }
        }
    }


    /**
     * 初始化各组件
     */
    private void initWedgits() {
        try {
            left = getCompoundDrawables()[0];
            right = getCompoundDrawables()[2];

            if (length() < 1)
                setCompoundDrawablesWithIntrinsicBounds(left, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initData(AttributeSet attrs) {
        TypedArray mTypedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CheckEditText);
        mMatchString = mTypedArray.getString(R.styleable.CheckEditText_matchType);
        mEeorMsg = mTypedArray.getString(R.styleable.CheckEditText_errorMsg);
        isOpenShake = mTypedArray.getBoolean(R.styleable.CheckEditText_openShake, false);
    }


    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        //设置一个循环加速器，使用传入的次数就会出现摆动的效果。
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(400);
        return translateAnimation;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                // 获取点击时手指抬起的X坐标
                xUp = (int) event.getX();
                // 当点击的坐标到当前输入框右侧的距离小于等于getCompoundPaddingRight()的距离时，则认为是点击了删除图标
                // getCompoundPaddingRight()的说明：Returns the right padding of the view, plus space for the right Drawable if any.
                if ((getWidth() - xUp) <= getCompoundPaddingRight()) {
                    if (!TextUtils.isEmpty(getText().toString())) {
                        setText("");
                    }
                }
                int yUp = (int) event.getY();
                int width = getMeasuredWidth();
                int height = getMeasuredHeight();
                Log.d("TAG",xUp+"::"+yUp);
                Log.d("TAG","width"+width +"::" +height);


                break;
            case MotionEvent.ACTION_OUTSIDE:
                Log.d("TAG","代表点击超出范围");
                setFocusable(false);
                break;
            default:
                break;
        }

        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            Log.d("TAG", "这里代表他失去了加点");
        }

        return super.onTouchEvent(event);
    }


}
