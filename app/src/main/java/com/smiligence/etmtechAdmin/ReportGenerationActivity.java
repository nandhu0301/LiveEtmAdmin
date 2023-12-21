package com.smiligence.etmtechAdmin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.smiligence.etmtechAdmin.Adapter.ReportAdapter;
import com.smiligence.etmtechAdmin.bean.ItemDetails;
import com.smiligence.etmtechAdmin.bean.OrderDetails;
import com.smiligence.etmtechAdmin.common.CommonMethods;
import com.smiligence.etmtechAdmin.common.DateUtils;
import com.smiligence.etmtechAdmin.common.NetworkStateReceiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.smiligence.etmtechAdmin.common.Constant.BILLED_DATE_COLUMN;
import static com.smiligence.etmtechAdmin.common.Constant.FORMATED_BILLED_DATE;
import static com.smiligence.etmtechAdmin.common.Constant.ORDER_DETAILS_FIREBASE_TABLE;


public class ReportGenerationActivity extends AppCompatActivity  implements NetworkStateReceiver.NetworkStateReceiverListener{

    Button generate_pdf;

    ArrayList<OrderDetails> reportsList = new ArrayList<OrderDetails>();
    Spinner dropdown;
    ListView reportsListView;
    ImageView backButton;
    private File pdfFile;
    int todaysTotalQty;
    int todaysItemCount;
    ReportAdapter reportAdapter;
    ProgressDialog progress;
    DatabaseReference billdataref, storeNameRef, dbref;
    StorageReference storeNameStorage;
    //  StoreDetails storeDetails = new StoreDetails();

    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;
    final boolean[] onDataChangeCheck = {false};
    private NetworkStateReceiver networkStateReceiver;
    androidx.appcompat.app.AlertDialog alertDialog;
    ViewGroup viewGroup;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_generation);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ActionBar actionBar = getSupportActionBar();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        reportsListView = findViewById(R.id.reportsListView);
        generate_pdf = findViewById(R.id.generate_pdf);
        backButton = findViewById(R.id.btn_back);



        viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.internet_dialog, viewGroup, false);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(dialogView);
        alertDialog = builder.create();


//        Utils.getDatabase();
        dropdown = findViewById(R.id.reportSpinner);
        String[] reportPeriodDropDown = new String[]{"Today", "Yesterday", "Last 7 days", "Last 30 days"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, reportPeriodDropDown);

        dropdown.setAdapter(adapter);
        //  dbref = FirebaseDatabase.getInstance().getReference(USER_DETAILS_TABLE);
        billdataref =CommonMethods.fetchFirebaseDatabaseReference(ORDER_DETAILS_FIREBASE_TABLE);


        StrictMode.VmPolicy.Builder builderpolicy = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builderpolicy.build());
        builderpolicy.detectFileUriExposure();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_dashboard = new Intent(ReportGenerationActivity.this, DashBoardActivity.class);
                intent_dashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent_dashboard);
            }
        });


        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                reportsList.clear();


                switch (position) {
                    case 0:
                        final Query todayQuery = billdataref.orderByChild(BILLED_DATE_COLUMN).equalTo(DateUtils.fetchCurrentDate());
                        Log.e("currentdate",DateUtils.fetchCurrentDate());
                        fetchBillDetails(todayQuery);
                        break;
                    case 1:

                        final Query yesterdayQuery = billdataref.orderByChild(BILLED_DATE_COLUMN).equalTo(DateUtils.fetchYesterdayDate());
                        fetchBillDetails(yesterdayQuery);
                        Log.e("yesterday",DateUtils.fetchYesterdayDate());
                        break;
                    case 2:

                        final Query last7daysQuery = billdataref.orderByChild(FORMATED_BILLED_DATE).startAt(DateUtils.fetchLast7days());
                        Log.e("last7daysdate",DateUtils.fetchLast7days());
                        fetchBillDetails(last7daysQuery);

                        break;
                    case 3:

                        String last30Date = DateUtils.fetchLast30Days();
                        final Query last30daysQuery = billdataref.orderByChild(FORMATED_BILLED_DATE).startAt(last30Date);
                        fetchBillDetails(last30daysQuery);
                        Log.e("faa",DateUtils.fetchLast30Days());

                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        generate_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (reportsList == null || reportsList.isEmpty()) {
                    Toast.makeText(ReportGenerationActivity.this, "No datas found", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        createPdfWrapper();

                        File docsFolder = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            docsFolder = new File(getApplicationContext().getExternalFilesDir(null) + "/" + "Download");
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            docsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + "Download");
                        } else {
                            docsFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Download");
                        }


                        File file = new File(docsFolder.getAbsolutePath(), "AdminSalesReports.pdf");
                        Uri uri = FileProvider.getUriForFile(ReportGenerationActivity.this, ReportGenerationActivity.this.getApplicationContext().getPackageName() + ".provider", file);

                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.putExtra(Intent.EXTRA_SUBJECT, "AdminSalesReports");

                        i.putExtra(Intent.EXTRA_STREAM, uri);
                        i.setType("image/png");
                        startActivity(Intent.createChooser(i, "Sharing via"));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                   /* try {
                        createPdfWrapper();

                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.setType("text/plain");
                        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hi...");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Reports From FRIO");
                        File root = Environment.getExternalStorageDirectory();
                        String pathToMyAttachedFile = "/Download/AdminSalesReports.pdf";
                        File file = new File(root, pathToMyAttachedFile);
                        Uri uri = Uri.fromFile(file);
                        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));

                    } catch (FileNotFoundException e) {

                        e.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();

                    }*/

                }

            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        dropdown.setSelection(0);
        reportsList.clear();
        final Query billDetailsQuery = billdataref.orderByChild(BILLED_DATE_COLUMN).equalTo(DateUtils.fetchCurrentDate());

        fetchBillDetails(billDetailsQuery);
    }

    private void fetchBillDetails(Query billDetailsQuery) {

        final ArrayList<OrderDetails> billDetailsArrayList = new ArrayList<>();

        billDetailsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                reportsList.clear();

                if (reportAdapter != null) {
                    reportAdapter.clear();
                }

                for (DataSnapshot billSnapShot : dataSnapshot.getChildren()) {

                    billDetailsArrayList.add(billSnapShot.getValue(OrderDetails.class));
                    onDataChangeCheck[0] = true;
                    //  Toast.makeText(ReportGenerationActivity.this, ""+billDetailsArrayList, Toast.LENGTH_SHORT).show();
                }

                if (onDataChangeCheck[0] ) {

                    Iterator billIterator = billDetailsArrayList.iterator();

                    while (billIterator.hasNext()) {

                        todaysTotalQty = 0;
                        todaysItemCount = 0;
                        OrderDetails billDetails = (OrderDetails) billIterator.next();
                        Iterator itemIterator = billDetails.getItemDetailList().iterator();

                        while (itemIterator.hasNext()) {

                            ItemDetails itemDetails = (ItemDetails) itemIterator.next();
                            todaysTotalQty = todaysTotalQty + itemDetails.getItemBuyQuantity();
                            todaysItemCount = todaysItemCount + 1;

                        }

                        OrderDetails newbillDetails = new OrderDetails();
                        Log.e("newbillDetails",String.valueOf(billDetails.getPaymentamount()));
                        newbillDetails.setOrderId(billDetails.getOrderId());
                        newbillDetails.setOrderId((billDetails.getOrderId()));
                        newbillDetails.setPaymentDate(billDetails.getPaymentDate());
                        newbillDetails.setDiscountAmount(billDetails.getDiscountAmount());
                        newbillDetails.setTotalAmount(billDetails.getTotalAmount());
                        newbillDetails.setTotalItem(todaysItemCount);
                        // newbillDetails.set

                        ItemDetails newitemDetails = new ItemDetails();

                        newitemDetails.setItemQuantity(todaysTotalQty);
                        newbillDetails.setItemDetails(newitemDetails);
                        reportsList.add(newbillDetails);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Collections.sort(reportsList);
                    }

                    reportAdapter = new ReportAdapter(ReportGenerationActivity.this, reportsList);
                    reportsListView.setAdapter(reportAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void createPdfWrapper() throws FileNotFoundException, IOException {

        int hasWriteStoragePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
                    showMessageOKCancel("You need to allow access to Storage",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                REQUEST_CODE_ASK_PERMISSIONS);
                                    }
                                }
                            });
                    return;
                }
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
            return;
        } else
            createPdf();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    try {
                        createPdfWrapper();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(this, "WRITE_EXTERNAL Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void createPdf() throws FileNotFoundException, IOException {
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Download");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            docsFolder = new File(getApplicationContext().getExternalFilesDir(null) + "/" + "Download");

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            docsFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + "Download");
        } else {
            docsFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "Download");
        }


        if (!docsFolder.exists()) {
            docsFolder.mkdir();

        }
        File docsFolder1 = new File(Environment.getExternalStorageDirectory() + "/Save");
        if (!docsFolder1.exists()) {
            docsFolder1.mkdir();

        }
        pdfFile = new File(docsFolder.getAbsolutePath(), "AdminSalesReports.pdf");
        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, output);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.open();
        try {


            Bitmap bm_header = null;


            Paragraph spacing = new Paragraph("\n" + "\n");
            document.add(spacing);


            Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));
            Font bfBold1 = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, new BaseColor(0, 0, 0));
            Font bf12 = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            Chunk glue = new Chunk(new VerticalPositionMark());
            DecimalFormat df = new DecimalFormat("0");


            Paragraph spaceshell = new Paragraph("\n");
            document.add(spaceshell);

            Font font = new Font(Font.FontFamily.TIMES_ROMAN, 19.0f, Font.BOLD, BaseColor.BLACK);
            Chunk c_font = new Chunk("Sales Reports" + "\n" + "\n", font);
            Paragraph name = new Paragraph(c_font);
            name.setAlignment(Element.ALIGN_TOP);
            name.setAlignment(Element.ALIGN_CENTER);
            document.add(name);

         /*   Paragraph spaceshell1= new Paragraph( "\n" + "\n");
            document.add(spaceshell1);
*/
            float[] columnWidths = {22f, 29f, 27f, 27f, 37f};
            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(90f);
            insertCell(table, "BILL NO", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "BILL DATE", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "ITEM COUNT", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "ITEM QUANTITY", Element.ALIGN_CENTER, 1, bfBold12);
            insertCell(table, "BILL AMOUNT", Element.ALIGN_CENTER, 1, bfBold12);

            table.setHeaderRows(1);

            Iterator billDetailsIterator = reportsList.iterator();

            while (billDetailsIterator.hasNext()) {
                OrderDetails billDetails = (OrderDetails) billDetailsIterator.next();

                insertCell(table, (billDetails.getOrderId()), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, (billDetails.getPaymentDate()), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, String.valueOf(billDetails.getTotalItem()), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, String.valueOf(billDetails.getItemDetails().getItemQuantity()), Element.ALIGN_CENTER, 1, bf12);
                insertCell(table, String.valueOf(billDetails.getTotalAmount()), Element.ALIGN_CENTER, 1, bf12);
            }

            Paragraph pv = new Paragraph("");
            pv.add(table);
            pv.setAlignment(Element.ALIGN_CENTER);
            document.add(pv);

            /*Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_new_foreground);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            Image img = null;
            byte[] byteArray = stream.toByteArray();
            try {
                img = Image.getInstance(byteArray);
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Bitmap scaledBmp=Bitmap.createScaledBitmap(bm,1200,518,false);
            img.scaleToFit(80, 80);

            img.setAlignment(Element.ALIGN_BOTTOM);
            img.setAlignment(Element.ALIGN_CENTER);

            document.add(img);*/
            Font newfonts = new Font(Font.FontFamily.TIMES_ROMAN, 12.0f, Font.BOLD, BaseColor.BLACK);

            Chunk chunknew = new Chunk("POWERED BY SMILIGENCE", newfonts);

            Paragraph Smiligence = new Paragraph(chunknew);
            Smiligence.setAlignment(Element.ALIGN_BOTTOM);
            Smiligence.setAlignment(Element.ALIGN_CENTER);
            document.add(Smiligence);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        document.close();

        previewPdf();

    }

    private void previewPdf() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        PackageManager packageManager = getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");

        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);

        if (list.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(pdfFile);
            intent.setDataAndType(uri, "application/pdf");
            startActivity(intent);

        } else {
            Toast.makeText(this, "Download a PDF Viewer to see the generated PDF", Toast.LENGTH_SHORT).show();
        }

    }

    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {
        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        table.getDefaultCell().setBorderColor(BaseColor.WHITE);
        cell.setBorderColor(BaseColor.BLACK);
        //in case there is no text and you wan to create an empty row
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(25f);
        }
        //add the call to the table
        table.addCell(cell);
        // progress.dismiss();
    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext (), DashBoardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    protected void onPause() {
        startNetworkBroadcastReceiver(this);
        unregisterNetworkBroadcastReceiver(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        startNetworkBroadcastReceiver(this);
        registerNetworkBroadcastReceiver(this);
        super.onResume();
    }

    @Override
    public void networkAvailable() {
        alertDialog.dismiss();
    }

    @Override
    public void networkUnavailable() {
        if (!((Activity) ReportGenerationActivity.this).isFinishing()) {
            showCustomDialog();
        }
    }

    public void startNetworkBroadcastReceiver(Context currentContext) {
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener((NetworkStateReceiver.NetworkStateReceiverListener) currentContext);
        registerNetworkBroadcastReceiver(currentContext);
    }


    public void registerNetworkBroadcastReceiver(Context currentContext) {
        currentContext.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void unregisterNetworkBroadcastReceiver(Context currentContext) {
        currentContext.unregisterReceiver(networkStateReceiver);
    }

    private void showCustomDialog() {

        alertDialog.setCancelable(false);
        alertDialog.show();

    }

}