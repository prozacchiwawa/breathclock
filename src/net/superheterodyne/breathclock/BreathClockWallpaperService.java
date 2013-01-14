package net.superheterodyne.breathclock;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.service.wallpaper.WallpaperService;

/**
 * Created with IntelliJ IDEA.
 * User: arty
 * Date: 1/13/13
 * Time: 10:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class BreathClockWallpaperService extends WallpaperService {
    @Override
    public WallpaperService.Engine onCreateEngine() {
        return new Engine();
    }

    class Engine extends WallpaperService.Engine {
        BreathClockDrawing drawing;

        public void onCreate(android.view.SurfaceHolder surfaceHolder) {
            drawing = new BreathClockDrawing(BreathClockWallpaperService.this);
        }

        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            drawing.onVisibilityChanged(visible);
        }

        public void onTouchEvent(android.view.MotionEvent event) {
            super.onTouchEvent(event);
            drawing.onTouchEvent(getSurfaceHolder(), event);
        }

        public void onSurfaceChanged(android.view.SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            drawing.onSurfaceChanged(holder, format, width, height);
        }

        public void onSurfaceDestroyed(android.view.SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            drawing.onSurfaceDestroyed(holder);
        }
    }
}
