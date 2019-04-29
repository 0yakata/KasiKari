package com.app.oyakata.kasikari;

class Debt {

    private int debtId;
    private int yen;
    private String memo;
    private int doneFlg;

    int getDebtId() { return debtId; }

    void setDebtId(int debtId) { this.debtId = debtId; }

    int getYen() { return yen; }

    String getYenString() {
        return String.valueOf(yen);
    }

    void setYen(int yen) {
        this.yen = yen;
    }

    String getMemo() {
        return memo;
    }

    void setMemo(String memo) {
        this.memo = memo;
    }

    boolean getDoneFlgBool() {
        return doneFlg == 1;
    }

    void setDoneFlg(int doneFlg) {
        this.doneFlg = doneFlg;
    }

}
