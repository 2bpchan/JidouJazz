package musicInfrastructure;
import java.util.ArrayList;

public class Measure {
	ArrayList<Note> notes;
	String chord;
	int measureNumber;
	public Measure(ArrayList<Note> notes, String chord, int measureNumber) {
		this.notes = notes;
		this.chord = chord;
		this.measureNumber = measureNumber;
	}
	public void addNote(Note note){
		this.getNotes().add(note);
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Measure #" + measureNumber + " (" + getChord() +"): \n");
		for(Note n : getNotes()) {
			sb.append(n);
			sb.append("\n");
		}
		return sb.toString();
	}
	public ArrayList<Note> getNotes() {
		return notes;
	}

	public String getChord() {
		return chord;
	}
}
