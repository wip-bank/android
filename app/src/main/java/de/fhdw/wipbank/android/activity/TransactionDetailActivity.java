package de.fhdw.wipbank.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;

import de.fhdw.wipbank.android.R;
import de.fhdw.wipbank.android.model.Transaction;

public class TransactionDetailActivity extends AppCompatActivity {

    TextView textSender;
    TextView textReceiver;
    TextView textAmount;
    TextView textReference;
    TextView textTransactionDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);


        Transaction transaction = new Transaction();
        String transactionJson;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                transactionJson  = null;
            } else {
                transactionJson = extras.getString("transaction");
            }
        } else {
            transactionJson = (String) savedInstanceState.getSerializable("transaction");
        }
        if (transactionJson != null){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
            // JSON in Java-Objekt konvertieren
            transaction = gson.fromJson(transactionJson, Transaction.class);
        }

        textSender = (TextView) findViewById(R.id.textSender);
        textReceiver = (TextView) findViewById(R.id.textReceiver);
        textAmount = (TextView) findViewById(R.id.textAmount);
        textReference = (TextView) findViewById(R.id.textReference);
        textTransactionDate = (TextView) findViewById(R.id.textTransactionDate);

        String sender = String.format("%s (%s)", transaction.getSender().getOwner(), transaction.getSender().getNumber());
        textSender.setText(sender);

        String receiver = String.format("%s (%s)", transaction.getReceiver().getOwner(), transaction.getReceiver().getNumber());
        textReceiver.setText(receiver);

        NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);
        formatter.setMinimumFractionDigits(2);
        String amount = formatter.format(transaction.getAmount());
        textAmount.setText(amount);

        textReference.setText(transaction.getReference());

        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.GERMANY);
        textTransactionDate.setText(dateFormatter.format(transaction.getTransactionDate()));

    }
}
