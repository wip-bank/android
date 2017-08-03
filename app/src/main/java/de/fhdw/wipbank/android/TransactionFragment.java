package de.fhdw.wipbank.android;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.List;

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
public class TransactionFragment extends Fragment implements AccountAsyncTask.OnAccountUpdatedListener {

    TransactionFragmentAdapter transactionFragmentAdapter;
    ListView listView;
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
        listView = (ListView) getView().findViewById(R.id.listTransactions);
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefresh);
        textBalance = (TextView) getView().findViewById(R.id.textBalance);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i("Daniel", "onRefresh called from SwipeRefreshLayout");

                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                update();
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
        transactionFragmentAdapter = new TransactionFragmentAdapter(getContext(), transactions);
        listView.setAdapter(transactionFragmentAdapter);

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


        textBalance.setText("" + balance);
    }

    public void update(){
        AccountAsyncTask.updateAccount(this, getContext());
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAccountUpdateSuccess() {
        loadTransactions();
    }

    @Override
    public void onAccountUpdateError(String errorMsg) {
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }
}
