package edu.iit.bluetoothbeacon;


import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.net.URL;

import edu.iit.bluetoothbeacon.models.Masterpiece;
import edu.iit.bluetoothbeacon.models.Translation;

public class MasterpieceFragment extends Fragment {
    private static final String MASTERPIECE = "masterpiece";
    private static final String LANGUAGE = "language";
    private MenuItem mLanguageMenuItem;
    private Masterpiece mMasterpiece;
    private String mCurrentLanguage = "pt-br";
    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private ImageView mImageView;


    public MasterpieceFragment() {
        // Required empty public constructor
    }

    public static MasterpieceFragment newInstance(Masterpiece masterpiece, String language) {
        MasterpieceFragment fragment = new MasterpieceFragment();
        Bundle args = new Bundle();
        args.putParcelable(MASTERPIECE, masterpiece);
        args.putString(LANGUAGE, language);
        fragment.setArguments(args);
        return fragment;
    }

    OnLanguageSelectedListener mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnLanguageSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLanguageSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMasterpiece = getArguments().getParcelable(MASTERPIECE);
            mCurrentLanguage = getArguments().getString(LANGUAGE);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_masterpiece, container, false);
        mTitleTextView = (TextView) v.findViewById(R.id.titleTextView);
        mDescriptionTextView = (TextView) v.findViewById(R.id.descriptionTextView);
        mImageView = (ImageView) v.findViewById(R.id.masterpieceImageView);
        Translation t = mMasterpiece.getOneTranslation(mCurrentLanguage);
        mTitleTextView.setText(t.getTitle());
        mDescriptionTextView.setText(t.getContent());
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        ImageRequest request = new ImageRequest(mMasterpiece.getImageUrl(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        mImageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        mImageView.setImageResource(android.R.drawable.stat_notify_error);
                    }
                });
        queue.add(request);
        //updateMenuTitle(mCurrentLanguage);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_fragment, menu);
        mLanguageMenuItem = menu.findItem(R.id.languageMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.languageMenu) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Select your language: ");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    android.R.layout.select_dialog_singlechoice);
            arrayAdapter.add("pt-br");
            arrayAdapter.add("en-us");

            builder.setNegativeButton("Cancel", null);

            builder.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mCurrentLanguage = arrayAdapter.getItem(which);
                            mCallback.onLanguageSelected(mCurrentLanguage);
                            Translation t = mMasterpiece.getOneTranslation(mCurrentLanguage);
                            mTitleTextView.setText(t.getTitle());
                            mDescriptionTextView.setText(t.getContent());
                            updateMenuTitle(mCurrentLanguage);
                        }
                    });
            builder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMenuTitle(String language) {
        mLanguageMenuItem.setTitle(language);
    }

    public interface OnLanguageSelectedListener {
        void onLanguageSelected(String language);
    }
}
