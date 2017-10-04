package de.fhdw.wipbank.android.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.fhdw.wipbank.android.R;
import de.fhdw.wipbank.android.model.Transaction;


/**
 * TransactionFragmentAdapter: dient der Anzeige von Listenelementen in der ListView in {@link TransactionFragment}.
 */
public class TransactionFragmentAdapter extends ArrayAdapter<Transaction> {

    private Context context;
    private List<Transaction> transactions;

    /** Kontruktor des TransactionFragmentAdapter
     * @param c Context
     * @param transactions Liste der anzuzeigenden Transaktionen
     */
    TransactionFragmentAdapter(Context c, List<Transaction> transactions) {
        super(c, R.layout.transaction_row, R.id.textFromTo, transactions);
        this.context = c;
        this.transactions = transactions;
    }

    /**
     * Hält die Views des Listen-Elements:
     * textDay, textMonth, textFromTo, textRef und textAmount.
     */
    private class MyViewHolder {
        TextView textDay;
        TextView textMonth;
        TextView textFromTo;
        TextView textRef;
        TextView textAmount;

        MyViewHolder(View v) {

            textDay = (TextView) v.findViewById(R.id.textDay);
            textMonth = (TextView) v.findViewById(R.id.textMonth);
            textFromTo = (TextView) v.findViewById(R.id.textFromTo);
            textRef = (TextView) v.findViewById(R.id.textRef);
            textAmount = (TextView) v.findViewById(R.id.textAmount);
        }
    }



    /** getView: Gibt den View zurück. Steuert, wie die Transaktion angezeigt wird.
     * @param position
     * @param convertView
     * @param parent
     * @return View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String accountNumber = prefs.getString(context.getString(R.string.pref_accountNumber_key), "");

        View row = convertView;
        MyViewHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.transaction_row, parent, false);
            holder = new MyViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (MyViewHolder) row.getTag();
        }

        Transaction transaction = transactions.get(position);
        Date date = transaction.getTransactionDate();


        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        DecimalFormat dayFormatter = new DecimalFormat("00");

        String day = dayFormatter.format(cal.get(Calendar.DAY_OF_MONTH));
        String month = new SimpleDateFormat("MMM").format(cal.getTime()).toUpperCase();
        //int year = cal.get(Calendar.YEAR);
        holder.textDay.setText(day);
        holder.textMonth.setText(month);
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        String amount = formatter.format(transaction.getAmount());
        if (transaction.getSender().getNumber().equals(accountNumber)) {
            // Transaktion vom BenutzerAccount an jemand anders
            holder.textFromTo.setText(transaction.getReceiver().getOwner());
            // Farbe von Amount rot
            holder.textAmount.setTextColor(ContextCompat.getColor(context, R.color.amount_negative));
            // Minus vor Amount
            amount = "-" + amount;
        } else {
            // Transaktion an den Benutzer
            holder.textFromTo.setText(transaction.getSender().getOwner());
            holder.textAmount.setTextColor(ContextCompat.getColor(context, R.color.amount_positive));
        }
        holder.textRef.setText(transaction.getReference());
        holder.textAmount.setText(amount);
        return row;
    }
}