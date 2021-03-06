package com.epsilon.FunwithStatus;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.epsilon.FunwithStatus.utills.Constants;
import com.epsilon.FunwithStatus.utills.Sessionmanager;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.util.UUID;

public class DemoActivity extends BaseActivity {

    ImageView iv_image;
    Button button_caption_send;
    EditText edit_caption;
    Activity activity;
    String subcat, token, name,maincat;
    int category_id;

    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 12;
    private final int MY_PERMISSIONS_RECORD_AUDIO = 2;
    private final int CROP_PIC = 3;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;
    Sessionmanager sessionmanager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        activity = this;
        Intent mIntent = getIntent();
        category_id = mIntent.getIntExtra("ID", 0);
        sessionmanager = new Sessionmanager(this);
//        requestStoragePermission();
        iv_image = (ImageView) findViewById(R.id.iv_image);
        button_caption_send = (Button) findViewById(R.id.button_caption_send);
        edit_caption = (EditText) findViewById(R.id.edit_caption);
        token = sessionmanager.getValue(Sessionmanager.Token);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                showFileChooser();
            }  else {
                //Request Location Permission
                checkCameraPermission();
                requestAudioPermissions();
            }
        } else {
            showFileChooser();
        }


        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        showFileChooser();
                    }  else {
                        //Request Location Permission
                        checkCameraPermission();
                        requestAudioPermissions();
                    }
                } else {
                    showFileChooser();
                }
            }
        });



//        12:30:01:BE:77:03:33:38:13:E2:D4:72:DE:1F:07:DD:42:C8:3B:1F

        button_caption_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!iv_image.equals("") && iv_image !=null) {
                    uploadMultipart();
                } else {
                    Toast.makeText(getActivity(), "Select Photo First", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(getActivity(), "Please grant permissions to record audio", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording
        else if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

        }
    }

    private void checkCameraPermission() {
            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED
                    ) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        android.Manifest.permission.CAMERA)
                        ) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions( getActivity(),
                            new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
    }

    public void uploadMultipart() {

        if (edit_caption.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getActivity(), "Describe photo", Toast.LENGTH_SHORT).show();
        }
        //getting name for the image
        name = edit_caption.getText().toString().trim();
        //getting the actual path of the image
        String path = getPath(getActivity(),filePath);
        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();
            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, Constants.UPLOAD_URL)
                    .addFileToUpload(path, "file") //Adding file
                    .addParameter("category_id", String.valueOf(category_id)) //Adding text parameter to the request
                    .addParameter("name", name) //Adding text parameter to the request
                    .addParameter("token", token) //Adding text parameter to the request
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload(); //Starting the upload
            Toast.makeText(getActivity(), "Add Successfully", Toast.LENGTH_SHORT).show();
            Intent it = new Intent(getActivity(), ImageListActivity.class);
            it.putExtra("NAME", subcat);
            it.putExtra("REALNAME", maincat);
            startActivity(it);
            finish();
        } catch (Exception exc) {
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            performCrop();
        }
        // user is returning from cropping the image
        else if (requestCode == CROP_PIC) {
            Bundle extras = data.getExtras();
            Bitmap thePic = extras.getParcelable("data");
            ImageView picView = (ImageView) findViewById(R.id.iv_image);
            picView.setImageBitmap(thePic);
        }
    }

//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                iv_image.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }



    //method to get the file path from uri
//    public String getPath(Uri uri) {
//        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//        cursor.moveToFirst();
//        String document_id = cursor.getString(0);
//        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
//        cursor.close();
//
//        cursor = getContentResolver().query(
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
//        cursor.moveToFirst();
//        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//        cursor.close();
//
//        return path;
//    }

    private void performCrop() {
        // take care of exceptions
        try {
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(filePath, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the getActivity() - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_PIC);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast toast = Toast
                    .makeText(this, "This device doesn't support the crop action!", Toast.LENGTH_SHORT);
            toast.show();
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

    //Requesting permission
//    private void requestStoragePermission() {
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
//            return;
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            //If the user has denied the permission previously your code will come to this block
//            //Here you can explain why you need this permission
//            //Explain here why you need this permission
//        }
//        //And finally ask for the permission
//        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
//    }
//
//
//    //This method will be called when the user will tap on allow or deny
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        //Checking the request code of our request
//        if (requestCode == STORAGE_PERMISSION_CODE) {
//
//            //If permission is granted
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //Displaying a toast
//                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
//            } else {
//                //Displaying another toast if permission is not granted
//                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
    public void onBackPressed() {
        if(subcat.equalsIgnoreCase("") && maincat.equalsIgnoreCase("")){
            Intent it = new Intent(DemoActivity.this, Dashboard.class);
            it.putExtra("NAME",subcat);
            it.putExtra("REALNAME",maincat);
            startActivity(it);
            finish();
        }
        Intent it = new Intent(DemoActivity.this, ImageListActivity.class);
        it.putExtra("NAME",subcat);
        it.putExtra("REALNAME",maincat);
        startActivity(it);
        finish();
    }
}
