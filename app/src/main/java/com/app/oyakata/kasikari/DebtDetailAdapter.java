package com.app.oyakata.kasikari;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.app.oyakata.kasikari.Utility.convertYen2k;

public class DebtDetailAdapter extends BaseAdapter {

    // 返済フラグUPDATE
    private static final String UPDATE_DEBT_DONE
            = "UPDATE debt SET upddt = CURRENT_TIMESTAMP, doneflg = ? WHERE _id = ?";

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Debt> debtList;

    public DebtDetailAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDebtList(ArrayList<Debt> debtList) {
        this.debtList = debtList;
    }

    @Override
    public int getCount() {
        return debtList.size();
    }

    @Override
    public Object getItem(int position) {
        return debtList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return debtList.get(position).getDebtId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 再利用可能なViewがあればそれを使う。初回は作成。
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.debt_detail_list_item, parent,false);
        }

        // k表記フラグ取得
        boolean kFlg = convertView.getContext().getSharedPreferences("pref", MODE_PRIVATE)
                .getBoolean("kFlg", false);

        ((TextView)convertView.findViewById(R.id.yen)).setText(convertYen2k(debtList.get(position).getYen(), kFlg));
        ((TextView)convertView.findViewById(R.id.memo)).setText(debtList.get(position).getMemo());
        final Switch doneSwitch = convertView.findViewById(R.id.done_switch);
        final int p = position;

        // viewを再利用している場合、リスナーも残ってしまい、
        // setCheckedでイベントが発火するので一回外す
        doneSwitch.setOnCheckedChangeListener(null);

        doneSwitch.setChecked(debtList.get(position).getDoneFlgBool());
        doneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 更新処理
                int debtId = debtList.get(p).getDebtId();
                int doneFlg = isChecked ? 1 : 0;
                MyDBHelper helper = new MyDBHelper(context);
                SQLiteDatabase db = helper.getWritableDatabase();
                db.beginTransaction();
                try {
                    db.execSQL(UPDATE_DEBT_DONE,
                            new String[] {String.valueOf(doneFlg), String.valueOf(debtId)});
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                // データリスト更新
                debtList.get(p).setDoneFlg(isChecked ? 1 : 0);

                // Toast発行
                String message = isChecked ? "返済完了" : "未返済";
                Toast.makeText(context, message, Snackbar.LENGTH_LONG).show();
            }
        });

        return convertView;
    }
}