package com.ryg.dynamicload.sample.mainpluginb;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ryg.dynamicload.DLBasePluginActivity;
import com.ryg.dynamicload.sample.docommon.HostInterfaceManager;
import com.ryg.dynamicload.sample.mainhost.TestHostClass;

import java.io.FileDescriptor;
import java.io.IOException;

public class MainActivity extends DLBasePluginActivity {

    private static final String TAG = "MainActivity";
    MediaPlayer mp;
    Context mContext;

    private SharedPreferences spThis;
    private SharedPreferences spThat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = that;
        initView(savedInstanceState);


    }

    private void initView(Bundle savedInstanceState) {
        that.setContentView(generateContentView(that));
    }

    private View generateContentView(final Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        TextView textView = new TextView(context);
        textView.setText("Hello, I'm Plugin B.");
        textView.setTextSize(30);
        layout.addView(textView, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);

        Button button = new Button(context);
        button.setText("Invoke host method");
        layout.addView(button, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    HostInterfaceManager.getHostInterface().hostMethod(mContext);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
                }


            }
        });

        Button button1 = new Button(context);
        button1.setText("play or stop song");
        layout.addView(button1, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp == null) {
                    mp = new MediaPlayer();
                    try {
                        mp.setDataSource(mContext.getAssets().openFd("song.mp3").getFileDescriptor());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                if (mp.isPlaying()) {
                    Toast.makeText(mContext, "stop", Toast.LENGTH_SHORT).show();
                    mp.stop();
                } else {
                    try {
                        Toast.makeText(mContext, "play", Toast.LENGTH_SHORT).show();
                        mp.prepare();
                        mp.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        });


        Button button3 = new Button(context);
        button3.setText("db");
        layout.addView(button3, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        button3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DBAdapter adapter = new DBAdapter(mContext);
                adapter.open();
                adapter.insertContact("hello", "hi@163.com");
                Cursor cursor = adapter.getAllContacts();
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        Log.e("lmf", ">>name>>" + cursor.getString(1));
                        Toast.makeText(mContext, "name:" + cursor.getString(1), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        Button button4 = new Button(context);
        button4.setText("dialog");
        layout.addView(button4, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        button4.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(that);
                builder.setMessage("are you sure?");
                builder.setTitle("alert");
                builder.setPositiveButton("quit", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        that.finish();
                    }
                });
                builder.setNegativeButton("cancel", new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

            }
        });

        return layout;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult resultCode=" + resultCode);
        if (resultCode == RESULT_FIRST_USER) {
            that.finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
