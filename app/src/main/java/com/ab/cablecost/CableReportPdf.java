package com.ab.cablecost;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Exports an original technical summary authored by AB Cable Engineering.
 * Does NOT redistribute manufacturer PDFs or manufacture missing electrical data.
 * Android Storage Access Framework means user chooses the output destination.
 */
public final class CableReportPdf {
    static final int PAGE_W=595, PAGE_H=842, MARGIN=42, BOTTOM=800;
    private final Context context;
    private final PdfDocument document=new PdfDocument();
    private PdfDocument.Page page;
    private Canvas canvas;
    private int pageNo=0;
    private float y;
    private final boolean arabic;
    private final TextPaint p=new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private CableReportPdf(Context context){
        this.context=context;
        this.arabic=I18n.index(context)==0;
    }
    private void newPage(){
        if(page!=null)document.finishPage(page);
        pageNo++;
        page=document.startPage(new PdfDocument.PageInfo.Builder(PAGE_W,PAGE_H,pageNo).create());
        canvas=page.getCanvas();
        canvas.drawColor(Color.WHITE);
        Paint rect=new Paint(Paint.ANTI_ALIAS_FLAG);rect.setColor(0xff0b233d);
        canvas.drawRect(0,0,PAGE_W,83,rect);
        p.setColor(0xffe8b958);p.setTextSize(20);p.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("AB Cable Engineering",MARGIN,40,p);
        p.setColor(Color.WHITE);p.setTextSize(11);p.setTypeface(Typeface.DEFAULT);
        canvas.drawText("Global Cable Library · Source-linked technical report",MARGIN,60,p);
        y=107;
    }
    private void text(String value,int pt,int color,boolean bold){
        if(value==null||value.trim().isEmpty())return;
        p.setTextSize(pt);p.setColor(color);
        p.setTypeface(bold?Typeface.create("sans-serif",Typeface.BOLD):
            Typeface.create("sans-serif",Typeface.NORMAL));
        StaticLayout layout=StaticLayout.Builder.obtain(value,0,value.length(),p,PAGE_W-MARGIN*2)
            .setAlignment(arabic?Layout.Alignment.ALIGN_OPPOSITE:Layout.Alignment.ALIGN_NORMAL)
            .setTextDirection(arabic?TextDirectionHeuristics.RTL:TextDirectionHeuristics.LTR)
            .setIncludePad(true).build();
        if(y+layout.getHeight()+13>BOTTOM)newPage();
        canvas.save();canvas.translate(MARGIN,y);layout.draw(canvas);canvas.restore();
        y+=layout.getHeight()+11;
    }
    private void hr(){
        Paint line=new Paint(Paint.ANTI_ALIAS_FLAG);line.setColor(0xffd7dfe8);
        canvas.drawLine(MARGIN,y,MARGIN+(PAGE_W-MARGIN*2),y,line);
        y+=15;
    }
    private void pair(String key,String value){
        text(key+" : "+(value==null?I18n.t(context,"notPublished"):value),11,0xff223b53,false);
    }
    private void populate(VerifiedCableData.Cable c){
        newPage();
        text(c.brand+"  —  "+c.model,23,0xff0d263f,true);
        text(c.title(arabic),13,0xff334d65,false);
        text(c.size+"  |  "+c.country(arabic)+"  |  "+c.category(arabic),11,0xff54708b,false);
        hr();
        text(I18n.t(context,"technical"),16,0xff0e233a,true);
        pair(I18n.t(context,"conductor"),c.construction);
        pair(I18n.t(context,"voltage"),c.voltage);
        pair(I18n.t(context,"ampacity"),c.current);
        pair(I18n.t(context,"resistanceValue"),c.conductorResistance);
        pair(I18n.t(context,"nominalDiameter"),c.diameter);
        pair(I18n.t(context,"temperature"),c.temperature);
        hr();
        text(I18n.t(context,"diagram"),16,0xff0e233a,true);
        for(int i=0;i<c.layers.size();i++){
            VerifiedCableData.Layer layer=c.layers.get(i);
            text((i+1)+".  "+layer.name(arabic),12,0xffa47422,true);
            text(layer.description(arabic),11,0xff223b53,false);
        }
        hr();
        text(I18n.t(context,"applications"),15,0xff0e233a,true);
        text(c.uses(arabic),11,0xff223b53,false);
        text(I18n.t(context,"standards"),15,0xff0e233a,true);
        text(c.standards,11,0xff223b53,false);
        hr();
        text(I18n.t(context,"sourceDetails"),15,0xff0e233a,true);
        text(c.sourceName+"  |  "+c.revision,10,0xff223b53,false);
        text(c.sourceUrl,9,0xff2462aa,false);
        text(I18n.t(context,"dataDisclaimer"),10,0xffaa3e27,false);
        text(I18n.t(context,"technicalDiagramNote"),9,0xff66758c,false);
    }
    public static void export(Context context,VerifiedCableData.Cable cable,Uri dest)
            throws IOException {
        CableReportPdf r=new CableReportPdf(context);
        try{
            r.populate(cable);
            r.document.finishPage(r.page);
            r.page=null;
            try(OutputStream stream=context.getContentResolver().openOutputStream(dest)){
                if(stream==null)throw new IOException("No writable PDF destination");
                r.document.writeTo(stream);
            }
        }finally{
            r.document.close();
        }
    }
}
