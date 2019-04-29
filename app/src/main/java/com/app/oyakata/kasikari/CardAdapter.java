package com.app.oyakata.kasikari;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import com.squareup.picasso.Picasso;

public class CardAdapter extends ArrayAdapter<Card> {

    private List<Card> cardList;

    CardAdapter(Context context, int resourceId, List<Card> cardList) {
        super(context, resourceId, cardList);
        this.cardList = cardList;
    }

    @Override
    public int getCount() {
        return cardList.size();
    }

    @Override
    public Card getItem(int position) {
        return cardList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card, null);
            viewHolder  = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final Card CARDINFO = getItem(position);
        if (CARDINFO != null) {
            String path = "https://twitter.com/" + CARDINFO.getTwitterId() + "/profile_image?size=original";
            Picasso.get().load(path).into(viewHolder.getTwitterIconImage());
            viewHolder.getOtakuNameText().setText(CARDINFO.getOtakuName());
            viewHolder.getTwitterIdText().setText("@" + CARDINFO.getTwitterId());
            viewHolder.getDebtSumText().setText(CARDINFO.getDebtSum());
            viewHolder.getDebtDetailText().setText(CARDINFO.getDebtDetail());
        }

        return convertView;
    }

    private class ViewHolder {
        private ImageView twitterIconImage = null;
        private TextView otakuNameText = null;
        private TextView twitterIdText = null;
        private TextView debtSumText = null;
        private TextView debtDetailText = null;

        public ViewHolder(View view) {
            twitterIconImage = (ImageView)view.findViewById(R.id.twitterIconImage);
            otakuNameText = (TextView) view.findViewById(R.id.otakuNameText);
            twitterIdText = (TextView) view.findViewById(R.id.twitterIdText);
            debtSumText = (TextView) view.findViewById(R.id.debtSumText);
            debtDetailText = (TextView) view.findViewById(R.id.debtDetailText);
        }

        public ImageView getTwitterIconImage() {
            return this.twitterIconImage;
        }

        public TextView getOtakuNameText() {
            return this.otakuNameText;
        }

        public TextView getTwitterIdText() {
            return this.twitterIdText;
        }

        public TextView getDebtSumText() {
            return this.debtSumText;
        }

        public TextView getDebtDetailText() {
            return this.debtDetailText;
        }
    }
}
