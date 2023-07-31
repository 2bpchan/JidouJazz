import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MidiPlayground {
    public static void main(String args[]) throws InvalidMidiDataException, IOException {
        MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
        MidiDevice.Info selectedInputDevice = null; // my MIDI keyboard, or a virtual one from MIDITools
        MidiDevice.Info selectedOutputDevice = null; // output device to write to

        for (int i = 0; i < info.length; i++) {
            System.out.println(i);
            MidiDevice.Info currentMidiDeviceInfo = info[i];
            System.out.println("name: " + currentMidiDeviceInfo.getName());
            System.out.println("desc: " + currentMidiDeviceInfo.getDescription());
            System.out.println("vendor: " + currentMidiDeviceInfo.getVendor());
            System.out.println("version: " + currentMidiDeviceInfo.getVersion());
            if ((currentMidiDeviceInfo.getName()).equals("MPK mini 3") && currentMidiDeviceInfo.getDescription().equals("No details available")) {
                selectedInputDevice = currentMidiDeviceInfo;
            }
            if (info[i].getName().equals("MIDI Bridge") && currentMidiDeviceInfo.getDescription().equals("External MIDI Port")) {
                selectedOutputDevice = currentMidiDeviceInfo;
            }

        }

        System.out.println("Selected: " + selectedInputDevice);

        MidiDevice inputDevice = null;
        MidiDevice outputDevice = null;

        try {
            inputDevice = MidiSystem.getMidiDevice(selectedInputDevice);
            outputDevice = MidiSystem.getMidiDevice(selectedOutputDevice);

            System.out.println("Using: " + inputDevice);
            if (inputDevice instanceof Synthesizer) {
                System.out.println("Device chosen is a Synthesizer");
            }
            if (inputDevice instanceof Sequencer) {
                System.out.println("Device chosen is a Sequencer");
            }
                inputDevice.open();

            if (!outputDevice.isOpen()){
                outputDevice.open();
            }


            Transmitter transmitter = inputDevice.getTransmitter();
            Receiver internalReceiver = outputDevice.getReceiver();
            Receiver receiver = new BridgeMidiReceiver(internalReceiver);
//            Receiver receiver = new PrintMidiReceiver();
//            Receiver receiver = new NoteMidiReceiver();
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
