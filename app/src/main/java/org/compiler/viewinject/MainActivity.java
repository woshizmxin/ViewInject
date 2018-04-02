package org.compiler.viewinject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.annotation.BindView;

import org.compiler.inject.ViewInject;

/**
 * Created by JokAr on 16/8/8.
 */
public class MainActivity extends AppCompatActivity {
    @BindView({R.id.img, R.id.textView})
    ImageView mImageView;

    @BindView({R.id.img2, R.id.textView})
    ImageView mImageView2;

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
                    textView.setText("TextView");
                } else {
                    textView.setText("");
                }
                isEmpty = !isEmpty;
            }
        });
    }

    public void ccc() {
        Log.d("jamal.jo", "ccc: ");
    }
}
