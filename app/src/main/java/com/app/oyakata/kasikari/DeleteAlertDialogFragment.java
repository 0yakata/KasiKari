package com.app.oyakata.kasikari;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class DeleteAlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String tag = getTag();
        final String id = String.valueOf(getArguments().getInt("id"));
        builder.setMessage("一覧から削除します")
                .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        MyDBHelper helper = new MyDBHelper(getContext());
                        SQLiteDatabase db = helper.getWritableDatabase();
                        db.beginTransaction();
                        try {
                            db.execSQL("DELETE FROM " + tag + " WHERE _id = " + id);
                            if(tag.equals("otaku")){
                                db.execSQL("DELETE FROM debt WHERE otaku_id = " + id);
                            }
                            db.setTransactionSuccessful();
                        } finally {
                            db.endTransaction();
                        }
                        if (tag.equals("otaku")){
                            MainFragment frg = (MainFragment) getParentFragment();
                            frg.onReturnFromDialog();
                        }else if(tag.equals("debt")){
                            DetailFragment frg = (DetailFragment) getParentFragment();
                            frg.onReturnFromDialog();
                        }
                    }
                })
                .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                    }
                });
        return builder.create();
    }
}

