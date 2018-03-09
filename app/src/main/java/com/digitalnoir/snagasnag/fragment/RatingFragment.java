package com.digitalnoir.snagasnag.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.digitalnoir.snagasnag.R;
import com.digitalnoir.snagasnag.adapter.CommentAdapter;
import com.digitalnoir.snagasnag.model.Comment;
import com.digitalnoir.snagasnag.model.Rating;
import com.digitalnoir.snagasnag.model.Sizzle;
import com.digitalnoir.snagasnag.utility.CommentCreator;
import com.digitalnoir.snagasnag.utility.LogUtil;
import com.digitalnoir.snagasnag.utility.RatingCreator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.digitalnoir.snagasnag.MapsActivity.EXTRA_SELECTED_SIZZLE;
import static com.digitalnoir.snagasnag.utility.DataUtil.isInternetConnected;
import static com.digitalnoir.snagasnag.utility.TextValidation.validateTextWithPattern;

/**
 * RatingFragment controls Sizzle rating popup window (fragment_rating)
 */

public class RatingFragment extends Fragment {

    private OnRatingButtonsClickListener mCallback;

    private ImageButton closeBtn;

    private ImageView snagAvatar;
    private TextView sizzleTitle;

    private RatingBar sausageRatingBar;
    private RatingBar breadRatingBar;
    private RatingBar onionRatingBar;
    private RatingBar sauceRatingBar;

    double sausageScore = 1;
    double breadScore = 1;
    double onionScore =1;
    double sauceScore =1;
    double totalScore =1;

    private TextView totalScoreTv;

    private Button doneBtn;

    private int sizzleId;

    Rating rating;

    // default constructor
    public RatingFragment() {
    }

    /**
     * The inner type interface {@link OnRatingButtonsClickListener} let the activity, which must implement
     * it, control the communication between fragments. In its onAttach() method it can check if the
     * activity correctly implements this interface.
     */
    public interface OnRatingButtonsClickListener {

        void onRatingCloseBtnClick();

        void onDoneBtnClick();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRatingButtonsClickListener) {
            mCallback = (OnRatingButtonsClickListener) context;
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

        closeBtn = (ImageButton) view.findViewById(R.id.closeBtn);
        snagAvatar = (ImageView) view.findViewById(R.id.snagAvatar);
        sizzleTitle = (TextView) view.findViewById(R.id.sizzleTitle);

          sausageRatingBar = (RatingBar) view.findViewById(R.id.sausageRatingBar);
          breadRatingBar = (RatingBar) view.findViewById(R.id.breadRatingBar);
          onionRatingBar = (RatingBar) view.findViewById(R.id.onionRatingBar);
          sauceRatingBar = (RatingBar) view.findViewById(R.id.sauceRatingBar);

          totalScoreTv = (TextView) view.findViewById(R.id.totalScoreTv);

        sausageRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                sausageScore = ratingBar.getRating();

                totalScore = (sausageScore + breadScore +onionScore + sauceScore)/4;
                totalScoreTv.setText(String.format(Locale.getDefault(),"%.2f", totalScore));
            }
        });
        breadRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                breadScore = ratingBar.getRating();

                totalScore = (sausageScore + breadScore +onionScore + sauceScore)/4;
                totalScoreTv.setText(String.format(Locale.getDefault(),"%.2f", totalScore));
            }
        });
        onionRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                onionScore = ratingBar.getRating();

                totalScore = (sausageScore + breadScore +onionScore + sauceScore)/4;
                totalScoreTv.setText(String.format(Locale.getDefault(),"%.2f", totalScore));
            }
        });
        sauceRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                sauceScore = ratingBar.getRating();

                totalScore = (sausageScore + breadScore +onionScore + sauceScore)/4;
                totalScoreTv.setText(String.format(Locale.getDefault(),"%.2f", totalScore));
            }
        });

        doneBtn = (Button) view.findViewById(R.id.rateBtn);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo

            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.onRatingCloseBtnClick();
            }
        });

        return view;
    }

    private void addNewRating() {
        // retrieve userId from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int userId = prefs.getInt("userId", 0);



       /* // If there is a network connection and text validation is ok then send a request to create comment
        if (isInternetConnected(getActivity())) {

            Rating newRating = new Rating(userId, sizzleId, sausageScore, breadScore,onionScore, sauceScore);

            // if userId exist, then go straight to creating a new comment
            if (userId != 0) {

                // send a request to web service
                RatingCreator ratingCreator = new RatingCreator(getActivity(), newRating);
                ratingCreator.execute();

            } else {
                LogUtil.debug("triensharedUId", String.valueOf(userId));
            }
        }*/

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {

            /*// retrieve sizzle object
            Sizzle sizzle = bundle.getParcelable(EXTRA_SELECTED_SIZZLE);

            // retrieve sizzle id
            sizzleId = sizzle != null ? sizzle.getSizzleId() : 0;
            LogUtil.debug("trienSizzleIdFragment", String.valueOf(sizzle.getSizzleId()));

            // get and set address for addressEditText
            if (sizzle.getPhotoUrl() != null) {

                Glide.with(this).load(sizzle.getPhotoUrl())
                        .apply(bitmapTransform(new RoundedCornersTransformation(30, 0, RoundedCornersTransformation.CornerType.ALL)))
                        .into(snagAvatar);
            }

            sizzleTitle.setText(sizzle.getName());
            addressTv.setText(sizzle.getAddress());

            // get and populate rating details
            rating = sizzle.getRating();

            Spanned sausageScore = formatText(getResources(), String.valueOf(rating.getSausage()));
            Spanned breadScore = formatText(getResources(), String.valueOf(rating.getBread()));
            Spanned onionScore = formatText(getResources(), String.valueOf(rating.getOnion()));
            Spanned sauceScore = formatText(getResources(), String.valueOf(rating.getSauce()));
            Spanned totalScore = formatText(getResources(), String.valueOf(rating.getAggregateRating()));

            sausageScoreTv.setText(sausageScore);
            breadScoreTv.setText(breadScore);
            onionScoreTv.setText(onionScore);
            sauceScoreTv.setText(sauceScore);
            totalScoreTv.setText(totalScore);

            // get and populate comments
            commentList = sizzle.getComments();
            commentAdapter.swapCommentData(commentList);

            LogUtil.debug("triencom", commentList.toString());
*/
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if we call super.onActivityResult here, this would trigger onActivityResult() from hosting activity
        //super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * Helper method to format text nicely.
     */
    private static Spanned formatText(Resources res, CharSequence text) {

        return Html.fromHtml(res.getString(R.string.rate_points, text));
    }

}