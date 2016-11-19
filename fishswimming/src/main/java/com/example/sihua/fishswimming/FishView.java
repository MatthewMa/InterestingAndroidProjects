package com.example.sihua.fishswimming;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

/**
 * Created by Sihua on 2016/11/18.
 */
public class FishView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Bitmap[] fishes;
    private Bitmap bg;
    private float fishX=778;
    private float fishY=500;
    private float fishSpeed=6;
    private int fishIndex=0;
    private boolean hasSurface;
    private UpdateThread updateThread;
    private int fishAngle=new Random().nextInt(60);
    Matrix matrix=new Matrix();
    public FishView(Context context) {
        super(context);
        holder=getHolder();
        holder.addCallback(this);
        hasSurface=false;
        bg= BitmapFactory.decodeResource(this.getResources(),R.drawable.fishbg);
        fishes=new Bitmap[10];
        for (int i = 0; i < 10; i++) {
            try {
                int fishId=(Integer)R.drawable.class.getField("fish"+i).get(null);
                Bitmap fish=BitmapFactory.decodeResource(getResources(),fishId);
                fishes[i]=fish;

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    public void resume(){
        if(updateThread==null){
            updateThread=new UpdateThread();
            if(hasSurface==true){
                updateThread.start();
            }
        }
    }

    public void pause(){
        if(updateThread!=null){
            updateThread.requestExitAndWait();
            updateThread=null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        hasSurface=true;
        resume();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(updateThread!=null){
            updateThread.onWindowResize(width,height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface=false;
        pause();
    }

    private class UpdateThread extends Thread {

        private boolean isDone;

        public UpdateThread() {
            super();
            isDone=false;
        }

        @Override
        public void run() {
            SurfaceHolder mholder=holder;
            while(!isDone){
                Canvas canvas = mholder.lockCanvas();
                canvas.drawBitmap(bg,0,0,null);
                if(fishX<0){
                    fishX=778;
                    fishY=500;
                    fishAngle=new Random().nextInt(60);
                }
                if(fishY<0){
                    fishX=778;
                    fishY=500;
                    fishAngle=new Random().nextInt(60);
                }
                matrix.reset();
                matrix.setRotate(fishAngle);
                matrix.postTranslate(fishX-=fishSpeed*Math.cos(Math.toRadians(fishAngle)),
                        fishY-=fishSpeed*Math.cos(Math.toRadians(fishAngle)));
                canvas.drawBitmap(fishes[fishIndex++%fishes.length],matrix,null);
                holder.unlockCanvasAndPost(canvas);
                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

        public void requestExitAndWait() {
            isDone=true;
            try {
                join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void onWindowResize(int width, int height) {
        }
    }
}
