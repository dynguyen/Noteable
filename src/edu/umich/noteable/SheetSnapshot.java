package edu.umich.noteable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import openomr.imageprocessing.DoBlackandWhite;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class SheetSnapshot extends Activity {
	private static final int CAMERA_PIC_REQUEST = 1337;
	private static final int CHOOSE_PIC_REQUEST = 1500;
	private Uri selectedImagePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_snapshot);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_sheet_snapshot, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void loadFile(View view) {

    	Intent intent = new Intent();
        intent.setType("image/*"); //opens up gallery
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select an image"), CHOOSE_PIC_REQUEST); //

    }
    public void takephoto(View view){
    
    //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
    
   // startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
    	
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

    	Uri picFile = getOutputMediaFilePath("IMG_raw.jpg");
    	intent.putExtra(MediaStore.EXTRA_OUTPUT, picFile);
    	startActivityForResult(intent, CAMERA_PIC_REQUEST);
    
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (requestCode == CAMERA_PIC_REQUEST) {
        	if (resultCode == RESULT_OK) {
        		selectedImagePath = getOutputMediaFilePath("IMG_raw.jpg");
        		Toast.makeText(getBaseContext(), "Photo taken", Toast.LENGTH_SHORT);
        	} else if (resultCode == RESULT_CANCELED) {
        		Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_SHORT).show();
        		return;
        	} else {
        		Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
        		return;
        	}
        } else if (requestCode == CHOOSE_PIC_REQUEST) {
        	if (resultCode == RESULT_OK) {
                selectedImagePath = data.getData();
                Toast.makeText(getBaseContext(), "Picture selected", Toast.LENGTH_SHORT);
        	} else if (resultCode == RESULT_CANCELED) {
        		Toast.makeText(getBaseContext(), "Canceled", Toast.LENGTH_SHORT).show();
        		return;
        	} else {
        		Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
        		return;
        	}
        }
//    	Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath.getPath());
//    	Bitmap bit2 = Bitmap.createScaledBitmap(bitmap, 2048, 2048, true);
//    	
//    	ImageView image = (ImageView) findViewById(R.id.viewer);
//    	image.setImageBitmap(bit2);
    	//bit2=null;
//    	DoBlackandWhite bwProcess = new DoBlackandWhite(bit2);
//    	bit2 = bwProcess.doBW();
//    	
//    	selectedImagePath = getOutputMediaFilePath("IMG_processed.png");
//    	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//    	File processedImage = new File(selectedImagePath);
//    	bit2.compress(Bitmap.CompressFormat.PNG, 40, bytes);
//    	OutputStream imageOutput = null;
//    	try {
//			imageOutput = getContentResolver().openOutputStream(Uri.fromFile(processedImage));
//			imageOutput.write(bytes.toByteArray());
//			imageOutput.flush();
//			imageOutput.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//    	
//    	ImageView image = (ImageView) findViewById(R.id.viewer);
//    	image.setImageBitmap(bit2);
    }
    
    public void processImage(View view) {
    	Intent intent = new Intent(this, ProcessActivity.class);
    	intent.putExtra("IMAGE_PATH", selectedImagePath.getPath());
    	startActivity(intent);
    }
    
    /** Create a File for saving the image */
	private static Uri getOutputMediaFilePath(String fileName){

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "Noteable");

	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("Noteable", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    File mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
	    return Uri.fromFile(mediaFile);
	}
	

 
}
