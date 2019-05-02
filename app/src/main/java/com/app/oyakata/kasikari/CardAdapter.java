package com.app.oyakata.kasikari;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        private ImageView twitterIconImage;
        private TextView otakuNameText;
        private TextView twitterIdText;
        private TextView debtSumText;
        private TextView debtDetailText;

        ViewHolder(View view) {
            twitterIconImage = view.findViewById(R.id.twitterIconImage);
            otakuNameText = view.findViewById(R.id.otakuNameText);
            twitterIdText = view.findViewById(R.id.twitterIdText);
            debtSumText = view.findViewById(R.id.debtSumText);
            debtDetailText = view.findViewById(R.id.debtDetailText);
        }

        ImageView getTwitterIconImage() {
            return this.twitterIconImage;
        }

        TextView getOtakuNameText() {
            return this.otakuNameText;
        }

        TextView getTwitterIdText() {
            return this.twitterIdText;
        }

        TextView getDebtSumText() {
            return this.debtSumText;
        }

        TextView getDebtDetailText() {
            return this.debtDetailText;
        }
    }
}