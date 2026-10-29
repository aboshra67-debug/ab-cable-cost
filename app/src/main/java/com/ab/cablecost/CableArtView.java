package com.ab.cablecost;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;

/**
 * DPI-independent original engineering illustration. Rendered from vectors at
 * runtime; no cropped low-resolution previews or copyrighted product photos.
 *
 * Geometry is schematic, not manufacturer-measured 3D CAD. Dragging a 3D mode
 * illustration rotates the exposed conductor configuration on screen.
 */
public final class CableArtView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final VerifiedCableData.Cable cable;
    private final boolean interactive;
    private float rotation=0;
    private float touchX=0;
    private static final int NAVY=0xff0e2239;
    private static final int GOLD=0xffd9a748;
    private static final int COPPER=0xffb9632d;
    private static final int[] WIRES={0xffe3443c,0xff242f39,0xff3d9e70,0xffe9ebee};
    public CableArtView(Context context,VerifiedCableData.Cable cable,boolean interactive){
        super(context);this.cable=cable;this.interactive=interactive;
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        setContentDescription(interactive?"3D schematic: drag to rotate the illustrative cable":
            "Schematic cutaway illustrating the cable construction layers");
    }
    private void color(int c){p.reset();p.setAntiAlias(true);p.setColor(c);p.setStyle(Paint.Style.FILL);}
    private void gradient(int x0,int y0,int x1,int y1,int[] stops){
        color(stops[0]);p.setShader(new LinearGradient(x0,y0,x1,y1,stops,null,Shader.TileMode.CLAMP));
    }
    private void circle(Canvas c,float x,float y,float r,int fill) {
        color(fill);c.drawCircle(x,y,r,p);
    }
    private void stroke(Canvas c,float ax,float ay,float bx,float by,float width,int col){
        color(col);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeWidth(width);
        c.drawLine(ax,ay,bx,by,p);
    }
    private void round(Canvas c,float l,float t,float r,float b,float rad,int col) {
        color(col);c.drawRoundRect(new RectF(l,t,r,b),rad,rad,p);
    }
    private void coat(Canvas c,float y,int fill){
        // Exposed colour-coded insulated wire extends from jacket opening.
        Path wire=new Path();
        wire.moveTo(70,y-14);wire.cubicTo(137,y-15,225,y-17,332,y-20);
        wire.lineTo(332,y+18);wire.cubicTo(222,y+18,137,y+15,70,y+15);wire.close();
        gradient(60,(int)(y-15),60,(int)(y+20),new int[]{light(fill),fill,dark(fill)});
        c.drawPath(wire,p);p.setShader(null);
        // exposed tinned/copper end with realistic strands
        round(c,40,y-9,105,y+10,9,0xffb26f3a);
        gradient(0,(int)y-10,0,(int)y+10,new int[]{0xfff3c17e,0xffa65c27,0xffe7a365});
        c.drawRoundRect(new RectF(40,y-9,105,y+10),8,8,p);p.setShader(null);
        for(int i=0;i<7;i++){
            float cy=y-7+i*2.3f;
            stroke(c,41,cy,102,cy-2,1.4f,i%2==0?0xfff9d498:0xff99501a);
        }
    }
    private static int light(int c){
        return Color.rgb(Math.min(255,Color.red(c)+40),
            Math.min(255,Color.green(c)+35),Math.min(255,Color.blue(c)+30));
    }
    private static int dark(int c){
        return Color.rgb(Color.red(c)*6/10,Color.green(c)*6/10,Color.blue(c)*6/10);
    }
    private void jacket(Canvas c){
        Path outer=new Path();
        outer.moveTo(290,60);outer.cubicTo(410,52,557,62,615,60);
        outer.lineTo(615,241);outer.cubicTo(492,247,391,248,290,241);outer.close();
        gradient(0,60,0,246,new int[]{0xff59687c,0xff101a26,0xff060e1c,0xff293544});
        c.drawPath(outer,p);p.setShader(null);
        // Sleeve bevel and its exposed elliptical section
        gradient(245,0,328,0,new int[]{0xffdbe3ec,0xff596b80,0xff1c2c3d});
        c.drawOval(new RectF(265,60,335,242),p);p.setShader(null);
        color(0xffd4dde5);c.drawOval(new RectF(260,66,327,235),p);
        color(0xff60778a);c.drawOval(new RectF(268,76,323,228),p);
        color(0xffd4e0e8);c.drawOval(new RectF(274,86,320,218),p);
        color(0xff12233a);c.drawOval(new RectF(281,94,318,210),p);
        p.setTextSize(19);p.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        p.setColor(0xffe3e8ee);p.setShadowLayer(2,0,1,0xff111111);
        c.drawText(cable.model.length()>15?cable.model.substring(0,15):cable.model,432,156,p);
        p.clearShadowLayer();
        // metallic foil and drain cross-sectional ends
        stroke(c,281,95,281,212,3,0xffb2c9dd);
        stroke(c,289,98,289,201,1.3f,GOLD);
    }
    private void coax(Canvas c){
        // single solid central core and shields, for RG59
        coat(c,160,0xffdad5c7);
        jacket(c);
        circle(c,301,151,16,0xffc48b45);circle(c,301,151,7,0xfff9d28c);
    }
    private void numbered(Canvas c,float x,float y,int n,int fill){
        color(fill);c.drawCircle(x,y,16,p);
        color(Color.WHITE);p.setTextAlign(Paint.Align.CENTER);
        p.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);p.setTextSize(18);
        c.drawText(String.valueOf(n),x,y+6,p);p.setTextAlign(Paint.Align.LEFT);
    }
    /** Records whose datasheet lists exactly two conductors (one pair). The
     * fan illustration is schematic; this only keeps the conductor count honest. */
    private static boolean isTwoConductor(String id){
        return "BELDEN_5120FN".equals(id)||"BELDEN_9841".equals(id)||"BELDEN_8760".equals(id)
            ||"BELDEN_8761".equals(id)||"BELDEN_3105A".equals(id)||"BELDEN_8451".equals(id);
    }
    private void drawBody(Canvas c){
        if(cable.category.equals("Coax"))coax(c);
        else{
            // Rotate the fan of the conductors in interactive mode.
            int count=isTwoConductor(cable.id)?2:4;
            float[] base=count==2?new float[]{128,188}:new float[]{100,144,186,229};
            float[] ys=new float[count];
            for(int i=0;i<count;i++){
                ys[i]=base[i]+(interactive?(float)Math.sin(rotation+i*1.6f)*18:0);
            }
            // Wires first; outer jacket covers their end sections.
            for(int i=count-1;i>=0;i--){
                int paintColor=WIRES[i%WIRES.length];
                coat(c,ys[i],paintColor);
            }
            jacket(c);
        }
        if(!interactive){
            stroke(c,120,44,150,92,2.3f,GOLD);numbered(c,118,41,1,0xffc64135);
            stroke(c,226,44,252,112,2.3f,GOLD);numbered(c,223,41,2,0xff267cc0);
            stroke(c,321,24,295,95,2.3f,GOLD);numbered(c,322,22,3,0xff278859);
            stroke(c,371,259,300,208,2.3f,GOLD);numbered(c,371,260,4,0xffe18a37);
            stroke(c,516,258,516,207,2.3f,GOLD);numbered(c,516,260,5,0xff606c7a);
        }
    }
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int save=canvas.save();
        canvas.drawColor(0xfff3f6fa);
        float sx=getWidth()/640f,sy=getHeight()/280f;
        canvas.scale(sx,sy);
        // Contrast-safe, infinitely-scalable vector artwork.
        drawBody(canvas);
        canvas.restoreToCount(save);
    }
    @Override public boolean onTouchEvent(MotionEvent event){
        if(!interactive)return super.onTouchEvent(event);
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:touchX=event.getX();return true;
            case MotionEvent.ACTION_MOVE:
                rotation+=(event.getX()-touchX)/45f;
                touchX=event.getX();invalidate();return true;
            case MotionEvent.ACTION_UP:performClick();return true;
            default:return true;
        }
    }
    @Override public boolean performClick(){super.performClick();return true;}
}
