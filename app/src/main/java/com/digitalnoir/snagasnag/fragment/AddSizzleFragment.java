package com.digitalnoir.snagasnag.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.digitalnoir.snagasnag.R;
import com.digitalnoir.snagasnag.model.Sizzle;
import com.digitalnoir.snagasnag.utility.HttpClient;
import com.google.android.gms.maps.model.Marker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * AddSizzleFragment controls Add sizzle popup window (fragment_add_sizzle)
 */

public class AddSizzleFragment extends Fragment {

    public static final String EXTRA_TEXT ="text";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST= 99;

    private String url = "http://snag.digitalnoirtest.net.au/snag-create-post/";

    private ActionInterface listener;

    private EditText sizzleNameEditText;
    private EditText addressEditText;
    private EditText descEditText;
    private Button addSizzleBtn;
    private Button  snagItBottom;
    private ImageButton closeBtn;


    Bitmap bitmap = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_sizzle,
                container, false);

        sizzleNameEditText  = (EditText) view.findViewById(R.id.nameEditText);
        addressEditText = (EditText) view.findViewById(R.id.addressEditText);
        descEditText = (EditText) view.findViewById(R.id.descEditText);
        addSizzleBtn = (Button) view.findViewById(R.id.addSizzleBtn);
        snagItBottom  = (Button) view.findViewById(R.id.snagItBottom);
        closeBtn = (ImageButton) view.findViewById(R.id.cancelBtn);

        addSizzleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFileChooser();

            }
        });

        snagItBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uniqueKey = "qfP2ixc0fBIxoxiDfx3jbgaq";
                String userId = String.valueOf(132);
                String lat = "-34.933121";
                String lng =  "138.575439";
                String sizzleName = sizzleNameEditText.getText().toString();
                String address = "108 Railway tce";
                String desc = descEditText.getText().toString();

//                item.setActionView(R.layout.progress);
                SendHttpRequestTask t = new SendHttpRequestTask();

                String[] params = new String[]{url, uniqueKey, userId, lat, lng, sizzleName, address, desc};
                t.execute(params);

            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getFragmentManager().beginTransaction().remove(AddSizzleFragment.this).commit();
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
        /*Bundle bundle = getArguments();
        if (bundle != null) {
            String text = bundle.getString(EXTRA_TEXT);
            setText(text);
        }*/
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
    public void updateDetail(String uri) {
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

    /**
     * Storage permission.
     *
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

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                Toast.makeText(getActivity(), ""+bitmap, Toast.LENGTH_SHORT).show();

                //Setting the Bitmap to ImageView
                BitmapDrawable bDrawable = new BitmapDrawable(getActivity().getResources(),bitmap);
                addSizzleBtn.setBackground(bDrawable);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendHttpRequestTask extends AsyncTask<String, Void, String> {

        Sizzle sizzle;
/*
        public SendHttpRequestTask(Sizzle sizzle) {
            this.sizzle = sizzle;
        }*/

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String param1 = params[1];
            String param2 = params[2];
            String param3 = params[3];
            String param4 = params[4];
            String param5 = params[5];
            String param6 = params[6];
            String param7 = params[7];


            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_default_sizzle);
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);

            try {
                HttpClient client = new HttpClient(url);
                client.connectForMultipart();
                client.addFormPart("unique_key", param1);
                client.addFormPart("user_id", param2);
                client.addFormPart("latitude", param3);
                client.addFormPart("longitude", param4);
                client.addFormPart("name", param5);
                client.addFormPart("address", param6);
                client.addFormPart("details", param7);
                client.addFilePart("photo", "unnamed-file.png", baos.toByteArray());
                client.finishMultipart();
                String data = client.getResponse();
                Log.d("trien", data);
            }
            catch(Throwable t) {
                t.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
//            item.setActionView(null);

        }
    }

}