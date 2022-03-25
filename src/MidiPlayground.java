import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class MidiPlayground {
    public static void main(String args[]) throws InvalidMidiDataException, IOException {
//        MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
//        for(int i = 0; i<info.length; i++){
//            System.out.println("name: " + info[i]);
//            System.out.println("desc: " + info[i].getDescription());
//        }
        Sequence seq = null;
        seq = new Sequence(Sequence.PPQ, 10);

        Track track = seq.createTrack();

        //Track otherTrack = seq.createTrack();

        ShortMessage noteStart = new ShortMessage();

        noteStart.setMessage(ShortMessage.NOTE_ON, 0, 61, 93);

        long startTime = -1;

        MidiEvent noteStartEvent = new MidiEvent(noteStart, startTime);

        track.add(noteStartEvent);

        ShortMessage noteEnd = new ShortMessage();

        noteEnd.setMessage(ShortMessage.NOTE_OFF, 0, 61, 93);

        long endTime = 160;

        MidiEvent noteEndEvent = new MidiEvent(noteEnd, endTime);

        track.add(noteEndEvent);

        MidiPlayground.playNote(track, 0, 240, 80, 64, 93);

        File output = new File("testOutput.mid");

        MidiSystem.write(seq, 0, output);
    }

    public static void playNote(Track t, int channel, long start, long length, int value, int volume) throws InvalidMidiDataException {

        ShortMessage noteStart = new ShortMessage();

        noteStart.setMessage(ShortMessage.NOTE_ON, channel, value, volume);

        long startTime = start;

        MidiEvent noteStartEvent = new MidiEvent(noteStart, startTime);

        t.add(noteStartEvent);

        ShortMessage noteEnd = new ShortMessage();

        noteEnd.setMessage(ShortMessage.NOTE_OFF, channel, value, volume);

        long endTime = start+length;

        MidiEvent noteEndEvent = new MidiEvent(noteEnd, endTime);

        t.add(noteEndEvent);
    }
}
