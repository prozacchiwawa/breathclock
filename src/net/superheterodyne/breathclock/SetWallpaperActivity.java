package net.superheterodyne.breathclock;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class SetWallpaperActivity extends Activity {
    BreathClockDrawing drawing;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        SurfaceView view = (SurfaceView)findViewById(R.id.wpdrawing);
        drawing = new BreathClockDrawing(this);
        drawing.onSurfaceChanged(view.getHolder(), 0, view.getWidth(), view.getHeight());
    }

    @Override
    public void onDestroy() {
        SurfaceView view = (SurfaceView)findViewById(R.id.wpdrawing);
        drawing.onSurfaceDestroyed(view.getHolder());
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        SurfaceView view = (SurfaceView)findViewById(R.id.wpdrawing);
        drawing.onTouchEvent(view.getHolder(), event);
        return true;
    }
}
