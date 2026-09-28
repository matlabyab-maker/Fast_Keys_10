package com.fastkeyboard;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.PopupWindow;
import android.graphics.drawable.GradientDrawable;

import java.util.LinkedHashMap;
import java.util.Map;

/** XML/AppCompatButton keyboard prototype. Text sizing is handled by AppCompat auto-size. */
public class XmlKeyboardView extends LinearLayout {
    private int dp(float v){ return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private final FastKeyboardInputMethodService service;
    private boolean english = false;
    private boolean caps = false;
    private final Map<Integer,String> fa = new LinkedHashMap<>();
    private final Map<Integer,String> en = new LinkedHashMap<>();

    public XmlKeyboardView(Context context, FastKeyboardInputMethodService service) {
        super(context);
        this.service = service;
        setOrientation(VERTICAL);
        setBackgroundColor(Color.rgb(255,253,245));
        LayoutInflater.from(context).inflate(com.fastkeyboard.R.layout.ime_keyboard_xml, this, true);
        bind();
        updateLanguageVisibility();
    }

    private void bind(){
        int[] faIds={R.id.key_fa_za,R.id.key_fa_sad,R.id.key_fa_sad2,R.id.key_fa_qaf,R.id.key_fa_fa,R.id.key_fa_gh,R.id.key_fa_ain,R.id.key_fa_he,R.id.key_fa_khe,R.id.key_fa_ha,R.id.key_fa_jim,R.id.key_fa_sh,R.id.key_fa_sin,R.id.key_fa_ye,R.id.key_fa_be,R.id.key_fa_lam,R.id.key_fa_alef,R.id.key_fa_te,R.id.key_fa_noon,R.id.key_fa_mim,R.id.key_fa_kaf,R.id.key_fa_gaf,R.id.key_fa_za2,R.id.key_fa_ta,R.id.key_fa_zhe,R.id.key_fa_ze,R.id.key_fa_re,R.id.key_fa_zal,R.id.key_fa_dal,R.id.key_fa_pe,R.id.key_fa_vav,R.id.key_fa_che};
        String[] faText={"ض","ص","ث","ق","ف","غ","ع","ه","خ","ح","ج","ش","س","ی","ب","ل","ا","ت","ن","م","ک","گ","ظ","ط","ژ","ز","ر","ذ","د","پ","و","چ"};
        for(int i=0;i<faIds.length;i++){ final String s=faText[i]; fa.put(faIds[i],s); findViewById(faIds[i]).setOnClickListener(v->service.typeUnit(caps?toUpperPlaceholder(s):s)); }
        findViewById(R.id.key_fa_alef).setOnLongClickListener(v -> { showAlefPopup(v); return true; });
        int[] enIds={R.id.key_q,R.id.key_w,R.id.key_e,R.id.key_r,R.id.key_t,R.id.key_y,R.id.key_u,R.id.key_i,R.id.key_o,R.id.key_p,R.id.key_a,R.id.key_s,R.id.key_d,R.id.key_f,R.id.key_g,R.id.key_h,R.id.key_j,R.id.key_k,R.id.key_l,R.id.key_z,R.id.key_x,R.id.key_c,R.id.key_v,R.id.key_b,R.id.key_n,R.id.key_m,R.id.key_lbr,R.id.key_rbr,R.id.key_slash,R.id.key_semicolon,R.id.key_quote,R.id.key_comma,R.id.key_question};
        String[] enText={"Q","W","E","R","T","Y","U","I","O","P","A","S","D","F","G","H","J","K","L","Z","X","C","V","B","N","M","[","]","\\",";","'",",","?"};
        for(int i=0;i<enIds.length;i++){ final String s=enText[i]; en.put(enIds[i],s); findViewById(enIds[i]).setOnClickListener(v->service.typeEnglish(s,caps)); }
        for(int i=1;i<=9;i++) findViewById(getResources().getIdentifier("key_n"+i,"id",getContext().getPackageName())).setOnClickListener(v->service.type(((Button)v).getText().toString()));
        findViewById(R.id.key_n0).setOnClickListener(v->service.type("0"));
        findViewById(R.id.key_minus).setOnClickListener(v->service.type("-"));
        findViewById(R.id.key_equal).setOnClickListener(v->service.type("="));
        findViewById(R.id.key_space).setOnClickListener(v->service.type(" "));
        findViewById(R.id.key_backspace).setOnClickListener(v->service.backspace());
        findViewById(R.id.key_backspace_en).setOnClickListener(v->service.backspace());
        findViewById(R.id.key_enter).setOnClickListener(v->service.enter());
        findViewById(R.id.key_enter_en).setOnClickListener(v->service.enter());
        findViewById(R.id.key_left).setOnClickListener(v->service.moveCursorHorizontal(-1));
        findViewById(R.id.key_right).setOnClickListener(v->service.moveCursorHorizontal(1));
        findViewById(R.id.key_copy_all).setOnClickListener(v->service.copyAll());
        findViewById(R.id.key_copy_screen).setOnClickListener(v->service.copyScreen());
        findViewById(R.id.key_paste).setOnClickListener(v->service.paste());
        findViewById(R.id.key_cut).setOnClickListener(v->service.cut());
        findViewById(R.id.key_undo).setOnClickListener(v->service.undo());
        findViewById(R.id.key_redo).setOnClickListener(v->service.redo());
        findViewById(R.id.key_mic).setOnClickListener(v->service.voiceSearch(english?"en-US":"fa-IR"));
        findViewById(R.id.key_globe).setOnClickListener(v->{english=!english; updateLanguageVisibility();});
        findViewById(R.id.key_caps).setOnClickListener(v->{caps=!caps;});
        findViewById(R.id.key_caps_en).setOnClickListener(v->{caps=!caps;});
        findViewById(R.id.key_hidden).setOnClickListener(v->{ setVisibility(GONE); });
        findViewById(R.id.key_symbols).setOnClickListener(v->service.typeUnit("123"));
    }

    private void showAlefPopup(View anchor) {
        final PopupWindow popup = new PopupWindow(getContext());
        LinearLayout box = new LinearLayout(getContext());
        box.setOrientation(LinearLayout.HORIZONTAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(dp(10), dp(8), dp(10), dp(8));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(205, 209, 218));
        bg.setCornerRadius(dp(14));
        popup.setBackgroundDrawable(bg);
        popup.setElevation(dp(8));

        final String[] choices = {"آ", "ع", "أ", "إ"};
        for (int i = 0; i < choices.length; i++) {
            final String ch = choices[i];
            TextView item = new TextView(getContext());
            item.setText(ch);
            item.setGravity(Gravity.CENTER);
            item.setTextColor(i == 0 ? Color.WHITE : Color.BLACK);
            item.setTextSize(24);
            item.setTypeface(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL);
            item.setPadding(dp(12), 0, dp(12), 0);
            if (i == 0) {
                GradientDrawable selected = new GradientDrawable();
                selected.setColor(Color.rgb(90, 145, 235));
                selected.setCornerRadius(dp(10));
                item.setBackground(selected);
            }
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(58), dp(58));
            lp.setMargins(dp(3), 0, dp(3), 0);
            box.addView(item, lp);
            item.setOnClickListener(v -> {
                service.typeUnit(ch);
                popup.dismiss();
            });
        }

        popup.setContentView(box);
        popup.setWidth(dp(300));
        popup.setHeight(dp(76));
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);

        int[] loc = new int[2];
        anchor.getLocationOnScreen(loc);
        int x = loc[0] + anchor.getWidth() / 2 - dp(150);
        int y = loc[1] - dp(84);
        popup.showAtLocation(this, Gravity.TOP | Gravity.START, Math.max(0, x), Math.max(0, y));
    }

    private String toUpperPlaceholder(String s){ return s; }

    private void setRowVisibility(int id, int visibility){ View v=findViewById(id); if(v!=null) v.setVisibility(visibility); }
    private void updateLanguageVisibility(){
        int faV=english?GONE:VISIBLE, enV=english?VISIBLE:GONE;
        setRowVisibility(R.id.fa_top,faV); setRowVisibility(R.id.fa_mid,faV); setRowVisibility(R.id.fa_bottom,faV);
        setRowVisibility(R.id.en_top,enV); setRowVisibility(R.id.en_mid,enV); setRowVisibility(R.id.en_bottom,enV);
    }
}
