import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MidiPlayground {
    public static void main(String args[]) throws InvalidMidiDataException, IOException {
        MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
        MidiDevice.Info selectedDevice = null;
        for (int i = 0; i < info.length; i++) {
            System.out.println(i);
            System.out.println("name: " + info[i].getName());
            System.out.println("desc: " + info[i].getDescription());
            System.out.println("vendor: " + info[i].getVendor());
            System.out.println("version: " + info[i].getVersion());
            if ((info[i].getName()).equals("loopMIDI Port")) {
                selectedDevice = info[i];

                break;
            }
        }

        System.out.println("Selected: " + selectedDevice);

        MidiDevice device = null;

        try {
            device = MidiSystem.getMidiDevice(selectedDevice);

            System.out.println("Using: " + device);

            device.open();

            Transmitter transmitter = device.getTransmitter();

            Receiver receiver = new PrintMidiReceiver();

            transmitter.setReceiver(receiver);


            Scanner s = new Scanner(System.in);

            System.out.println("type 'stop' to stop recording");
            System.out.print(">");
            while(!s.nextLine().equals("stop")){

            }


            File output = new File("testOutputAgain.mid");


//            Track[] trackList = sequence.getTracks();
//            for(Track t: trackList){
//                System.out.println(t.toString());
//            }


            //MidiSystem.write(sequence, 1, (File) output);


        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }


        if (device instanceof Synthesizer) {
            System.out.println("Device chosen is a Synthesizer");
        }
        if (device instanceof Sequencer) {
            System.out.println("Device chosen is a Sequencer");
        }


//        Sequence seq = null;
//        seq = new Sequence(Sequence.PPQ, 10);
//
//        Track track = seq.createTrack();
//
//        //Track otherTrack = seq.createTrack();
//
//        ShortMessage noteStart = new ShortMessage();
//
//        noteStart.setMessage(ShortMessage.NOTE_ON, 0, 61, 93);
//
//        long startTime = -1;
//
//        MidiEvent noteStartEvent = new MidiEvent(noteStart, startTime);
//
//        track.add(noteStartEvent);
//
//        ShortMessage noteEnd = new ShortMessage();
//
//        noteEnd.setMessage(ShortMessage.NOTE_OFF, 0, 61, 93);
//
//        long endTime = 160;
//
//        MidiEvent noteEndEvent = new MidiEvent(noteEnd, endTime);
//
//        track.add(noteEndEvent);
//
//        MidiPlayground.playNote(track, 0, 240, 80, 64, 93);
//
//        File output = new File("testOutput.mid");
//
//        MidiSystem.write(seq, 0, output);
    }

    public static void playNote(Track t, int channel, long start, long length, int value, int volume) throws InvalidMidiDataException {

        ShortMessage noteStart = new ShortMessage();

        noteStart.setMessage(ShortMessage.NOTE_ON, channel, value, volume);

        long startTime = start;

        MidiEvent noteStartEvent = new MidiEvent(noteStart, startTime);

        t.add(noteStartEvent);

        ShortMessage noteEnd = new ShortMessage();

        noteEnd.setMessage(ShortMessage.NOTE_OFF, channel, value, volume);

        long endTime = start + length;

        MidiEvent noteEndEvent = new MidiEvent(noteEnd, endTime);

        t.add(noteEndEvent);
    }
}
