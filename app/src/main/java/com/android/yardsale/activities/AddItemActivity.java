package com.android.yardsale.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.yardsale.R;
import com.android.yardsale.fragments.YouDoNotOwnThisAlertDialog;
import com.android.yardsale.helpers.image.ImageHelper;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.YardSale;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.IOException;

public class AddItemActivity extends ActionBarActivity {

    public final String APP_TAG = "YardSaleApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int PICK_PHOTO_CODE = 1046;
    private YardSaleApplication client;
    public String photoFileName = "photo.jpg";
    private ImageView ivItemPreview;
    private EditText etAddItemDescription;
    private EditText etAddItemPrice;
    private YardSale yardSale;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        //TODO setup action bar

        image = null;

        String yardSaleId = getIntent().getStringExtra("yard_sale_id");
        ParseQuery getQuery = YardSale.getQuery();
        try {
            yardSale = (YardSale) getQuery.get(yardSaleId);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!isAuthorized(yardSale)) {
            YouDoNotOwnThisAlertDialog dialog = YouDoNotOwnThisAlertDialog.newInstance("add to yardsale " + yardSale.getTitle());
            dialog.show(getFragmentManager(), "cannot_add_item");
        }

        client = new YardSaleApplication(this);

        ivItemPreview = (ImageView) findViewById(R.id.ivItemPreview);
        etAddItemDescription = (EditText) findViewById(R.id.etAddItemDescription);
        etAddItemPrice = (EditText) findViewById(R.id.etAddItemPrice);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onTakePicture(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name
        // Start the image capture intent to take photo
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public Uri getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                image = BitmapFactory.decodeFile(takenPhotoUri.getPath());
                Bitmap bMapScaled = Bitmap.createScaledBitmap(image, 150, 100, true);
                ivItemPreview.setImageBitmap(bMapScaled);
            }
        } else if (requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            // Do something with the photo based on Uri
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
            // Load the selected image into a preview
            Bitmap bMapScaled = Bitmap.createScaledBitmap(image, 150, 100, true);
            ivItemPreview.setImageBitmap(bMapScaled);
        }
    }

    public void addItem(View view) {
        Number price = Double.parseDouble(etAddItemPrice.getText().toString());
        String description = String.valueOf(etAddItemDescription.getText());
        if(image == null){
            Toast.makeText(this, "please add image!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        ParseFile imageParseFile = new ParseFile(ImageHelper.getBytesFromBitmap(image));
        client.createItem(this,description, price, imageParseFile, yardSale);

    }

    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Bring up gallery to select a photo
        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    private boolean isAuthorized(YardSale yardSale) {
        return yardSale.getSeller().equals(ParseUser.getCurrentUser());
    }


}
