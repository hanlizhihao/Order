package com.hlz.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * 点菜动画
 * Created by Hanlizhi on 2016/10/24.
 */

public class AddGreensAnimator extends View implements ValueAnimator.AnimatorUpdateListener{
    private ImageButton start;
    private int tox;
    private int toy;
    private int fromx;
    private int fromy;
    private ImageView toGoal;
    private AnimatorSet animatorSet;
    // 定义小球的大小的常量
    private static final float BALL_SIZE = 50F;
    // 定义小球从屏幕上方下落到屏幕底端的总时间
    static final float FULL_TIME = 1000;
    public final ArrayList<ShapeHolder> balls
            = new ArrayList<>();

    public AddGreensAnimator(Context context) {
        super(context);
    }

    public void init(ImageButton view, ImageView toGoal){
        this.start=view;
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        this.fromx=location[0];
        this.fromy=location[1];
        this.toGoal=toGoal;
        toGoal.getLocationOnScreen(location);
        this.tox=location[0];
        this.toy=location[1];
    }

    /**
     * 控制ImageButton移动，在x,y两个方向上变化
     */
    public void initView() {
        /**
         * 初始化椭圆
         */
        ShapeHolder newBall = addBall(fromx,fromy);
        ValueAnimator fallyAnim=ObjectAnimator.ofFloat(newBall,"y",fromy,toy);
        fallyAnim.setDuration(700);fallyAnim.setInterpolator(new AccelerateInterpolator());
        ObjectAnimator fallxAnim=ObjectAnimator.ofFloat(newBall,"x",fromx,tox);
        fallxAnim.setDuration(700);fallxAnim.setInterpolator(new LinearInterpolator());
        ObjectAnimator fadeAnim=ObjectAnimator.ofFloat(newBall,"alpha",1f,0f);
        fadeAnim.setDuration(250);
        fadeAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                balls.remove(((ObjectAnimator)animation).getTarget());
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        fallxAnim.addUpdateListener(this);
        fadeAnim.addUpdateListener(this);
        fallyAnim.addUpdateListener(this);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.play(fallxAnim).with(fallyAnim).before(fadeAnim);
        animatorSet.start();
    }
    protected void onDraw(Canvas canvas)
    {
        // 遍历balls集合中的每个ShapeHolder对象
        for (ShapeHolder shapeHolder : balls)
        {
            // 保存canvas的当前坐标系统
            canvas.save();
            // 坐标变换：将画布坐标系统平移到shapeHolder的X、Y坐标处
            canvas.translate(shapeHolder.getX()
                    , shapeHolder.getY());
            // 将shapeHolder持有的圆形绘制在Canvas上
            shapeHolder.getShape().draw(canvas);
            // 恢复Canvas坐标系统
            canvas.restore();
        }
    }
    private ShapeHolder addBall(float x, float y)
    {
        // 创建一个椭圆
        OvalShape circle = new OvalShape();
        // 设置该椭圆的宽、高
        circle.resize(BALL_SIZE, BALL_SIZE);
        // 将椭圆包装成Drawable对象
        ShapeDrawable drawable = new ShapeDrawable(circle);
        // 创建一个ShapeHolder对象
        ShapeHolder shapeHolder = new ShapeHolder(drawable);
        // 设置ShapeHolder的x、y坐标
        shapeHolder.setX(x);
        shapeHolder.setY(y);
        int red = (int) (Math.random() * 255);
        int green = (int) (Math.random() * 255);
        int blue = (int) (Math.random() * 255);
        // 将red、green、blue三个随机数组合成ARGB颜色
//        int color = 0xff000000 + red << 16 | green << 8 | blue;
        int color=0xfffe864b;
        // 获取drawable上关联的画笔
        Paint paint = drawable.getPaint();
        // 将red、green、blue三个随机数除以4得到商值组合成ARGB颜色
        int darkColor = 0xff000000 | red / 4 << 16
                | green / 4 << 8 | blue / 4;
        // 创建圆形渐变
        RadialGradient gradient = new RadialGradient(
                37.5f, 12.5f, BALL_SIZE,color, darkColor
                , Shader.TileMode.CLAMP);
        paint.setShader(gradient);
        // 为shapeHolder设置paint画笔
        shapeHolder.setPaint(paint);
        balls.add(shapeHolder);
        return shapeHolder;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        this.invalidate();
    }
}
