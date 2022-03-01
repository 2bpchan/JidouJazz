package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import musicInfrastructure.Measure;
import musicInfrastructure.Motif;
import musicInfrastructure.Note;
import scoring.AutoScorer;

public class MusicDriver {

	public static double slowdownMultiplier = 20;
	public static double backgroundVolumeMultiplier = 0.8;
	private static MidiChannel[] channels;
	private static int INSTRUMENT = 0;
	private static int BACKGROUND_INSTRUMENT = 1;
	private static int VOLUME = 80;

	public static void main(String[] args) {

		// HashMap<String, ArrayList<String>> playableNotesPerChord =
		// initializeChordLibrary();

		System.out.println("Welcome to the JidouJazz Motif Generator!");
		System.out.println("For lazy reasons, this is a console-based app.");
		while (true) {
			Scanner s = new Scanner(System.in);
			System.out.println(
					"Use the command 'generate' to generate a new motif, or read a motif from JSON using 'read'.");
			Motif melody = null;
			ArrayList<Motif> bulkMelodies = new ArrayList<Motif>();
			while (melody == null && bulkMelodies.isEmpty()) {
				switch (s.nextLine().toLowerCase()) {
				case "g":
				case "generate":
					melody = new Motif();
					break;
				case "bg":
				case "bulkgenerate":
					System.out.println("How many would you like to generate?");
					int numberToGenerate = 10000;
					for (int i = 0; i < numberToGenerate; i++) {
						bulkMelodies.add(new Motif());
					}
					break;
				case "r":
				case "read":
					System.out.println("File name?: ");
					String fileName = s.nextLine();
					JSONParser jsonParser = new JSONParser();
					try (FileReader reader = new FileReader(fileName)) {
						JSONArray measureArray = (JSONArray) jsonParser.parse(reader);
						melody = new Motif(measureArray);
					} catch (FileNotFoundException e) {
						System.out.println("File not found.");
						continue;
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ParseException e) {
						System.out.println("Selected JSON file had bad formatting.");
						e.printStackTrace();
					}
					break;
				case "e":
				case "ex":
				case "exit":
					s.close();
					System.exit(0);
					break;
				default:
					System.out.println("Unrecognized command.");
					break;
				}
			}
			if (!bulkMelodies.isEmpty()) {
				AutoScorer scorer = new AutoScorer();
				System.out.println("Bulk motifs generated.");
				System.out.println("Automatically scoring and ranking motifs...");
				for (Motif m : bulkMelodies) {
					m.score = scorer.masterScore(m);
				}
				bulkMelodies.sort(null);
				Collections.reverse(bulkMelodies);
				melody = bulkMelodies.get(0);
				while (melody != null) {
					System.out.println("Select a motif (0-" + (bulkMelodies.size() - 1) + ")");
					// TODO: error handling for bad numbers
					int selectedMotifIndex = Integer.parseInt(s.nextLine());
					melody = bulkMelodies.get(selectedMotifIndex);
					performActionsOnMotif(melody, s);
				}

			} else {
				performActionsOnMotif(melody, s);
			}

		}

	}

	public static void performActionsOnMotif(Motif melody, Scanner s) {
		while (true) {
			System.out.println("What would you like to do with the motif you have selected/generated?");
			System.out.println("Use the command 'play' to listen to the motif, or write it to JSON using 'write'");
			System.out.println("Use the 'back' command to go back to reading/generating motifs");
			switch (s.nextLine().toLowerCase()) {
			case "p":
			case "play":
				playMotif(melody);
				break;
			case "w":
			case "write":
				System.out.println("File name? (.json)");
				String fileName = s.nextLine();
				writeMotifToFile(melody, fileName);
				break;
			case "s":
			case "score":
				AutoScorer scorer = new AutoScorer();
				System.out.println(scorer.masterScore(melody));
				break;
			case "rs":
			case "rstring":
				System.out.println("Rhythm String: " + melody.getRhythmString());
				System.out.println("Continous Rhythm String: " + melody.getContinuousRhythmString());
				break;
			case "e":
			case "ex":
			case "exit":
				s.close();
				System.exit(0);
			case "back":
			case "b":
				melody = null;
				break;

			}
			if (melody == null) {
				break;
			}
		}
	}

	public static HashMap<String, ArrayList<String>> initializeChordLibrary() {
		HashMap<String, ArrayList<String>> playableNotesPerChord = new HashMap<String, ArrayList<String>>();
		ArrayList<String> playableGSharp = new ArrayList<String>(
				Arrays.asList("C", "D#", "F", "F#", "G", "G#", "A#", "B"));
		ArrayList<String> playableG = new ArrayList<String>(Arrays.asList("C", "D", "D#", "F", "F#", "G", "A#", "B"));
		ArrayList<String> playableC = new ArrayList<String>(Arrays.asList("C", "D", "D#", "F", "F#", "G", "A#", "B"));
		ArrayList<String> playableDSharp = new ArrayList<String>(
				Arrays.asList("C", "C#", "D", "D#", "F", "F#", "G", "A#"));

		ArrayList<String> keyOfDSharp = new ArrayList<>(Arrays.asList("D#", "F", "G", "G#", "A#", "C", "D", "D#"));

//		playableNotesPerChord.put("G#", make234Octaves(playableGSharp));
//		playableNotesPerChord.put("G", make234Octaves(playableG));
//		playableNotesPerChord.put("C", make234Octaves(playableC));
//		playableNotesPerChord.put("D#", make234Octaves(playableDSharp));

		playableNotesPerChord.put("G#", make234Octaves(keyOfDSharp));
		playableNotesPerChord.put("G", make234Octaves(keyOfDSharp));
		playableNotesPerChord.put("C", make234Octaves(keyOfDSharp));
		playableNotesPerChord.put("D#", make234Octaves(keyOfDSharp));

		return playableNotesPerChord;
	}

	public static ArrayList<String> make234Octaves(ArrayList<String> toOctavize) {
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 2; i < 5; i++) {
			for (String s : toOctavize) {
				result.add(i + s);
			}
		}
		return result;
	}

	public static void writeMotifToFile(Motif melody) {
		JSONArray melodyArray = melody.toJsonArray();
		System.out.println(melodyArray);
		String currentDateTime = LocalDateTime.now().toString().replaceAll("\\.|:", "-");
		try {

			String fileName = "./" + currentDateTime + "-motif.json";
			System.out.println(fileName);
			File file = new File(fileName);
			file.createNewFile();
			FileWriter fw = new FileWriter(fileName);
			fw.write(melodyArray.toJSONString());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeMotifToFile(Motif melody, String fileName) {
		JSONArray melodyArray = melody.toJsonArray();
		System.out.println(melodyArray);
		String currentDateTime = LocalDateTime.now().toString().replaceAll("\\.|:", "-");
		try {

			fileName += ".json";
			System.out.println(fileName);
			File file = new File(fileName);
			file.createNewFile();
			FileWriter fw = new FileWriter(fileName);
			fw.write(melodyArray.toJSONString());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void playMotif(Motif melody) {
		HashMap<String, ArrayList<String>> playableNotesPerChord = initializeChordLibrary();
		Synthesizer synth;
		try {
			synth = MidiSystem.getSynthesizer();
			synth.open();
			channels = synth.getChannels();
			for (Measure m : melody.getMeasures()) {
				ArrayList<String> playableNotes = playableNotesPerChord.get(m.getChord());
				playMeasure(m, playableNotes);
			}

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void playMeasure(Measure measure, ArrayList<String> playableNotes) throws InterruptedException {
		switch (measure.getChord()) {
		case "G#":
			channels[BACKGROUND_INSTRUMENT].noteOn(id("2G#"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOn(id("3C"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOn(id("3D#"), (int) (VOLUME * backgroundVolumeMultiplier));
			break;
		case "G":
			channels[BACKGROUND_INSTRUMENT].noteOn(id("2G"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOn(id("2B"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOn(id("3D"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOn(id("3F"), (int) (VOLUME * backgroundVolumeMultiplier));
			break;
		case "C":
			channels[BACKGROUND_INSTRUMENT].noteOn(id("3C"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOn(id("3D#"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOn(id("3G"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOn(id("3A#"), (int) (VOLUME * backgroundVolumeMultiplier));
			break;
		case "D#":
			channels[BACKGROUND_INSTRUMENT].noteOn(id("3D#"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOn(id("3G"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOn(id("3A#"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOn(id("4C#"), (int) (VOLUME * backgroundVolumeMultiplier));
			break;
		}
		for (Note n : measure.getNotes()) {
			int noteValue = n.getValue();
			if (noteValue == -1) {
				rest((int) (n.getLength() * slowdownMultiplier));
			} else {
				String realNote = playableNotes.get(noteValue);
				play(realNote, (int) (n.getLength() * slowdownMultiplier));
			}

		}
		switch (measure.getChord()) {
		case "G#":
			channels[BACKGROUND_INSTRUMENT].noteOff(id("2G#"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOff(id("3C"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOff(id("3D#"), (int) (VOLUME * backgroundVolumeMultiplier));
			break;
		case "G":
			channels[BACKGROUND_INSTRUMENT].noteOff(id("2G"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOff(id("2B"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOff(id("3D"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOff(id("3F"), (int) (VOLUME * backgroundVolumeMultiplier));
			break;
		case "C":
			channels[BACKGROUND_INSTRUMENT].noteOff(id("3C"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOff(id("3D#"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOff(id("3G"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOff(id("3A#"), (int) (VOLUME * backgroundVolumeMultiplier));
			break;
		case "D#":
			channels[BACKGROUND_INSTRUMENT].noteOff(id("3D#"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOff(id("3G"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOff(id("3A#"), (int) (VOLUME * backgroundVolumeMultiplier));
			channels[BACKGROUND_INSTRUMENT].noteOff(id("4C#"), (int) (VOLUME * backgroundVolumeMultiplier));
			break;
		}
	}

	private static void play(String note, int duration) throws InterruptedException {
		// * start playing a note
		channels[INSTRUMENT].noteOn(id(note), VOLUME);
		// * wait
		Thread.sleep(duration);
		// * stop playing a note
		channels[INSTRUMENT].noteOff(id(note));
	}

	/**
	 * Plays nothing for the given duration
	 */
	private static void rest(int duration) throws InterruptedException {
		Thread.sleep(duration);
	}

	/**
	 * Returns the MIDI id for a given note: eg. 4C -> 60
	 * 
	 * @return
	 */
	private static int id(String note) {
		ArrayList<String> notes = new ArrayList<String>(
				Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"));
		int octave = Integer.parseInt(note.substring(0, 1));
		return notes.indexOf(note.substring(1)) + 12 * octave + 12;


	}

}
