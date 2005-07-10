package jake2.render;

import jake2.render.lwjgl.LWJGLBase;

import java.nio.*;

public class DummyGL extends LWJGLBase {
    
    protected void glAlphaFunc(int func, float ref) {
        // do nothing
    }

    protected void glBegin(int mode) {
        // do nothing
    }

    protected void glBindTexture(int target, int texture) {
        // do nothing
    }

    protected void glBlendFunc(int sfactor, int dfactor) {
        // do nothing
    }

    protected void glClear(int mask) {
        // do nothing
    }

    protected void glClearColor(float red, float green, float blue, float alpha) {
        // do nothing
    }

    protected void glColor3f(float red, float green, float blue) {
        // do nothing
    }

    protected void glColor3ub(byte red, byte green, byte blue) {
        // do nothing
    }

    protected void glColor4f(float red, float green, float blue, float alpha) {
        // do nothing
    }

    protected void glColor4ub(byte red, byte green, byte blue, byte alpha) {
        // do nothing
    }

    protected void glColorPointer(int size, boolean unsigned, int stride,
            ByteBuffer pointer) {
        // do nothing
    }
    
    protected void glColorPointer(int size, int stride, FloatBuffer pointer) {
        // do nothing
    }

    protected void glCullFace(int mode) {
        // do nothing
    }

    protected void glDeleteTextures(IntBuffer textures) {
        // do nothing
    }

    protected void glDepthFunc(int func) {
        // do nothing
    }

    protected void glDepthMask(boolean flag) {
        // do nothing
    }

    protected void glDepthRange(double zNear, double zFar) {
        // do nothing
    }

    protected void glDisable(int cap) {
        // do nothing
    }

    protected void glDisableClientState(int cap) {
        // do nothing
    }

    protected void glDrawArrays(int mode, int first, int count) {
        // do nothing
    }

    protected void glDrawBuffer(int mode) {
        // do nothing
    }

    protected void glDrawElements(int mode, IntBuffer indices) {
        // do nothing
    }

    protected void glEnable(int cap) {
        // do nothing
    }

    protected void glEnableClientState(int cap) {
        // do nothing
    }

    protected void glEnd() {
        // do nothing
    }

    protected void glFinish() {
        // do nothing
    }

    protected void glFlush() {
        // do nothing
    }

    protected void glFrustum(double left, double right, double bottom,
            double top, double zNear, double zFar) {
        // do nothing
    }

    protected int glGetError() {
        return GL_NO_ERROR;
    }

    protected void glGetFloat(int pname, FloatBuffer params) {
        // do nothing
    }

    protected String glGetString(int name) {
        switch (name) {
        case GL_EXTENSIONS:
            return "GL_ARB_multitexture";
        default:
            return "";
        }
    }

    protected void glInterleavedArrays(int format, int stride,
            FloatBuffer pointer) {
        // do nothing
    }

    protected void glLoadIdentity() {
        // do nothing
    }

    protected void glLoadMatrix(FloatBuffer m) {
        // do nothing
    }

    protected void glMatrixMode(int mode) {
        // do nothing
    }

    protected void glOrtho(double left, double right, double bottom,
            double top, double zNear, double zFar) {
        // do nothing
    }

    protected void glPixelStorei(int pname, int param) {
        // do nothing
    }

    protected void glPointSize(float size) {
        // do nothing
    }

    protected void glPolygonMode(int face, int mode) {
        // do nothing
    }

    protected void glPopMatrix() {
        // do nothing
    }

    protected void glPushMatrix() {
        // do nothing
    }

    protected void glReadPixels(int x, int y, int width, int height,
            int format, int type, ByteBuffer pixels) {
        // do nothing
    }

    protected void glRotatef(float angle, float x, float y, float z) {
        // do nothing
    }

    protected void glScalef(float x, float y, float z) {
        // do nothing
    }

    protected void glScissor(int x, int y, int width, int height) {
        // do nothing
    }

    protected void glShadeModel(int mode) {
        // do nothing
    }

    protected void glTexCoord2f(float s, float t) {
        // do nothing
    }

    protected void glTexCoordPointer(int size, int stride, FloatBuffer pointer) {
        // do nothing
    }

    protected void glTexEnvi(int target, int pname, int param) {
        // do nothing
    }

    protected void glTexImage2D(int target, int level, int internalformat,
            int width, int height, int border, int format, int type,
            ByteBuffer pixels) {
        // do nothing
    }

    protected void glTexImage2D(int target, int level, int internalformat,
            int width, int height, int border, int format, int type,
            IntBuffer pixels) {
        // do nothing
    }

    protected void glTexParameterf(int target, int pname, float param) {
        // do nothing
    }

    protected void glTexParameteri(int target, int pname, int param) {
        // do nothing
    }

    protected void glTexSubImage2D(int target, int level, int xoffset,
            int yoffset, int width, int height, int format, int type,
            IntBuffer pixels) {
        // do nothing
    }

    protected void glTranslatef(float x, float y, float z) {
        // do nothing
    }

    protected void glVertex2f(float x, float y) {
        // do nothing
    }

    protected void glVertex3f(float x, float y, float z) {
        // do nothing
    }

    protected void glVertexPointer(int size, int stride, FloatBuffer pointer) {
        // do nothing
    }

    protected void glViewport(int x, int y, int width, int height) {
        // do nothing
    }

    protected void glColorTable(int target, int internalFormat, int width,
            int format, int type, ByteBuffer data) {
        // do nothing
    }

    protected void glActiveTextureARB(int texture) {
        // do nothing
    }

    protected void glClientActiveTextureARB(int texture) {
        // do nothing
    }

    protected void glPointParameterEXT(int pname, FloatBuffer pfParams) {
        // do nothing
    }

    protected void glPointParameterfEXT(int pname, float param) {
        // do nothing
    }

}
