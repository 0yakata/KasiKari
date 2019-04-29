package com.app.oyakata.kasikari;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import static android.content.Context.MODE_PRIVATE;
import static android.support.design.widget.Snackbar.LENGTH_LONG;
import static com.app.oyakata.kasikari.Utility.convertYen2k;

public class MainFragment extends Fragment {

    // SELECT文
    private static final String SELECT_DEBT_SUM
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
            "  GROUP BY otaku._id, otaku.name, otaku.twitterid;";

    ListView debtSumListView;
    List<Card> cards = new ArrayList<>();
    CardAdapter adapter;
    int sum;
    boolean kFlg;
    AdView adView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    public void onViewCreated(View view, final Bundle savedInstanceState) {
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
                    adView.setVisibility(View.GONE);
                }
            });
        }

        // FAB
        FloatingActionButton fab = view.findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 新しいオタクを追加するdialogを出す
                DialogFragment newFragment = new UpdateDialogFragment();
                Bundle args = new Bundle();
                args.putInt("id", -1);
                args.putString("text1", "");
                args.putString("text2", "");
                newFragment.setArguments(args);
                newFragment.show(getChildFragmentManager(), "otakuInsert");
            }
        });

        kFlg = getActivity().getSharedPreferences("pref", MODE_PRIVATE)
                .getBoolean("kFlg", false);

        // データ生成
        createData();

        // カードリスト生成
        debtSumListView = view.findViewById(R.id.debt_list);

        adapter = new CardAdapter(this.getContext(), R.layout.card, cards);
        debtSumListView.setAdapter(adapter);

        debtSumListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // 詳細画面へ値を渡す
                DetailFragment fragment = new DetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("otaku_id", cards.get(position).getOtakuId());
                fragment.setArguments(bundle);
                // 詳細画面を呼び出す
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment, fragment, "DetailFragment");
                // 戻るボタンで戻ってこれるように
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        debtSumListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                // オタクを削除するdialogを出す
                DialogFragment newFragment = new DeleteAlertDialogFragment();
                Bundle args = new Bundle();
                args.putInt("id", cards.get(position).getOtakuId());
                newFragment.setArguments(args);
                newFragment.show(getChildFragmentManager(), "otaku");
                return true;    // onItemClick を呼ばなくなる
            }

        });

    }

    private void createData(){
        cards.clear();
        sum = 0;

        // カード
        MyDBHelper helper = new MyDBHelper(getContext());
        SQLiteDatabase db = helper.getWritableDatabase();

        Cursor cursor = db.rawQuery(SELECT_DEBT_SUM, null);
        try {
            while (cursor.moveToNext()) {
                int otakuId = cursor.getInt(cursor.getColumnIndex("_id"));
                String twitterId = cursor.getString(cursor.getColumnIndex("twitterid"));
                String otakuName = cursor.getString(cursor.getColumnIndex("name"));
                String debtSum = convertYen2k(cursor.getInt(cursor.getColumnIndex("yen")), kFlg);
                String debtDetail = cursor.getString(cursor.getColumnIndex("memo"));
                cards.add(new Card(otakuId, twitterId, otakuName, debtSum, debtDetail));

                sum += cursor.getInt(cursor.getColumnIndex("yen"));
            }
        } finally {
            cursor.close();
        }

        // 総額設定
        TextView allSumText = getView().findViewById(R.id.allSumText);
        allSumText.setText(" ＋/－合計 : " + convertYen2k(sum, kFlg));
    }

    public void onReturnFromDialog() {
        createData();
        adapter.notifyDataSetChanged();
    }

    public String getCardsText(){
        StringBuilder sb = new StringBuilder();
        for (Card c : cards) {
            if (!c.getDebtSum().startsWith("0 ")) {
                sb.append(c.getOtakuName() + " " + c.getDebtSum() + " " + c.getDebtDetail() + "\r\n");
            }
        }
        return sb.toString();
    }
}