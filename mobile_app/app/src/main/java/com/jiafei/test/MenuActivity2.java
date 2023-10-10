package com.jiafei.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;
import com.hitomi.cmlibrary.OnMenuStatusChangeListener;

import java.util.concurrent.Delayed;

public class MenuActivity2 extends AppCompatActivity {
    CircleMenu circleMenu;
    ConstraintLayout constraintLayout;
    int choose=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        circleMenu=findViewById(R.id.circle_menu);
        constraintLayout=findViewById(R.id.menu_view);
        circleMenu.setMainMenu(Color.parseColor("#CDCDCD"), R.drawable.icon_menu, R.drawable.icon_cancel)
                .addSubMenu(Color.parseColor("#258CFF"), R.drawable.icon_home)
                .addSubMenu(Color.parseColor("#30A400"), R.drawable.yibiaopan)//1仪表盘
                .addSubMenu(Color.parseColor("#FF4B32"), R.drawable.view)//2可视化
                .addSubMenu(Color.parseColor("#8A39FF"), R.drawable.setup)//3调试
                .addSubMenu(Color.parseColor("#FF6A00"), R.drawable.root)//4用户
                .setOnMenuSelectedListener(new OnMenuSelectedListener() {

                    @Override
                    public void onMenuSelected(int index) {
                         choose= index;
                        switch(index)
                        {
                            case 0:
                                constraintLayout.setBackgroundColor(Color.parseColor("#ecfffb"));
                                break;
                            case 1:
                                constraintLayout.setBackgroundColor(Color.parseColor("#96f7d2"));
                                break;
                            case 2:
                                constraintLayout.setBackgroundColor(Color.parseColor("#fac4a2"));
                                break;
                            case 3:
                                constraintLayout.setBackgroundColor(Color.parseColor("#d3cde6"));
                                break;
                            case 4:
                                constraintLayout.setBackgroundColor(Color.parseColor("#fff591"));
                                break;
                        }
                    }
                }).setOnMenuStatusChangeListener(new OnMenuStatusChangeListener() {

                    @Override
                    public void onMenuOpened() {}

                    @Override
                    public void onMenuClosed() {
                        switch(choose)
                        {
                            case 0:
                                Toast.makeText(MenuActivity2.this,"主菜单",Toast.LENGTH_SHORT).show();
                                 break;
                            case 1:
                                Toast.makeText(MenuActivity2.this,"仪表盘",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                break;
                            case 2:
                                Toast.makeText(MenuActivity2.this,"可视化",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),TabActivity.class));
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                break;
                            case 3:
                                Toast.makeText(MenuActivity2.this,"调试",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),DataActivity.class));
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                break;
                            case 4:
                                Toast.makeText(MenuActivity2.this,"命令下发",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),RootActivity.class));
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                break;
                        }

                    }

                });
    }
}