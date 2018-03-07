package com.digitalnoir.snagasnag.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.digitalnoir.snagasnag.R;
import com.digitalnoir.snagasnag.model.Sizzle;
import com.digitalnoir.snagasnag.utility.LogUtil;

import static com.digitalnoir.snagasnag.MapsActivity.EXTRA_SELECTED_SIZZLE;

/**
 * SizzleDetailFragment controls Sizzle detail popup window (fragment_sizzle_details)
 */

public class SizzleDetailFragment extends Fragment {

    private OnSizzleDetailButtonsClickListener mCallback;

    private TextView sizzleTitle;
    private ImageButton closeBtn;
    private TextView addressTv;

    private TextView sausageScoreTv;
    private TextView breadScoreTv;
    private TextView onionScoreTv;
    private TextView sauceScoreTv;
    private TextView totalScoreTv;

    private EditText commentEditText;

    private Button commentBtn;
    private Button rateBtn;

    private int sizzleId;

    // default constructor
    public SizzleDetailFragment() {
    }

    /**
     * The inner type interface {@link OnSizzleDetailButtonsClickListener} let the activity, which must implement
     * it, control the communication between fragments. In its onAttach() method it can check if the
     * activity correctly implements this interface.
     */
    public interface OnSizzleDetailButtonsClickListener {

        void onSizzleDetailCloseBtnClick();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSizzleDetailButtonsClickListener) {
            mCallback = (OnSizzleDetailButtonsClickListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement SizzleDetailFragment.OnAddSizzleButtonsClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sizzle_details,
                container, false);

        sizzleTitle = (TextView) view.findViewById(R.id.sizzleTitle);
        closeBtn = (ImageButton) view.findViewById(R.id.closeBtn);
        addressTv = (TextView) view.findViewById(R.id.addressTv);

        sausageScoreTv = (TextView) view.findViewById(R.id.sausageScoreTv);
        breadScoreTv = (TextView) view.findViewById(R.id.breadScoreTv);
        onionScoreTv = (TextView) view.findViewById(R.id.onionScoreTv);
        sauceScoreTv = (TextView) view.findViewById(R.id.sauceScoreTv);
        totalScoreTv = (TextView) view.findViewById(R.id.totalScoreTv);

        commentEditText = (EditText) view.findViewById(R.id.commentEditText);

        commentBtn = (Button) view.findViewById(R.id.commentBtn);
        rateBtn = (Button) view.findViewById(R.id.rateBtn);

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.onSizzleDetailCloseBtnClick();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {

            // get and set address for addressEditText
            Sizzle sizzle = bundle.getParcelable(EXTRA_SELECTED_SIZZLE);
LogUtil.debug("trienSizzleIdFragment", String.valueOf(sizzle.getSizzleId()));
            if (sizzle != null) {
                sizzleTitle.setText(sizzle.getName());
                addressTv.setText(sizzle.getAddress());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if we call super.onActivityResult here, this would trigger onActivityResult() from hosting activity
        //super.onActivityResult(requestCode, resultCode, data);

    }

}