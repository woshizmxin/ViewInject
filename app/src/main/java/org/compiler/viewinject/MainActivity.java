package org.compiler.viewinject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.annotation.BindView;

import org.compiler.inject.ViewInject;

/**
 * Created by JokAr on 16/8/8.
 */
public class MainActivity extends AppCompatActivity {
    @BindView({R.id.textView2, R.id.textView})
    TextView mTextView2;

    Button mButton;
    boolean isEmpty;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewInject.inject(this);
        textView = (TextView) findViewById(R.id.textView);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty) {
                    textView.setText("hi,我不是空的");
                } else {
                    textView.setText("");
                }
                isEmpty = !isEmpty;
            }
        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())) {
                    mTextView2.setVisibility(View.GONE);
                } else {
                    mTextView2.setVisibility(View.VISIBLE);
                }

            }
        };
        textView.addTextChangedListener(textWatcher);
    }

    public void ccc() {
        Log.d("jamal.jo", "ccc: ");
    }
}
