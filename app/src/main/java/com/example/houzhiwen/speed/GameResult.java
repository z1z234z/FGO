package com.example.houzhiwen.speed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by 四季折之羽 on 2017/6/3.
 */

public class GameResult extends AppCompatActivity {
    private Intent intent;
    private String score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.result_layout);
        ActionBar actionbar=getSupportActionBar();
        if(actionbar!=null) actionbar.hide();
        Button btn_return = (Button) findViewById(R.id.button_resultreturn);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameResult.this, FirstActivity.class);
                startActivity(intent);
                GameResult.this.finish();
            }
        });
        intent = getIntent();
        score = intent.getStringExtra("final scores");
        TextView t = (TextView)findViewById(R.id.finally_scores);
        t.setText(score);
    }
}
