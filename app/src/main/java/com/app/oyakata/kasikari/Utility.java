package com.app.oyakata.kasikari;

class Utility {
    static String convertYen2k(int yen, boolean kFlg){
        if(kFlg){
            return (double) yen / 1000 + " k";
        }else{
            return yen + " 円";
        }
    }
}
