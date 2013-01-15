package net.superheterodyne.breathclock;

import java.util.*;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
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

    class Drip extends Point {
        public boolean active;
        public boolean impotice;
    }

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
    int kernel_size = 5;
    float []frostKernel = new float[100];
    GenerateFrostImage genImage;
    Runnable drawRunner;
    float [][][]drawPath = new float[][][] {
            { // 0
                    { -0.5f, -0.5f },
                    {  0.5f, -0.5f },
                    {  0.5f,  0.5f },
                    { -0.5f,  0.5f },
                    { -0.5f, -0.5f }
            },
            { // 1
                    { -0.1f, -0.4f },
                    {  0.0f, -0.5f },
                    {  0.0f,  0.5f },
                    null,
                    { -0.5f,  0.5f },
                    {  0.5f,  0.5f },
            },
            { // 2
                    { -0.5f, -0.5f },
                    {  0.5f, -0.5f },
                    {  0.5f,  0.0f },
                    { -0.5f,  0.0f },
                    { -0.5f,  0.5f },
                    {  0.5f,  0.5f }
            },
            { // 3
                    { -0.5f, -0.5f },
                    {  0.5f, -0.5f },
                    {  0.5f,  0.5f },
                    { -0.5f,  0.5f },
                    null,
                    { -0.5f,  0.0f },
                    {  0.5f,  0.0f },
            },
            { // 4
                    { -0.5f, -0.5f },
                    { -0.5f,  0.0f },
                    {  0.5f,  0.0f },
                    null,
                    {  0.2f, -0.1f },
                    {  0.2f,  0.5f }
            },
            { // 5
                    {  0.5f, -0.5f },
                    { -0.5f, -0.5f },
                    { -0.5f,  0.0f },
                    {  0.5f,  0.0f },
                    {  0.5f,  0.5f },
                    { -0.5f,  0.5f }
            },
            { // 6
                    {  0.5f, -0.5f },
                    { -0.5f, -0.5f },
                    { -0.5f,  0.5f },
                    {  0.5f,  0.5f },
                    {  0.5f,  0.0f },
                    { -0.5f,  0.0f }
            },
            { // 7
                    { -0.5f, -0.5f },
                    {  0.5f, -0.5f },
                    {  0.0f,  0.5f }
            },
            { // 8
                    { -0.5f, -0.5f },
                    {  0.5f, -0.5f },
                    { -0.5f,  0.5f },
                    {  0.5f,  0.5f },
                    { -0.5f, -0.5f }
            },
            { // 9
                    {  0.5f,  0.0f },
                    { -0.5f,  0.0f },
                    { -0.5f, -0.5f },
                    {  0.5f, -0.5f },
                    {  0.5f,  0.5f },
                    { -0.5f,  0.5f }
            },
            { // :
                    {  0.0f, -0.25f },
                    null,
                    {  0.0f,  0.25f }
            },
            { // A
                    { -0.5f,  0.5f },
                    {  0.0f, -0.5f },
                    {  0.5f,  0.5f },
                    null,
                    { -0.25f, 0.0f },
                    {  0.25f, 0.0f }
            },
            { // P
                    { -0.5f,  0.5f },
                    { -0.5f, -0.5f },
                    {  0.5f, -0.5f },
                    {  0.5f,  0.0f },
                    { -0.5f,  0.0f }
            },
            { // M
                    { -0.5f,  0.5f },
                    { -0.25f,-0.5f },
                    {  0.0f,  0.5f },
                    {  0.25f,-0.5f },
                    {  0.5f,  0.5f }
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
        int whiteReplaceSteps;
        PointF target = null, toward = null, momentum = null;
        int steps;
        ArrayList<PointF> currentPath = new ArrayList<PointF>();
        PointF where;
        String timeText = "waiting...";
        Canvas c = new Canvas();

        public GenerateFrostImage() {
            c.setBitmap(frostImage);
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
            Paint pfrost = new Paint(), pclear = new Paint(), pa = new Paint();
            pfrost.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            pfrost.setShader(new BitmapShader(originalImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            pclear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            pclear.setShader(new BitmapShader(originalImage, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

            for (int i = 0; !Thread.currentThread().isInterrupted() && steps % 10 == 0 && i < 1; i++) {
                int x = random.nextInt(10) - 5;
                int y = random.nextInt(10) - 5;
                pfrost.setAlpha(3);
                c.drawBitmap(whiteImage, 0, 0, pfrost);
                pfrost.setAlpha(1);
                c.drawBitmap(frostImage, x, y, pfrost);
                if (whiteReplaceSteps < 100) {
                    whiteReplaceSteps++;
                    if (whiteReplaceSteps == 100)
                        whiteImage = frostImage.copy(Bitmap.Config.ARGB_8888, false);
                    return;
                }
            }

            if (xoff == -1 || yoff == -1)
                return;

            if (currentPath.size() == 0) {
                steps = 0;
                target = null;
                toward = null;
                momentum = null;
                Date currentDate = new Date(System.currentTimeMillis());
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
                pclear.setAlpha(255);
                c.drawBitmap
                        (originalImage,
                                new Rect((int) target.x, (int) target.y, (int) target.x + 20, (int) target.y + 20),
                                new Rect((int) target.x, (int) target.y, (int) target.x + 20, (int) target.y + 20), pclear);
//                    pa.setStrokeWidth(5.0f);
//                    pa.setColor(Color.RED);
//                    c.drawLine(target.x, target.y, toward.x, toward.y, pa);
//                    c.drawRect(new Rect((int) target.x, (int) target.y, (int) target.x + 5, (int) target.y + 5), pclear);
//                    c.drawText(timeText, (float)originalImage.getWidth() / 2.0f, (float)originalImage.getHeight() / 2.0f, pclear);
                target.x += /*(2.0f * random.nextFloat() */ momentum.x;
                target.y += /*(2.0f * random.nextFloat() */ momentum.y;
                steps--;
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
        if (xoff != -1 && yoff != -1) {
            target = new PointF(event.getX() + xoff, event.getY() + yoff);

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
            handler.postDelayed(drawRunner, 100);
        }
    }
}
