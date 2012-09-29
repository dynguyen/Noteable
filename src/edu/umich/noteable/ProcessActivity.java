package edu.umich.noteable;

import org.joone.net.NeuralNet;

import openomr.ann.ANNInterrogator;
import openomr.omr_engine.DetectionProcessor;
import openomr.omr_engine.StaveDetection;
import openomr.omr_engine.StaveParameters;
import openomr.omr_engine.YProjection;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;

public class ProcessActivity extends Activity {

	private Bitmap image;
	private NeuralNet neuralNetwork;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        String imagePath = getIntent().getExtras().getString("IMAGE_PATH");
        image = BitmapFactory.decodeFile(imagePath);
        ANNInterrogator ann = new ANNInterrogator(getBaseContext());
        neuralNetwork = ann.getNeuralNetwork();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_process, menu);
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

    public void processImage(View view) {
    	YProjection yproj = new YProjection(image);
    	yproj.calcYProjection(0, image.getHeight(), 0, image.getWidth());
    	
    	StaveParameters params = new StaveParameters(image);
    	params.calcParameters();
    	
    	StaveDetection staveDetection = new StaveDetection(yproj, params);
    	staveDetection.locateStaves();
    	staveDetection.calcNoteDistance();
    	Log.d("Noteable", String.valueOf(staveDetection.getStaveList().size()));
    	DetectionProcessor processor = new DetectionProcessor(image, staveDetection, neuralNetwork);
    	processor.processAll();
    	
    }
    
    public void loadExampleImage(View view) {
    	image = BitmapFactory.decodeFile("/storage/sdcard0/Pictures/MyCameraApp/sample.png");
    }
}
