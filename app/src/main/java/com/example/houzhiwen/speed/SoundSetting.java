package com.example.houzhiwen.speed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;

/**
 * Created by 四季折之羽 on 2017/6/3.
 */

public class SoundSetting extends AppCompatActivity {
    private SeekBar volumeSeekBar;
    private SeekBar speekSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sound_layout);
        ActionBar actionbar=getSupportActionBar();
        if(actionbar!=null) actionbar.hide();
        Button btn_return = (Button) findViewById(R.id.button_soundreturn);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundSetting.this.finish();
            }
        });
        Button btn_in = (Button) findViewById(R.id.button_soundboy);
        btn_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button= (Button) findViewById(R.id.button_soundboy);
                button.setBackgroundResource(R.drawable.sound_boy_2);
                button=(Button)findViewById(R.id.button_soundgirl);
                button.setBackgroundResource(R.drawable.sound_girl);
                Gamewindows.gender = "xiaoyu";
            }
        });
        Button btn_out = (Button) findViewById(R.id.button_soundgirl);
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button= (Button) findViewById(R.id.button_soundgirl);
                button.setBackgroundResource(R.drawable.sound_girl_2);
                button=(Button)findViewById(R.id.button_soundboy);
                button.setBackgroundResource(R.drawable.sound_boy);
                Gamewindows.gender = "xiaoyan";
            }
        });
        volumeSeekBar = (SeekBar) findViewById(R.id.volumeseekbar);
        volumeSeekBar.setProgress(Gamewindows.volume);
        volumeSeekBar.setOnSeekBarChangeListener(volumeseekListener);
        speekSeekBar = (SeekBar) findViewById(R.id.speedseekbar);
        speekSeekBar.setProgress(Gamewindows.speed);
        speekSeekBar.setOnSeekBarChangeListener(speekseekListener);
        if(Gamewindows.gender == "xiaoyan"){
            Button button= (Button) findViewById(R.id.button_soundgirl);
            button.setBackgroundResource(R.drawable.sound_girl_2);
        }
        else{
            Button button= (Button) findViewById(R.id.button_soundboy);
            button.setBackgroundResource(R.drawable.sound_boy_2);
        }
    }
    private SeekBar.OnSeekBarChangeListener volumeseekListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
           Gamewindows.volume = volumeSeekBar.getProgress();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };
    private SeekBar.OnSeekBarChangeListener speekseekListener = new SeekBar.OnSeekBarChangeListener(){
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            Gamewindows.speed = speekSeekBar.getProgress();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

        }
    };

}

