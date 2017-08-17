package de.fhdw.wipbank.android.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.fhdw.wipbank.android.R;
import de.fhdw.wipbank.android.account.AccountAsyncTask;
import de.fhdw.wipbank.android.account.AccountService;
import de.fhdw.wipbank.android.model.Account;
import de.fhdw.wipbank.android.model.Transaction;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TransactionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionFragment extends Fragment implements AccountAsyncTask.OnAccountUpdateListener {

    TransactionFragmentAdapter transactionFragmentAdapter;
    ListView listTransactions;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView textBalance;

    private OnFragmentInteractionListener mListener;



    public TransactionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.

     *
     * @return A new instance of fragment TransactionFragment.
     */

    public static TransactionFragment newInstance() {
        TransactionFragment fragment = new TransactionFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);



        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listTransactions = (ListView) getView().findViewById(R.id.listTransactions);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefresh);
        textBalance = (TextView) getView().findViewById(R.id.textBalance);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                update();
            }
        });


        // src: http://nlopez.io/swiperefreshlayout-with-listview-done-right/
        listTransactions.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                int topRowVerticalPosition = (listTransactions == null || listTransactions.getChildCount() == 0) ? 0 : listTransactions.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });


        listTransactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Transaction transaction = transactionFragmentAdapter.getItem(position);
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

                Intent intent = new Intent(getContext(), TransactionDetailActivity.class);
                intent.putExtra("transaction", gson.toJson(transaction));
                startActivity(intent);
            }
        });


        loadTransactions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void loadTransactions(){
        List<Transaction> transactions;
        Account account = AccountService.getAccount();
        transactions = account.getTransactions();

        if (transactions == null){
            transactions = new ArrayList<Transaction>();
        }

        transactionFragmentAdapter = new TransactionFragmentAdapter(getContext(), transactions);
        listTransactions.setAdapter(transactionFragmentAdapter);

        BigDecimal balance = new BigDecimal(0);
        for (Transaction transaction : transactions) {
            if(transaction.getSender().getNumber().equals(account.getNumber()))
                // Benutzer überweist Geld an wen anders
                balance = balance.subtract(transaction.getAmount());
            else
                // Benutzer bekommt Geld
                balance = balance.add(transaction.getAmount());
        }
        switch(balance.compareTo(new BigDecimal(0))) {
            case 1:
                textBalance.setTextColor(ContextCompat.getColor(getContext(), R.color.amount_positive)) ;
                break;
            case -1:
                textBalance.setTextColor(ContextCompat.getColor(getContext(), R.color.amount_negative)) ;
                break;
            case 0:
                textBalance.setTextColor(ContextCompat.getColor(getContext(), R.color.amount_neutral)) ;
                break;
        }

        NumberFormat formatter = NumberFormat.getInstance(Locale.GERMANY);
        formatter.setMinimumFractionDigits(2);
        textBalance.setText(formatter.format(balance));
    }

    public void update(){
        // Account über REST Service laden
        new AccountAsyncTask(this, getContext()).execute();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAccountUpdateSuccess() {
        loadTransactions();
    }

    @Override
    public void onAccountUpdateError(String errorMsg) {
        String toastMsg;
        if (errorMsg.equals("null")){
            toastMsg = "Keine Verbindung zum Server";
        }else{
            toastMsg = "Response: " + errorMsg;
        }
        Toast.makeText(getContext(), toastMsg, Toast.LENGTH_SHORT).show();
    }
}
