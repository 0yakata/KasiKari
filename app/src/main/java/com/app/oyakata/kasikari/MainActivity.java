package com.app.oyakata.kasikari;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.ads.MobileAds;

import static android.support.design.widget.Snackbar.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 広告
        MobileAds.initialize(this, "ca-app-pub-7465224301871178~5381685701");
        long adOpenedDate = getSharedPreferences("pref", MODE_PRIVATE)
                            .getLong("adOpenedDate", 0);
        if(adOpenedDate + 259200000 < System.currentTimeMillis()){
            Toast.makeText(this,"広告はタップすると3日間消えます",LENGTH_LONG).show();
        }

        MainFragment mainFragment = new MainFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_fragment, mainFragment);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        boolean kFlg = getSharedPreferences("pref", MODE_PRIVATE)
                       .getBoolean("kFlg", false);
        menu.findItem(R.id.action_k).setChecked(kFlg);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Twitterボタン
        if (id == R.id.action_twitter) {
            String message = "";
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if(count == 0){
                MainFragment f = (MainFragment)getSupportFragmentManager().findFragmentById(R.id.main_fragment);
                message = f.getCardsText();
            }else if(count == 1){
                DetailFragment f = (DetailFragment)getSupportFragmentManager().findFragmentByTag("DetailFragment");
                message = f.getDetailText();
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uri = Uri.encode(message);
            intent.setData(Uri.parse("twitter://post?message=" + uri));
            startActivity(intent);
            return true;
        }

        // k表記メニュー
        if(id == R.id.action_k){
            // チェックボックスの状態変更を行う
            item.setChecked(!item.isChecked());
            // 反映後の状態を取得する
            boolean checked = item.isChecked();
            // 設定を保存
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("kFlg", checked);
            editor.apply();
            Toast.makeText(getBaseContext(),
                      "設定を変更しました。\r\n画面を切り替えると反映されます。",
                           Snackbar.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
