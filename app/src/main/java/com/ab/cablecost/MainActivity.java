package com.ab.cablecost;
import android.app.*; import android.os.*; import android.graphics.Color; import android.view.*; import android.widget.*; import java.util.*;
public class MainActivity extends Activity {
 LinearLayout root,body; EditText diameter,strands,length,outer,insulated,plasticDensity,price; Spinner metal,category,mode; TextView output; final int navy=Color.rgb(13,29,54),gold=Color.rgb(191,148,55);
 TextView text(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextColor(navy);t.setTextSize(size);t.setPadding(12,12,12,12);t.setGravity(Gravity.RIGHT);return t;}
 EditText field(String hint,String def){EditText e=new EditText(this);e.setHint(hint);e.setText(def);e.setInputType(8194);e.setTextSize(17);e.setGravity(Gravity.RIGHT);body.addView(e);return e;}
 Spinner spin(String... a){Spinner s=new Spinner(this);ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,a);s.setAdapter(adapter);body.addView(s);return s;}
 void button(String title,Runnable click){Button b=new Button(this);b.setText(title);b.setTextColor(Color.WHITE);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(navy));body.addView(b);b.setOnClickListener(v->click.run());}
 double val(EditText e){return Double.parseDouble(e.getText().toString().trim());}
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(navy);root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(Color.WHITE);root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);setContentView(root);TextView title=text("AB Cable Cost  |  حساب الكابلات",23);title.setTextColor(gold);title.setBackgroundColor(navy);root.addView(title);ScrollView scroll=new ScrollView(this);root.addView(scroll);body=new LinearLayout(this);body.setPadding(20,14,20,20);body.setOrientation(1);scroll.addView(body);
 body.addView(text("نوع الكابل",17));category=spin("أسلاك معزولة","كابلات ترمو","كابلات مسلحة","كابلات كنترول","كابلات مخصوصة");
 body.addView(text("نوع الموصل",17));metal=spin("نحاس أحمر","ألومنيوم أبيض","ألومنيوم منحّس CCA (+5%)","نحاس مقصدر (+5%)");
 diameter=field("قطر الشعرة بالملم","0.16");strands=field("عدد الشعرات","48");length=field("الطول بالمتر","100");
 button("احسب وزن الموصل",()->calc());
 body.addView(text("حساب وزن العزل",20));mode=spin("بالميزان","بالأقطار والكثافة");
 insulated=field("وزن السلك المعزول بالكامل بالجرام (للميزان)","");outer=field("القطر الخارجي للعزل بالملم (للحساب الهندسي)","");plasticDensity=field("كثافة العزل جرام/سم³ (من الداتا شيت)","");
 button("احسب وزن العزل",()->calcInsulation());
 body.addView(text("سعر كيلو الموصل (اختياري)",16));price=field("سعر الكيلو","");button("احسب تكلفة الموصل",()->cost());
 output=text("ادخل المقاسات واضغط احسب.",19);body.addView(output);
 }
 double conductor() throws Exception {double d=val(diameter),n=val(strands),l=val(length);if(d<=0||n<=0||l<=0||n!=Math.floor(n))throw new Exception("المقاسات لازم تكون أكبر من صفر وعدد الشعرات صحيح");int m=metal.getSelectedItemPosition();double density=(m==1||m==2)?2.70:8.96;double factor=(m>=2)?1.05:1;return Math.PI/4*d*d*n*l*density*factor;}
 void show(String s){output.setText(s);}void error(Exception e){show("راجع البيانات: "+e.getMessage());}
 void calc(){try{double grams=conductor();double area=Math.PI/4*Math.pow(val(diameter),2)*val(strands);show(String.format(Locale.US,"مساحة النحاس/الموصل: %.4f مم²\nوزن الموصل: %.2f جرام\n= %.4f كيلو\nملحوظة: زيادة 5%% تقديرية للـCCA والمقصدر فقط.",area,grams,grams/1000));}catch(Exception e){error(e);}}
 void calcInsulation(){try{double metalWeight=conductor();double plastic;if(mode.getSelectedItemPosition()==0){plastic=val(insulated)-metalWeight;}else{double outerD=val(outer),conductorD=Math.sqrt(4*(Math.PI/4*Math.pow(val(diameter),2)*val(strands))/Math.PI);if(outerD<=conductorD)throw new Exception("قطر العزل لازم يكون أكبر من قطر الموصل المكافئ");plastic=Math.PI/4*(outerD*outerD-conductorD*conductorD)*val(length)*val(plasticDensity); }if(plastic<0)throw new Exception("وزن العزل طلع بالسالب؛ راجع القياسات");show(String.format(Locale.US,"وزن العزل التقريبي: %.2f جرام\nالوزن بالجرام لنفس طول العينة. الحساب الهندسي تقديري للشعرات المجدولة.",plastic));}catch(Exception e){error(e);}}
 void cost(){try{double p=val(price);if(p<0)throw new Exception("السعر مش صحيح");show(String.format(Locale.US,"وزن الموصل: %.3f كيلو\nتكلفة الموصل: %.2f\nتكلفة الموصل للمتر: %.4f\n(مش شامل العزل أو التصنيع)",conductor()/1000,conductor()/1000*p,conductor()/1000*p/val(length)));}catch(Exception e){error(e);}}
}