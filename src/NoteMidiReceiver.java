import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.Arrays;

public class NoteMidiReceiver implements Receiver {
    private static int INSTRUMENT = 0;
    private static MidiChannel[] channels;
    private static Synthesizer synth;
    public NoteMidiReceiver() {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
        } catch (MidiUnavailableException e) {
            throw new RuntimeException(e);
        }

        channels = synth.getChannels();
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        ShortMessage msg = (ShortMessage) message;

        System.out.println("Received MidiMessage " + " with command " + msg.getCommand() + " on channel " + msg.getChannel() + ", data 1: " + msg.getData1() + ", data 2: " + msg.getData2());
        int note = msg.getData1();
        int vel = msg.getData2();
        if(msg.getData2() > 0){
            System.out.println("Playing Note: " + getReadableNoteValue(note));
            try {
                noteOn(note, vel);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else if (msg.getData2() <= 0){
            System.out.println("Releasing Note: " + getReadableNoteValue(note));
            try {
                noteOff(note);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }



    }
    public int getNormalizedNoteValue(int note){
        return note % 12;
    }
    public String getReadableNoteValue(int note){
        int targetNote = this.getNormalizedNoteValue(note);
        ArrayList<String> notes = new ArrayList<String>(
                Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"));
        return notes.get(targetNote % 12);
    }

    private static void noteOn(int note, int vel) throws InterruptedException {
        channels[INSTRUMENT].noteOn(note, vel);
    }
    private static void noteOff(int note) throws InterruptedException {
        channels[INSTRUMENT].noteOff(note);
    }
    @Override
    public void close() {
        //i don't use resources so this should just be able to close cleanly
    }
}
