package com.dawnling.app;

import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;
import com.gyf.barlibrary.ImmersionBar;

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
