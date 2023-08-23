import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

public class BundledGlobalKeyAdapter extends GlobalKeyAdapter {
    public BridgeMidiReceiver receiver;

    public BundledGlobalKeyAdapter(BridgeMidiReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void keyPressed(GlobalKeyEvent event) {
        int vKey = event.getVirtualKeyCode();
        if(vKey == 114){
            System.out.println("changing from deck #" + receiver.deck);
            receiver.changeDeck(-1);
            System.out.println("to deck #" + receiver.deck);
        }
        if(vKey == 115){
            System.out.println("changing from deck #" + receiver.deck);
            receiver.changeDeck(1);
            System.out.println("to deck #" + receiver.deck);
        }
        if(vKey == 113){
            for (int i = 0; i<4 ; i++){
                receiver.chordSelectionArray[i] = false;
            }
        }
        if(vKey == 112){
            for (int i = 0; i<4 ; i++){
                receiver.chordSelectionArray[i] = true;
            }
        }

        switch(vKey){
            case 53:
                receiver.setKey = true;
                break;
        }
    }

    @Override
    public void keyReleased(GlobalKeyEvent event) {
        int vKey = event.getVirtualKeyCode();

        switch(vKey){
            case 49:
                receiver.chordSelectionArray[0] = !receiver.chordSelectionArray[0];
                //System.out.println("toggling option 1 to: " + receiver.chordSelectionArray[0]);
                break;
            case 50:
                receiver.chordSelectionArray[1] = !receiver.chordSelectionArray[1];
                //System.out.println("toggling option 2 to: " + receiver.chordSelectionArray[1]);
                break;
            case 51:
                receiver.chordSelectionArray[2] = !receiver.chordSelectionArray[2];
                //System.out.println("toggling option 3 to: " + receiver.chordSelectionArray[2]);
                break;
            case 52:
                receiver.chordSelectionArray[3] = !receiver.chordSelectionArray[3];
                //System.out.println("toggling option 4 to: " + receiver.chordSelectionArray[3]);
                break;
            case 53:
                receiver.setKey = false;
                break;
        }
    }
}
