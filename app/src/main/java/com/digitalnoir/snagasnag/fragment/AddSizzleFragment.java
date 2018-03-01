package com.digitalnoir.snagasnag.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitalnoir.snagasnag.R;

/**
 * AddSizzleFragment controls Add sizzle popup window (fragment_add_sizzle)
 */

public class AddSizzleFragment extends Fragment {

    public static final String EXTRA_TEXT ="text";

    private ActionInterface listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sizzle,
                container, false);
        return view;
    }

    /**
     * The inner type interface {@link ActionInterface} let the activity, which must implement
     * it, control the communication between fragments. In its onAttach() method it can check if the
     * activity correctly implements this interface.
     */
    public interface ActionInterface {
        void onActionClick(String link);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String text = bundle.getString(EXTRA_TEXT);
            setText(text);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActionInterface) {
            listener = (ActionInterface) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement AddSizzleFragment.ActionInterface");
        }
    }

    // triggers update of the details fragment
    public void updateDetail() {
        // create fake data
        String newTime = String.valueOf(System.currentTimeMillis());
        // send data to activity
        // inform the Activity about the change based on interface definition
        listener.onActionClick(newTime);
    }

    public void setText(String text) {
        //TextView view = (TextView) getView().findViewById(R.id.detailsText);
       // view.setText(text);
    }
}