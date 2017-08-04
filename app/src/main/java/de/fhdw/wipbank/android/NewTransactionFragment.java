package de.fhdw.wipbank.android;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigDecimal;

import de.fhdw.wipbank.android.model.Account;
import de.fhdw.wipbank.android.model.Transaction;

/**
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
    // TODO: Rename and change types and number of parameters
    public static NewTransactionFragment newInstance() {
        NewTransactionFragment fragment = new NewTransactionFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_transaction, container, false);
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

    private void execute(){
        Transaction transaction = new Transaction();
        Account user = AccountService.getAccount();
        Account sender = new Account();
        Account receiver = new Account();
        sender.setNumber(user.getNumber());
        transaction.setSender(sender);
        receiver.setNumber(edtReceiverNumber.getText().toString());
        transaction.setReceiver(receiver);
        transaction.setAmount(BigDecimal.valueOf(Double.valueOf(edtAmount.getText().toString()))); // ToDo: angucken ob das besser geht
        transaction.setReference(edtReference.getText().toString());
        TransactionAsyncTask.executeTransaction(transaction, this, getContext());
    }

    @Override
    public void onTransactionSuccess() {

    }

    @Override
    public void onTransactionError(String errorMsg) {

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
}
