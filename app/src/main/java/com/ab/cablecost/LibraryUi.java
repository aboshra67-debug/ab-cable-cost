package com.ab.cablecost;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Compact, accessible navy/gold visual system shared by library and detail. */
final class LibraryUi {
    static final int NAVY=0xff071b30, MID=0xff102b46, CARD=0xff0e273f,
        GOLD=0xffefc06b, GOLD2=0xffd8a74d, WHITE=0xfff5f8fe,
        MUTED=0xffb5c8da, BORDER=0xff36536e, GREEN=0xff40cc91,
        LIGHT=0xfff3f6fb, INK=0xff102338;
    static int dp(Context c,float n){return (int)(c.getResources().getDisplayMetrics().density*n+.5f);}
    static boolean rtl(Context c){return I18n.index(c)==0;}
    static String tr(Context c,String key){return I18n.t(c,key);}
    static GradientDrawable bg(Context ctx,int col,int rad,int border){
        GradientDrawable d=new GradientDrawable();
        d.setColor(col);d.setCornerRadius(dp(ctx,rad));
        if(border!=0)d.setStroke(dp(ctx,1),border);
        return d;
    }
    static LinearLayout col(Context c){
        LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.VERTICAL);
        l.setLayoutDirection(rtl(c)?View.LAYOUT_DIRECTION_RTL:View.LAYOUT_DIRECTION_LTR);
        return l;
    }
    static LinearLayout row(Context c){
        LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.HORIZONTAL);
        l.setLayoutDirection(rtl(c)?View.LAYOUT_DIRECTION_RTL:View.LAYOUT_DIRECTION_LTR);
        l.setGravity(Gravity.CENTER_VERTICAL);return l;
    }
    static TextView text(Context c,String s,int size,int color,boolean bold){
        TextView t=new TextView(c);t.setText(s);t.setTextSize(size);t.setTextColor(color);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        t.setTextDirection(rtl(c)?View.TEXT_DIRECTION_RTL:View.TEXT_DIRECTION_LTR);
        t.setGravity((rtl(c)?Gravity.RIGHT:Gravity.LEFT)|Gravity.CENTER_VERTICAL);
        return t;
    }
    static TextView centered(Context c,String s,int size,int color,boolean bold){
        TextView t=text(c,s,size,color,bold);t.setGravity(Gravity.CENTER);return t;
    }
    static LinearLayout.LayoutParams lp(Context c,int w,int h){
        return new LinearLayout.LayoutParams(w<0?w:dp(c,w),h<0?h:dp(c,h));
    }
    static void gap(Context c,LinearLayout col,int size){
        col.addView(new View(c),lp(c,1,size));
    }
    static TextView button(Context c,String value,boolean gold,Runnable action){
        TextView t=centered(c,value,14,gold?NAVY:WHITE,true);
        t.setBackground(bg(c,gold?GOLD:NAVY,11,gold?0:BORDER));
        t.setPadding(dp(c,6),dp(c,9),dp(c,6),dp(c,9));
        t.setMinHeight(dp(c,43));
        t.setOnClickListener(v->action.run());
        t.setContentDescription(value);
        return t;
    }
    static void tappable(View view,String desc,Runnable action){
        view.setClickable(true);view.setFocusable(true);view.setContentDescription(desc);
        view.setOnClickListener(v->action.run());
    }
    static TextView chip(Context c,String label,boolean selected,Runnable onPress){
        TextView t=centered(c,label,12,selected?NAVY:WHITE,selected);
        t.setSingleLine(true);
        t.setPadding(dp(c,10),dp(c,7),dp(c,10),dp(c,7));
        t.setBackground(bg(c,selected?GOLD:MID,10,selected?GOLD:BORDER));
        tappable(t,label,onPress);
        return t;
    }
    private LibraryUi(){}
}
