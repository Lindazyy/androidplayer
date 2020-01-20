package com.daasuu.epf.filter;

import android.opengl.GLES20;

import com.daasuu.epf.EglUtil;
import com.daasuu.epf.callbacks.QuardGridFilterCallback;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;



public class GlInteractPreviewFilter extends GlPreviewFilter {

    private int rowIndex;
    private int colIndex;
    private float blxIntervalRow;
    private float blxIntervalCol;

    private int viewNumPerRow;
    private int viewNumPerCol;
    private QuardGridFilterCallback quardGridFilterCallback;


//    public GlInteractPreviewFilter(final int texTarget) {
//        super(texTarget);
//    }

    public GlInteractPreviewFilter(final int texTarget, int viewNumPerRow, int viewNumPerCol, QuardGridFilterCallback quardGridFilterCallback) {
        super(texTarget);
        this.viewNumPerRow = viewNumPerRow;
        this.viewNumPerCol = viewNumPerCol;
        blxIntervalRow = 1.0f / viewNumPerRow;
        blxIntervalCol = 1.0f / viewNumPerCol;
        this.quardGridFilterCallback = quardGridFilterCallback;
        updateViewPort();
    }

    private void updateViewPort(){
        if(quardGridFilterCallback!=null){
            int viewIndex = quardGridFilterCallback.getNewViewIndex();
            rowIndex = viewIndex / viewNumPerCol;
            colIndex = viewIndex % viewNumPerCol;
        }
    }

    public void draw(final int texName, final float[] mvpMatrix, final float[] stMatrix, final float aspectRatio) {
        useProgram();

        GLES20.glUniformMatrix4fv(getHandle("uMVPMatrix"), 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(getHandle("uSTMatrix"), 1, false, stMatrix, 0);
        GLES20.glUniform1f(getHandle("uCRatio"), aspectRatio);

        updateViewPort();

        float ulx = colIndex * blxIntervalCol;
        float uly = 1 - rowIndex * blxIntervalRow;
        float urx = ulx + blxIntervalCol;
        float ury = uly;
        float blx = ulx;
        float bly = uly - blxIntervalRow;
        float brx = urx;
        float bry = bly;

        float[] TmpVerticesData = new float[]{
                // X, Y, Z, U, V
                -1.0f, 1.0f, 0.0f, ulx, uly,
                1.0f, 1.0f, 0.0f, urx, ury,
                -1.0f, -1.0f, 0.0f, blx, bly,
                1.0f, -1.0f, 0.0f, brx, bry
        };

        int vertexBufferName = EglUtil.createBuffer(TmpVerticesData);

        GLES20.glBindBuffer(GL_ARRAY_BUFFER, vertexBufferName);
        GLES20.glEnableVertexAttribArray(getHandle("aPosition"));
        GLES20.glVertexAttribPointer(getHandle("aPosition"), VERTICES_DATA_POS_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_POS_OFFSET);
        GLES20.glEnableVertexAttribArray(getHandle("aTextureCoord"));
        GLES20.glVertexAttribPointer(getHandle("aTextureCoord"), VERTICES_DATA_UV_SIZE, GL_FLOAT, false, VERTICES_DATA_STRIDE_BYTES, VERTICES_DATA_UV_OFFSET);

        GLES20.glActiveTexture(GL_TEXTURE0);
        GLES20.glBindTexture(getTexTarget(), texName);
        GLES20.glUniform1i(getHandle(DEFAULT_UNIFORM_SAMPLER), 0);

        GLES20.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(getHandle("aPosition"));
        GLES20.glDisableVertexAttribArray(getHandle("aTextureCoord"));
        GLES20.glBindBuffer(GL_ARRAY_BUFFER, 0);
        GLES20.glBindTexture(GL_TEXTURE_2D, 0);

        GLES20.glDeleteBuffers(1, new int[]{vertexBufferName}, 0);
        vertexBufferName = 0;

    }

}
