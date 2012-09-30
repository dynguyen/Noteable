package edu.umich.noteable.midi;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import android.util.Log;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

import openomr.omr_engine.L0_Segment;
import openomr.omr_engine.PitchCalculation;
import openomr.omr_engine.Staves;

public class MidiUtil {

	public static MidiFile generate(LinkedList<Staves> stavesList) {
		MidiTrack tempoTrack = new MidiTrack();
		MidiTrack noteTrack = new MidiTrack();
		
		TimeSignature ts = new TimeSignature();
		ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
		
		Tempo t = new Tempo();
		t.setBpm(228);
		
		tempoTrack.insertEvent(ts);
		tempoTrack.insertEvent(t);
		
		for (int i = 0; i < stavesList.size(); i++) {
			Staves tempStave = stavesList.get(i);
			LinkedList<L0_Segment> tempSymbol = tempStave.getSymbolPos();
			Log.d("Noteable", "Symbols: " + tempSymbol.size());
			for (int k=0; k<tempSymbol.size(); k++) {
				L0_Segment tempPos = tempSymbol.get(k);
				LinkedList<PitchCalculation> notes = tempPos.getNotes();
				Log.d("Noteable", "Notes: " + notes.size());
				for (int j=0; j<notes.size(); j++) {
					PitchCalculation tempNote = notes.get(i);
					Log.d("Noteable", "Pitch: " + tempNote.getNote());
					//System.out.println("Note: " + (tempNote.getNote()+64));
					//add(tempNote.getNote()+64, tempNote.getDuration());
					NoteOn on = new NoteOn(j*480, 0, tempNote.getNote() + 64, 100);
					NoteOff off = new NoteOff(j*480 + 120, 0, tempNote.getNote() + 64, 0);
					noteTrack.insertEvent(on);
					noteTrack.insertEvent(off);
					//noteTrack.insertNote(0, tempNote.getNote() + 64, 10, 4800, 120);
				}
				//System.out.println("End Symbol");
			}
		}
//		for(int i = 0; i < 80; i++) {
//			
//			int channel = 0, pitch = 1 + i, velocity = 100;
//			NoteOn on = new NoteOn(i*480, channel, pitch, velocity);
//			NoteOff off = new NoteOff(i*480 + 120, channel, pitch, 0);
//			
//			noteTrack.insertEvent(on);
//			noteTrack.insertEvent(off);
//			
//			// There is also a utility function for notes that you should use instead of the above.
//			noteTrack.insertNote(channel, pitch + 2, velocity, i*480, 120);
//		}
	
		ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
		tracks.add(tempoTrack);
		tracks.add(noteTrack);
		
		MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
		return midi;
	}
}
