# ConstraintLayoutAnimation
ConstraintLayout动画实现布局卡片式滑动放大缩放
ConstraintLayout（约束布局）是Android Studio 2.2中主要的新增功能之一，Android studio升级到2.3版本之后，不管是新建Activity或fragment，xml默认布局由RelativeLayout更改为ConstraintLayout了。

但是ConstraintLayout远远比想象中的强大，不仅可以解决布局层层嵌套的缺点，还可以实现动画效果
初学ConstraintLayout动画效果推荐可以看下 ：http://www.uwanttolearn.com/android/constraint-layout-animations-dynamic-constraints-ui-java-hell/

按照以往惯例先上效果图：
![GIF.gif](http://upload-images.jianshu.io/upload_images/3989735-75c24684344ded64.gif?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
上图效果所示 根据手势滑动View  改变View的位置实现方法多种，此处记录下使用ConstraintLayout布局动画实现的过程（此篇笔记仅仅只记录应用demo，不讲解动画实现的原理，如有错误之处还望指正 3Q^_^）：
1.首先保证Android studio升级到2.3版本之后，然后新建项目，编写xml布局(引用android.support.constraint.ConstraintLayout控件)：
```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/colorPrimary"
    android:orientation="vertical"
    tools:context="com.dawnling.app.MainActivity">

    <ImageView
        android:id="@+id/imgBack"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginLeft="8dp"
        android:layout_marginTop="15dp"
        android:layout_marginBottom="15dp"
        app:srcCompat="@mipmap/ic_back" />

    <android.support.constraint.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:id="@+id/main">

        <RelativeLayout
            android:id="@+id/rlTop"
            android:layout_width="0dp"
            android:layout_height="375dp"
            android:background="@drawable/shape_bg_qrcode_transparent"
            app:layout_constraintTop_toTopOf="parent"
            android:layout_marginEnd="16dp"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            android:layout_marginStart="16dp">

            <TextView
                android:id="@+id/tvTitleTop"
                android:layout_width="match_parent"
                android:layout_height="50dp"
                android:background="@drawable/shape_bg_qrcode_top"
                android:textColor="@color/colorTextPrimaryLight"
                android:textSize="16sp"
                android:gravity="center_vertical"
                android:paddingLeft="15dp"
                android:text="会员卡" />

            <RelativeLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_below="@+id/tvTitleTop"
                android:background="@drawable/shape_bg_qrcode_bottom">

             </RelativeLayout>

        </RelativeLayout>

        <TextView
            android:id="@+id/tvSettingPayPws"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:padding="8dp"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"
            android:layout_marginBottom="50dp"
            android:background="@drawable/shap_password_bg"
            android:textColor="@color/white"
            android:text="设置支付密码，保护交易安全"
            android:visibility="invisible"/>

        <RelativeLayout
            android:id="@+id/rlBottom"
            android:layout_width="0dp"
            android:layout_height="400dp"
            android:background="@drawable/shape_bg_qrcode_transparent"
            android:layout_marginRight="16dp"
            app:layout_constraintTop_toBottomOf="@+id/tvSettingPayPws"
            android:layout_marginEnd="16dp"
            android:layout_marginLeft="16dp"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            android:layout_marginStart="16dp">

            <TextView
                android:id="@+id/tvTitleBottom"
                android:layout_width="match_parent"
                android:layout_height="50dp"
                android:background="@drawable/shape_bg_qrcode_top"
                android:textColor="@color/colorTextPrimaryLight"
                android:textSize="16sp"
                android:gravity="center_vertical"
                android:paddingLeft="15dp"
                android:text="付款码" />

            <RelativeLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:layout_below="@+id/tvTitleBottom"
                android:background="@drawable/shape_bg_qrcode_bottom">

            </RelativeLayout>
        </RelativeLayout>

    </android.support.constraint.ConstraintLayout>

</LinearLayout>
```

2.ConstraintLayout 动画要new一个ConstraintSet变量用于存放view的改变状态
```
private ConstraintSet applyConstraintSet = new ConstraintSet();
```

3.接下来看下MainActivity.java代码：
```
/**
 * Created by LXL on 2018/1/17.
 * http://my.csdn.net/lxlyhm
 * https://github.com/LXLYHM
 * http://www.jianshu.com/u/8fd63a0d4c4c
 * ConstraintLayout 动画
 */
public class MainActivity extends AppCompatActivity {

    private int hight;
    private ConstraintLayout constraintLayout;
    private ConstraintSet applyConstraintSet = new ConstraintSet();
    private View rlTop;//会员卡
    private View rlBottom;//付款码
    private int rlBottomHeight;
    private int rlTopHeight;
    private int rlTopWidth;
    private TextView tvSettingPayPws;//设置密码
    private TextView tvTitleTop;
    private int[] location = new int[2] ;//会员卡布局 坐标
    private float y1;
    private float y2;
    private TextView tvTitleBottom;
    private boolean isFirstMeasure = true;//是否是第一次测量
    private boolean isBottom = false;//付款码是否隐藏  是否在底部 true 是  false 不是

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //状态栏全局默认统一成白底黑字
        ImmersionBar.with(this).statusBarColor(R.color.colorPrimary).fitsSystemWindows(true).init();

        hight = ScreenUtils.getScreenHeight();//屏幕高度

        constraintLayout = (ConstraintLayout) findViewById(R.id.main);
        rlTop = findViewById(R.id.rlTop);
        rlBottom = findViewById(R.id.rlBottom);
        tvTitleTop = (TextView) findViewById(R.id.tvTitleTop);
        tvSettingPayPws = (TextView) findViewById(R.id.tvSettingPayPws);
        tvTitleBottom = (TextView) findViewById(R.id.tvTitleBottom);

        getRlTopMeasure();
    }

    /**
     * 获取会员卡 付款码 布局宽高
     */
    private void getRlTopMeasure() {
        rlBottomHeight = rlBottom.getLayoutParams().height;//付款码 高
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        rlTop.measure(w, h);
        rlTopHeight = rlTop.getMeasuredHeight();//会员卡高
    }

    //上滑覆盖
    public void slide(){
        TransitionManager.beginDelayedTransition(constraintLayout);

        //设置 会员卡布局
        applyConstraintSet.constrainWidth(R.id.rlTop, rlTopWidth - 36);//设置变小
        applyConstraintSet.constrainHeight(R.id.rlTop, rlTopHeight);//设置高度不变
        applyConstraintSet.connect(R.id.rlTop, ConstraintSet.TOP, R.id.main,ConstraintSet.TOP, 20);//设置marginTop
        applyConstraintSet.centerHorizontally(R.id.rlTop, R.id.main);

        //设置 付款码布局
        applyConstraintSet.constrainWidth(R.id.rlBottom, rlTopWidth);//设置变小
        applyConstraintSet.constrainHeight(R.id.rlBottom, rlBottomHeight);//设置高度不变
        applyConstraintSet.connect(R.id.rlBottom, ConstraintSet.TOP, R.id.main,ConstraintSet.TOP, (location[1]/2 + 25));//设置marginTop
        applyConstraintSet.centerHorizontally(R.id.rlBottom, R.id.main);

        //设置支付密码
        tvSettingPayPws.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {//延时设置标题栏透明度
                tvTitleTop.setBackgroundResource(R.drawable.shape_bg_qrcode_top_transparent);
                tvTitleBottom.setBackgroundResource(R.drawable.shape_bg_qrcode_top);
            }
        }, 300);

        applyConstraintSet.applyTo(constraintLayout);
    }

    //下滑隐藏
    public void inHiding(){
        TransitionManager.beginDelayedTransition(constraintLayout);

        //设置 会员卡布局
        applyConstraintSet.constrainWidth(R.id.rlTop, rlTopWidth);//设置还原
        applyConstraintSet.constrainHeight(R.id.rlTop, rlTopHeight);//设置高度不变
        applyConstraintSet.connect(R.id.rlTop, ConstraintSet.TOP, R.id.main,ConstraintSet.TOP, 20);//设置marginTop
        applyConstraintSet.centerHorizontally(R.id.rlTop, R.id.main);

        //设置 付款码布局
        applyConstraintSet.constrainWidth(R.id.rlBottom, rlTopWidth);
        applyConstraintSet.constrainHeight(R.id.rlBottom, rlBottomHeight);//设置高度不变
        applyConstraintSet.connect(R.id.rlBottom, ConstraintSet.TOP, R.id.main, ConstraintSet.TOP, hight - location[1] - 50);//设置marginBottom
        applyConstraintSet.centerHorizontally(R.id.rlBottom, R.id.main);

        //设置支付密码
        tvSettingPayPws.setVisibility(View.INVISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {//延时设置标题栏透明度
                tvTitleTop.setBackgroundResource(R.drawable.shape_bg_qrcode_top);
                tvTitleBottom.setBackgroundResource(R.drawable.shape_bg_qrcode_top_transparent);
            }
        }, 300);

        applyConstraintSet.applyTo(constraintLayout);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //继承了Activity的onTouchEvent方法，直接监听点击事件
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            //当手指按下的时候
            y1 = event.getY();
        }
        if(event.getAction() == MotionEvent.ACTION_UP) {
            //当手指离开的时候
            y2 = event.getY();

            if (isFirstMeasure){
                rlTopWidth = rlTop.getWidth();
            }
            isFirstMeasure = false;
            rlTop.getLocationInWindow(location); //获取rlTop在当前窗口内的绝对坐标，含toolBar
            isBottom = !isBottom;

            if(y1 - y2 > 10) {//向上滑动
                slide();
            } else if(y2 - y1 > 10) {//向下滑动
                inHiding();
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
```

Demo传送门：https://github.com/LXLYHM/ConstraintLayoutAnimation

需要kotlin版本的小伙伴可以留言或私信我
