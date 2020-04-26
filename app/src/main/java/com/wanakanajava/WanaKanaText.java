package com.wanakanajava;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Objects;

/**
 * A Java version of the Javascript WanaKana romaji-to-kana converter library (https://github.com/WaniKani/WanaKana)
 * Version 1.1.1
 * From https://github.com/MasterKale/WanaKanaJava or more specifically
 * https://github.com/MasterKale/WanaKanaJava/blob/master/wanakana-android/src/main/java/com/wanakanajava/WanaKanaJavaText.java
 *
 * Since there is no public release to a maven repository and it's a single file, let's just
 * include it here directly.
 */
public class WanaKanaText
{

    public interface Listener {
        void afterChanged(String kana);
    }

    private EditText gInputWindow;
    private WanaKanaJava wanaKana;
    private Listener listener;

    public WanaKanaText(EditText et, Boolean useObsoleteKana)
    {
        this(et, new WanaKanaJava(useObsoleteKana));
    }

    public WanaKanaText(EditText et, WanaKanaJava wanaKana)
    {
        gInputWindow = et;
        this.wanaKana = wanaKana;
    }

    TextWatcher tw = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void afterTextChanged(Editable romaji)
        {
            unbind();
            // Convert the text
            String romajiString = romaji.toString();
            String sKana = wanaKana.toKana(romajiString);
            if (!Objects.equals(romajiString, sKana))
            {
                gInputWindow.setText(sKana);
                gInputWindow.setSelection(gInputWindow.getText().length());
                if (listener != null) {
                    listener.afterChanged(sKana);
                }
            }
            bind();
        }
    };

    // Bind a listener to the EditText so we know to start converting text entered into it
    public void bind()
    {
        if(gInputWindow != null)
        {
            gInputWindow.addTextChangedListener(tw);
        }
    }

    // Stop listening to text input on the EditText
    public void unbind()
    {
        if(gInputWindow != null)
        {
            gInputWindow.removeTextChangedListener(tw);
        }
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
