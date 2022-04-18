import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import static javax.sound.midi.ShortMessage.NOTE_ON;

public class PrintMidiReceiver implements Receiver {
    @Override
    public void send(MidiMessage message, long timeStamp) {
        ShortMessage msg = (ShortMessage) message;

        System.out.println("Received MidiMessage " + " with command " + msg.getCommand() + " on channel " + msg.getChannel() + ", data 1: " + msg.getData1() + ", data 2: " + msg.getData2());
    }

    @Override
    public void close() {
        //i don't use resources so this should just be able to close cleanly
    }
}
