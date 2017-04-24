package com.jelliroo.mallmapbeta.customviews;

/**
 * Created by roger on 2/13/2017.
 */


import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.jelliroo.mallmapbeta.R;
import com.jelliroo.mallmapbeta.bean.DrawPin;
import com.jelliroo.mallmapbeta.bean.MapPin;
import com.jelliroo.mallmapbeta.enums.ClassType;
import com.jelliroo.mallmapbeta.enums.PinType;

import java.util.ArrayList;


public class PinView extends SubsamplingScaleImageView{

    private PointF sPin;

    private String locationLabel;
    ArrayList<MapPin> mapPins = new ArrayList<>();
    ArrayList<DrawPin> drawnPins;
    Context context;
    String tag = getClass().getSimpleName();
    private boolean routeEnabled = false;

    public void setRouteEnabled(boolean routeEnabled) {
        this.routeEnabled = routeEnabled;
    }

    public PinView(Context context) {
        this(context, null);
        this.context = context;
    }

    public PinView(Context context, AttributeSet attr) {
        super(context, attr);
        this.context = context;
        initialise();
    }

    public void setPins(ArrayList<MapPin> mapPins) {
        this.mapPins = mapPins;
        initialise();
        invalidate();
    }

    public void addPin(MapPin mapPin){
        mapPins.add(mapPin);
        initialise();
        invalidate();
    }

    public void setPin(PointF pin) {
        this.sPin = pin;
    }

    public PointF getPin() {
        return sPin;
    }

    public DrawPin getDrawPinAt(int pos){
        return drawnPins.get(pos);
    }

    private void initialise() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        //initialize
        drawnPins = new ArrayList<>();

        //initialize required objects
        Paint paint = new Paint();
        Paint linePaint = new Paint();
        paint.setAntiAlias(true);
        float density = getResources().getDisplayMetrics().densityDpi;


        MapPin previous = null;
        //iterate through the pins
        for (int i = 0; i < mapPins.size(); i++) {
            MapPin mPin = mapPins.get(i);


            //Bitmap bmpPin = Utils.getBitmapFromAsset(context, mPin.getPinImgSrc());
            Bitmap bmpPin;
            if(mPin.getPinType() == PinType.CLASS) {

                if(mPin.getClassType() != null && mPin.getClassType() == ClassType.CLASS){
                    bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.blue_placer);
                } else {
                    bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.flag_placer);
                }


            }
            else {
                bmpPin = BitmapFactory.decodeResource(this.getResources(), R.drawable.red_placer);
            }

            float w = (density / 420f) * bmpPin.getWidth();
            float h = (density / 420f) * bmpPin.getHeight();
            bmpPin = Bitmap.createScaledBitmap(bmpPin, (int) w, (int) h, true);

            PointF vPin = sourceToViewCoord(mPin.getPoint());
            //in my case value of point are at center point of pin image, so we need to adjust it here

            float vX = vPin.x - (bmpPin.getWidth()/2);
            float vY = vPin.y - (bmpPin.getHeight());

            if(routeEnabled) {
                if (i == 0) previous = mPin;
                else {
                    PointF vPin2 = sourceToViewCoord(previous.getPoint());

                    linePaint.setColor(Color.parseColor("#16a085"));
                    linePaint.setStrokeWidth(20 * (density / 420f));
                    canvas.drawLine(vPin.x, vPin.y, vPin2.x, vPin2.y, linePaint);
                    linePaint.setColor(Color.parseColor("#3498db"));
                    linePaint.setStrokeWidth(10 * (density / 420f));
                    canvas.drawLine(vPin.x, vPin.y, vPin2.x, vPin2.y, linePaint);
                    previous = mPin;
                }
            }


            if(mPin.getClassType() == ClassType.HELPER_CLASS && routeEnabled){

            } else {
                canvas.drawBitmap(bmpPin, vX, vY, paint);
                paint.setTextSize(40 * (density / 420f));
                paint.setFakeBoldText(true);


                paint.setColor(Color.BLACK);
                Rect classBounds = new Rect(), textBounds = new Rect();

                if(mPin.getPinType() == PinType.CLASS){
                    paint.getTextBounds(mPin.getClassName(), 0, mPin.getClassName().length(), textBounds);
//
                    float left = 0, right = 0;
                    left = vX - (10) - ((textBounds.width() / 2)) + w - w / 4;
                    right = left + textBounds.width() + (20);
//
//                    Paint rectPaint = new Paint();
//                    rectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//                    rectPaint.setColor(Color.GRAY);
//                    canvas.drawRect(vX - ((density/420f) * 10) - ((density / 420f) * (textBounds.width() / 2)) + w - w / 4, vY + h/3 + ((density / 420f) * 120), (vX - ((density / 420f) * (textBounds.width() / 2)) + w - w / 4) + textBounds.width() + ((density/420f) * 10), (vY + h + ((density / 420f) * 120) + textBounds.height()), rectPaint);
//                    paint.setColor(Color.WHITE);
//                    canvas.drawText(mPin.getClassName(), vX - ((density / 420f) * (textBounds.width() / 2)) + w - w / 4, vY + h + ((density / 420f) * 120), paint);


                    Paint rectPaint = new Paint();
                    rectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    rectPaint.setColor(Color.GRAY);
                    canvas.drawRect(left, vY + h + h/4, right, (vY + h + h/2 + h/2 + textBounds.height()), rectPaint);

                    paint.setColor(Color.WHITE);
                    canvas.drawText(mPin.getClassName(), left + 10, vY + h + h/2 + h/3, paint);

                }
                else {
                    String string = "You are here at";
                    paint.getTextBounds(string, 0, string.length(), textBounds);
                    paint.getTextBounds(mPin.getClassName(), 0, mPin.getClassName().length(), classBounds);
                    paint.setColor(Color.BLACK);




                    float left = 0, right = 0;

                    if(textBounds.width() > classBounds.width()){
                        left = vX - (10) - ((textBounds.width() / 2)) + w - w / 4;
                        right = left + textBounds.width() + (20);

                    } else {
                        left = vX - (10) -((density / 420f) * (classBounds.width() / 2)) + w - w / 4;
                        right = left + classBounds.width() + ((density/420f) * 20);
                    }



                    Paint rectPaint = new Paint();
                    rectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                    rectPaint.setColor(Color.GRAY);
                    canvas.drawRect(left, vY + h + h/4, right, (vY + h + h/2 + h/2 + textBounds.height()), rectPaint);

                    paint.setColor(Color.WHITE);
                    canvas.drawText(string, left + 10, vY + h + h/2 + h/3, paint);
                    canvas.drawText(mPin.getClassName(), left + 10, vY + h + h/2 + h/3 + textBounds.height(), paint);



                }
            }





            //add added pin to an Array list to get touched pin
            if(mPin.getClassType() != ClassType.HELPER_CLASS){
                DrawPin dPin = new DrawPin();
                dPin.setStartX(mPin.getX() - w / 2);
                dPin.setEndX(mPin.getX() + w / 2);
                dPin.setStartY(mPin.getY() - h / 2);
                dPin.setEndY(mPin.getY() + h / 2);
                canvas.drawRect(dPin.getStartX(), dPin.getEndX(), dPin.getStartY(), dPin.getEndY(), paint);
                dPin.setId(mPin.getId());
                dPin.setClassName(mPin.getClassName());
                drawnPins.add(dPin);
            }
        }
    }



    public int getPinPositionByPoint(PointF point) {

        for (int i = drawnPins.size() - 1; i >= 0; i--) {
            DrawPin dPin = drawnPins.get(i);
            if (point.x >= dPin.getStartX() && point.x <= dPin.getEndX()) {
                if (point.y >= dPin.getStartY() && point.y <= dPin.getEndY()) {
                    return i;
                }
            }
        }
        return -1; //negative no means no pin selected
    }

    public void setLocationLabel(String locationLabel) {
        this.locationLabel = locationLabel;
    }



}
