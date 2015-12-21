package com.example.fondson.mylockscreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import com.example.fondson.mylockscreen.AutoStart;
import com.example.fondson.mylockscreen.UpdateService;

import java.util.HashSet;
import java.util.Set;

import io.github.homelocker.lib.HomeKeyLocker;

public class MainActivity extends AppCompatActivity {

    final HomeKeyLocker homeKeyLocker = new HomeKeyLocker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, UpdateService.class));
        final LinearLayout ll = (LinearLayout)findViewById(R.id.llItems);
        ll.setOrientation(LinearLayout.VERTICAL);
        //disableCheck();
        final EditText etInput = (EditText) findViewById(R.id.editText);
        etInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                                              @Override
                                              public boolean onEditorAction(TextView arg0, int arg1, KeyEvent event) {
                                                  if (arg1 == EditorInfo.IME_ACTION_NEXT && !(etInput.getText().toString().trim().matches(""))) {
                                                      final CheckBox cb = new CheckBox(MainActivity.this);
                                                      cb.setText(etInput.getText().toString().trim());
                                                      cb.setTextSize(17);
                                                      ll.addView(cb, 0);
                                                      etInput.setText("");
                                                      cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                          @Override
                                                          public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                                                              if (isChecked) {
                                                                  Handler handler = new Handler();
                                                                  handler.postDelayed(new Runnable() {
                                                                      @Override
                                                                      public void run() {
                                                                          ll.removeView(cb);
                                                                          Toast.makeText(MainActivity.this, cb.getText() + " removed.", Toast.LENGTH_SHORT).show();
                                                                      }
                                                                  }, 220);
                                                              }
                                                          }
                                                      });
                                                      return true;
                                                  }
                                                  return false;
                                              }
                                          }
        );
        etInput.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View view) {
                //enableCheck();
                homeKeyLocker.unlock();
            }
        });
        //ScrollView sv = (ScrollView)findViewById(R.id.sv);
        //sv.setOnTouchListener(new View.OnTouchListener() {
        //    long down;

        //    @Override
        //    public boolean onTouch(View v, MotionEvent event) {

        //    }
        //});
        final SeekBar sb = (SeekBar) findViewById(R.id.seekBar);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > 80) {
                } else {
                    seekBar.setThumb(getResources().getDrawable(R.mipmap.ic_launcher));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 80) {
                    moveTaskToBack(true);
                }
            }
        });
    }
    // Don't finish Activity on Back press
    @Override
    public void onBackPressed() {
        return;
    }
    protected void onPause() {
        hideKeyboard();
        homeKeyLocker.unlock();
        startService(new Intent(this, UpdateService.class));
        super.onPause();
    }
    protected void onResume() {
        homeKeyLocker.lock(this);
        final SeekBar sb = (SeekBar) findViewById(R.id.seekBar);
        sb.setProgress(0);
        //disableCheck();
        super.onResume();
    }

    public void disableCheck() {
        LinearLayout myLayout = (LinearLayout) findViewById(R.id.llItems);
        for ( int i = 0; i < myLayout.getChildCount();  i++ ){
            View view = myLayout.getChildAt(i);
            view.setEnabled(false);
            view.setClickable(false);
        }
    }
    public void enableCheck(){
        LinearLayout myLayout = (LinearLayout) findViewById(R.id.llItems);
        for ( int i = 0; i < myLayout.getChildCount();  i++ ){
            View view = myLayout.getChildAt(i);
            view.setEnabled(true);
            view.setClickable(true);
        }
    }
    public void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
