package com.android.yardsale.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.yardsale.R;
import com.android.yardsale.helpers.YardSaleApplication;
import com.android.yardsale.models.Item;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

public class EditItemActivity extends ActionBarActivity {

    private ImageView ivItemPreview;
    private EditText etEditItemDescription;
    private EditText etEditItemPrice;
    private Item item;
    private YardSaleApplication client;

    public final String APP_TAG = "YardSaleApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2034;
    public final static int PICK_PHOTO_CODE = 2046;
    public String photoFileName = "photo.jpg";
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        client = new YardSaleApplication(this);

        String itemId = getIntent().getStringExtra("item_id");
        ivItemPreview = (ImageView) findViewById(R.id.ivItemPreview);
        etEditItemDescription = (EditText) findViewById(R.id.etItemDescription);
        etEditItemPrice = (EditText) findViewById(R.id.etItemPrice);
        Button btnSaveItem = (Button) findViewById(R.id.btnSaveItem);

        ParseQuery getItemQuery = Item.getQuery();
        try {
            item = (Item) getItemQuery.get(itemId);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Picasso.with(this).load(item.getPhoto().getUrl()).into(ivItemPreview);
        etEditItemDescription.setText(item.getDescription());
        etEditItemPrice.setText("$" + item.getPrice().toString());
        etEditItemPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String current = "";
                if (!s.toString().equals(current)) {
                    etEditItemPrice.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[$,.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed / 100));

                    current = formatted;
                    etEditItemPrice.setText(formatted);
                    etEditItemPrice.setSelection(formatted.length());

                    etEditItemPrice.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(v);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Bring up gallery to select a photo
        startActivityForResult(intent, PICK_PHOTO_CODE);
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

    public void addItem(View view) {
        Number price = Double.parseDouble(etEditItemPrice.getText().toString().replace("$", ""));
        String description = String.valueOf(etEditItemDescription.getText());
        ParseFile imageParseFile;
        if (image != null)
            imageParseFile = new ParseFile(getBytesFromBitmap(image));
        else
            imageParseFile = item.getParseFile("photo");
        client.updateItem(item.getObjectId(), description, price, imageParseFile, item.getYardSale());

        Intent data = new Intent();
        data.putExtra("price", String.valueOf(etEditItemPrice.getText()));
        data.putExtra("desc", String.valueOf(etEditItemDescription.getText()));
        data.putExtra("obj_id", item.getObjectId());
        setResult(RESULT_OK, data);
        finish();
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
