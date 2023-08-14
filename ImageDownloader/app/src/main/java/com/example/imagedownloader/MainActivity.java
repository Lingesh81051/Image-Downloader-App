package com.example.imagedownloader;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button download,browse;
//    Button download2;
    ImageView imageView;
    URL url;
    AsyncTask asyncTask;
    EditText textView;
    EditText qual;
    private ProgressDialog progressDialog;
    BitmapDrawable bitmapDrawable;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qual = findViewById(R.id.quality);
        textView = findViewById(R.id.editTextText);
        browse = findViewById(R.id.button2);
        download = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
//        download2 = findViewById(R.id.button4);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncTask = new DownloadTask().execute(StringToURL());


            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,1);
            }
        });

//        download2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
//                bitmap = bitmapDrawable.getBitmap();
//
//                FileOutputStream fileOutputStream = null;
//
//
//                File sdCard = Environment.getExternalStorageDirectory();
//                File Directory = new File(sdCard.getAbsolutePath()+ "/Download");
//                Directory.mkdir();
//
//                String filename = String.format("%d.jpg",System.currentTimeMillis());
//                File outFile = new File(Directory,filename);
//
//                Toast.makeText(MainActivity.this, "Image saved Successfully", Toast.LENGTH_SHORT).show();
//                try{
//                    fileOutputStream = new FileOutputStream(outFile);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100,fileOutputStream);
//                    fileOutputStream.flush();
//                    fileOutputStream.close();
//
//                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                    intent.setData(Uri.fromFile(outFile));
//                    sendBroadcast(intent);
//                }catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode == RESULT_OK && null !=data){
            Uri selectedImage = data.getData();
            String[] filepath = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,filepath,null,null,null);
            cursor.moveToFirst();
            int columneIndex = cursor.getColumnIndex(filepath[0]);
            String picturepath = cursor.getString(columneIndex);
            cursor.close();

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturepath));
            String filename = picturepath.substring(picturepath.lastIndexOf("/")+1);
            textView.setText(filename);
        }
    }

    private class DownloadTask extends AsyncTask<URL,Void,Bitmap> {

        @Override
        protected void onPreExecute() {
          progressDialog = ProgressDialog.show(MainActivity.this,"","please wait...",false,false);

        }

        @Override
        protected Bitmap doInBackground(URL... urls) {
            URL url = urls[0];
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection)  url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                return BitmapFactory.decodeStream(bufferedInputStream);
            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            progressDialog.dismiss();
            if (bitmap!=null){
                imageView.setImageBitmap(bitmap);
                DownloadImage();

            }else {
                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void DownloadImage() {
        bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        bitmap = bitmapDrawable.getBitmap();

        FileOutputStream fileOutputStream = null;

        File sdCard = Environment.getExternalStorageDirectory();
        File Directory = new File(sdCard.getAbsolutePath() + "/Download");
        Directory.mkdir();

        String filename = String.format("%d.jpg",System.currentTimeMillis());
        File outFile = new File(Directory,filename);

        Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
        try{
            fileOutputStream = new FileOutputStream(outFile);
            int qualInt = 100;
            qualInt=Integer.parseInt(qual.getText().toString());


            bitmap.compress(Bitmap.CompressFormat.JPEG, qualInt,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(outFile));
            sendBroadcast(intent);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected URL StringToURL() {
        try{
            String textview = textView.getText().toString();
            url = new URL(textview);
            return url;


        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }
}