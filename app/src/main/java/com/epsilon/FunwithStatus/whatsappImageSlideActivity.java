package com.epsilon.FunwithStatus;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.epsilon.FunwithStatus.adapter.WhatsappImageAdapter;
import com.epsilon.FunwithStatus.retrofit.APIClient;
import com.epsilon.FunwithStatus.retrofit.APIInterface;
import com.epsilon.FunwithStatus.utills.Constants;
import com.epsilon.FunwithStatus.utills.Sessionmanager;
import com.epsilon.FunwithStatus.utills.Utils;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.UUID;

public class whatsappImageSlideActivity extends BaseActivity {

    ViewPager pager;
    Activity activity;
    String name;
    APIInterface apiInterface ;
    LinearLayout layout_content, ll;
    RelativeLayout mainlayout;
    ImageView display_image, download, like, dislike, share, delete, whatsapp, facebook;
    InputStream is = null;
    Sessionmanager sessionmanager;
    ProgressDialog mProgressDialog;
    Toolbar toolbar;
    TextView upload_it;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsapp_image_slide);

        activity = this;
        apiInterface = APIClient.getClient().create(APIInterface.class);
        sessionmanager = new Sessionmanager(getActivity());
        pager = (ViewPager) findViewById(R.id.pager);
        Intent mIntent = getIntent();
        final int position = mIntent.getIntExtra("position", 0);
        final String Id = getIntent().getStringExtra("Id");
        name = getIntent().getStringExtra("NAME");
        String u_name = getIntent().getStringExtra("U_NAME");
        final String email = sessionmanager.getValue(Sessionmanager.Email);
        final String loginuser = sessionmanager.getValue(Sessionmanager.Name);
        final String bitmap = getIntent().getStringExtra("picture");


        display_image = (ImageView) findViewById(R.id.display_image);
        download = (ImageView) findViewById(R.id.download);
        share = (ImageView) findViewById(R.id.share);
        whatsapp = (ImageView) findViewById(R.id.whatsapp);
        facebook = (ImageView) findViewById(R.id.facebook);
        layout_content = (LinearLayout) findViewById(R.id.layout_content);
        ll = (LinearLayout) findViewById(R.id.ll);
        mainlayout = (RelativeLayout) findViewById(R.id.mainlayout);
        upload_it = (TextView) findViewById(R.id.upload_it);
        share.setColorFilter(getResources().getColor(R.color.colorAccent));
        download.setColorFilter(getResources().getColor(R.color.colorAccent));

        if (name.equalsIgnoreCase("WHATSAPP")) {
            WhatsappImageAdapter adapter = new WhatsappImageAdapter(getActivity());
            pager.setAdapter(adapter);
            pager.post(new Runnable() {
                @Override
                public void run() {
                    pager.setCurrentItem(position, true);
                }
            });
        }

        upload_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadMultipart(bitmap);
            }
        });
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation animation_2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.bounce);
                final Animation animation_3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);

                whatsapp.startAnimation(animation_2);

                animation_2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        whatsapp.startAnimation(animation_3);
                        boolean result = Utils.checkPermission(getActivity());
                        if (result) {
                            String path = Constants.items.get(position).getImage();//it contain your path of image..im using a temp string..
                            Uri uri = Uri.parse(path);
                            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.epsilon.FunwithStatus");
                            whatsappIntent.setType("text/plain");
                            whatsappIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            whatsappIntent.setType("image/jpeg");
                            whatsappIntent.setPackage("com.whatsapp");

                            try {
                                getActivity().startActivity(whatsappIntent);
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getActivity(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation animation_2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.bounce);
                final Animation animation_3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);

                facebook.startAnimation(animation_2);

                animation_2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        facebook.startAnimation(animation_3);
                        boolean result = Utils.checkPermission(getActivity());
                        if (result) {
                            try {
                                String path = Constants.items.get(position).getImage();//it contain your path of image..im using a temp string..
                                MediaScannerConnection.scanFile(getActivity(),
                                        new String[]{path}, null,
                                        new MediaScannerConnection.OnScanCompletedListener() {
                                            @Override
                                            public void onScanCompleted(final String path,
                                                                        final Uri picUri) {
                                                try {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                Intent videoshare = new Intent(Intent.ACTION_SEND);
                                                                videoshare.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.epsilon.FunwithStatus");
                                                                videoshare.setType("text/plain");
                                                                videoshare.putExtra(Intent.EXTRA_STREAM, picUri);
                                                                videoshare.setType("image/*");
                                                                videoshare.setPackage("com.facebook.katana");
                                                                videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                getActivity().startActivity(videoshare);

                                                            } catch (Exception e) {
                                                                Log.e("Error....", e.toString());
                                                                e.printStackTrace();

                                                                Intent videoshare = new Intent(Intent.ACTION_SEND);
                                                                videoshare.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.epsilon.FunwithStatus");
                                                                videoshare.setType("text/plain");
                                                                videoshare.putExtra(Intent.EXTRA_STREAM, picUri);
                                                                videoshare.setType("image/*");
                                                                videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                getActivity().startActivity(Intent.createChooser(videoshare, "Share video by..."));
                                                            }
                                                        }
                                                    });

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Animation animation_2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.bounce);
                final Animation animation_3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);

                share.startAnimation(animation_2);

                animation_2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        share.startAnimation(animation_3);
                        boolean result = Utils.checkPermission(getActivity());
                        if (result) {
                            String path = Constants.items.get(position).getImage();//it contain your path of image..im using a temp string..
                            Uri uri = Uri.parse(path);
                            try {

                                Intent videoshare = new Intent(Intent.ACTION_SEND);
                                videoshare.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.epsilon.FunwithStatus");
                                videoshare.setType("text/plain");
                                videoshare.putExtra(Intent.EXTRA_STREAM, uri);
                                videoshare.setType("image/*");
                                videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                getActivity().startActivity(videoshare);
                            } catch (Exception e) {
                                Log.e("Error....", e.toString());
                                e.printStackTrace();

                                Intent videoshare = new Intent(Intent.ACTION_SEND);
                                videoshare.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.epsilon.FunwithStatus");
                                videoshare.setType("text/plain");
                                videoshare.putExtra(Intent.EXTRA_STREAM, uri);
                                videoshare.setType("image/*");
                                videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                getActivity().startActivity(Intent.createChooser(videoshare, "Share video by..."));
                            }
                        }

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String sourceLocation = Constants.items.get(position).getImage();
                final String targetLocation = Environment.getExternalStorageDirectory() + "/" + "FunwithStatus" + "/";
                final Animation animation_2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.bounce);
                final Animation animation_3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.abc_fade_out);

                download.startAnimation(animation_2);

                animation_2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        download.startAnimation(animation_3);
                        boolean result = Utils.checkPermission(getActivity());
                        if (result) {
                            Utils.copyFileOrDirectory(sourceLocation, targetLocation);
                            addImageToGallery(Constants.items.get(position).getImage(), getActivity());
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

        });
    }

    public void uploadMultipart(String bitmap) {

        String user = sessionmanager.getValue(Sessionmanager.Name);

        try {
            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, Constants.UPLOAD_URL)
                    .addFileToUpload(bitmap, "image") //Adding file
                    .addParameter("subcata", "Featured") //Adding text parameter to the request
                    .addParameter("name", user) //Adding text parameter to the request
                    .addParameter("user", user) //Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
            Toast.makeText(getActivity(), "Add Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception exc) {

        }
    }

    public static void addImageToGallery(String myDir, Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
        values.put(MediaStore.MediaColumns.DATA, myDir);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Toast.makeText(context, "Save Image Successfully", Toast.LENGTH_SHORT).show();
    }
}
