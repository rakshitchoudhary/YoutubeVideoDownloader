package com.youtubevideodownloader;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.resources.Compatibility;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private String selectedVideoPath = "";
    private Bitmap bMap;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //https://youtu.be/fodD6UHjLmw

        /*InitialFragment fragment = new InitialFragment();
        changeFragment(fragment, "InitialFragment", false);*/

    }

    public void changeFragment(@NonNull Fragment fragment, @NonNull String name, boolean isChild) {
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (transaction.isAddToBackStackAllowed()) {
            int count = fragmentManager.getBackStackEntryCount();
            if (count > 0) {
                androidx.fragment.app.FragmentManager.BackStackEntry entry =
                        fragmentManager.getBackStackEntryAt(count - 1);

                if (entry != null && name.equals(entry.getName())) {
                    fragmentManager.popBackStack();
                    if (!isChild) {
                        // We just removed it's duplicate from the back stack
                        // And we want at least one in it
                        transaction.addToBackStack(name);
                    }
                }
            }

            if (isChild) {
                transaction.addToBackStack(name);
            }
        }

        /*transaction.replace(R.id.fragmentContainer, fragment, name);
        transaction.commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();*/
    }
}