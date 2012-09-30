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
import openomr.imageprocessing.DoBlackandWhite;
import openomr.midi.ScoreGenerator;
import openomr.omr_engine.DetectionProcessor;
import openomr.omr_engine.StaveDetection;
import openomr.omr_engine.StaveParameters;
import openomr.omr_engine.YProjection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
    	Bitmap processedImage = DoBlackandWhite.doBW(image);
    	ImageView imageView = (ImageView) findViewById(R.id.imageView1);
        imageView.setImageBitmap(processedImage);
    	YProjection yproj = new YProjection(processedImage);
    	
    	yproj.calcYProjection(0, processedImage.getHeight(), 0, processedImage.getWidth());
    	
    	StaveParameters params = new StaveParameters(processedImage);
    	params.calcParameters();
    	
    	StaveDetection staveDetection = new StaveDetection(yproj, params);
    	staveDetection.locateStaves();
    	staveDetection.calcNoteDistance();
    	Log.d("Noteable", "Staves recognized: " + staveDetection.getStaveList().size());
    	if (staveDetection.getStaveList().size() == 0) {
    		Toast.makeText(getBaseContext(), "No staves recognized", Toast.LENGTH_SHORT).show();
    	} else {
    		DetectionProcessor processor = new DetectionProcessor(processedImage, staveDetection, neuralNetwork);
    		processor.processAll();
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
    
    public void playMusic(View view) {
    	File midiStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "Noteable");
    	File midiFile = new File(midiStorageDir.getPath() + File.separator + "out.mid");
    	Uri midiUri = Uri.fromFile(midiFile);
    	MediaPlayer player = new MediaPlayer();
    	player.setAudioStreamType(AudioManager.STREAM_MUSIC);
    	try {
			player.setDataSource(getBaseContext(), midiUri);
			player.prepare();
			player.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
}
