# TryGitHub
>在使用EditText的时候，发现想要添加一个清除文本的按钮还是听麻烦的，于是诞生出想自定一个控件来实现这样的效果。
好了，先来看看实现的效果吧。

![effect.gif](http://upload-images.jianshu.io/upload_images/2178834-aa12305e5ab5a869.gif?imageMogr2/auto-orient/strip)

通过效果图，我们可以看到我们的自定义EditText实现了以下的公呢个：
* 当有内容输入的时候，右边会出现一个红色的删除图片。点击图片就会清除文本的内容；
* 当文本框中没有内容的时候，右边的xx会消失；
* 当我们输入不合法的数据时，文本框会震动会提示输入错误。
* 当输入合法的数据时，文本空不会有提示。
基本上实现的功能就这些了把。下面先讲讲怎么使用。
## 1. 直接添加依赖
因为我将这个控件已经打包放到Bintray上了，所以可以通过添加依赖的方式添加到自己的项目中。具体操作如下：
 1. 在项目的==build.gradle==添加依赖
```
 maven { url 'https://hut.bintray.com/Maven' }
```
2. 在要使用的module的build.gradle上添加依赖：
```
compile 'reoger.hut.com.mylibrary:mylibrary:1.0.1'
```

然后就可以在项目中进行引用了。
因为是自定义的控件，所以是放在xml文件中进行引用。示例如下：
```
 <reoger.hut.com.mylibrary.CheckEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:drawableRight="@drawable/x"
        app:errorMsg="请输入数字"
        app:openShake="true"
        app:matchType="@string/checkPhoneNumber"/>
```
如果你的xml文件中没有定义app的话，需要在根部局添加如下的代码，以便导入我的自定义属性：
```
xmlns:app="http://schemas.android.com/apk/res-auto"
```
下面对上面自定义的属性进行说明：
- matchType 属性定义的是匹配的规则（正则表达式），我找了三个比较常用的匹配规则，包括检验是否为手机号码、电子邮箱和RUL地址，如果这个属性不定义的话，下面两个属性都将没有作用
- errorMsg 属性定义的是当输入错误时，提示的错误信息。
- openShake 属性是当输入信息不合法时，是否震荡提示，true表示开启震荡提示
- drawableRight 属性定义的是右边图片的样式。

最后在使用的时候，提供CheckEditText.dataLegality 这个变量来表示数据是否合法。为true时表示合法，false表示不合法。

## 2.下载library包，添加依赖
首先贴上我的[github](https://github.com/Reoger/TryGitHub)地址,下载myLibrary包，导入到项目中，就可以直接使用了。使用方法同方法1.

## 3. 直接贴代码：
简单起见，我直接贴上主要的代码了，代码中有部分注释，相信理解起来很简单：
```
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
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}

```
然后在values文件加下新建attrs文件，代码如下：
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <declare-styleable name="CheckEditText">
        <attr name="matchType" format="string" />
        <attr name="errorMsg" format="string" />
        <attr name="openShake" format="boolean"/>
    </declare-styleable>
</resources>
```
在strings添加如下的代码：
```
<resources>
    <string name="checkPhoneNumber">^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$</string>
    <string name="checkEmail">^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$</string>
    <string name="checkURL">((http|ftp|https)://)(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9\\&%_\\./-~-]*)?</string>
</resources>
```
如此一来，也就完全的实现了自定义的功效。使用方法还是同方法1。

（ps，这个自定义的控件还是有一个小问题，因为采用的是 当控件失去焦点时去判断里面的内容是否合法，当没有其他控件能让这个控件去失去焦点的时候，就无法进行判断是否合法的逻辑。目前找到的方式是在父布局中分发点击事件，但是如此就无法完全达到将此控件独立出来简单使用的目的、如果有什么好方法，还请多多指教！）。
