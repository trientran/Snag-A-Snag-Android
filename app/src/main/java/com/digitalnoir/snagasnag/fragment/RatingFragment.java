package com.digitalnoir.snagasnag.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.digitalnoir.snagasnag.R;
import com.digitalnoir.snagasnag.model.Rating;
import com.digitalnoir.snagasnag.model.Sizzle;
import com.digitalnoir.snagasnag.utility.LogUtil;
import com.digitalnoir.snagasnag.utility.RatingCreator;

import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.digitalnoir.snagasnag.MapsActivity.EXTRA_SELECTED_SIZZLE;
import static com.digitalnoir.snagasnag.utility.DataUtil.isInternetConnected;

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
    double onionScore = 1;
    double sauceScore = 1;
    double totalScore = 5;

    private TextView totalScoreTv;

    private Button doneBtn;

    private int sizzleId;

    Sizzle sizzle;
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
        View view = inflater.inflate(R.layout.fragment_rating,
                container, false);

        closeBtn = (ImageButton) view.findViewById(R.id.closeBtn);
        snagAvatar = (ImageView) view.findViewById(R.id.snagAvatar);
        sizzleTitle = (TextView) view.findViewById(R.id.sizzleTitle);

        sausageRatingBar = (RatingBar) view.findViewById(R.id.sausageRatingBar);
        breadRatingBar = (RatingBar) view.findViewById(R.id.breadRatingBar);
        onionRatingBar = (RatingBar) view.findViewById(R.id.onionRatingBar);
        sauceRatingBar = (RatingBar) view.findViewById(R.id.sauceRatingBar);

        totalScoreTv = (TextView) view.findViewById(R.id.totalScoreTv);

        sausageRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                if (v < 1.0f) {
                    ratingBar.setRating(1.0f);
                }

                sausageScore = ratingBar.getRating();

                totalScore = (sausageScore + breadScore + onionScore + sauceScore) / 4;
                Spanned totalScoreSpanned = formatText(getResources(), String.valueOf(totalScore));
                totalScoreTv.setText(totalScoreSpanned);
                //totalScoreTv.setText(String.format(Locale.getDefault(),"%.2f", totalScore));
            }
        });
        breadRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                if (v < 1.0f) {
                    ratingBar.setRating(1.0f);
                }

                breadScore = ratingBar.getRating();

                totalScore = (sausageScore + breadScore + onionScore + sauceScore) / 4;
                Spanned totalScoreSpanned = formatText(getResources(), String.valueOf(totalScore));
                totalScoreTv.setText(totalScoreSpanned);
                //totalScoreTv.setText(String.format(Locale.getDefault(),"%.2f", totalScore));
                LogUtil.debug("trienBreadScore", String.valueOf(breadScore));
            }
        });
        onionRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                if (v < 1.0f) {
                    ratingBar.setRating(1.0f);
                }

                onionScore = ratingBar.getRating();

                totalScore = (sausageScore + breadScore + onionScore + sauceScore) / 4;
                Spanned totalScoreSpanned = formatText(getResources(), String.valueOf(totalScore));
                totalScoreTv.setText(totalScoreSpanned);
                //totalScoreTv.setText(String.format(Locale.getDefault(),"%.2f", totalScore));
            }
        });
        sauceRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                if (v < 1.0f) {
                    ratingBar.setRating(1.0f);
                }

                sauceScore = ratingBar.getRating();

                totalScore = (sausageScore + breadScore + onionScore + sauceScore) / 4;
                Spanned totalScoreSpanned = formatText(getResources(), String.valueOf(totalScore));
                totalScoreTv.setText(totalScoreSpanned);
                //totalScoreTv.setText(String.format(Locale.getDefault(),"%.2f", totalScore));
            }
        });

        doneBtn = (Button) view.findViewById(R.id.doneBtn);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNewRating();
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

        // If there is a network connection and text validation is ok then send a request to create comment
        if (isInternetConnected(getActivity())) {

            Rating newRating = new Rating(
                    userId, sizzleId,
                    String.valueOf(sausageScore),
                    String.valueOf(breadScore),
                    String.valueOf(onionScore),
                    String.valueOf(sauceScore));
            LogUtil.debug("trienScore", String.valueOf(sausageScore));
            LogUtil.debug("trienScore", String.valueOf(breadScore));
            LogUtil.debug("trienScore", String.valueOf(onionScore));
            LogUtil.debug("trienScore", String.valueOf(sauceScore));

            // if userId exist, then go straight to creating a new comment
            if (userId != 0) {

                // send a request to web service
                RatingCreator ratingCreator = new RatingCreator(getActivity(), newRating);
                ratingCreator.execute();
                mCallback.onDoneBtnClick();

            } else {
                LogUtil.debug("triensharedUId", String.valueOf(userId));
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {

            // retrieve sizzle object
            sizzle = bundle.getParcelable(EXTRA_SELECTED_SIZZLE);

            if (sizzle != null) {
                // Log retrieval of sizzle id
                LogUtil.debug("trienSizzleIdFragment", String.valueOf(sizzle.getSizzleId()));

                sizzleId = sizzle.getSizzleId();

                // get and set address for addressEditText
                if (sizzle.getPhotoUrl() != null) {

                    MultiTransformation multi = new MultiTransformation(
                            new CropSquareTransformation(),
                            new RoundedCornersTransformation(30, 0, RoundedCornersTransformation.CornerType.ALL));
                    Glide.with(this).load(sizzle.getPhotoUrl())
                            .apply(bitmapTransform(multi))
                            .into(snagAvatar);
                }

                sizzleTitle.setText(sizzle.getName());

            }
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

        if (text.toString().toLowerCase().equals("n/a")) {
            return Html.fromHtml(res.getString(R.string.rate_points_n_a, text));
        }

        return Html.fromHtml(res.getString(R.string.rate_points_out_of_5, text));
    }

}