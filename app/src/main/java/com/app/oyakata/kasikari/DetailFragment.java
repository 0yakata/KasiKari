package com.app.oyakata.kasikari;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.app.oyakata.kasikari.Utility.convertYen2k;

public class DetailFragment extends Fragment {

    // SELECT文
    private static final String SELECT_CARD
            = "SELECT" +
            "    otaku._id" +
            "  , otaku.name" +
            "  , otaku.twitterid" +
            "  , SUM(COALESCE(debt.yen,0)) AS yen" +
            "  , GROUP_CONCAT(COALESCE(debt.memo,''), '/') AS memo" +
            "  FROM otaku" +
            "  LEFT OUTER JOIN debt" +
            "  ON  debt.otaku_id = otaku._id" +
            "  AND debt.doneflg = 0" +
            "  WHERE otaku._id = ?;";

    private static final String SELECT_DEBT
            = "SELECT" +
            "    _id" +
            "  , yen" +
            "  , memo" +
            "  , doneflg" +
            "  FROM debt" +
            "  WHERE otaku_id = ?;";

    ListView cardView;
    ListView debtDetailListView;
    ArrayList<Debt> debts = new ArrayList<>();
    DebtDetailAdapter debtDetailAdapter;
    List<Card> cards = new ArrayList<>();
    CardAdapter cardAdapter;
    int otaku_id;
    AdView adView;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        Bundle args = getArguments();
        otaku_id = args.getInt("otaku_id");

        // 広告
        long adOpenedDate = getActivity().getSharedPreferences("pref", MODE_PRIVATE)
                .getLong("adOpenedDate", 0);
        if(adOpenedDate + 259200000 < System.currentTimeMillis()){
            adView = new AdView(getContext());
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
            adView = getActivity().findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {
                @Override
                public void onAdOpened() {
                    SharedPreferences pref = getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putLong("adOpenedDate", System.currentTimeMillis());
                    editor.apply();
                }
            });
        }

        // FAB
        FloatingActionButton fab = view.findViewById(R.id.fab_detail);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 新たなメモを登録するdialogを出す
                DialogFragment newFragment = new UpdateDialogFragment();
                Bundle args = new Bundle();
                args.putInt("id", -cards.get(0).getOtakuId());    // マイナスの値でIF(新規登録)
                args.putString("text1", "");
                args.putString("text2", "");
                newFragment.setArguments(args);
                newFragment.show(getChildFragmentManager(), "debt");
            }
        });

        // データ生成
        createData();

        // カード生成
        cardView = view.findViewById(R.id.otaku_card);
        cardAdapter = new CardAdapter(this.getContext(), R.layout.card, cards);
        cardView.setAdapter(cardAdapter);

        cardView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // オタクを編集するdialogを出す
                DialogFragment newFragment = new UpdateDialogFragment();
                Bundle args = new Bundle();
                args.putInt("id", cards.get(0).getOtakuId());
                args.putString("text1", cards.get(0).getOtakuName());
                args.putString("text2", cards.get(0).getTwitterId());
                newFragment.setArguments(args);
                newFragment.show(getChildFragmentManager(), "otaku");
            }
        });

        // 一覧生成
        debtDetailAdapter = new DebtDetailAdapter(getContext());
        debtDetailAdapter.setDebtList(debts);
        debtDetailListView = view.findViewById(R.id.debt_detail_list);
        debtDetailListView.setAdapter(debtDetailAdapter);

        debtDetailListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // メモを編集するdialogを出す
                DialogFragment newFragment = new UpdateDialogFragment();
                Bundle args = new Bundle();
                args.putInt("id", debts.get(position).getDebtId());
                args.putString("text1", debts.get(position).getYenString());
                args.putString("text2", debts.get(position).getMemo());
                newFragment.setArguments(args);
                newFragment.show(getChildFragmentManager(), "debt");
            }
        });

        debtDetailListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                // メモを削除するdialogを出す
                DialogFragment newFragment = new DeleteAlertDialogFragment();
                Bundle args = new Bundle();
                args.putInt("id", debts.get(position).getDebtId());
                args.putInt("itemIndex", position);
                newFragment.setArguments(args);
                newFragment.show(getChildFragmentManager(), "debt");
                return true;    // onItemClick を呼ばなくなる
            }
        });
    }

    private void createData(){
        cards.clear();
        debts.clear();

        // DB作成
        MyDBHelper helper = new MyDBHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;

        // カード生成
        cursor = db.rawQuery(SELECT_CARD, new String[]{String.valueOf(otaku_id)});
        try {
            while (cursor.moveToNext()) {
                int otakuId = cursor.getInt(cursor.getColumnIndex("_id"));
                String twitterId = cursor.getString(cursor.getColumnIndex("twitterid"));
                String otakuName = cursor.getString(cursor.getColumnIndex("name"));
                cards.add(new Card(otakuId, twitterId, otakuName, "", ""));
            }
        } finally {
            cursor.close();
        }

        // 詳細一覧生成
        cursor = db.rawQuery(SELECT_DEBT, new String[] {String.valueOf(otaku_id)});
        try {
            while (cursor.moveToNext()) {
                Debt debt = new Debt();
                debt.setDebtId(cursor.getInt(cursor.getColumnIndex("_id")));
                debt.setYen(cursor.getInt(cursor.getColumnIndex("yen")));
                debt.setMemo(cursor.getString(cursor.getColumnIndex("memo")));
                debt.setDoneFlg(cursor.getInt(cursor.getColumnIndex("doneflg")));
                debts.add(debt);
            }
        } finally {
            cursor.close();
        }
    }

    public void onReturnFromDialog() {
        createData();
        cardAdapter.notifyDataSetChanged();
        debtDetailAdapter.notifyDataSetChanged();
    }

    public String getDetailText(){
        // k表記フラグ取得
        boolean kFlg = getActivity().getSharedPreferences("pref", MODE_PRIVATE)
                .getBoolean("kFlg", false);

        StringBuilder sb = new StringBuilder();

        // リプライ先
        sb.append("@").append(cards.get(0).getTwitterId()).append("\r\n");

        for (Debt d : debts) {
            if(!d.getDoneFlgBool()){
                String yen = convertYen2k(d.getYen(),kFlg);
                sb.append(yen).append(" ").append(d.getMemo()).append("\r\n");
            }
        }
        sb.append("#KasiKari");
        return sb.toString();
    }
}