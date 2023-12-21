package com.smiligence.etmtechAdmin.common;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CommonMethods extends AppCompatActivity {

    //public static  int reviewCountInt;
    public static DatabaseReference fetchFirebaseDatabaseReference(String FirebaseTableName) {
        //for Dev

//        DatabaseReference mDataRef = FirebaseDatabase.getInstance ("https://techdev-da79b-default-rtdb.asia-southeast1.firebasedatabase.app/")
//                .getReference ( FirebaseTableName);

        //for test
//       DatabaseReference mDataRef = FirebaseDatabase.getInstance("https://ftstestenv-default-rtdb.asia-southeast1.firebasedatabase.app/")
//               .getReference(FirebaseTableName);

        // for test https://testetmpro-default-rtdb.asia-southeast1.firebasedatabase.app/
        DatabaseReference mDataRef = FirebaseDatabase.getInstance("https://liveetmproject-default-rtdb.firebaseio.com/").getReference(FirebaseTableName);

//        //for live
//        DatabaseReference mDataRef = FirebaseDatabase.getInstance("https://favtechshop-501d7-default-rtdb.asia-southeast1.firebasedatabase.app/")
//                .getReference(FirebaseTableName);


        return mDataRef;
    }
    

    public static StorageReference fetchFirebaseStorageReference(String FirebaseTableName) {


        StorageReference mStorageRef = FirebaseStorage.getInstance("gs://liveetmproject.appspot.com")
                .getReference(FirebaseTableName);
//        StorageReference mStorageRef = FirebaseStorage.getInstance("gs://favtechshop-501d7.appspot.com")
//                .getReference(FirebaseTableName);

        return mStorageRef;
    }


    public static String encrypt(String password) throws Exception {
        String key = "1234567812345678";
        String iv = "1234567812345678";

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);

        byte[] cipherText = cipher.doFinal(password.getBytes());
        // String encryptedValue= Base64.encodeBase64String(cipherText);
        String encryptedValue = new String(Base64.encodeBase64(cipherText));
        return encryptedValue;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String password) throws Exception {

        String key = "1234567812345678";
        String iv = "1234567812345678";

        java.util.Base64.Decoder decoder = java.util.Base64.getDecoder();
        byte[] encrypted1 = decoder.decode(password);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

        cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original);

        return originalString;
    }

    //Secreat key generation function
    public static SecretKeySpec generatekey(String password) throws Exception {

        final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        messageDigest.update(bytes, 0, bytes.length);
        byte[] key = messageDigest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;

    }

    public static String generateString(int length) {
        // char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789!@#$%^&*".toCharArray ();
        char[] chars = "The quick brown fox jumps over the lazy dog.".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public static void setSpinnerError(Spinner spinner, String error) {
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError("error"); // any name of the error will do
            selectedTextView.setTextColor(Color.RED); //text color in which you want your error message to be displayed
            selectedTextView.setText(error); // actual error message
            spinner.performClick(); // to open the spinner list if error is found.
        }
    }

    public static void loadSalesBarChart(BarChart salesBarChart, HashMap<String, Integer> billFinalAmountHashMap) {
        ArrayList<String> timeArrayListXAxis = DateUtils.fetchTimeInterval();

        // initialize the Bardata with argument labels and dataSet
        BarData data = new BarData(timeArrayListXAxis, getBillFinalAmount(timeArrayListXAxis, billFinalAmountHashMap));
        salesBarChart.setData(data);
    }

    private static ArrayList getBillFinalAmount(ArrayList<String> timeInterval, HashMap<String, Integer> billFinalAmountHashMap) {
        ArrayList billAmountArrayList = new ArrayList();
        ArrayList salesDataSets = new ArrayList();


        int billAmount = 0;

        for (int i = 0, j = 1; i < timeInterval.size(); ) {

            for (Map.Entry<String, Integer> entry : billFinalAmountHashMap.entrySet()) {
                String startTime = timeInterval.get(i);
                String endTime = timeInterval.get(j);

                if (DateUtils.isHourInInterval(entry.getKey(), startTime, endTime)) {
                    billAmount += entry.getValue();


                }
            }

            BarEntry value = new BarEntry(billAmount, i);
            billAmountArrayList.add(value);
            billAmount = 0;

            if (j == timeInterval.size() - 1) {
                break;
            } else {
                i++;
                j++;
            }
        }
        BarDataSet salesBarDataSet = new BarDataSet(billAmountArrayList, "Bill Amount");
        // barDataSet.setColor(Color.rgb(0, 155, 0));
        salesBarDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        salesDataSets.add(salesBarDataSet);
        return salesDataSets;
    }

   /* public static void loadBarChart(BarChart barChart, ArrayList<String> billTimeList){

        ArrayList<String> timeArrayListXAxis  = new ArrayList<>();
        timeArrayListXAxis = DateUtils.fetchTimeInterval();

        BarData data = new BarData(timeArrayListXAxis, getBillCountData(timeArrayListXAxis,billTimeList));
        barChart.setData(data);
    }

    private static ArrayList getBillCountData(ArrayList<String> timeInterval, ArrayList<String> billTimeList) {

        ArrayList billTimeArrayList = new ArrayList();
        ArrayList dataSets = new ArrayList();

        int counter = 0;

        System.out.println("billTimeList" + billTimeList);

        for (int i = 0, j = 1; i < timeInterval.size(); ) {

            for (String billTime : billTimeList) {
                String startTime = timeInterval.get(i);
                String endTime = timeInterval.get(j);
                System.out.println("startTime" + startTime);
                System.out.println("endTime" + billTime);

                if (DateUtils.isHourInInterval(billTime, startTime, endTime)) {
                    counter++;

                }
            }
            System.out.println("countermmmm" + i);
            BarEntry value = new BarEntry(counter, i);
            billTimeArrayList.add(value);
            counter = 0;

            if (j == timeInterval.size() - 1) {
                break;
            } else {
                i++;
                j++;
            }
        }
        BarDataSet barDataSet = new BarDataSet(billTimeArrayList, "Bill");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSets.add(barDataSet);

        return dataSets;
    }
*/

    public static void loadBarChart(BarChart barChart, ArrayList<String> billTimeList) {

        ArrayList<String> timeArrayListXAxis = new ArrayList<>();
        timeArrayListXAxis = DateUtils.fetchTimeInterval();

        BarData data = new BarData(timeArrayListXAxis, getBillCountData(timeArrayListXAxis, billTimeList));
        barChart.setData(data);
    }

    private static ArrayList getBillCountData(ArrayList<String> timeInterval, ArrayList<String> billTimeList) {

        ArrayList billTimeArrayList = new ArrayList();
        ArrayList dataSets = new ArrayList();

        int counter = 0;
        for (int i = 0, j = 1; i < timeInterval.size(); ) {

            for (String billTime : billTimeList) {
                String startTime = timeInterval.get(i);
                String endTime = timeInterval.get(j);


                if (DateUtils.isHourInInterval(billTime, startTime, endTime)) {
                    counter++;

                }
            }

            BarEntry value = new BarEntry(counter, i);
            billTimeArrayList.add(value);
            counter = 0;

            if (j == timeInterval.size() - 1) {
                break;
            } else {
                i++;
                j++;
            }
        }
        BarDataSet barDataSet = new BarDataSet(billTimeArrayList, "Bill");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSets.add(barDataSet);

        return dataSets;
    }
   /* public  static  void reviewCount(){
        DatabaseReference itemReviewDataRef;
        ArrayList<ItemReviewAndRatings> itemDetailsArrayList=new ArrayList<>();
        final ItemReviewAndRatings[] itemReviewAndRatings = new ItemReviewAndRatings[1];
     //   final boolean[] check = tr;
        itemReviewDataRef = CommonMethods.fetchFirebaseDatabaseReference(ITEM_RATING_REVIEW_TABLE);
        Query itemApprovalStatusQuery = itemReviewDataRef.orderByChild("itemRatingReviewStatus").equalTo("Waiting For Approval");

        itemApprovalStatusQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              //  if (check[0] = true) {

                    if (dataSnapshot.getChildrenCount() > 0) {
                        itemDetailsArrayList.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            itemReviewAndRatings[0] = dataSnapshot1.getValue(ItemReviewAndRatings.class);
                            if ("Waiting For approval".equalsIgnoreCase(itemReviewAndRatings[0].getItemRatingReviewStatus())) {
                                itemDetailsArrayList.add(itemReviewAndRatings[0]);
                            }
                            reviewCountInt=itemDetailsArrayList.size();
                        }



                    }

               // }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
}

