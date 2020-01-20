package com.daasuu.epf;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.daasuu.epf.filter.GlFilter;

import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRAMEBUFFER;


abstract class EFrameBufferObjectRenderer implements GLSurfaceView.Renderer {

    private EFramebufferObject framebufferObject;
    private GlFilter normalShader;

    private final Queue<Runnable> runOnDraw;


    EFrameBufferObjectRenderer() {
        runOnDraw = new LinkedList<Runnable>();
    }


    @Override
    public final void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
        framebufferObject = new EFramebufferObject();
        normalShader = new GlFilter();
        normalShader.setup();
        onSurfaceCreated(config);
    }

//    @Override
//    public final void onSurfaceChanged(final GL10 gl, final int width, final int height) {
//        framebufferObject.setup(width, height);
//        normalShader.setFrameSize(width, height);
//        onSurfaceChanged(width, height);
//    }

    @Override
    public final void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        framebufferObject.setup(width, height);
        normalShader.setFrameSize(width, height);
        onSurfaceChanged(width, height);
    }

    @Override
    public final void onDrawFrame(final GL10 gl) {
        synchronized (runOnDraw) {
            while (!runOnDraw.isEmpty()) {
                runOnDraw.poll().run();
            }
        }

        int screenWidth = EPlayerView.screenwidth; int screenHeight = EPlayerView.screenheight;
        int renderWidth, renderHeight, x0, y0, canvasWidth, canvasHeight;

        framebufferObject.enable();
        if(framebufferObject.getWidth()>screenWidth){
            renderWidth = framebufferObject.getWidth(); x0 = renderWidth - screenWidth; canvasWidth = screenWidth;
        }else{
            renderWidth = screenWidth; x0 = 0; canvasWidth = framebufferObject.getWidth();
        }
        if(framebufferObject.getHeight() > screenHeight){
            renderHeight = framebufferObject.getHeight(); y0 = renderHeight - screenHeight; canvasHeight = screenHeight;
        }else{
            renderHeight = framebufferObject.getHeight(); y0 = 0; canvasHeight = framebufferObject.getHeight(); y0 = 0;
        }

        GLES20.glViewport(0, 0, renderWidth, renderHeight);



        Log.v("buffer size: ",framebufferObject.getWidth()+" "+framebufferObject.getHeight()+" "+renderWidth+" "+renderHeight+" "+canvasWidth+" "+canvasHeight+" "+y0);

        onDrawFrame(framebufferObject);

        GLES20.glBindFramebuffer(GL_FRAMEBUFFER, 0);
        GLES20.glViewport(70, y0, canvasWidth-140, canvasHeight);

        GLES20.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        normalShader.draw(framebufferObject.getTexName(), null);

    }

    @Override
    protected void finalize() throws Throwable {

    }

    public abstract void onSurfaceCreated(EGLConfig config);

    public abstract void onSurfaceChanged(int width, int height);

    public abstract void onDrawFrame(EFramebufferObject fbo);
}
