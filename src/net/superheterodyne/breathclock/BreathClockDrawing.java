package net.superheterodyne.breathclock;

import java.util.*;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.DropBoxManager;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.view.MotionEvent;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.os.Handler;

/**
 * Created with IntelliJ IDEA.
 * User: arty
 * Date: 1/13/13
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */

public class BreathClockDrawing {
    private final Handler handler = new Handler();

    static final int refreshTime = 100; // ms
    static final int lagUntilResetTime = 10000; // ms
    static final int fingerWidth = 17;
    static final int userFinger = 25;

    Context context;
    Bitmap frostImage;
    Bitmap originalImage;
    Bitmap whiteImage;
    Random random = new Random();
    private boolean visible = true;
    private boolean touchEnabled;
    SurfaceHolder holder;
    PointF target;
    int width, height, xoff, yoff;
    GenerateFrostImage genImage;
    Runnable drawRunner;
    public BreathClockDrawing(Context context) {
        this.context = context;
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        touchEnabled = prefs.getBoolean("touch", true);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        originalImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.road_9, opts);
        opts.inMutable = true;
        frostImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.road_9, opts);
        whiteImage = frostImage.copy(Bitmap.Config.ARGB_4444, true);
        Canvas whiteCanvas = new Canvas();
        whiteCanvas.setBitmap(whiteImage);
        whiteCanvas.drawColor(0xffD5E1F2);
        genImage = new GenerateFrostImage();
        drawRunner = new Runnable() {
            @Override
            public void run() {
                genImage.run();
                draw(holder);
            }
        };
        handler.post(drawRunner);
    }

    class GenerateFrostImage implements Runnable
    {
        float [][][]drawPath = new float[][][] {
                { // 0
					{ 40f, 5f },
					{ 53f, 8f },
					{ 62f, 21f },
					{ 67f, 50f },
					{ 62f, 79f },
					{ 53f, 82f },
					{ 40f, 95f },
					{ 27f, 82f },
					{ 18f, 79f },
					{ 13f, 50f },
					{ 18f, 21f },
					{ 27f, 8f },
					{ 40f, 5f }
                },
                { // 1
					{ 40f, 10f },
					{ 50f, 0f },
					{ 50f, 100f },
					null,
					{ 0f, 100f },
					{ 100f, 100f },
                },
                { // 2
					{ 10f, 26f },
					{ 14f, 14f },
					{ 25f, 6f },
					{ 36f, 5f },
					{ 50f, 9f },
					{ 59f, 17f },
					{ 62f, 28f },
					{ 59f, 42f },
					{ 44f, 58f },
					{ 18f, 83f },
					{ 8f, 100f },
					{ 65f, 100f },
                },
                { // 3
					{ 7f, 22f },
					{ 15f, 11f },
					{ 28f, 6f },
					{ 38f, 6f },
					{ 50f, 13f },
					{ 56f, 27f },
					{ 50f, 40f },
					{ 40f, 47f },
					{ 29f, 50f },
					{ 43f, 51f },
					{ 56f, 60f },
					{ 60f, 74f },
					{ 57f, 87f },
					{ 45f, 99f },
					{ 32f, 101f },
					{ 16f, 97f },
					{ 9f, 88f },
					{ 6f, 82f },
                },
                { // 4
					{ 67f, 74f },
					{ 6f, 74f },
					{ 53f, 6f },
					{ 53f, 100f },
                },
                { // 5
					{ 62f, 8f },
					{ 20f, 8f },
					{ 10f, 52f },
					{ 24f, 44f },
					{ 38f, 41f },
					{ 53f, 47f },
					{ 62f, 59f },
					{ 64f, 71f },
					{ 59f, 87f },
					{ 49f, 97f },
					{ 35f, 102f },
					{ 19f, 97f },
					{ 9f, 82f },
                },
                { // 6
					{ 62f, 22f },
					{ 56f, 12f },
					{ 39f, 6f },
					{ 25f, 8f },
					{ 15f, 20f },
					{ 9f, 42f },
					{ 10f, 74f },
					{ 15f, 89f },
					{ 25f, 98f },
					{ 38f, 102f },
					{ 51f, 98f },
					{ 59f, 89f },
					{ 64f, 71f },
					{ 60f, 53f },
					{ 50f, 46f },
					{ 39f, 43f },
					{ 24f, 48f },
					{ 16f, 56f },
					{ 10f, 65f },
                },
                { // 7
					{ 6f, 8f },
					{ 64f, 8f },
					{ 53f, 24f },
					{ 39f, 47f },
					{ 30f, 70f },
					{ 26f, 87f },
					{ 24f, 100f },
                },
                { // 8
					{ 34f, 4f },
					{ 49f, 10f },
					{ 57f, 20f },
					{ 57f, 33f },
					{ 50f, 43f },
					{ 34f, 48f },
					{ 17f, 54f },
					{ 9f, 64f },
					{ 7f, 74f },
					{ 10f, 88f },
					{ 21f, 98f },
					{ 34f, 102f },
					{ 49f, 98f },
					{ 58f, 88f },
					{ 62f, 76f },
					{ 60f, 64f },
					{ 52f, 56f },
					{ 34f, 48f },
					{ 20f, 43f },
					{ 12f, 33f },
					{ 10f, 26f },
					{ 14f, 16f },
					{ 24f, 7f },
					{ 34f, 4f },
                },
                { // 9
					{ 11f, 85f },
					{ 18f, 97f },
					{ 33f, 102f },
					{ 48f, 96f },
					{ 57f, 85f },
					{ 63f, 69f },
					{ 63f, 34f },
					{ 57f, 17f },
					{ 48f, 8f },
					{ 36f, 5f },
					{ 21f, 9f },
					{ 13f, 17f },
					{ 9f, 25f },
					{ 8f, 36f },
					{ 11f, 50f },
					{ 14f, 55f },
					{ 23f, 60f },
					{ 35f, 64f },
					{ 47f, 60f },
					{ 58f, 49f },
					{ 63f, 34f }
                },
                { // :
					{  0.0f, -0.25f },
					null,
					{  0.0f,  0.25f }
                },
                { // A
					{ 0f,  100f },
					{ 50f, 0f },
					{ 100f, 100f },
					null,
					{ 25f, 50f },
					{ 75f, 50f }
                },
                { // P
					{ 8f, 40f },
					{ 8f, 7f },
					{ 21f, 8f },
					{ 24f, 10f },
					{ 26f, 15f },
					{ 24f, 22f },
					{ 19f, 25f },
					{ 8f, 26f },
                },
                { // ' '

                },
                { // ...
                        { -0.3f,  0.5f },
                        null,
                        {  0.0f,  0.5f },
                        null,
                        {  0.3f,  0.5f }
                }
        };

        int getCharOffset(char ch) {
            String chars = "0123456789:APM ";
            int idx = chars.indexOf(ch);
            if (idx == -1)
                return chars.length();
            else
                return idx;
        }

        int whiteReplaceSteps;
        PointF target = null, toward = null, momentum = null;
        int steps;
        ArrayList<PointF> currentPath = new ArrayList<PointF>();
        PointF where;
        String timeText = "waiting...";
        Canvas c = new Canvas();
        long drawnLast = System.currentTimeMillis();

        public GenerateFrostImage() {
            c.setBitmap(frostImage);
            // Normalize digit space
            for (int i = 0; i < drawPath.length; i++) {
                if (drawPath[i].length == 0)
                    continue;
                float lowx = drawPath[i][0][0], highx = drawPath[i][0][0], lowy = drawPath[i][0][1], highy = drawPath[i][0][1];
                for (int j = 1; j < drawPath[i].length; j++) {
                    if (drawPath[i][j] == null)
                        continue;
                    lowx = Math.min(lowx, drawPath[i][j][0]);
                    highx = Math.max(highx, drawPath[i][j][0]);
                    lowy = Math.min(lowy, drawPath[i][j][1]);
                    highy = Math.max(highy, drawPath[i][j][1]);
                }
                float scalex = highx - lowx;
                float scaley = highy - lowy;
                for (int j = 0; j < drawPath[i].length; j++) {
                    if (drawPath[i][j] == null)
                        continue;
                    drawPath[i][j][0] = (drawPath[i][j][0] / scalex) - 0.5f;
                    drawPath[i][j][1] = (drawPath[i][j][1] / scaley) - 0.5f;
                }
            }
        }

        void fingerOnWindow(PointF target, int fingerWidth, Paint pclear) {
            Rect transferRect =
                    new Rect((int) target.x, (int) target.y, (int) target.x + fingerWidth, (int) target.y + fingerWidth);
            pclear.setAlpha(255);
            c.drawBitmap(originalImage, transferRect, transferRect, pclear);
        }

        void refreshDatePath(long currentTime) {
            Date currentDate = new Date(currentTime);
            int hour;
            boolean pm = false;
            if (currentDate.getHours() > 12) {
                pm = true;
                hour = currentDate.getHours() - 12;
            } else if (currentDate.getHours() == 12) {
                pm = true;
                hour = 12;
            } else if (currentDate.getHours() == 0) {
                pm = false;
                hour = 12;
            } else {
                pm = false;
                hour = currentDate.getHours();
            }
            timeText = "" + hour + ":" + String.format("%02d", currentDate.getMinutes()) + ":" + String.format("%02d", currentDate.getSeconds()) + (pm ? "P" : "A");
            setTime(timeText);
        }

        public void setTime(String timeString) {
            currentPath.clear();
            RectF stringRect = new RectF(0, 0, width * 5.0f / 6.0f, height / 4.0f);
            stringRect.offset((originalImage.getWidth() - stringRect.width()) / 2, (originalImage.getHeight() - stringRect.height()) / 2);
            float w = stringRect.width() / timeString.length();
            RectF charRect = new RectF(stringRect.left, stringRect.top, stringRect.left + (stringRect.width() / ((timeString.length() * 3) / 2)), stringRect.top + stringRect.height());
            for (int i = 0; i < timeString.length(); i++) {
                int id = getCharOffset(timeString.charAt(i));
                for (int j = 0; j < drawPath[id].length; j++) {
                    if (drawPath[id][j] == null)
                        currentPath.add(null);
                    else {
                        PointF pf = new PointF(drawPath[id][j][0], drawPath[id][j][1]);
                        currentPath.add(new PointF(((0.5f + pf.x) * charRect.width()) + charRect.left, ((0.5f + pf.y) * charRect.height()) + charRect.top));
                    }
                }
                charRect.offset(w, 0);
                currentPath.add(null);
            }
        }

        public void run() {
            Paint pclear = new Paint();
            pclear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            pclear.setShader(new BitmapShader(originalImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

            int x = random.nextInt(10) - 5;
            int y = random.nextInt(10) - 5;
            pclear.setAlpha(3);
            c.drawBitmap(whiteImage, 0, 0, pclear);
            pclear.setAlpha(1);
            c.drawBitmap(frostImage, x, y, pclear);
            if (whiteReplaceSteps < 75) {
                whiteReplaceSteps++;
                if (whiteReplaceSteps == 75)
                    whiteImage = frostImage.copy(Bitmap.Config.ARGB_8888, false);
                return;
            }

            if (xoff == -1 || yoff == -1)
                return;

            long currentTime = System.currentTimeMillis();

            if (currentPath.size() == 0 || currentTime - drawnLast > lagUntilResetTime) {
                steps = 0;
                target = null;
                toward = null;
                momentum = null;
                refreshDatePath(currentTime);
            }
            drawnLast = currentTime;
            if (target == null || steps == 0) {
                target = currentPath.get(0);
                currentPath.remove(0);
                if (target != null) {
                    momentum = new PointF(0,0);
                    toward = new PointF(target.x, target.y);
                    steps = 1;
                    if (currentPath.size() > 0 && currentPath.get(0) != null) {
                        toward = currentPath.get(0);
                        double dx = toward.x - target.x;
                        double dy = toward.y - target.y;
                        double dist = Math.sqrt((dx * dx) + (dy * dy));
                        double step = dist / 10.0f;
                        momentum = new PointF((float)((toward.x - target.x) / step), (float)((toward.y - target.y) / step));
                        steps = (int)step;
                    }
                }
            } else {
                target.x += momentum.x;
                target.y += momentum.y;
                steps--;
                fingerOnWindow(target, fingerWidth, pclear);
            }
        }


    }

    public void onVisibilityChanged(boolean visible) {
        this.visible = visible;
        if (visible) {
            handler.post(drawRunner);
        } else {
            handler.removeCallbacks(drawRunner);
        }
    }

    public void onSurfaceDestroyed(SurfaceHolder holder) {
        this.visible = false;
        handler.removeCallbacks(drawRunner);
    }

    public void onSurfaceChanged(SurfaceHolder holder, int format,
                                 int width, int height) {
        this.holder = holder;
        xoff = -1;
        yoff = -1;
    }

    public void onTouchEvent(SurfaceHolder holder, MotionEvent event) {
        if (touchEnabled && xoff != -1 && yoff != -1) {
            PointF target = new PointF(event.getX() + xoff, event.getY() + yoff);
            Paint pclear = new Paint();
            pclear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            pclear.setShader(new BitmapShader(originalImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            genImage.fingerOnWindow(target, userFinger, pclear);
        }
    }

    public void draw(SurfaceHolder holder) {
        Canvas canvas = null;
        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                xoff = (frostImage.getWidth() - canvas.getWidth()) / 2;
                yoff = (frostImage.getHeight() - canvas.getHeight()) / 2;
                width = canvas.getWidth();
                height = canvas.getHeight();
                canvas.clipRect(new Rect(0,0,canvas.getWidth(), canvas.getHeight()), Region.Op.REPLACE);
                canvas.drawBitmap(frostImage, -xoff, -yoff, new Paint());
            }
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage() + ": " + canvas);
        } finally {
            if (canvas != null)
                holder.unlockCanvasAndPost(canvas);
        }
        handler.removeCallbacks(drawRunner);
        if (visible) {
            handler.postDelayed(drawRunner, refreshTime);
        }
    }
}
