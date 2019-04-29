package com.app.oyakata.kasikari;

class Card {
    private int otakuId;
    private String twitterId;
    private String otakuName;
    private String debtSum;
    private String debtDetail;

    Card(int otakuId, String twitterId, String otakuName, String debtSum, String debtDetail) {
        this.otakuId = otakuId;
        this.twitterId = twitterId;
        this.otakuName = otakuName;
        this.debtSum = debtSum;
        this.debtDetail = debtDetail;
    }

    int getOtakuId() {
        return otakuId;
    }

    String getTwitterId() {
        return twitterId;
    }

    String getOtakuName() {
        return otakuName;
    }

    String getDebtSum() {
        return debtSum;
    }

    String getDebtDetail() {
        return debtDetail;
    }

}
