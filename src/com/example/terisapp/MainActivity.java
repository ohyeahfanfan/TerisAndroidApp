package com.example.terisapp;

import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;
public class MainActivity extends Activity {
	public int count = 0;
	public JTetris tetris = new JTetris(15);
	public TextView t = null;
	public JTetris tetris2 = new JTetris(15);
	public TextView t2 = null;
    @TargetApi(11)
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t = (TextView)findViewById(R.id.textView1);
        t.setLineSpacing(0f,0.6f);
        t.setTextSize(20);
        t.setTypeface(Typeface.MONOSPACE);
        tetris.startGame();
        t.setText(tetris.paintComponent());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run()
            {
            	runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                    	tetris.tick(JTetris.DOWN);
                    	t.setText(tetris.paintComponent());
                    }
                });
            	
            }
        },0,1000);
        
       
        t2 = (TextView)findViewById(R.id.textView2);
        t2.setLineSpacing(0f,0.6f);
        t2.setTextSize(20);
        t2.setTypeface(Typeface.MONOSPACE);
        tetris2.startGame();
        t2.setText(tetris.paintComponent());
        Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run()
            {
            	runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                    	tetris2.tick(JTetris.DOWN);
                    	t2.setText(tetris2.paintComponent());
                    }
                });
            	
            }
        },0,1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void selfDestruct(View view) {
    	TextView t = (TextView)findViewById(R.id.textView1);
    	String line = "Lele is a big pig!\n";
    	t.setText("Lele is a big pig!\nLele is a big pig!\nLele is a big pig!\nLele is a big pig!\nLele is a big pig!\n");
    }
    
    public void moveLeft(View view) {
    	tetris.tick(JTetris.LEFT);
    	t.setText(tetris.paintComponent());
    }
    
    public void moveRight(View view) {
    	tetris.tick(JTetris.RIGHT);
    	t.setText(tetris.paintComponent());
    }
    
    public void moveDown(View view) {
    	tetris.tick(JTetris.DOWN);
    	t.setText(tetris.paintComponent());
    }
    public void rotate(View view) {
    	tetris.tick(JTetris.ROTATE);
    	t.setText(tetris.paintComponent());
    }
}
