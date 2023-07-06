package com.youtubevideodownloader;

import static com.youtubevideodownloader.AppUtils.replaceString;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
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
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class DownloadFragment extends Fragment {

    private Context context;
    private String title, fulltitle, viewcount, likecount, thumbnail, videolink, filesize, filepath;
    private ImageView img_thumbnail;
    private TextView txt_title, txt_view, txt_like, txt_progress, txt_size;
    private ProgressBar progressBar;
    private Double percent;
    private double sizeInMb;
    private RelativeLayout rel_download, rel_failure;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = replaceString(getArguments().getString("title"));
        fulltitle = replaceString(getArguments().getString("fulltitle"));
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
        View view = inflater.inflate(R.layout.downloadfragment, container, false);

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

        rel_download = view.findViewById(R.id.rel_download);
        rel_failure = view.findViewById(R.id.rel_failure);

        txt_title.setText(fulltitle);
        txt_view.setText(viewcount + " Views");
        txt_like.setText(likecount + " Likes");

        sizeInMb = Double.parseDouble(filesize) / (1024 * 1024);
        txt_size.setText(String.format("%.2f", sizeInMb) + " MB");

        progressBar = view.findViewById(R.id.progress);

        if (AppUtils.checkInternetConnection(context)) {
            getYoutubeUrl(); //Method for extracting youtube downloadable link for downloading video
        } else
            AppUtils.noInternetDialog(context);
    }

    @SuppressLint("StaticFieldLeak")
    private void getYoutubeUrl() {
        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    int itag = 22;
                    String downloadUrl = ytFiles.get(itag).getUrl();

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            //pd.dismiss();
                            new DownloadFileFromURL().execute(downloadUrl);
                        }
                    }, 2000);
                }
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                rel_download.setVisibility(View.GONE);
                rel_failure.setVisibility(View.VISIBLE);
            }
        }.extract(videolink);
    }

    @SuppressLint("StaticFieldLeak")
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.connect();

                File file = new File(filepath);
                if (!file.exists()) {
                    file.mkdirs();

                    File outputFile = new File(filepath, title.replace(" ", "") + ".mp4");
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    InputStream is = c.getInputStream();

                    byte[] buffer;
                    buffer = new byte[4096];

                    int lenghtOfFile = c.getContentLength();
                    int len1 = 0;
                    long total = 0;

                    while ((len1 = is.read(buffer)) != -1) {
                        total += len1;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        fos.write(buffer, 0, len1);
                    }

                    fos.close();
                    is.close();
                } else {
                    File outputFile = new File(filepath, title.replace(" ", "") + ".mp4");

                    FileOutputStream fos = new FileOutputStream(outputFile);
                    InputStream is = c.getInputStream();

                    byte[] buffer;
                    buffer = new byte[4096];

                    int lenghtOfFile = c.getContentLength();
                    int len1 = 0;
                    long total = 0;

                    while ((len1 = is.read(buffer)) != -1) {
                        total += len1;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        fos.write(buffer, 0, len1);
                    }

                    fos.close();
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        rel_download.setVisibility(View.GONE);
                        rel_failure.setVisibility(View.VISIBLE);
                        Toast.makeText(context, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            requireActivity().runOnUiThread(new Runnable() {
                public void run() {
                    percent = (Double.parseDouble(filesize) * Double.parseDouble(progress[0])) / 100;
                    double sizeInMb = percent / (1024 * 1024);
                    txt_progress.setText(String.format("%.2f", sizeInMb) + " MB");
                    progressBar.setProgress(Integer.parseInt(progress[0]));
                }
            });
        }

        @Override
        protected void onPostExecute(String file_url) {
            ConvertFragment fragment = new ConvertFragment();
            Bundle b = new Bundle();
            b.putString("title", title);
            b.putString("fulltitle", fulltitle);
            b.putString("viewcount", viewcount);
            b.putString("likecount", likecount);
            b.putString("thumbnail", thumbnail);
            b.putString("filesize", String.format("%.2f", sizeInMb));
            b.putString("filepath", filepath);
            b.putString("videolink", videolink);
            fragment.setArguments(b);

            ((MainActivity) requireActivity()).changeFragment(fragment, "DownloadFragment", false);
        }

    }
}

