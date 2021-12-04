package musicInfrastructure;

public class Note {
	int value; // can change what note this note is referring to based on
				// what key/what chord is currently being played
				// not to be used for calculating intervals because the notes aren't 1:1 to
				// chromatic scale
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
		this.value = noteValue;
		this.start = noteStart;
		this.length = noteLength;
	}

	/**
	 * @param noteValue
	 * @param noteStart
	 * @param noteType  Constructor for creating Note objects from scratch, using
	 *                  NoteType instead of length
	 */
	public Note(int noteValue, int noteStart, NoteType noteType) {
		int noteLength = 0;
		switch (noteType) {
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

	/**
	 * @return a NoteType enum that ISN'T a triplet
	 */
	public static NoteType getRandomNonTriplet() {
		int notePicker = (int) Math.floor(Math.random() * 5);
		switch (notePicker) {
		case 0:
			return NoteType.WHOLE;
		case 1:
			return NoteType.HALF;
		case 2:
			return NoteType.QUARTER;
		case 3:
			return NoteType.EIGHTH;
		case 4:
			return NoteType.SIXTEENTH;
		}
		return NoteType.QUARTER;
	}

	/**
	 * @param noteType
	 * @return noteType's converted length
	 */
	public static int getNoteTypeLength(NoteType noteType) {
		int noteLength = -1;
		switch (noteType) {
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
		return "Value: " + this.getValue() + ", " + this.start + "->" + (this.getLength() + this.start) + " ("
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
