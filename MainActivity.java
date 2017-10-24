package myeverlastinng.net.paygate;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.Payment;
import com.interswitchng.sdk.payment.android.inapp.PayWithCard;
import com.interswitchng.sdk.payment.android.inapp.PayWithToken;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.model.PurchaseResponse;
import com.interswitchng.sdk.util.RandomString;

import org.w3c.dom.Text;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private Activity activity;
    private Context context;
    private String tref;
    private Button doPay;
    private Button ptoken;
    private TextView txtStatus;
    private TextView token;
    private TextView token2;
    private TextView expiry;
    private TextView expiry2;
    private EditText amt;

    private String PaymentToken;
    private String PaymentTokenExpiry;
    private String PanLast4Digits;
    private String customerID;

    String pref = "mypref";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        Payment.overrideApiBase(Payment.SANDBOX_API_BASE);
        Passport.overrideApiBase(Passport.SANDBOX_API_BASE);


        amt = (EditText) findViewById(R.id.editAmt);

        doPay = (Button) findViewById(R.id.btnPay);
        ptoken = (Button) findViewById(R.id.btntoken);
        token = (TextView) findViewById(R.id.txttoken);
        token2 = (TextView) findViewById(R.id.txttoken2);
        expiry = (TextView) findViewById(R.id.txtExpiry);
        expiry2 = (TextView) findViewById(R.id.txtexpiry2);

        //-------------PAY WITH CARD ---------------- //
        doPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Random rnd = new Random();
                int n = 100000 + rnd.nextInt(9999999);
                customerID = "JB-" + n + "-SDK";

                tref = RandomString.numeric(12);
                String amtvalue = "666";
                        //amt.getText().toString().trim();
                final RequestOptions options = RequestOptions.builder()
                        .setClientId("IKIA9614B82064D632E9B6418DF358A6A4AEA84D7218")
                        .setClientSecret("XCTiBtLy1G9chAnyg0z3BcaFK4cVpwDg/GTw2EmjTZ8=")
                        .build();
                PayWithCard payWithCard = new PayWithCard(activity, customerID,"JB SDK DEMO",amtvalue,"NGN",options, new IswCallback<PurchaseResponse>() {
                    @Override
                    public void onError(Exception error){
                        Util.notify(context, "ERROR", error.getMessage(), "Close", false);
                        System.out.println(error);
                        Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG);
                        Log.d("Debug", error.getMessage());

                        //Set Response Field
                        txtStatus = (TextView) findViewById(R.id.txtStatus);
                        txtStatus.setText("Error: " + error.getMessage());
                    }
                    @Override
                    public void onSuccess(final PurchaseResponse purchaseResponse){
                        //SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = getSharedPreferences(pref, MODE_PRIVATE).edit();
                        editor.putString("token", purchaseResponse.getToken());
                        editor.putString("tokenExpiry", purchaseResponse.getTokenExpiryDate());
                        editor.commit();

                        String ref = purchaseResponse.getTransactionIdentifier();
                        String payToken = purchaseResponse.getToken();
                        String payTokenExp = purchaseResponse.getTokenExpiryDate();
                        String payLast4Digits = purchaseResponse.getPanLast4Digits();
                        String cardBrand = purchaseResponse.getCardType();
                        String responseCode = purchaseResponse.getResponseCode();

                        //Util.notify(context,"Success","Ref: "+ ref+ " Tranx Ref : " + purchaseResponse.getTransactionRef(),"Close", false);
                        //System.out.println("Success : " + ref);
                        //Toast.makeText(getApplicationContext(), "Success: "+ref,Toast.LENGTH_LONG);

                        //Set Response Field
                        txtStatus = (TextView) findViewById(R.id.txtStatus);
                        txtStatus.setText("Success: " + ref + "     " + "Response Code: " + responseCode);

                        //Set Token Field
                        token2 = (TextView) findViewById(R.id.txttoken2);
                        token2.setText("Token: " + payToken + "     " + "Card Type: " + cardBrand);

                        PaymentToken = payToken;

                        //Set Expiry Field
                        expiry2 = (TextView) findViewById(R.id.txtexpiry2);
                        expiry2.setText("Expiry: " + payTokenExp);
                        PaymentTokenExpiry = payTokenExp;

                        //Set Last4Digits
                        PanLast4Digits = payLast4Digits;
                    }
                });
                payWithCard.start();
            }
        });
        // ---------------------------------------------------------------------------------------------------  //


        //  PAY WITH TOKEN   //
        ptoken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = getSharedPreferences(pref, MODE_PRIVATE);
                String tk = sharedPref.getString("token", null);
                String dat = sharedPref.getString("tokenExpiry", null);
                token.setText(tk);
                expiry.setText(dat);
                System.out.println(tk + " " + dat);
                RequestOptions options = RequestOptions.builder()
                        .setClientId("IKIA9614B82064D632E9B6418DF358A6A4AEA84D7218")
                        .setClientSecret("XCTiBtLy1G9chAnyg0z3BcaFK4cVpwDg/GTw2EmjTZ8=")
                        .build();
                PayWithToken payWithToken = new PayWithToken(activity, "John", "500", "5123459XXXXXXXXXXX", "1801", "NGN",
                        "Verve", "0011", "JB TEST PAYMENT", options, new IswCallback<PurchaseResponse>() {
                    @Override
                    public void onError(Exception error) {
                        // Handle error
                        // Payment not successful.
                        System.out.println(error);
                        Toast.makeText(getApplicationContext(), error.getMessage(),Toast.LENGTH_LONG);
                        Log.d("Debug", error.getMessage());
                        txtStatus = (TextView) findViewById(R.id.txtStatus);
                        txtStatus.setText("Error: " + error.getMessage());
                    }

                    @Override
                    public void onSuccess(final PurchaseResponse response) {
            /* Handle success
               Payment successful. The response object contains fields transactionIdentifier,
               message, amount, token, tokenExpiryDate, panLast4Digits, transactionRef and cardType.
               Save the token, tokenExpiryDate cardType and panLast4Digits
               in order to pay with the token in the future.
            */
                        String ref = response.getTransactionIdentifier();

                        //Util.notify(context,"Success","Ref: "+ ref+ " Tranx Ref : " + response.getTransactionRef(),"Close", false);
                        //System.out.println("Success : " + ref);
                        //Toast.makeText(getApplicationContext(), "Success: "+ref,Toast.LENGTH_LONG);

                        String responseCode = response.getResponseCode();

                        txtStatus = (TextView) findViewById(R.id.txtStatus);
                        txtStatus.setText("Success: " + ref + "     " + responseCode);
                    }
                });
                payWithToken.start();
            }
        });
        // ---------------------------------------------------------------------------------------------------  //

    }
}
