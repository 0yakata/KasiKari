package com.app.oyakata.kasikari;

import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class UpdateDialogFragment extends DialogFragment {
    EditText editText1;
    EditText editText2;
    int id;
    String sql;

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        id = getArguments().getInt("id");
        final String tag = getTag();
        if(tag.startsWith("otaku")){
            dialog.setContentView(R.layout.fragment_update_otaku_dialog);
            if (id < 0){
                sql = "INSERT INTO otaku(name, twitterid) VALUES ('%1$s','%2$s')";
            } else {
                sql = "UPDATE otaku SET name = '%1$s', twitterid = '%2$s' WHERE _id = '%3$s'";
            }
        }else if(tag.startsWith("debt")){
            dialog.setContentView(R.layout.fragment_update_debt_dialog);
            if (id < 0){
                sql = "INSERT INTO debt(yen, memo, otaku_id) VALUES ('%1$s','%2$s','%3$s')";
            } else {
                sql = "UPDATE debt SET yen = '%1$s', memo = '%2$s' WHERE _id = '%3$s'";
            }
        }

        // 初期値セット
        editText1 = dialog.findViewById(R.id.editText1);
        editText1.setText(getArguments().getString("text1"), TextView.BufferType.NORMAL);
        editText2 = dialog.findViewById(R.id.editText2);
        editText2.setText(getArguments().getString("text2"), TextView.BufferType.NORMAL);

        // キャンセルボタン
        dialog.findViewById(R.id.negativeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // 登録ボタン
        dialog.findViewById(R.id.positiveButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text1 = editText1.getText().toString();
                String text2 = editText2.getText().toString();

                //両方埋まってる場合のみ処理
                if(!text1.isEmpty() && !text2.isEmpty()) {
                    MyDBHelper helper = new MyDBHelper(getContext());
                    SQLiteDatabase db = helper.getWritableDatabase();
                    db.beginTransaction();
                    try {
                        db.execSQL(String.format(sql, text1, text2, String.valueOf(Math.abs(id))));  // マイナスを外す
                        db.setTransactionSuccessful();
                    } finally {
                        db.endTransaction();
                    }
                    if(tag.equals("otakuInsert")){
                        MainFragment frg = (MainFragment) getParentFragment();
                        frg.onReturnFromDialog();
                    } else {
                        DetailFragment frg = (DetailFragment) getParentFragment();
                        frg.onReturnFromDialog();
                    }
                    dismiss();
                } else {
                    // 無反応
                }
            }
        });

        return dialog;
    }
}