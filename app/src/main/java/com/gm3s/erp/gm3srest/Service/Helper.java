package com.gm3s.erp.gm3srest.Service;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario on 27/11/15.
 */
public class Helper {


    /*public static boolean isConnected(Context context) {
        boolean bConectado = false;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connMgr.getAllNetworkInfo();
        for (int i = 0; i < 2; i++) {
            if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                bConectado = true;
            }
        }
        return bConectado;

    }*/


    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnected2(Context context){
        NetworkInfo info = Helper.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static boolean isConnectedWifi(Context context){
        NetworkInfo info = Helper.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isConnectedMobile(Context context){
        NetworkInfo info = Helper.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }


    public static boolean isConnected3(Context context){
        if(isConnectedWifi(context)){
            return true;
        }
        if(isConnectedMobile(context)){
            return  true;
        }
        else{
            return false;
        }
    }

    public static boolean validate(EditText[] a) {
        boolean flag = false;
        for (int i = 0; i< a.length; i++){
            if (a[i].getText().toString().trim().equals(""))
                flag = false;
            else
                flag = true;
        }
        return flag;
    }

    public static boolean validate2(String[] a) {
        boolean flag = false;
        for (int i = 0; i< a.length; i++){
            if (a[i].toString().trim().equals(""))
                flag = false;
            else
                flag = true;
        }
        return flag;
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public static byte[] getBytesFromInputStream(InputStream is) throws IOException
    {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
        {
            byte[] buffer = new byte[0xFFFF];

            for (int len; (len = is.read(buffer)) != -1;)
                os.write(buffer, 0, len);

            os.flush();

            return os.toByteArray();
        }
    }

    public static String numberFormat(String numero){
        int aux = 3;
        char [] array = numero.toCharArray();
        List<Character> array2 = new ArrayList<Character>();
        String numeroF="$ ";
        array2.add(0,array[array.length-1]);
        array2.add(1,array[array.length-2]);
        array2.add(2,array[array.length-3]);
        int aux2= array.length-3;

        for(int i=aux2; i>0; i-=3){
            switch (i){
                case 1:
                    if(array[i-1] == '-'){
                        array2.add(aux-1,array[i-1]);
                    }
                    else{
                        array2.add(aux,array[i-1]);
                    }
                    break;
                case 2:
                    array2.add(aux,array[i-1]);
                    array2.add(aux+1,array[i-2]);
                    break;
                case 3:
                    array2.add(aux,array[i-1]);
                    array2.add(aux+1,array[i-2]);
                    array2.add(aux+2,array[i-3]);
                    break;
                default:
                    array2.add(aux,array[i-1]);
                    array2.add(aux+1,array[i-2]);
                    array2.add(aux+2,array[i-3]);
                    array2.add(aux+3,',');
                    aux = aux +4;
                    break;
            }
        }

        String word= new String();
        for(char c:array2){
            word= word+ c;
        }


        for (int j = word.length() - 1; j >= 0; j--){
            numeroF = numeroF + word.charAt(j);
        }
        return numeroF;
    }


    public static Double formatDouble(Double number){
        return new BigDecimal(number.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }


    public static BigDecimal formatBigDec(BigDecimal number){
        return  number.setScale(2, RoundingMode.HALF_UP);
        //return new BigDecimal(number.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static byte[] concatBytes(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c= new byte[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
