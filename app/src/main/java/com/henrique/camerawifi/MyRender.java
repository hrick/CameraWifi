package com.henrique.camerawifi;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Log;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyRender implements Renderer {

    public static final float DEFAULT_MAX_SCALE = 6.0f;
    public static final float DEFAULT_MID_SCALE = 3.0f;
    public static final float DEFAULT_MIN_SCALE = 1.0f;
    public static final int TEX_TYPE = 5121;
    public static final int TRANS_DOWN = 1;
    public static final int TRANS_LEFT = 2;
    public static final int TRANS_RIGHT = 3;
    boolean bNeedSleep;
    private boolean isFlip;
    private boolean isMirror;
    int mHeight;
    private final float[] mMVPMatrix;
    int mMVPMatrixHandle;
    private final float[] mProjMatrix;
    private final float[] mRotationMatrix;
    private float mScale;
    private float mTransXFac;
    private float mTransYFac;
    ByteBuffer mUByteBuffer;
    ByteBuffer mVByteBuffer;
    private final float[] mVMatrix;
    int mWidth;
    ByteBuffer mYByteBuffer;
    FloatBuffer positionBuffer;
    final float[] positionBufferData;
    int positionSlot;
    int programHandle;
    int texRangeSlot;
    FloatBuffer textCoodBuffer;
    final float[] textCoodBufferData;
    int[] texture;
    int[] textureSlot;
    int vertexShader;
    byte[] yuvData;
    int yuvFragmentShader;

    public MyRender(GLSurfaceView paramGLSurfaceView) {
        this.mMVPMatrix = new float[16];
        this.mProjMatrix = new float[16];
        this.mVMatrix = new float[16];
        this.mRotationMatrix = new float[16];
        this.mHeight = 0;
        this.mUByteBuffer = null;
        this.mVByteBuffer = null;
        this.mWidth = 0;
        this.mYByteBuffer = null;
        this.positionBuffer = null;
        this.positionSlot = 0;
        this.programHandle = 0;
        this.texRangeSlot = 0;
        this.texture = new int[3];
        this.textureSlot = new int[3];
        this.vertexShader = 0;
        this.yuvFragmentShader = 0;
        this.yuvData = null;
        this.textCoodBuffer = null;
        this.bNeedSleep = true;
        this.isMirror = false;
        this.isFlip = false;
        this.mScale = DEFAULT_MIN_SCALE;
        this.mTransXFac =DEFAULT_MIN_SCALE;
        this.mTransYFac = DEFAULT_MIN_SCALE;
        this.textCoodBufferData = new float[]{0.0f, 0.0f, 0.0f, DEFAULT_MIN_SCALE, 0.0f, DEFAULT_MIN_SCALE, 0.0f, DEFAULT_MIN_SCALE, DEFAULT_MIN_SCALE, 0.0f, 0.0f, DEFAULT_MIN_SCALE, DEFAULT_MIN_SCALE, DEFAULT_MIN_SCALE, 0.0f, DEFAULT_MIN_SCALE};
        this.positionBufferData = new float[]{-1.0f, DEFAULT_MIN_SCALE, 0.0f, DEFAULT_MIN_SCALE, -1.0f, -1.0f, 0.0f, DEFAULT_MIN_SCALE, DEFAULT_MIN_SCALE, DEFAULT_MIN_SCALE, 0.0f, DEFAULT_MIN_SCALE, DEFAULT_MIN_SCALE, -1.0f, 0.0f, DEFAULT_MIN_SCALE};
        paramGLSurfaceView.setEGLContextClientVersion(2);
    }

    public static int compileShader(String paramString, int paramInt) {
        int i = GLES20.glCreateShader(paramInt);
        if (i == 0) {
            return i;
        }
        int[] arrayOfInt = new int[1];
        GLES20.glShaderSource(i, paramString);
        GLES20.glCompileShader(i);
        GLES20.glGetShaderiv(i, 35713, arrayOfInt, 0);
        if (arrayOfInt[0] != 0) {
            return i;
        }
        Log.e("compileShader", "compile shader err:" + GLES20.glGetProgramInfoLog(i));
        GLES20.glDeleteShader(i);
        return 0;
    }

    public long createShaders() {
        String fragmentShaderCode = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf("uniform sampler2D Ytex;\n" + "uniform sampler2D Utex;\n")).append("uniform sampler2D Vtex;\n").toString())).append("precision mediump float;  \n").toString())).append("varying vec4 VaryingTexCoord0; \n").toString())).append("vec4 color;\n").toString())).append("void main()\n").toString())).append("{\n").toString())).append("float yuv0 = (texture2D(Ytex,VaryingTexCoord0.xy)).r;\n").toString())).append("float yuv1 = (texture2D(Utex,VaryingTexCoord0.xy)).r;\n").toString())).append("float yuv2 = (texture2D(Vtex,VaryingTexCoord0.xy)).r;\n").toString())).append("\n").toString())).append("color.r = yuv0 + 1.4022 * yuv2 - 0.7011;\n").toString())).append("color.r = (color.r < 0.0) ? 0.0 : ((color.r > 1.0) ? 1.0 : color.r);\n").toString())).append("color.g = yuv0 - 0.3456 * yuv1 - 0.7145 * yuv2 + 0.53005;\n").toString())).append("color.g = (color.g < 0.0) ? 0.0 : ((color.g > 1.0) ? 1.0 : color.g);\n").toString())).append("color.b = yuv0 + 1.771 * yuv1 - 0.8855;\n").toString())).append("color.b = (color.b < 0.0) ? 0.0 : ((color.b > 1.0) ? 1.0 : color.b);\n").toString())).append("gl_FragColor = color;\n").toString())).append("}\n").toString();
        int[] arrayOfInt = new int[1];
        int i = compileShader(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf("uniform mat4 uMVPMatrix;   \n" + "attribute vec4 vPosition;  \n")).append("attribute vec4 myTexCoord; \n").toString())).append("varying vec4 VaryingTexCoord0; \n").toString())).append("void main(){               \n").toString())).append("VaryingTexCoord0 = myTexCoord; \n").toString())).append("gl_Position = vPosition *uMVPMatrix ; \n").toString())).append("}  \n").toString(), 35633);
        this.vertexShader = i;
        if (i == 0) {
            Log.e("createShaders", "failed when compileShader(vertex)");
        }
        int j = compileShader(fragmentShaderCode, 35632);
        this.yuvFragmentShader = j;
        if (j == 0) {
            Log.e("createShaders", "failed when compileShader(fragment)");
        }
        this.programHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(this.programHandle, this.vertexShader);
        GLES20.glAttachShader(this.programHandle, this.yuvFragmentShader);
        GLES20.glLinkProgram(this.programHandle);
        GLES20.glGetProgramiv(this.programHandle, 35714, arrayOfInt, 0);
        if (arrayOfInt[0] == 0) {
            Log.e("createShaders", "link program err:" + GLES20.glGetProgramInfoLog(this.programHandle));
            destroyShaders();
        }
        this.texRangeSlot = GLES20.glGetAttribLocation(this.programHandle, "myTexCoord");
        this.textureSlot[0] = GLES20.glGetUniformLocation(this.programHandle, "Ytex");
        this.textureSlot[1] = GLES20.glGetUniformLocation(this.programHandle, "Utex");
        this.textureSlot[2] = GLES20.glGetUniformLocation(this.programHandle, "Vtex");
        this.mMVPMatrixHandle = GLES20.glGetUniformLocation(this.programHandle, "uMVPMatrix");
        this.positionSlot = GLES20.glGetAttribLocation(this.programHandle, "vPosition");
        Log.d("aaaaa", "texRangeSlot: " + this.texRangeSlot);
        Log.d("aaaaa", "positionSlot: " + this.positionSlot);
        Log.d("aaaaa", "textureSlot[0]: " + this.textureSlot[0]);
        Log.d("aaaaa", "textureSlot[1]: " + this.textureSlot[1]);
        Log.d("aaaaa", "textureSlot[2]: " + this.textureSlot[2]);
        return 0;
    }

    public long destroyShaders() {
        if (this.programHandle != 0) {
            GLES20.glDetachShader(this.programHandle, this.yuvFragmentShader);
            GLES20.glDetachShader(this.programHandle, this.vertexShader);
            GLES20.glDeleteProgram(this.programHandle);
            this.programHandle = 0;
        }
        if (this.yuvFragmentShader != 0) {
            GLES20.glDeleteShader(this.yuvFragmentShader);
            this.yuvFragmentShader = 0;
        }
        if (this.vertexShader != 0) {
            GLES20.glDeleteShader(this.vertexShader);
            this.vertexShader = 0;
        }
        return 0;
    }

    public int draw(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2, ByteBuffer paramByteBuffer3, int paramInt1, int paramInt2) {
        GLES20.glClear(AccessibilityNodeInfoCompat.ACTION_COPY);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, DEFAULT_MIN_SCALE);
        GLES20.glUseProgram(this.programHandle);
        paramByteBuffer1.position(0);
        GLES20.glActiveTexture(33984);
        loadTexture(this.texture[0], paramInt1, paramInt2, paramByteBuffer1);
        paramByteBuffer2.position(0);
        GLES20.glActiveTexture(33985);
        loadTexture(this.texture[1], paramInt1 >> 1, paramInt2 >> 1, paramByteBuffer2);
        paramByteBuffer3.position(0);
        GLES20.glActiveTexture(33986);
        loadTexture(this.texture[2], paramInt1 >> 1, paramInt2 >> 1, paramByteBuffer3);
        GLES20.glUniform1i(this.textureSlot[0], 0);
        GLES20.glUniform1i(this.textureSlot[1], 1);
        GLES20.glUniform1i(this.textureSlot[2], 2);
        this.positionBuffer.position(0);
        GLES20.glEnableVertexAttribArray(this.positionSlot);
        GLES20.glVertexAttribPointer(this.positionSlot, 4, 5126, false, 0, this.positionBuffer);
        GLES20.glUniformMatrix4fv(this.mMVPMatrixHandle, 1, false, this.mMVPMatrix, 0);
        this.textCoodBuffer.position(0);
        GLES20.glEnableVertexAttribArray(this.texRangeSlot);
        GLES20.glVertexAttribPointer(this.texRangeSlot, 4, 5126, false, 0, this.textCoodBuffer);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glDisableVertexAttribArray(this.positionSlot);
        GLES20.glDisableVertexAttribArray(this.texRangeSlot);
        return 0;
    }

    public int loadTexture(int paramInt1, int paramInt2, int paramInt3, Buffer paramBuffer) {
        GLES20.glBindTexture(3553, paramInt1);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexImage2D(3553, 0, 6409, paramInt2, paramInt3, 0, 6409, TEX_TYPE, paramBuffer);
        return 0;
    }

    public int loadVBOs() {
        this.textCoodBuffer = ByteBuffer.allocateDirect(this.textCoodBufferData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.textCoodBuffer.put(this.textCoodBufferData).position(0);
        this.positionBuffer = ByteBuffer.allocateDirect(this.positionBufferData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.positionBuffer.put(this.positionBufferData).position(0);
        return 0;
    }

    public void setScale(float scale) {
        this.mScale = scale;
    }

    public void translat(float x, float y) {
        this.mTransXFac = x;
        this.mTransYFac = y;
    }

    public void toggleMirror() {
        this.isMirror = !this.isMirror;
    }

    public void toggleFlip() {
        this.isFlip = !this.isFlip;
    }

    public void onDrawFrame(GL10 paramGL10) {
        GLES20.glClear(AccessibilityNodeInfoCompat.ACTION_COPY);
        synchronized (this) {
            if (this.mWidth == 0 || this.mHeight == 0 || this.mYByteBuffer == null || this.mUByteBuffer == null || this.mVByteBuffer == null) {
                return;
            }
            if (this.bNeedSleep) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.bNeedSleep = true;
            Matrix.setLookAtM(this.mVMatrix, 0, 0.0f, 0.0f, DEFAULT_MID_SCALE, 0.0f, 0.0f, -3.0f, 0.0f, DEFAULT_MIN_SCALE, 0.0f);
            Matrix.multiplyMM(this.mMVPMatrix, 0, this.mProjMatrix, 0, this.mVMatrix, 0);
            if (this.isFlip) {
                Matrix.setRotateM(this.mRotationMatrix, 0, 180.0f, 0.0f, DEFAULT_MIN_SCALE, 0.0f);
                Matrix.multiplyMM(this.mMVPMatrix, 0, this.mRotationMatrix, 0, this.mMVPMatrix, 0);
            }
            if (this.isMirror) {
                Matrix.setRotateM(this.mRotationMatrix, 0, 180.0f, DEFAULT_MIN_SCALE, 0.0f, 0.0f);
                Matrix.multiplyMM(this.mMVPMatrix, 0, this.mRotationMatrix, 0, this.mMVPMatrix, 0);
            }
            Matrix.scaleM(this.mMVPMatrix, 0, this.mScale, this.mScale, DEFAULT_MIN_SCALE);
            draw(this.mYByteBuffer, this.mUByteBuffer, this.mVByteBuffer, this.mWidth, this.mHeight);
        }
    }

    public void onSurfaceChanged(GL10 paramGL10, int paramInt1, int paramInt2) {
        GLES20.glViewport(0, 0, paramInt1, paramInt2);
    }

    public void onSurfaceCreated(GL10 paramGL10, EGLConfig paramEGLConfig) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, DEFAULT_MIN_SCALE);
        GLES20.glGenTextures(3, this.texture, 0);
        Matrix.frustumM(this.mProjMatrix, 0, -1.0f, DEFAULT_MIN_SCALE, -1.0f, DEFAULT_MIN_SCALE, DEFAULT_MID_SCALE, 7.0f);
        createShaders();
        loadVBOs();
    }

    public int unloadVBOs() {
        if (this.positionBuffer != null) {
            this.positionBuffer = null;
        }
        return 0;
    }

    int writeSample(byte[] paramArrayOfByte, int width, int height) {
        synchronized (this) {
            if (width == 0 || height == 0) {
                return 0;
            }
            if (!(width == this.mWidth && height == this.mHeight)) {
                this.mWidth = width;
                this.mHeight = height;
                this.mYByteBuffer = ByteBuffer.allocate(this.mWidth * this.mHeight);
                this.mUByteBuffer = ByteBuffer.allocate((this.mWidth * this.mHeight) / 4);
                this.mVByteBuffer = ByteBuffer.allocate((this.mWidth * this.mHeight) / 4);
            }
            if (this.mYByteBuffer != null) {
                this.mYByteBuffer.position(0);
                this.mYByteBuffer.put(paramArrayOfByte, 0, this.mWidth * this.mHeight);
                this.mYByteBuffer.position(0);
            }
            if (this.mUByteBuffer != null) {
                this.mUByteBuffer.position(0);
                this.mUByteBuffer.put(paramArrayOfByte, this.mWidth * this.mHeight, (this.mWidth * this.mHeight) / 4);
                this.mUByteBuffer.position(0);
            }
            if (this.mVByteBuffer != null) {
                this.mVByteBuffer.position(0);
                this.mVByteBuffer.put(paramArrayOfByte, ((this.mWidth * this.mHeight) * 5) / 4, (this.mWidth * this.mHeight) / 4);
                this.mVByteBuffer.position(0);
            }
            this.bNeedSleep = false;
            return 1;
        }
    }
}
