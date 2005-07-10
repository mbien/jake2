package jake2.render;

import java.nio.*;

public class JoglGL extends JoglBase {
    
    protected void glAlphaFunc(int func, float ref) {
        gl.glAlphaFunc(func, ref);
    }

    protected void glBegin(int mode) {
        gl.glBegin(mode);
    }

    protected void glBindTexture(int target, int texture) {
        gl.glBindTexture(target, texture);
    }

    protected void glBlendFunc(int sfactor, int dfactor) {
        gl.glBlendFunc(sfactor, dfactor);
    }

    protected void glClear(int mask) {
        gl.glClear(mask);
    }

    protected void glClearColor(float red, float green, float blue, float alpha) {
        gl.glClearColor(red, green, blue, alpha);
    }

    protected void glColor3f(float red, float green, float blue) {
        gl.glColor3f(red, green, blue);
    }

    protected void glColor3ub(byte red, byte green, byte blue) {
        gl.glColor3ub(red, green, blue);
    }

    protected void glColor4f(float red, float green, float blue, float alpha) {
        gl.glColor4f(red, green, blue, alpha);
    }

    protected void glColor4ub(byte red, byte green, byte blue, byte alpha) {
        gl.glColor4ub(red, green, blue, alpha);
    }

    protected void glColorPointer(int size, boolean unsigned, int stride,
            ByteBuffer pointer) {
        gl.glColorPointer(size, GL_UNSIGNED_BYTE, stride, pointer);
    }
    
    protected void glColorPointer(int size, int stride, FloatBuffer pointer) {
        gl.glColorPointer(size, GL_FLOAT, stride, pointer);
    }

    protected void glCullFace(int mode) {
        gl.glCullFace(mode);
    }

    protected void glDeleteTextures(IntBuffer textures) {
        gl.glDeleteTextures(textures.limit(), textures);
    }

    protected void glDepthFunc(int func) {
        gl.glDepthFunc(func);
    }

    protected void glDepthMask(boolean flag) {
        gl.glDepthMask(flag);
    }

    protected void glDepthRange(double zNear, double zFar) {
        gl.glDepthRange(zNear, zFar);
    }

    protected void glDisable(int cap) {
        gl.glDisable(cap);
    }

    protected void glDisableClientState(int cap) {
        gl.glDisableClientState(cap);
    }

    protected void glDrawArrays(int mode, int first, int count) {
        gl.glDrawArrays(mode, first, count);
    }

    protected void glDrawBuffer(int mode) {
        gl.glDrawBuffer(mode);
    }

    protected void glDrawElements(int mode, IntBuffer indices) {
        gl.glDrawElements(mode, indices.limit(), GL_UNSIGNED_INT, indices);
    }

    protected void glEnable(int cap) {
        gl.glEnable(cap);
    }

    protected void glEnableClientState(int cap) {
        gl.glEnableClientState(cap);
    }

    protected void glEnd() {
        gl.glEnd();
    }

    protected void glFinish() {
        gl.glFinish();
    }

    protected void glFlush() {
        gl.glFlush();
    }

    protected void glFrustum(double left, double right, double bottom,
            double top, double zNear, double zFar) {
        gl.glFrustum(left, right, bottom, top, zNear, zFar);
    }

    protected int glGetError() {
        return gl.glGetError();
    }

    protected void glGetFloat(int pname, FloatBuffer params) {
        gl.glGetFloatv(pname, params);
    }

    protected String glGetString(int name) {
        return gl.glGetString(name);
    }

    protected void glInterleavedArrays(int format, int stride,
            FloatBuffer pointer) {
        gl.glInterleavedArrays(format, stride, pointer);
    }

    protected void glLoadIdentity() {
        gl.glLoadIdentity();
    }

    protected void glLoadMatrix(FloatBuffer m) {
        gl.glLoadMatrixf(m);
    }

    protected void glMatrixMode(int mode) {
        gl.glMatrixMode(mode);
    }

    protected void glOrtho(double left, double right, double bottom,
            double top, double zNear, double zFar) {
        gl.glOrtho(left, right, bottom, top, zNear, zFar);
    }

    protected void glPixelStorei(int pname, int param) {
        gl.glPixelStorei(pname, param);
    }

    protected void glPointSize(float size) {
        gl.glPointSize(size);
    }

    protected void glPolygonMode(int face, int mode) {
        gl.glPolygonMode(face, mode);
    }

    protected void glPopMatrix() {
        gl.glPopMatrix();
    }

    protected void glPushMatrix() {
        gl.glPushMatrix();
    }

    protected void glReadPixels(int x, int y, int width, int height,
            int format, int type, ByteBuffer pixels) {
        gl.glReadPixels(x, y, width, height, format, type, pixels);
    }

    protected void glRotatef(float angle, float x, float y, float z) {
        gl.glRotatef(angle, x, y, z);
    }

    protected void glScalef(float x, float y, float z) {
        gl.glScalef(x, y, z);
    }

    protected void glScissor(int x, int y, int width, int height) {
        gl.glScissor(x, y, width, height);
    }

    protected void glShadeModel(int mode) {
        gl.glShadeModel(mode);
    }

    protected void glTexCoord2f(float s, float t) {
        gl.glTexCoord2f(s, t);
    }

    protected void glTexCoordPointer(int size, int stride, FloatBuffer pointer) {
        gl.glTexCoordPointer(size, GL_FLOAT, stride, pointer);
    }

    protected void glTexEnvi(int target, int pname, int param) {
        gl.glTexEnvi(target, pname, param);
    }

    protected void glTexImage2D(int target, int level, int internalformat,
            int width, int height, int border, int format, int type,
            ByteBuffer pixels) {
        gl.glTexImage2D(target, level, internalformat, width, height, border,
                format, type, pixels);
    }

    protected void glTexImage2D(int target, int level, int internalformat,
            int width, int height, int border, int format, int type,
            IntBuffer pixels) {
        gl.glTexImage2D(target, level, internalformat, width, height, border,
                format, type, pixels);
    }

    protected void glTexParameterf(int target, int pname, float param) {
        gl.glTexParameterf(target, pname, param);
    }

    protected void glTexParameteri(int target, int pname, int param) {
        gl.glTexParameteri(target, pname, param);
    }

    protected void glTexSubImage2D(int target, int level, int xoffset,
            int yoffset, int width, int height, int format, int type,
            IntBuffer pixels) {
        gl.glTexSubImage2D(target, level, xoffset, yoffset, width, height,
                format, type, pixels);
    }

    protected void glTranslatef(float x, float y, float z) {
        gl.glTranslatef(x, y, z);
    }

    protected void glVertex2f(float x, float y) {
        gl.glVertex2f(x, y);
    }

    protected void glVertex3f(float x, float y, float z) {
        gl.glVertex3f(x, y, z);
    }

    protected void glVertexPointer(int size, int stride, FloatBuffer pointer) {
        gl.glVertexPointer(size, GL_FLOAT, stride, pointer);
    }

    protected void glViewport(int x, int y, int width, int height) {
        gl.glViewport(x, y, width, height);
    }

    protected void glColorTable(int target, int internalFormat, int width,
            int format, int type, ByteBuffer data) {
        gl.glColorTable(target, internalFormat, width, format, type, data);
    }

    protected void glActiveTextureARB(int texture) {
        gl.glActiveTextureARB(texture);
    }

    protected void glClientActiveTextureARB(int texture) {
        gl.glClientActiveTextureARB(texture);
    }

    protected void glPointParameterEXT(int pname, FloatBuffer pfParams) {
        gl.glPointParameterfvEXT(pname, pfParams);
    }

    protected void glPointParameterfEXT(int pname, float param) {
        gl.glPointParameterfEXT(pname, param);
    }

}
