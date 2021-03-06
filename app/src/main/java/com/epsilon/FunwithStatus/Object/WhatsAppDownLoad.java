package com.epsilon.FunwithStatus.Object;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.epsilon.FunwithStatus.adapter.GridViewItem;
import com.epsilon.FunwithStatus.utills.Constants;
import com.epsilon.FunwithStatus.utills.Utils;

import java.io.File;

public class WhatsAppDownLoad extends AsyncTask<Void, Void, String> {
    ProgressDialog mProgressDialog;

    Context context;
//    String video;
    int position;
    GridViewItem data;

    public WhatsAppDownLoad(Context context, int position) {
        this.context = context;
//        this.video = video;
        this.position = position;
    }

    public WhatsAppDownLoad(Context context, GridViewItem data) {
        this.context = context;
//        this.video = video;
        this.data = data;
    }

    protected void onPreExecute() {
        mProgressDialog = ProgressDialog.show(context, "",
                "Please wait, Download …");
    }

    @Override
    protected String doInBackground(Void... voids) {
        final String sourceLocation = data.getImage();
        final String targetLocation = Environment.getExternalStorageDirectory() + "/" + "FunwithStatus" + "/";
        String path = data.getImage();//it contain your path of image..im using a temp string..
        String filename = path.substring(path.lastIndexOf("/") + 1);
        File myFile = new File(Environment.getExternalStorageDirectory() + "/" + "FunwithStatus" + "/" + filename);

        if (!myFile.exists()) {
            Utils.copyFileOrDirectory(sourceLocation, targetLocation);
        } else {
            Log.e("downloadFile", "myFile:" + myFile.getAbsolutePath());
            //sharewhatupp(position);
        }
        return "done";
    }


    protected void onPostExecute(String result) {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
        if (result.equals("done")) {
            sharewhatupp();
        }
    }


    public void sharewhatupp() {

        String path = data.getImage();//it contain your path of image..im using a temp string..
        String filename = path.substring(path.lastIndexOf("/") + 1);
        File myFile = new File(Environment.getExternalStorageDirectory() + "/" + "FunwithStatus" + "/" + filename);
        if (myFile.exists()) {
            Log.e("downloadFile", "file:" + myFile.getAbsolutePath());
            Uri uri = Uri.parse(myFile.getPath());
            try {

                Intent videoshare = new Intent(Intent.ACTION_SEND);
                videoshare.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.epsilon.FunwithStatus");
                videoshare.setType("text/plain");
                videoshare.putExtra(Intent.EXTRA_STREAM, uri);
                videoshare.setType("video/*");
                videoshare.setPackage("com.whatsapp");
                videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(videoshare);
            } catch (Exception e) {
                Log.e("Error....", e.toString());
                e.printStackTrace();
                Intent videoshare = new Intent(Intent.ACTION_SEND);
                videoshare.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.epsilon.FunwithStatus");
                videoshare.setType("text/plain");
                videoshare.putExtra(Intent.EXTRA_STREAM, uri);
                videoshare.setType("video/*");
                videoshare.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(Intent.createChooser(videoshare, "Share video by..."));
            }
        }
    }
}

