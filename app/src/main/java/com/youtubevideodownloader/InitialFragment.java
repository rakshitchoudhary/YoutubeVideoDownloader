package com.youtubevideodownloader;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLException;
import com.yausername.youtubedl_android.mapper.VideoInfo;

public class InitialFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private TextInputLayout input_youtube_link;
    private TextInputEditText edt_youtube_link;
    private TextView txt_folder;
    private Button btn_proceed;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            YoutubeDL.getInstance().init(context);
        } catch (YoutubeDLException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.initialfragment, container, false);

        setUpView(view); //Method for initializing UI
        return view;
    }

    private void setUpView(View view) {
        input_youtube_link = view.findViewById(R.id.input_youtube_link);

        edt_youtube_link = view.findViewById(R.id.edt_youtube_link);
        edt_youtube_link.requestFocus();
        edt_youtube_link.setText("https://youtu.be/fodD6UHjLmw");

        txt_folder = view.findViewById(R.id.txt_folder);

        btn_proceed = view.findViewById(R.id.btn_proceed);

        txt_folder.setOnClickListener(this);
        btn_proceed.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_proceed:
                input_youtube_link.setEnabled(false);
                input_youtube_link.setHelperTextEnabled(false);
                txt_folder.setClickable(false);
                txt_folder.setFocusable(false);

                if (AppUtils.checkInternetConnection(context)) {
                    getYoutubeVideoDetails(edt_youtube_link.getText().toString()); //Method for getting youtube video details from link
                } else
                    AppUtils.noInternetDialog(context);
                break;
            case R.id.txt_folder:
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                i.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityIntent.launch(i);
                break;
        }
    }

    private void getYoutubeVideoDetails(String link) {
        VideoInfo streamInfo = null;
        try {
            streamInfo = YoutubeDL.getInstance().getInfo(link);

            DownloadFragment fragment = new DownloadFragment();
            Bundle b = new Bundle();
            b.putString("title", streamInfo.getTitle());
            b.putString("fulltitle", streamInfo.getFulltitle());
            b.putString("viewcount", streamInfo.getViewCount());
            b.putString("likecount", streamInfo.getLikeCount());
            b.putString("thumbnail", streamInfo.getThumbnail());
            b.putString("filesize", String.valueOf(streamInfo.getFileSizeApproximate()));
            b.putString("filepath", txt_folder.getText().toString());
            b.putString("videolink", edt_youtube_link.getText().toString());
            fragment.setArguments(b);

            ((MainActivity) getActivity()).changeFragment(fragment, "DownloadFragment", false);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to grab video info", Toast.LENGTH_LONG).show();
            input_youtube_link.setEnabled(true);
            input_youtube_link.setHelperTextEnabled(true);
            txt_folder.setClickable(true);
            txt_folder.setFocusable(true);
        }
    }

    ActivityResultLauncher<Intent> startActivityIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //Log.e("Test", "Result URI " + result.getData().getData().getLastPathSegment());
                    String path[] = result.getData().getData().getLastPathSegment().split(":");
                    txt_folder.setText(Environment.getExternalStorageDirectory() + "/" + path[1]);
                }
            });
}
