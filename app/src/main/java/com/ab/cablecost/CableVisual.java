package com.ab.cablecost;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.view.View;

/** Original vector-like cable product drawings used only for UI, not datasheet evidence. */
public class CableVisual extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int kind;
    private static Bitmap photoStrip;
    private static Bitmap referenceArtwork;
    private final int navy=Color.rgb(16,34,56), gold=Color.rgb(215,164,75), copper=Color.rgb(207,114,45);
    public CableVisual(Context context,int kind){super(context);this.kind=kind;setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    private void fill(int color){p.reset();p.setAntiAlias(true);p.setColor(color);p.setStyle(Paint.Style.FILL);}
    private void pipe(Canvas c,float x,float y,float r,int insColor){
        fill(Color.rgb(243,245,250)); p.setShadowLayer(3,0,1,0x26000000);c.drawCircle(x,y,r+7,p);p.clearShadowLayer();
        fill(insColor);c.drawCircle(x,y,r+4,p);
        fill(Color.rgb(23,44,67));c.drawCircle(x,y,r,p);
        fill(copper);c.drawCircle(x,y,r*.69f,p);
        fill(Color.rgb(239,178,97));c.drawCircle(x-r*.12f,y-r*.1f,r*.35f,p);
        fill(Color.rgb(172,90,37));for(int i=0;i<7;i++){double a=Math.PI*2*i/7;c.drawCircle(x+(float)Math.cos(a)*r*.45f,y+(float)Math.sin(a)*r*.45f,r*.12f,p);}
    }
    @Override protected void onDraw(Canvas raw){
        super.onDraw(raw);
        int save=raw.save();float w=getWidth(),h=getHeight();
        raw.scale(w/160f,h/110f);Canvas c=raw;
        if(kind>=10 && kind<=16) {
            // Seven photo slices extracted from the user's approved static
            // home mockup. These are artwork, not manufacturer datasheets.
            if(referenceArtwork==null) {
                referenceArtwork=BitmapFactory.decodeResource(
                    getResources(),R.drawable.ab_home_art);
            }
            if(referenceArtwork!=null) {
                int cell=referenceArtwork.getWidth()/7;
                int idx=kind-10;
                Rect src=new Rect(idx*cell,0,(idx+1)*cell,referenceArtwork.getHeight());
                p.reset();p.setAntiAlias(true);p.setFilterBitmap(true);
                c.drawBitmap(referenceArtwork,src,new RectF(0,0,160,110),p);
                raw.restoreToCount(save);
                return;
            }
        }
        // Product pictures are crops of the previously approved design, embedded
        // locally and never presented as technical datasheet evidence.
        if(kind>=1&&kind<=4){
            if(photoStrip==null)photoStrip=BitmapFactory.decodeResource(
                getResources(),R.drawable.ab_product_mini);
            if(photoStrip!=null){
                p.reset();p.setAntiAlias(true);p.setFilterBitmap(true);
                int cell=photoStrip.getWidth()/4;
                Rect src=new Rect((kind-1)*cell,0,kind*cell,photoStrip.getHeight());
                RectF dst=new RectF(4,4,156,106);
                c.drawBitmap(photoStrip,src,dst,p);
                raw.restoreToCount(save);return;
            }
        }
        if(kind==2){ // coax camera RG cable with inner copper and shield
            fill(0xff111d2c);c.drawRoundRect(15,27,149,94,28,28,p);
            fill(0xff8d9daf);c.drawRoundRect(30,35,119,83,22,22,p);
            fill(0xfff7efe0);c.drawRoundRect(41,41,104,77,16,16,p);
            pipe(c,66,59,11,0xffefe7d6);
            p.setColor(gold);p.setStrokeWidth(3);p.setStyle(Paint.Style.STROKE);
            for(int i=0;i<5;i++){c.drawLine(125+i*4,44,145+i*2,70,p);}
        }else if(kind==3){ // fire alarm twin with red flame-proof sheath
            fill(0xffc83532);p.setShadowLayer(8,0,3,0x26000000);c.drawRoundRect(15,25,147,96,31,31,p);p.clearShadowLayer();
            fill(0xfff8d9d0);c.drawCircle(72,60,29,p);
            pipe(c,58,60,13,0xffe63c31);pipe(c,87,60,13,0xff0c2235);
        }else if(kind==4){ // foil shield cable with braid
            fill(0xff142c44);c.drawRoundRect(10,22,149,98,30,30,p);
            fill(0xffbdcad2);c.drawCircle(66,62,38,p);
            fill(0xfff1eee5);c.drawCircle(66,62,32,p);
            fill(0xff455e73);p.setStrokeWidth(1.6f);p.setStyle(Paint.Style.STROKE);
            for(int i=-7;i<8;i++){
                c.drawLine(29+i*4,32,94+i*4,91,p);
                c.drawLine(29+i*4,91,94+i*4,32,p);
            }
            p.setStyle(Paint.Style.FILL);
            pipe(c,53,53,11,0xff336dd4);pipe(c,79,58,11,0xffdc4e38);pipe(c,58,76,10,0xff46a77f);
            fill(gold);c.drawCircle(101,77,3,p);
        }else if(kind==5){ // armoured bigger multi-core
            fill(0xff262f40);c.drawRoundRect(12,19,148,100,34,34,p);
            fill(0xffaab4c3);c.drawCircle(63,60,36,p);
            fill(0xffd8d2c5);c.drawCircle(63,60,30,p);
            pipe(c,44,53,11,0xff314d86);pipe(c,75,48,11,0xffcf4237);pipe(c,67,76,11,0xffd9b04e);
        }else{ // thermal and generic multi-core
            fill(0xffe0e5eb);p.setShadowLayer(6,0,3,0x28000000);c.drawRoundRect(16,20,146,101,34,34,p);p.clearShadowLayer();
            fill(0xfffcfcff);c.drawCircle(64,62,38,p);
            pipe(c,50,48,13,0xff356dc0);pipe(c,80,54,13,0xffac6044);pipe(c,65,80,13,0xffe5b84b);
        }
        raw.restoreToCount(save);
    }
}
