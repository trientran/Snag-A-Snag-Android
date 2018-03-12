package com.digitalnoir.snagasnag.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.digitalnoir.snagasnag.R;
import com.digitalnoir.snagasnag.adapter.CommentAdapter;
import com.digitalnoir.snagasnag.model.Comment;
import com.digitalnoir.snagasnag.model.Rating;
import com.digitalnoir.snagasnag.model.Sizzle;
import com.digitalnoir.snagasnag.utility.CommentCreator;
import com.digitalnoir.snagasnag.utility.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.centerCropTransform;
import static com.digitalnoir.snagasnag.MapsActivity.EXTRA_SELECTED_SIZZLE;
import static com.digitalnoir.snagasnag.utility.DataUtil.isInternetConnected;
import static com.digitalnoir.snagasnag.utility.TextValidation.validateTextWithPattern;

/**
 * SizzleDetailFragment controls Sizzle detail popup window (fragment_sizzle_details)
 */

public class SizzleDetailFragment extends Fragment {

    private OnSizzleDetailButtonsClickListener mCallback;

    private ImageView snagAvatar;
    private TextView sizzleTitle;
    private ImageButton closeBtn;
    private TextView addressTv;

    private TextView sausageScoreTv;
    private TextView breadScoreTv;
    private TextView onionScoreTv;
    private TextView sauceScoreTv;
    private TextView totalScoreTv;

    private EditText commentEditText;

    private Button rateBtn;

    // all about comments
    private Button commentBtn;
    private CommentAdapter commentAdapter;
    private RecyclerView mRecyclerView;
    private List<Comment> commentList;
    LinearLayoutManager layoutManager;

    NestedScrollView nestedScrollView;

    Sizzle sizzle;
    Rating rating;

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

        void onRateBtnClick(Sizzle sizzle);
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

        commentList = new ArrayList<>();

        nestedScrollView = (NestedScrollView) view.findViewById(R.id.scrollView);

        snagAvatar = (ImageView)  view.findViewById(R.id.snagAvatar);
        sizzleTitle = (TextView) view.findViewById(R.id.sizzleTitle);
        closeBtn = (ImageButton) view.findViewById(R.id.closeBtn);
        addressTv = (TextView) view.findViewById(R.id.ratingHintTv);

        sausageScoreTv = (TextView) view.findViewById(R.id.sausageScoreTv);
        breadScoreTv = (TextView) view.findViewById(R.id.breadScoreTv);
        onionScoreTv = (TextView) view.findViewById(R.id.onionScoreTv);
        sauceScoreTv = (TextView) view.findViewById(R.id.sauceScoreTv);
        totalScoreTv = (TextView) view.findViewById(R.id.totalScoreTv);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) view.findViewById(R.id.commentRecyclerView);

        /*
         * In our case, we want a vertical list, so we pass in the constant from the
         * LinearLayoutManager class for vertical lists, LinearLayoutManager.VERTICAL.         *
         * The third parameter (shouldReverseLayout) should be true if you want to reverse your
         * layout. Generally, this is only true with horizontal lists that need to support a
         * right-to-left layout.
         */
        layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);

        /* setLayoutManager associates the LayoutManager we created above with our RecyclerView */
        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        commentAdapter = new CommentAdapter(getActivity());

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(commentAdapter);


        commentEditText = (EditText) view.findViewById(R.id.commentEditText);

        commentBtn = (Button) view.findViewById(R.id.commentBtn);
        rateBtn = (Button) view.findViewById(R.id.rateBtn);

        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addNewComment();

            }
        });

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // pass sizzle object to MapsActivity
                mCallback.onRateBtnClick(sizzle);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCallback.onSizzleDetailCloseBtnClick();
            }
        });

        // make mRecyclerView not scrollable so it scroll along with it container parent
        mRecyclerView.setNestedScrollingEnabled(false);
        return view;
    }

    private void addNewComment() {
        // retrieve userId from SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int userId = prefs.getInt("userId", 0);
        String userName = prefs.getString("username", "missing");

        // validate new comment string with common pattern first
        String newCommentString = validateTextWithPattern(getActivity(), commentEditText, getActivity().getString(R.string.comment_field_name));

        // If there is a network connection and text validation is ok then send a request to create comment
        if (isInternetConnected(getActivity()) && newCommentString != null) {

            Comment newComment = new Comment(userId, sizzle.getSizzleId(), newCommentString);

            // if userId exist, then go straight to creating a new comment
            if (userId != 0) {

                // send a request to web service
                CommentCreator commentCreator = new CommentCreator(getActivity(), newComment);
                commentCreator.execute();

                // add the new comment to Recycler view
                SimpleDateFormat fmtOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
                Date currentTime = Calendar.getInstance().getTime();
                String currentDatetimeString = fmtOut.format(currentTime);
                newComment.setDate(currentDatetimeString);

                newComment.setUsername(userName);

                commentAdapter.onChildAdded(newComment, mRecyclerView, nestedScrollView);

                LogUtil.debug("trienTime", currentDatetimeString);
            }

            else {
                LogUtil.debug("triensharedUId", String.valueOf(userId));
            }
        }

        // clear commentEditText field
        commentEditText.getText().clear();
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
                addressTv.setText(sizzle.getAddress());

                // get and populate rating details
                rating = sizzle.getRating();

                Spanned sausageScore = formatText(getResources(),String.valueOf(rating.getSausage()));
                Spanned breadScore = formatText(getResources(),String.valueOf(rating.getBread()));
                Spanned onionScore = formatText(getResources(),String.valueOf(rating.getOnion()));
                Spanned sauceScore = formatText(getResources(),String.valueOf(rating.getSauce()));
                Spanned totalScore = formatText(getResources(),String.valueOf(rating.getAggregateRating()));

                sausageScoreTv.setText(sausageScore);
                breadScoreTv.setText(breadScore);
                onionScoreTv.setText(onionScore);
                sauceScoreTv.setText(sauceScore);
                totalScoreTv.setText(totalScore);

                // get and populate comments
                commentList = sizzle.getComments();
                commentAdapter.swapCommentData(commentList);

                LogUtil.debug("triencom", commentList.toString()); // retrieve sizzle id
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

        return Html.fromHtml(res.getString(R.string.rate_points, text));
    }

}