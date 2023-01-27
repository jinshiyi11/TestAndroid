package com.shuai.test.spinner;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.shuai.test.R;

public class TestSpinnerActivity extends AppCompatActivity {
    private TextView mTvTest;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_spinner);
        mSpinner=findViewById(R.id.sp_test);
        mTvTest=findViewById(R.id.tv_test);
        mTvTest.setSelected(true);

        String data[] = {"111","2222"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, data){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view= super.getDropDownView(position, convertView, parent);
                if(position==mSpinner.getSelectedItemPosition()){
                    view.setSelected(true);
                    ((TextView)view).setTextColor(0xff00ff00);
                }else{
                    view.setSelected(false);
                }
                return view;
            }
        };
        //设置下拉样式以后显示的样式
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0xffff, View.MeasureSpec.AT_MOST);
        mSpinner.setAdapter(adapter);
        mSpinner.measure(measureSpec,measureSpec);
        mSpinner.setDropDownVerticalOffset(mSpinner.getMeasuredHeight());
        mSpinner.setSelection(0);
    }

}
