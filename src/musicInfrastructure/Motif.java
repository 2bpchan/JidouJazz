package musicInfrastructure;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author chanb
 *
 */
public class Motif implements Comparable<Motif>{
	public final String[] BASIC_CHORDS = new String[] { "G#", "G#", "G", "G", "C", "C", "D#", "D#" };
	private ArrayList<Measure> measures;
	
	public final double percentRestLowerBound = 0.05;
	public final double percentRestUpperBound = 0.3;
	public double score;
	/**
	 * Basic constructor for a random Motif
	 */
	public Motif() {
		setMeasures(new ArrayList<Measure>());
		double percentRestRange = percentRestUpperBound - percentRestLowerBound;
		double motifSpecificPercentRest = Math.random()*percentRestRange + percentRestLowerBound;
		//System.out.println(motifSpecificPercentRest);
		for (int i = 0; i < BASIC_CHORDS.length; i++) {
			int beats = 0;
			Measure currentMeasure = new Measure(new ArrayList<Note>(), BASIC_CHORDS[i], i);
			while (beats < 96) {
				// make notes and subtract from beats until the measure is filled
				// add some randomness but not too much because this is still a basic test
				NoteType type = Note.getRandomNonTriplet();
				if (Note.getNoteTypeLength(type) <= 96 - beats) {
					// note fits in the remaining beats
					Note newNote = new Note((int) ((Math.random() * 24)), beats, type);
					if (Math.random() < motifSpecificPercentRest) {
						newNote.value = -1;
					}
					beats += Note.getNoteTypeLength(type);
					currentMeasure.addNote(newNote);
				}
			}
			getMeasures().add(currentMeasure);
		}
	}
	
	/**
	 * @param Motif in JSONArray format
	 * Constructor that takes a previously constructed Motif in JSONArray format
	 */
	@SuppressWarnings("unchecked")
	public Motif(JSONArray motif) {
		setMeasures(new ArrayList<Measure>());
		Iterator<JSONObject> measureIterator = motif.iterator();
		while (measureIterator.hasNext()) {
			JSONObject currentMeasure = measureIterator.next();
			int measureNumber = Integer.parseInt((String) currentMeasure.get("number"));
			String chord = (String) currentMeasure.get("chord");
			JSONArray notesJSON = (JSONArray) currentMeasure.get("notes");
			ArrayList<Note> notes = new ArrayList<Note>();
			Iterator<JSONObject> noteIterator = notesJSON.iterator();
			while (noteIterator.hasNext()) {
				JSONObject currentNote = noteIterator.next();
				int noteValue = Integer.parseInt((String) currentNote.get("value"));
				int noteStart = Integer.parseInt((String) currentNote.get("start"));
				int noteLength = Integer.parseInt((String) currentNote.get("length"));
				;
				notes.add(new Note(noteValue, noteStart, noteLength));
			}
			this.measures.add(new Measure(notes, chord, measureNumber));
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Measure m : getMeasures()) {
			sb.append(m);
		}
		return sb.toString();
	}

	public ArrayList<Measure> getMeasures() {
		return measures;
	}

	public void setMeasures(ArrayList<Measure> measures) {
		this.measures = measures;
	}

	/**
	 * Compiles the notes and rests into the following string format for easier
	 * analysis of patterns. Whole Note/Rest -> W/w Half Note/Rest -> H/h Quarter
	 * Note/Rest -> Q/q Eighth Note/Rest -> E/e Sixteenth Note/Rest -> S/s Triplets
	 * -> TBD
	 * 
	 * @return Rhythm String
	 */
	public String getRhythmString() {
		StringBuilder sb = new StringBuilder();
		for (Measure m : this.measures) {
			for (Note n : m.getNotes()) {
				switch (n.length) {
				case 96:
					if (n.value == -1) {
						sb.append("w");
					} else {
						sb.append("W");
					}
					break;
				case 48:
					if (n.value == -1) {
						sb.append("h");
					} else {
						sb.append("H");
					}
					break;
				case 24:
					if (n.value == -1) {
						sb.append("q");
					} else {
						sb.append("Q");
					}
					break;
				case 12:
					if (n.value == -1) {
						sb.append("e");
					} else {
						sb.append("E");
					}
					break;
				case 6:
					if (n.value == -1) {
						sb.append("s");
					} else {
						sb.append("S");
					}
					break;
				}
			}
			sb.append(",");
		}
		return sb.toString();
	}
	
	/**
	 * Simple method to remove measure separators for cross-measure rhythm analysis
	 * @return Rhythm String, no commas
	 */
	public String getContinuousRhythmString() {
		return getRhythmString().replaceAll(",","");
	}
	/**
	 * Method to convert a motif into a JSON format for storage
	 * @return Motif's data in JSON format, stored in a JSONArray Object
	 */
	@SuppressWarnings("unchecked")
	public JSONArray toJsonArray() {
		JSONArray allMeasures = new JSONArray();
		int measureIndex = 0;
		for (Measure m : getMeasures()) {
			JSONArray notes = new JSONArray();
			int noteIndex = 0;
			for (Note n : m.getNotes()) {
				JSONObject noteDetails = new JSONObject();
				noteDetails.put("value", new Integer(n.getValue()).toString());
				noteDetails.put("start", new Integer(n.getStart()).toString());
				noteDetails.put("length", new Integer(n.getLength()).toString());
				notes.add(noteDetails);
				noteIndex++;
			}
			JSONObject measure = new JSONObject();

			measure.put("number", new Integer(m.measureNumber).toString());
			measure.put("chord", m.getChord());
			measure.put("notes", notes);
			allMeasures.add(measure);
		}
		return allMeasures;
	}

	@Override
	public int compareTo(Motif other) {
		if(this.score<other.score) {
			return -1;
		} else if (this.score>other.score) {
			return 1;
		} else {
			//System.out.println("GO BUY A LOTTERY TICKET");
		}
		return 0;
	}
}
