package com.epsilon.FunwithStatus;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.rockerhieu.emojicon.EmojiconEditText;

import java.io.IOException;

public class AddOptionActivity extends BaseActivity {
    Activity activity;
    ImageView ileft, iright, iv_image;
    TextView title;
    EmojiconEditText et_text;
    FloatingActionButton video, image;
    VideoView vv_video;
    private int PICK_IMAGE_REQUEST = 1;
    private int VIDEO_CAPTURE = 2;
    private int PIC_CROP = 3;
    private static final int STORAGE_PERMISSION_CODE = 123;
    String selectpath;
    private Bitmap bitmap, bmOverlay;
    LinearLayout linearLayout;
    private final String PREFERENCE_NAME = "ad_counter_preference";
    private final String COUNTER_INTERSTITIAL_ADS = "ad_counter";
    private int mAdCounter = 0;
    InterstitialAd mInterstitialAd;
    com.google.android.gms.ads.InterstitialAd interstitialAd;

    //Uri to store the image uri
    private Uri filePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_option);
        activity = this;
        IdMappings();

        ileft.setVisibility(View.GONE);
        title.setText("Add Text/Image/Video");
        iright.setImageResource(R.drawable.vc_done);
        requestStoragePermission();

        mInterstitialAd = new InterstitialAd(activity, getString(R.string.placement_id));
        loadInterstitialAd();

        SharedPreferences preferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        mAdCounter = preferences.getInt(COUNTER_INTERSTITIAL_ADS, 0);

        if (mAdCounter == 3) {
            mInterstitialAd.setAdListener(new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    // Interstitial ad displayed callback
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    // Interstitial dismissed callback
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    mInterstitialAd.show();
                    // Interstitial ad is loaded and ready to be displayed
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                }
            });
            mInterstitialAd.loadAd();
            mAdCounter = 0; //Clear counter variable
        } else {
            mAdCounter++; // Increment counter variable
        }

        // Save counter value back to SharedPreferences
        editor.putInt(COUNTER_INTERSTITIAL_ADS, mAdCounter);
        editor.commit();

        iright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), CategoriesoptionActivity.class);
                it.putExtra("TEXT", et_text.getText().toString());
                it.putExtra("TYPE", "STATUS");
                Log.e("TEXT", et_text.getText().toString());
                startActivity(it);
            }
        });


        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(activity);
                et_text.setVisibility(View.GONE);
                vv_video.setVisibility(View.GONE);
                iv_image.setVisibility(View.VISIBLE);
                showFileChooser();
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard(activity);
                et_text.setVisibility(View.GONE);
                vv_video.setVisibility(View.VISIBLE);
                iv_image.setVisibility(View.GONE);
                dispatchTakeVideoIntent();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == activity.RESULT_CANCELED) {
            return;
        }
        if (requestCode == VIDEO_CAPTURE) {
            vv_video.setVisibility(View.VISIBLE);
            Uri selectedVideoUri = data.getData();
            selectpath = getPath(getActivity(), selectedVideoUri);
            vv_video.setVideoPath(selectpath);
            vv_video.setVideoURI(selectedVideoUri);
            vv_video.setMediaController(new MediaController(this));
            vv_video.start();
            iright.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(getActivity(), CategoriesoptionActivity.class);
                    it.putExtra("selectpath", selectpath);
                    it.putExtra("TYPE", "VIDEO");
                    Log.e("selectpath", selectpath);
                    startActivity(it);
                    finish();
                }
            });
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            final String stringUri = getPath(getActivity(), filePath);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                iv_image.setImageBitmap(bitmap);
                iright.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(getActivity(), CategoriesoptionActivity.class);
                        it.putExtra("imageUri", stringUri);
                        it.putExtra("TYPE", "IMAGE");
                        startActivity(it);
                        finish();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        // check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProviderif (isMediaDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[] { split[1] };

            return getDataColumn(context, contentUri, selection,
                    selectionArgs);
        }
        // MediaStore (and general)
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private void dispatchTakeVideoIntent() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_CAPTURE);
    }


    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
                showFileChooser();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void IdMappings() {
        ileft = (ImageView) findViewById(R.id.ileft);
        iright = (ImageView) findViewById(R.id.iright);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        title = (TextView) findViewById(R.id.title);
        et_text = (EmojiconEditText) findViewById(R.id.et_text);
        vv_video = (VideoView) findViewById(R.id.vv_video);
        video = (FloatingActionButton) findViewById(R.id.video);
        image = (FloatingActionButton) findViewById(R.id.image);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
    }
    public void onBackPressed() {
        Intent it = new Intent(activity, Dashboard.class);
        startActivity(it);
        finish();
    }

    private void loadInterstitialAd() {
        mInterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        mInterstitialAd.loadAd();
    }
    public static void hideSoftKeyboard(Context context) {
        if(((Activity)context).getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(), 0);
        }
    }
}
