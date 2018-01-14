package com.example.houzhiwen.speed;

/**
 * Created by 四季折之羽 on 2017/6/2.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;;
import android.widget.TextView;
import android.widget.Toast;


public class GameSetting extends Activity{
    private int gameNum;
    private TextView Text;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.setting_layout);
        android.app.ActionBar actionbar= getActionBar();
        if(actionbar!=null) actionbar.hide();
        Button btn_return = (Button) findViewById(R.id.button_setreturn);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameSetting.this, FirstActivity.class);
                startActivity(intent);
            }
        });
        Button btn_gameup = (Button) findViewById(R.id.button_gameup);
        btn_gameup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Text = (TextView)findViewById(R.id.gamenum);
                gameNum = Integer.valueOf(Text.getText().toString());
                if(gameNum<10)
                    gameNum++;
                Text.setText(String.valueOf(gameNum));
            }
        });
        Button btn_gamedown = (Button) findViewById(R.id.button_gamedown);
        btn_gamedown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Text = (TextView)findViewById(R.id.gamenum);
                gameNum = Integer.valueOf(Text.getText().toString());
                if(gameNum>1)
                    gameNum--;
                Text.setText(String.valueOf(gameNum));
            }
        });
        Button btn_in = (Button) findViewById(R.id.button_in);
        btn_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button= (Button) findViewById(R.id.button_in);
                button.setBackgroundResource(R.drawable.model_in_2);
                button=(Button)findViewById(R.id.button_out);
                button.setBackgroundResource(R.drawable.model_out);
                Gamewindows.type = 2;
            }
        });
        Button btn_out = (Button) findViewById(R.id.button_out);
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button= (Button) findViewById(R.id.button_out);
                button.setBackgroundResource(R.drawable.model_out_2);
                button=(Button)findViewById(R.id.button_in);
                button.setBackgroundResource(R.drawable.model_in);
                Gamewindows.type = 1;
            }
        });
        Button btn_start = (Button) findViewById(R.id.button_gamestart);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Text = (TextView) findViewById(R.id.gamenum);
                gameNum = Integer.valueOf(Text.getText().toString());
                Intent intent = new Intent(GameSetting.this, SetActivity.class);
                intent.putExtra("gameNum", String.valueOf(gameNum));
                startActivity(intent);
                GameSetting.this.finish();
            }
        });
        if(Gamewindows.type == 1){
            Button button= (Button) findViewById(R.id.button_out);
            button.setBackgroundResource(R.drawable.model_out_2);
        }
        else{
            Button button= (Button) findViewById(R.id.button_in);
            button.setBackgroundResource(R.drawable.model_in_2);
        }
    }
}
