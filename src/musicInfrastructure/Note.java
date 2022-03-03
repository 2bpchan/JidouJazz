package musicInfrastructure;

import java.util.ArrayList;
import java.util.Arrays;

public class Note {
    int value; // value now corresponds to a value of a note in a chromatic scale, independent of its key
    // ex: for the key of C, the notes of the chromatic scale are
    // C, C#, D, D#, E, F, F#, G, G#, A, A#, B
    // and the note numbers are
    // 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11

    // major scale is 0,2,4,5,7,9,11

    // this is also combined with + 12 for every octave it is shifted upwards


    int start; // relative to 4/4 measure
    // 96, each beat has 24 beats to work with?
    // 96 - whole note
    // 48 - half note
    // 24 - quarter note
    // 12 - eighth note
    // 6 - sixteenth note
    // 3 - 32nd note, if you really need it
    // triplets to take up a half note's length:3x16
    // triplets to take up a quarter note's length: 3x8
    int length;
    // implement later
    // double laziness(dragging or rushing, probably would be better to split this
    // into two parts,
    // since these could affect noteLength or noteStart, or both)
    // int velocity //volume (0-127)

    /**
     * @param noteValue
     * @param noteStart
     * @param noteLength Constructor for creating Note objects from JSON input
     */
    public Note(int noteValue, int noteStart, int noteLength) {
        this.value = noteValue; // enforce int from 0 to 11
        this.start = noteStart;
        this.length = noteLength;
    }

    public Note(int noteStart, NoteLengthType noteLengthType, NoteScaleType noteScaleType){
        int noteLength = -1;
        switch (noteLengthType) {
            case WHOLE:
                noteLength = 96;
                break;
            case HALF:
                noteLength = 48;
                break;
            case QUARTER:
                noteLength = 24;
                break;
            case EIGHTH:
                noteLength = 12;
                break;
            case SIXTEENTH:
                noteLength = 6;
                break;
            case HALF_TRIPLET:
                noteLength = 16;
                break;
            case QUARTER_TRIPLET:
                noteLength = 8;
                break;
        }

        this.start = noteStart;
        this.length = noteLength;
        this.value = getRandomNoteValue(noteScaleType, 5, 6);
    }
    public static int getRandomNoteValue(NoteScaleType noteScaleType, int octaveLow, int octaveHigh){
        int octave = ((int) (Math.random() * (octaveHigh-octaveLow))) + octaveLow;
        int pickedNote = 0;
        switch(noteScaleType){
            case MAJOR:
                int[] possibleMajorNotes = {0,2,4,5,7,9,11};
                pickedNote = possibleMajorNotes[(int) (Math.random() * possibleMajorNotes.length)];
                break;
            case BLUES:
                int[] possibleBluesNotes = {6, 0, 2, 3, 4, 7};
                pickedNote = possibleBluesNotes[(int) (Math.random() * possibleBluesNotes.length)];
                break;
        }
        //System.out.println("picked " + pickedNote);
        return pickedNote + (octave * 12);
    }
    /**
     * @param noteValue
     * @param noteStart
     * @param noteLengthType  Constructor for creating Note objects from scratch, using
     *                  NoteType instead of length TODO: REMOVE THIS, DEPRECATED CONSTRUCTOR
     */
    public Note(int noteValue, int noteStart, NoteLengthType noteLengthType) {

        int noteLength = 0;
        switch (noteLengthType) {
            case WHOLE:
                noteLength = 96;
                break;
            case HALF:
                noteLength = 48;
                break;
            case QUARTER:
                noteLength = 24;
                break;
            case EIGHTH:
                noteLength = 12;
                break;
            case SIXTEENTH:
                noteLength = 6;
                break;
            case HALF_TRIPLET:
                noteLength = 16;
                break;
            case QUARTER_TRIPLET:
                noteLength = 8;
                break;
        }
        this.value = noteValue;
        this.start = noteStart;
        this.length = noteLength;
    }

    public int getNormalizedNoteValue(){
        return this.value % 12;
    }
    public int getOctave(){
        return this.value / 12;
    }
    public String getReadableNoteValue(){
        if(this.getNormalizedNoteValue() == -1){
            return "REST";
        }
        int targetNote = this.getNormalizedNoteValue();
        ArrayList<String> notes = new ArrayList<String>(
                Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"));
        return notes.get(targetNote % 12);
    }

    /**
     * @return a NoteType enum that ISN'T a triplet
     */
//	public static NoteType getRandomNonTriplet() {
//		if(Math.random() > 0.5) {
//			return NoteType.EIGHTH;
//		}
//		return NoteType.SIXTEENTH;
//	}
    public static NoteLengthType getRandomNonTriplet() {

        int notePicker = (int) Math.floor(Math.random() * 5);
        switch (notePicker) {
            case 0:
                return NoteLengthType.WHOLE;
            case 1:
                return NoteLengthType.HALF;
            case 2:
                return NoteLengthType.QUARTER;
            case 3:
                return NoteLengthType.EIGHTH;
            case 4:
                return NoteLengthType.SIXTEENTH;
        }
        return NoteLengthType.QUARTER;
    }

    /**
     * @param noteLengthType
     * @return noteType's converted length
     */
    public static int getNoteTypeLength(NoteLengthType noteLengthType) {
        int noteLength = -1;
        switch (noteLengthType) {
            case WHOLE:
                noteLength = 96;
                break;
            case HALF:
                noteLength = 48;
                break;
            case QUARTER:
                noteLength = 24;
                break;
            case EIGHTH:
                noteLength = 12;
                break;
            case SIXTEENTH:
                noteLength = 6;
                break;
            case HALF_TRIPLET:
                noteLength = 16;
                break;
            case QUARTER_TRIPLET:
                noteLength = 8;
                break;
        }
        return noteLength;
    }

    public String toString() {
        return "Value: " + this.getReadableNoteValue() + "("+ this.getNormalizedNoteValue()+")" + ", " + this.start + "->" + (this.getLength() + this.start) + " ("
                + this.getLength() + ")";
    }

    public int getValue() {
        return value;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }
}
