package com.shuai.test.textview;

import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.widget.TextView;

import com.shuai.test.R;

public class TestTextViewActivity extends AppCompatActivity {
    private TextView mTvTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_text_view);
        mTvTest = findViewById(R.id.tv_test);

        String contactName = "预告";
        final SpannableStringBuilder sb = new SpannableStringBuilder();
        TextView tv = createContactTextView();
        Drawable bd = DrawableUtil.convertViewToDrawable(tv);
        bd.setBounds(0, 0, bd.getIntrinsicWidth(), bd.getIntrinsicHeight());

        sb.append("哈哈"+contactName + ",123");
        ImageSpanEx imageSpan = new ImageSpanEx(bd, ImageSpanEx.ALIGN_CENTER);
        imageSpan.setLeftMargin(100).setRightMargin(100);
        sb.setSpan(imageSpan, 2, 2+contactName.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        BackgroundImageSpan backgroundImageSpan=new BackgroundImageSpan(R.drawable.notice_bg,getResources().getDrawable(R.drawable.notice_bg));
        sb.setSpan(backgroundImageSpan, 5, 5+contactName.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mTvTest.setText(sb);
    }

    private TextView createContactTextView() {
        //creating textview dynamically
        TextView tv = (TextView) getLayoutInflater().inflate(R.layout.view_notice, null, false);
        return tv;
    }


}
