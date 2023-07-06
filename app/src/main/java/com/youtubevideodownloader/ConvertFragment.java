package com.youtubevideodownloader;

import static com.youtubevideodownloader.AppUtils.replaceString;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Objects;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class ConvertFragment extends Fragment {

    private Context context;
    private String title, fulltitle, viewcount, likecount, thumbnail, videolink, filesize, filepath, selectedVideoPath;
    private ImageView img_thumbnail;
    private TextView txt_title, txt_view, txt_like, txt_progress, txt_size, txt_info;
    private ProgressBar progressBar;
    private Double percent;
    private double sizeInMb;
    private Bitmap bMap;
    private RelativeLayout rel_progress, rel_success, rel_failure;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = replaceString(getArguments().getString("title"));
        fulltitle = getArguments().getString("fulltitle");
        viewcount = getArguments().getString("viewcount");
        likecount = getArguments().getString("likecount");
        thumbnail = getArguments().getString("thumbnail");
        videolink = getArguments().getString("videolink");
        filesize = getArguments().getString("filesize");
        filepath = getArguments().getString("filepath");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.convertfragment, container, false);

        setUpView(view); //Method for initializing UI
        return view;
    }

    private void setUpView(View view) {
        img_thumbnail = view.findViewById(R.id.img_thumbnail);
        Glide.with(context).load(thumbnail).into(img_thumbnail);

        txt_title = view.findViewById(R.id.txt_title);
        txt_view = view.findViewById(R.id.txt_view);
        txt_like = view.findViewById(R.id.txt_like);
        txt_progress = view.findViewById(R.id.txt_progress);
        txt_size = view.findViewById(R.id.txt_size);
        txt_info = view.findViewById(R.id.txt_info);

        rel_progress = view.findViewById(R.id.rel_progress);
        rel_success = view.findViewById(R.id.rel_success);
        rel_failure = view.findViewById(R.id.rel_failure);

        txt_title.setText(fulltitle);
        txt_view.setText(viewcount + " Views");
        txt_like.setText(likecount + " Likes");

        progressBar = view.findViewById(R.id.progress);

        if (AppUtils.checkInternetConnection(context)) {
            convertSaveAudio(); //Method for converting youtube video and saving it in selected folder
        } else
            AppUtils.noInternetDialog(context);
    }

    private void convertSaveAudio() {
        selectedVideoPath = filepath + "/" + title.replace(" ", "") + ".mp4";

        if (filepath != null && !filepath.isEmpty()) {
            File file = new File(filepath + "/" + title.replace(" ", "") + ".mp4");
            File file1 = new File(filepath);
            if (file.exists()) {
                bMap = ThumbnailUtils.createVideoThumbnail(selectedVideoPath, MediaStore.Images.Thumbnails.MINI_KIND);

                File dir = new File(filepath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                RxFFmpegInvoke.getInstance().setDebug(true);

                String destpath = "";
                if (file1.listFiles().length > 0) {
                    destpath = filepath + "/" + title.replace(" ", "") + "("+ file1.listFiles().length+ ").mp3";
                } else {
                    destpath = filepath + "/" + title.replace(" ", "") + ".mp3";
                }

                String text = "ffmpeg -i " + selectedVideoPath + " -f mp3 -ab 192000 -vn " + destpath;

                String[] commands = text.split(" ");

                RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(new RxFFmpegSubscriber() {
                    @Override
                    public void onFinish() {
                        Log.e("Status", "Finished");
                        rel_success.setVisibility(View.VISIBLE);
                        rel_progress.setVisibility(View.GONE);
                        rel_failure.setVisibility(View.GONE);
                        txt_info.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onProgress(int progress, long progressTime) {
                        Log.e("Status", "In Progress");
                        rel_success.setVisibility(View.GONE);
                        rel_progress.setVisibility(View.VISIBLE);
                        rel_failure.setVisibility(View.GONE);
                        txt_info.setVisibility(View.GONE);
                        txt_progress.setText(progress + "%");
                        progressBar.setProgress(progress);
                    }

                    @Override
                    public void onCancel() {
                        Log.e("Status", "Canceled");
                        rel_success.setVisibility(View.GONE);
                        rel_progress.setVisibility(View.GONE);
                        rel_failure.setVisibility(View.VISIBLE);
                        txt_info.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(String message) {
                        Log.e("Status", "Error : " + message);
                        rel_success.setVisibility(View.GONE);
                        rel_progress.setVisibility(View.GONE);
                        rel_failure.setVisibility(View.VISIBLE);
                        txt_info.setVisibility(View.GONE);
                    }
                });
            } else {
                Toast.makeText(context, "File not downloaded. Please try again", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "File not downloaded. Please try again", Toast.LENGTH_LONG).show();
        }
        //
    }

}
