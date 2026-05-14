package com.vvicat.depscatalog.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout content = new LinearLayout(this);
        content.setGravity(android.view.Gravity.CENTER);
        content.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText(ExampleMessages.brandName());
        title.setTextSize(24);

        TextView subtitle = new TextView(this);
        subtitle.setText("Gradle Deps Catalog");

        content.addView(title);
        content.addView(subtitle);
        setContentView(content);
    }
}
