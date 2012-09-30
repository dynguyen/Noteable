package edu.umich.noteable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.joone.net.NeuralNet;

import com.leff.midi.MidiFile;

import edu.umich.noteable.midi.MidiUtil;

import openomr.ann.ANNInterrogator;
import openomr.midi.ScoreGenerator;
import openomr.omr_engine.DetectionProcessor;
import openomr.omr_engine.StaveDetection;
import openomr.omr_engine.StaveParameters;
import openomr.omr_engine.YProjection;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
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
        ANNInterrogator ann = ANNInterrogator.getInstance(getBaseContext());
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
    	Log.d("Noteable", "Staves recognized: " + staveDetection.getStaveList().size());
    	if (staveDetection.getStaveList().size() == 0) {
    		Toast.makeText(getBaseContext(), "No staves recognized", Toast.LENGTH_SHORT).show();
    	} else {
    		DetectionProcessor processor = new DetectionProcessor(image, staveDetection, neuralNetwork);
    		processor.processAll();
    	}
    	
    	MidiFile midi = MidiUtil.generate(staveDetection.getStaveList());
    	File midiStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "Noteable");
    	if (!midiStorageDir.exists()) {
    		if (!midiStorageDir.mkdirs()) {
    			Log.d("Noteable", "failed to create directory");
    		}
    	}
    	File midiFile = new File(midiStorageDir.getPath() + File.separator + "out.mid");
    	try {
			midi.writeToFile(midiFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}
    	Toast.makeText(getBaseContext(), "Midi file saved", Toast.LENGTH_SHORT).show();
    	
    }
}
