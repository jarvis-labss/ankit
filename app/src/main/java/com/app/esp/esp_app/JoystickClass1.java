package com.app.esp.esp_app;

/*
 * Based on the work of @alexorcist <www.akexorcist.com>
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;


public class JoystickClass1 {

    public static final int STICK_NONE = 0;
    public static final int STICK_UP = 1;
    public static final int STICK_UPRIGHT = 2;
    public static final int STICK_RIGHT = 3;
    public static final int STICK_DOWNRIGHT = 4;
    public static final int STICK_DOWN = 5;
    public static final int STICK_DOWNLEFT = 6;
    public static final int STICK_LEFT = 7;
    public static final int STICK_UPLEFT = 8;

    private int OFFSET = 0;

    private ViewGroup mLayout;
    private LayoutParams params;
    private int stick_width, stick_height;

    private int position_x = 0, position_y = 0, min_distance = 0;
    private float distance = 0, angle = 0;

    private DrawCanvas draw;
    private Paint paint;
    private Bitmap stick;

    private boolean touch_state = false;

    public JoystickClass1(Context context, ViewGroup layout, int stick_res_id) {

        stick = BitmapFactory.decodeResource(context.getResources(),
                stick_res_id);

        stick_width = stick.getWidth();
        stick_height = stick.getHeight();

        draw = new DrawCanvas(context);
        paint = new Paint();
        mLayout = layout;
        params = mLayout.getLayoutParams();
    }

    public void drawStick(MotionEvent arg2) {
        position_x = (int) (arg2.getX() - (params.width/2));
        position_y = (int) (arg2.getY() - (params.height/2));
        //noinspection SuspiciousNameCombination: Android Studio is paranoid
        distance = (float) Math.sqrt(Math.pow(position_x, 2) + Math.pow(position_y, 2));
        angle = (float) cal_angle(position_x, position_y);


        if(arg2.getAction() == MotionEvent.ACTION_DOWN) {
            if(distance <= (params.width / 2) - OFFSET) {
                draw.position(arg2.getX(), arg2.getY());
                try {
                    draw();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                touch_state = true;
            }
        } else if(arg2.getAction() == MotionEvent.ACTION_MOVE && touch_state) {
            if(distance <= (params.width / 2) - OFFSET) {
                draw.position(arg2.getX(), arg2.getY());
                try {
                    draw();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if(distance > (params.width / 2) - OFFSET){
                float x = (float) (Math.cos(Math.toRadians(cal_angle(position_x, position_y))) * ((params.width / 2) - OFFSET));
                float y = (float) (Math.sin(Math.toRadians(cal_angle(position_x, position_y))) * ((params.height / 2) - OFFSET));
                x += (params.width / 2);
                y += (params.height / 2);
                draw.position(x, y);
                try {
                    draw();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mLayout.removeView(draw);
            }
        } else if(arg2.getAction() == MotionEvent.ACTION_UP) {
            mLayout.removeView(draw);
            touch_state = false;
        }
    }

    public int getX() {
        if(distance > min_distance && touch_state) {
            if(position_x>-100 && position_x<100) {
                if (position_x < 0) {
                    return -position_x;
                }
                else
                    return position_x;
            }
            else
                return 100;

        }
        return 0;
    }

    public int getY() {
        if(distance > min_distance && touch_state) {
            if(position_y>-100 && position_y<100) {
                if (position_y < 0) {
                    return -position_y;
                }
                else
                    return position_y;
            }
            else
                return 100;

        }
        return 0;
    }

    public float getAngle() {
        if(distance > min_distance && touch_state) {
            return angle;
        }
        return 0;
    }

    public float getDistance() {
        if(distance > min_distance && touch_state) {
            return distance;
        }
        return 0;
    }

    public void setMinimumDistance(int minDistance) {
        min_distance = minDistance;
    }

    public int get8Direction() {
        if(distance > min_distance && touch_state) {
            if(angle >= 247.5 && angle < 292.5 ) {
                return STICK_UP;
            } else if(angle >= 292.5 && angle < 337.5 ) {
                return STICK_UPRIGHT;
            } else if(angle >= 337.5 || angle < 22.5 ) {
                return STICK_RIGHT;
            } else if(angle >= 22.5 && angle < 67.5 ) {
                return STICK_DOWNRIGHT;
            } else if(angle >= 67.5 && angle < 112.5 ) {
                return STICK_DOWN;
            } else if(angle >= 112.5 && angle < 157.5 ) {
                return STICK_DOWNLEFT;
            } else if(angle >= 157.5 && angle < 202.5 ) {
                return STICK_LEFT;
            } else if(angle >= 202.5 && angle < 247.5 ) {
                return STICK_UPLEFT;
            }
        } else if(distance <= min_distance && touch_state) {
            return STICK_NONE;
        }
        return 0;
    }

    public void setOffset(int offset) {
        OFFSET = offset;
    }

    public void setStickAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    public void setLayoutAlpha(int alpha) {
        mLayout.getBackground().setAlpha(alpha);
    }

    public void setStickSize(int width, int height) {
        stick = Bitmap.createScaledBitmap(stick, width, height, false);
        stick_width = stick.getWidth();
        stick_height = stick.getHeight();
    }

    public void setLayoutSize(int width, int height) {
        params.width = width;
        params.height = height;
    }

    private double cal_angle(float x, float y) {
        if(x >= 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x));
        else if(x < 0 && y >= 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if(x < 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 180;
        else if(x >= 0 && y < 0)
            return Math.toDegrees(Math.atan(y / x)) + 360;
        return 0;
    }

    private void draw() throws Exception {
        mLayout.removeView(draw);
        mLayout.addView(draw);
    }

    private class DrawCanvas extends View {
        float x, y;

        private DrawCanvas(Context mContext) {
            super(mContext);
        }

        public void onDraw(Canvas canvas) {
            canvas.drawBitmap(stick, x, y, paint);
        }

        private void position(float pos_x, float pos_y) {
            x = pos_x - (stick_width / 2);
            y = pos_y - (stick_height / 2);
        }
    }
}
