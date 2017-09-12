package com.go.picturechosedemo.view;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by go on 2017/9/4.
 */

public class RichEditText extends AppCompatEditText {

    public static final String MATCH_MENTION = "@([^@^\\s^:^,^;^'，'^'；'^>^<]{1,})";//@([^@^\\s^:]{1,})([\\s\\:\\,\\;]{0,1})");//@.+?[\\s:]
    public static final String MATCH_TOPIC = "#.+?#";
    public static boolean DEBUG = false;
    private static final String TAG = RichEditText.class.getName();
    private final TagSpanTextWatcher mTagSpanTextWatcher = new TagSpanTextWatcher();

    private TagSpan tagSpan;




    public RichEditText(Context context) {
        super(context);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        Spannable spannable = new SpannableString(text);

        spannable = matchMention(spannable);
        spannable = matchTopic(spannable);
        super.setText(spannable, type);
    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }




    private void init(){

      //  tagSpan = new TagSpan();

    }


    private class TagSpanTextWatcher implements TextWatcher{


        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }



    public static class TagSpan extends ForegroundColorSpan implements Parcelable{

        private String value;

        public TagSpan(String color) {
            super(0xFF24cf5f);
            value = color;
        }

        public TagSpan(Parcel src) {
            super(src);
            value = src.readString();
        }


        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
        }


        void changeRemoveState(){

        }
    }


    public static Spannable matchMention(Spannable spannable){
        String text = spannable.toString();
        Pattern pattern = Pattern.compile(MATCH_MENTION);


        Matcher matcher = pattern.matcher(text);

        if (matcher.find()){
            String str = matcher.group();
            int matcherStart = matcher.start();
            int matcherEnd = matcher.end();
            spannable.setSpan(new TagSpan(str), matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
           // log("matchTopic:" + str + " " + matcherStart + " " + matcherEnd);
        }
        return spannable;
    }


    public static Spannable matchTopic(Spannable spannable){
        String text = spannable.toString();
        Pattern pattern = Pattern.compile(MATCH_TOPIC);

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()){
            String str = matcher.group();
            int matcherStart = matcher.start();
            int matcherEnd = matcher.end();
            spannable.setSpan(new TagSpan(str), matcherStart, matcherEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }
}
