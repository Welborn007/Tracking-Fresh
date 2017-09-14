package com.kesari.trackingfresh.Settings.MyCards;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.kesari.trackingfresh.R;
import com.kesari.trackingfresh.network.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import id.zelory.compressor.Compressor;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by kesari on 06/09/17.
 */

public class CardsSettingFragment extends Fragment {

    public static Gson gson;
    public static RecyclerView recList;
    private static LinearLayoutManager LayoutManager;
    //public static RecyclerView.Adapter adapter;
    Cards_RecyclerAdpater adapter;
    private Button btnSubmit;
    private String TAG = this.getClass().getSimpleName();
    private static final int REQUEST_SCAN = 100;
    File dest;
    MyApplication myApplication;
    ListView listview;

    public CardsSettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_settings, container, false);

        gson = new Gson();
        recList = (RecyclerView) view.findViewById(R.id.recyclerView);

        recList.setHasFixedSize(true);
        LayoutManager = new LinearLayoutManager(getActivity());
        LayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(LayoutManager);

        listview = (ListView) view.findViewById(R.id.listview);

        myApplication = (MyApplication) getApplicationContext();
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);

        if(myApplication.getCardList() != null)
        {
            adapter = new Cards_RecyclerAdpater(myApplication.getCardList(), getActivity());
            listview.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onScanPress(v);
            }
        });

        return view;
    }

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(getActivity(), CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        //scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true);

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, REQUEST_SCAN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SCAN) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                final CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";

                Log.i("CardNumber",scanResult.cardNumber);
                Log.i("CardNumberFormatted",scanResult.getFormattedCardNumber());
                Log.i("Card Name",scanResult.cardholderName);
                Log.i("cvv",scanResult.cvv);
                Log.i("expiry",scanResult.expiryMonth + "/" + scanResult.expiryYear);

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

                Bitmap card = CardIOActivity.getCapturedCardImage(data);
                //mResultImage.setImageBitmap(card);

                final String filename = "TKFCards" + " " + timeStamp + ".png";
                File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"TKF");

                if (!sd.exists()) {
                    if (!sd.mkdirs()) {
                        Log.e("TravellerLog :: ", "Problem creating Image folder");
                    }
                }

                dest = new File(sd, filename);

                //Bitmap bitmap = (Bitmap)data.getExtras().get("data");
                try {
                    FileOutputStream out = new FileOutputStream(dest);
                    card.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.i("Original", getReadableFileSize(dest.length()));

                // Compress image using RxJava in background thread
                Compressor.getDefault(getActivity())
                        .compressToFileAsObservable(dest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<File>() {
                            @Override
                            public void call(File file) {
                                Log.i("Path",dest.getAbsolutePath());
                                dest = file;
                                Log.i("Compressed", getReadableFileSize(dest.length()));

                                CardPOJO cardPOJO = new CardPOJO();

                                cardPOJO.setCard_image(dest.getAbsolutePath());
                                cardPOJO.setCardholderName(scanResult.cardholderName);
                                cardPOJO.setCardNumber(scanResult.cardNumber);
                                cardPOJO.setCvv(scanResult.cvv);
                                cardPOJO.setExpiry(scanResult.expiryMonth + "/" + scanResult.expiryYear);
                                cardPOJO.setFormattedCardNumber(scanResult.getFormattedCardNumber());

                                /*for (Iterator<CardPOJO> it = myApplication.getCardList().iterator(); it.hasNext(); ) {
                                    CardPOJO cardPOJO1 = it.next();

                                    if (!cardPOJO1.isDefault())
                                    {
                                       cardPOJO.setDefault(true);
                                    }
                                }*/

                                myApplication.addCards(cardPOJO);

                                if(myApplication.getCardList() != null)
                                {
                                    adapter = new Cards_RecyclerAdpater(myApplication.getCardList(), getActivity());
                                    listview.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }

                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.i("Error", throwable.getMessage());
                            }
                        });

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }

    public String getReadableFileSize(long size) {

        if (size <= 0) {

            return "0";

        }

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};

        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}
