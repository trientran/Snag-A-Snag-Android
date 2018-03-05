package com.digitalnoir.snagasnag.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalnoir.snagasnag.R;
import com.digitalnoir.snagasnag.model.Sizzle;
import com.digitalnoir.snagasnag.utility.ImagePickerUtil;
import com.digitalnoir.snagasnag.utility.LogUtil;
import com.digitalnoir.snagasnag.utility.SizzleCreater;
import com.google.android.gms.maps.model.LatLng;

import static com.digitalnoir.snagasnag.utility.ImagePickerUtil.getPickImageIntent;

/**
 * AddSizzleFragment controls Add sizzle popup window (fragment_add_sizzle)
 */

public class AddSizzleFragment extends Fragment {

    public static final String EXTRA_SELECTED_ADDRESS = "selected_address";
    public static final String EXTRA_SELECTED_LAT_LNG = "selected_lat_lng";
    public static final int PERMISSION_REQUEST_CODE = 1;

    // PICK_IMAGE_ID for selecting image intent. The number doesn't matter
    private static final int PICK_IMAGE_ID = 234;

    private OnCloseBtnClickListener mCallback;


    private EditText sizzleNameEditText;
    private EditText addressEditText;
    private EditText detailEditText;

    private Button snagItBottom;
    private ImageButton closeBtn;

    private Button addPhotoBigBtn;
    private ImageButton addPhotoSmallBtn;
    private TextView addPhotoTv;

    LatLng latLng = null;
    Bitmap bitmap = null;

    // default constructor
    public AddSizzleFragment() {
    }

    /**
     * The inner type interface {@link OnCloseBtnClickListener} let the activity, which must implement
     * it, control the communication between fragments. In its onAttach() method it can check if the
     * activity correctly implements this interface.
     */
    public interface OnCloseBtnClickListener {
        void onCloseBtnClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCloseBtnClickListener) {
            mCallback = (OnCloseBtnClickListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement AddSizzleFragment.OnCloseBtnClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sizzle,
                container, false);

        sizzleNameEditText = (EditText) view.findViewById(R.id.nameEditText);
        addressEditText = (EditText) view.findViewById(R.id.addressEditText);
        detailEditText = (EditText) view.findViewById(R.id.descEditText);

        snagItBottom = (Button) view.findViewById(R.id.snagItBottom);
        closeBtn = (ImageButton) view.findViewById(R.id.cancelBtn);

        addPhotoBigBtn = (Button) view.findViewById(R.id.addPicBigBtn);
        addPhotoSmallBtn = (ImageButton) view.findViewById(R.id.addPicSmallBtn);
        addPhotoTv = (TextView) view.findViewById(R.id.addPhotoTv);

        addPhotoBigBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // showFileChooser();
                LogUtil.debug("addTrien", "aa");
            }
        });

        addPhotoSmallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chooseImageIntent = getPickImageIntent(getActivity());
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
                LogUtil.debug("addTrien", "aa");
            }
        });

        addPhotoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chooseImageIntent = getPickImageIntent(getActivity());
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
                LogUtil.debug("addTrien", "aa");
            }
        });

        snagItBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // retrieve userId from SharedPreferences
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                int userId = prefs.getInt("userId", 0);
                LogUtil.debug("triensharedUId", String.valueOf(userId));

                // create a new sizzle instance
                Double lat = latLng.latitude;
                Double lng = latLng.longitude;
                String sizzleName = sizzleNameEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String desc = detailEditText.getText().toString();
                Sizzle newSizzle = new Sizzle(String.valueOf(lat), String.valueOf(lng), sizzleName, address, desc);

                // if userId exist, then go straight to creating a new sizzle
                if (userId != 0) {
                    // item.setActionView(R.layout.progress);
                    SizzleCreater t = new SizzleCreater(getActivity(), userId, newSizzle, bitmap);
                    t.execute();
                }
                LogUtil.debug("addTrien", "aa");

                // close fragment
                mCallback.onCloseBtnClick();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.onCloseBtnClick();
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission();
            }
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {

            // get and set address for addressEditText
            String address = bundle.getString(EXTRA_SELECTED_ADDRESS);
            addressEditText.setText(address);

            // get LatLng details
            latLng = bundle.getParcelable(EXTRA_SELECTED_LAT_LNG);
        }
    }

    // triggers update of the details fragment
    public void updateDetail(String uri) {
        // create fake data
        String newTime = String.valueOf(System.currentTimeMillis());
        // send data to activity
        // inform the Activity about the change based on interface definition
        mCallback.onCloseBtnClick();
    }

    /**
     * Storage permission.
     */
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getActivity(), " Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission Granted Successfully! ", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied :( ", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if we call super.onActivityResult here, this would trigger onActivityResult() from hosting activity
        //super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case PICK_IMAGE_ID:
                bitmap = ImagePickerUtil.getImageFromResult(getActivity(), resultCode, data);
                setBitmapToImageBtn(addPhotoSmallBtn, bitmap);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    private void setBitmapToImageBtn(ImageButton imageBtn, Bitmap bitmap) {
        //Setting the Bitmap to ImageView
        BitmapDrawable bDrawable = new BitmapDrawable(getActivity().getResources(), bitmap);
        imageBtn.setBackground(bDrawable);
    }
}