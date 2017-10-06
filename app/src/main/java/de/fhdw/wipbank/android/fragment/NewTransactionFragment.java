package de.fhdw.wipbank.android.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigDecimal;

import de.fhdw.wipbank.android.R;
import de.fhdw.wipbank.android.model.Account;
import de.fhdw.wipbank.android.model.Transaction;
import de.fhdw.wipbank.android.rest.TransactionAsyncTask;

/**
 * NewTransactionFragment: In diesem Fragment kann eine Überweisung getätigt werden.
 * @author Daniel Sawenko
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewTransactionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewTransactionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewTransactionFragment extends Fragment implements TransactionAsyncTask.OnTransactionExecuteListener{


    EditText edtReceiverNumber;
    EditText edtAmount;
    EditText edtReference;
    Button btnExecute;


    private OnFragmentInteractionListener mListener;

    public NewTransactionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewTransactionFragment.
     */
    public static NewTransactionFragment newInstance() {
        NewTransactionFragment fragment = new NewTransactionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /** onCreate-Methode
     * @param savedInstanceState SavedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /** onActivityCreated-Methode
     * @param savedInstanceState SavedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        edtReceiverNumber = (EditText) getView().findViewById(R.id.edtReceiverNumber);
        edtAmount = (EditText) getView().findViewById(R.id.edtAmount);
        edtReference = (EditText) getView().findViewById(R.id.edtReference);
        btnExecute = (Button) getView().findViewById(R.id.btnExecute);
        btnExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execute();
            }
        });
    }

    /** onCreateView-Methode
     * @param inflater Inflater
     * @param container Container
     * @param savedInstanceState SavedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_transaction, container, false);
    }

    /** onAttach-Methode
     * @param context Context
     */
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

    /**
     * onDetach-Methode
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * execute: Hier wird eine Transaktion erstellt und mit Hilfe des REST-Service /transaction/ aufgeführt.
     * Die Daten für die Transaktion werden aus den Bildschirmfeldern ausgelesen.
     */
    private void execute(){
        Transaction transaction = new Transaction();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String accountNumber = sharedPreferences.getString(getContext().getString(R.string.pref_accountNumber_key), "");

        Account sender = new Account();
        Account receiver = new Account();
        sender.setNumber(accountNumber);
        transaction.setSender(sender);
        receiver.setNumber(edtReceiverNumber.getText().toString());
        transaction.setReceiver(receiver);


        BigDecimal amount;
        try {
            amount = new BigDecimal(edtAmount.getText().toString());
        } catch (NumberFormatException e) {
            amount = BigDecimal.ZERO;
        }

        transaction.setAmount(amount);
        transaction.setReference(edtReference.getText().toString());

        new TransactionAsyncTask(transaction, this, getContext()).execute();
    }

    /**
     * onTransactionSuccess: Wurde die Transaktion erfolgreich ausgeführt, so wird dies an den Listener (MainActivity) weitergegeben.
     */
    @Override
    public void onTransactionSuccess() {

        if (mListener != null) {
            mListener.onTransactionExecute();
        }
    }

    /** onTransactionError: Wurde die Transaktion fehlerhaft ausgeführt, so wird dem Benutzer die Fehlermeldung vom Server angezeigt.
     * @param response Response
     */
    @Override
    public void onTransactionError(String response) {
        String toastMsg;
        if (response == null){
            toastMsg = "Keine Verbindung zum Server";
        }else{
            toastMsg = response;
        }
        Toast.makeText(getContext(), toastMsg, Toast.LENGTH_SHORT).show();
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
        void onTransactionExecute();
    }
}
