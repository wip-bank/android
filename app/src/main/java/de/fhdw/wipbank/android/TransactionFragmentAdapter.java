package de.fhdw.wipbank.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.fhdw.wipbank.android.model.Transaction;


public class TransactionFragmentAdapter extends ArrayAdapter<Transaction> {

    Context context;
    List<Transaction> transactions;
    String accountNumber;

    public TransactionFragmentAdapter(Context c, List<Transaction> transactions) {
        super(c, R.layout.transaction_row, R.id.textFromTo, transactions);
        this.context = c;
        this.transactions = transactions;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        accountNumber = prefs.getString(context.getString(R.string.pref_accountNumber_key), "");
    }

    class MyViewHolder {
        TextView textDay;
        TextView textMonth;
        TextView textFromTo;
        TextView textRef;

        MyViewHolder(View v) {

            textDay = (TextView) v.findViewById(R.id.textDay);
            textMonth = (TextView) v.findViewById(R.id.textMonth);
            textFromTo = (TextView) v.findViewById(R.id.textFromTo);
            textRef = (TextView) v.findViewById(R.id.textRef);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        //int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        holder.textDay.setText(day);
        holder.textMonth.setText(month);
        if(transaction.getReceiver().getNumber() != accountNumber){
            holder.textFromTo.setText(transaction.getReceiver().getOwner());
        }else{
            holder.textFromTo.setText(transaction.getSender().getOwner());
        }
        holder.textRef.setText(transaction.getReference());
        return row;
    }
}