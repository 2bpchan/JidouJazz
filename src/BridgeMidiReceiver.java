import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BridgeMidiReceiver implements Receiver {
    Receiver destinationReceiver;

    public int deck = 0;
    public final int NUM_DECKS = 2;

    public int key = 0;
    public boolean setKey = false;

    public boolean[] chordSelectionArray = new boolean[]{false, false, false, false};
    public BridgeMidiReceiver(Receiver destinationReceiver) {
        this.destinationReceiver = destinationReceiver;

        GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true);
        for (Map.Entry<Long, String> keyboard : GlobalKeyboardHook.listKeyboards().entrySet()) {
            System.out.format("%d: %s\n", keyboard.getKey(), keyboard.getValue());
        }

        keyboardHook.addKeyListener(new BundledGlobalKeyAdapter(this));

    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        ShortMessage msg = (ShortMessage) message;

        System.out.println("Received MidiMessage " + " with command " + msg.getCommand() + " on channel " + msg.getChannel() + ", data 1: " + msg.getData1() + ", data 2: " + msg.getData2());

        if (setKey){
            key = msg.getData1() % 12;
            System.out.println("set key to: " + key);
            return;
        }
//

        try {
            List<MidiMessage> newMessages = mapToChord(message);
            for(MidiMessage m : newMessages){
                destinationReceiver.send(m, timeStamp);
            }
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void close() {
        //i don't use resources so this should just be able to close cleanly
    }
    public void changeDeck(int changeAmount){
        if(deck + changeAmount <= 0){
            deck = 0;
            return;
        }
        if(deck + changeAmount > NUM_DECKS){
            deck = NUM_DECKS;
            return;
        }
        deck += changeAmount;
    }

    public MidiMessage changeMessage(MidiMessage input){
        ShortMessage msg = (ShortMessage) input;

        System.out.println("Received MidiMessage " + " with command " + msg.getCommand() + " on channel " + msg.getChannel() + ", data 1: " + msg.getData1() + ", data 2: " + msg.getData2());

        try {
            return (MidiMessage) new ShortMessage(msg.getCommand(), msg.getChannel(), msg.getData1()+4, msg.getData2());
        } catch (InvalidMidiDataException e) {
            throw new RuntimeException(e);
        }
    }

    public List<MidiMessage> mapToChord(MidiMessage input) throws InvalidMidiDataException {
        ShortMessage msg = (ShortMessage) input;

        int command = msg.getCommand();
        int channel = msg.getChannel();
        int rootNote = msg.getData1();
        int vel = msg.getData2();

        int offset = getKeyOffset(rootNote);

        ArrayList<MidiMessage> noteList = new ArrayList<MidiMessage>();
        if (command != 144 && command != 128){
            return noteList; // ignore non Note On/ Note Off events
        }
        if(command == 128){
            for (int i = 30; i< 90; i++){
                noteList.add((MidiMessage) new ShortMessage(command, channel, i, vel));
            }
            return noteList;
        }
        switch(deck){
            case 0:
                // by default, do a triad in the key of $key
                noteList.add((MidiMessage) msg); // pass through root note
                if(offset == 2 || offset == 4 || offset == 9)
                    noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 3, vel));
                if(offset == 0 || offset == 5 || offset == 7)
                    noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 4, vel));

                if(offset == 11){
                    noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 3, vel));
                    noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 6, vel));
                } else {
                    noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 7, vel));
                }

                if(offset == 11){
                    noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 3, vel));
                    noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 6, vel));
                } else {
                    noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 7, vel));
                }
                // 4-3-4-3 half steps
                if (offset == 0 || offset == 5|| offset == 11){
                    int domOffset = 7;
                    if(offset == 11){
                        domOffset = 6;
                    }
                    if(chordSelectionArray[0] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + domOffset + 4, vel));
                    }
                    if(chordSelectionArray[1] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + domOffset + 4 + 3, vel));
                    }
                    if(chordSelectionArray[2] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + domOffset + 4 + 3 + 4, vel));
                    }
                    if(chordSelectionArray[3] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + domOffset + 4 + 3 + 4 + 3, vel));
                    }
                }
                // 3-4-3-4 half steps
                if (offset == 2 || offset == 4 || offset == 7 || offset == 9){
                    if(chordSelectionArray[0] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 7 + 3, vel));
                    }
                    if(chordSelectionArray[1] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 7 + 3 + 4, vel));
                    }
                    if(chordSelectionArray[2] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 7 + 3 + 4 + 3, vel));
                    }
                    if(chordSelectionArray[3] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 7 + 3 + 4 + 3 + 4, vel));
                    }
                }
                break;
            case 1:
                // by default, do a triad in the key of $key
                noteList.add((MidiMessage) msg); // pass through root note
                if(offset == 2 || offset == 4 || offset == 9)
                    noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 3, vel));
                if(offset == 0 || offset == 5 || offset == 7)
                    noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 4, vel));

                if(offset == 11){
                    noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 3, vel));
                    noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 6, vel));
                } else {
                    noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 7, vel));
                }
                // 4-3-4-3 half steps
                if (offset == 0 || offset == 5 || offset == 7 || offset == 11){
                    int domOffset = 7;
                    if(offset == 11){
                        domOffset = 6;
                    }
                    if(chordSelectionArray[0] || chordSelectionArray[1] || chordSelectionArray[2] || chordSelectionArray[3] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + domOffset + 4, vel));
                    }
                    if(chordSelectionArray[1] || chordSelectionArray[2] || chordSelectionArray[3] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + domOffset + 4 + 3, vel));
                    }
                    if(chordSelectionArray[2] || chordSelectionArray[3] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + domOffset + 4 + 3 + 4, vel));
                    }
                    if(chordSelectionArray[3] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + domOffset + 4 + 3 + 4 + 3, vel));
                    }
                }
                // 3-4-3-4 half steps
                if (offset == 2 || offset == 4 || offset == 9){
                    if(chordSelectionArray[0] || chordSelectionArray[1] || chordSelectionArray[2] || chordSelectionArray[3] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 7 + 3, vel));
                    }
                    if(chordSelectionArray[1] || chordSelectionArray[2] || chordSelectionArray[3] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 7 + 3 + 4, vel));
                    }
                    if(chordSelectionArray[2] || chordSelectionArray[3] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 7 + 3 + 4 + 3, vel));
                    }
                    if(chordSelectionArray[3] || command == 128){
                        noteList.add((MidiMessage) new ShortMessage(command, channel, rootNote + 7 + 3 + 4 + 3 + 4, vel));
                    }
                }
                break;
            case 2:
                break;
        }


        return noteList;
    }

    public int getKeyOffset(int note){
        //System.out.println("key is : " + key);
        //System.out.println("base of note played is: " + note % 12);

        int baseNote = note % 12;
        int diff = baseNote - key;
        if (diff < 0){
            diff += 12;
        }
        //System.out.println("relative diff between key and base note: "+ diff);
        return diff;
    }
}
