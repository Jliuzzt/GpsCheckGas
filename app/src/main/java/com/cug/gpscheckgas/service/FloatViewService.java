package com.cug.gpscheckgas.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cug.gpscheckgas.R;

public class FloatViewService extends Service {

    private static final String TAG = "FloatViewService";
    // 定义浮动窗口布局
    private LinearLayout mFloatLayout;
    private LayoutParams wmParams;
    // 创建浮动窗口设置布局参数的对象
    private WindowManager mWindowManager;
    public static Button mFloatView;
    static Context context;
    private GestureDetector mGestureDetector;
//	ExceptionReceiver exceptionReceiver = new ExceptionReceiver();

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        context = this;
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
//		exceptionBroadcast(context);
        createFloatView();
//		showException();
    }

    @SuppressWarnings({"static-access", "deprecation"})
    private void createFloatView() {
        wmParams = new LayoutParams();
        // 通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(
                getApplication().WINDOW_SERVICE);
        // 设置window type
        wmParams.type = LayoutParams.TYPE_PHONE;
        // 设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
        // 调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        wmParams.x = 1000;
        wmParams.y = 1000;

        // 设置悬浮窗口长宽数据
        wmParams.width = 100;
        wmParams.height = 150;

        mFloatLayout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
        mFloatLayout.setLayoutParams(params);

        mFloatView = new Button(context);
        mFloatView.setText("一键呼救");
        mFloatView.setTextSize(12f);
        mFloatView.setTextColor(Color.parseColor("#ffffff"));
//        mFloatView.setBackgroundColor(Color.parseColor("#ff5151"));
        mFloatView.setBackgroundResource(R.drawable.floatview_style);
//		mFloatView.setBackgroundDrawable(IDFileUtil.getImageFromAssetsFile(context, "exception.png"));
        mFloatView.setContentDescription(null);
        mFloatLayout.addView(mFloatView);

        // 添加mFloatLayout
        mWindowManager.addView(mFloatLayout, wmParams);
        // 浮动窗口按钮

        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        // 设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        });

    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            // TODO Auto-generated method stub
//			Intent intent = new Intent();
//			intent.setClass(context, ExceptionsActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(intent);
//			if (mFloatLayout != null) {
//				// 移除悬浮窗口
//				mFloatLayout.setVisibility(View.INVISIBLE);
//			}
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // TODO Auto-generated method stub
            wmParams.x = (int) e2.getRawX() - mFloatView.getMeasuredWidth() / 2;
            // 减75为状态栏的高度
            wmParams.y = (int) e2.getRawY() - mFloatView.getMeasuredHeight()
                    / 2 - 75;
            // 刷新
            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (mFloatLayout != null) {
            // 移除悬浮窗口
            mWindowManager.removeView(mFloatLayout);
        }
//		unregisterReceiver(exceptionReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
//
//	public void exceptionBroadcast(Context context) {
//		IntentFilter filter = new IntentFilter();
//		filter.addAction(IDConstants.SHOW_EXCEPTION_BUTTON);
//		filter.addAction(IDConstants.HIDE_EXCEPTION_BUTTON);
//		filter.addAction(IDConstants.EXCEPTIONACTION);
//		LocalBroadcastManager.getInstance(context).registerReceiver(exceptionReceiver, filter);
//	}
//
//	public class ExceptionReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
//			IDLogHelper.d(TAG, "action:" + intent.getAction());
//			if (intent.getAction().equals(IDConstants.EXCEPTIONACTION)) {
//				Toast.makeText(context, "接受广播成功", Toast.LENGTH_SHORT).show(); // 显示悬浮消息
//				showException();
//			} else if (intent.getAction().equals(
//					IDConstants.HIDE_EXCEPTION_BUTTON)) {
//				if (mFloatLayout != null) {
//					// 移除悬浮窗口
//					mFloatLayout.setVisibility(8);
//				}
//			} else if (intent.getAction().equals(
//					IDConstants.SHOW_EXCEPTION_BUTTON)) {
//				if (mFloatLayout != null) {
//					// 显示悬浮窗口
//					mFloatLayout.setVisibility(0);
//				}
//			}
//		}
//	}
//
//	public static void showException() {
//		// 打开数据表
//		List<IDExceptionInfo> exceptionInfo = IDDBHelper.getInstance(context)
//				.getAllExceptionInfo();
//
//		// 显示新消息数字（小红点）
//		BadgeView badgeView = new com.jauker.widget.BadgeView(context);
//		badgeView = new BadgeView(context);
//		badgeView.setTargetView(FloatViewService.mFloatView);
//		badgeView.setBadgeGravity(Gravity.RIGHT | Gravity.TOP);
//		badgeView.setBadgeCount(exceptionInfo.size());
//
//		Log.v("异常信息数", exceptionInfo.size() + "");
//	}
}
