import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.sound.midi.*;

public class MIDIplayer 
{
    private int noteOn = 144;
    private int noteOff = 128;
    int events[] = {127};
    private Sequencer player;
    private Sequence seq;
    private Track track;
    private MidiEvent[][] notesON = new MidiEvent[16][16];
    private MidiEvent[][] notesOFF = new MidiEvent[16][16];
    private static int[] instruments  = {35,38,39,40,46,51,56,60,61,62,63,67,69,70,73,75};
    
    MIDIplayer()
    {
        setupMidi();
        buildTrack();
    }
    
    public void setupMidi ()
    {
        try
        {
            player = MidiSystem.getSequencer();
            player.open();
            seq = new Sequence(Sequence.PPQ, 4);
            fillNotes();
        }
        catch (Exception e)
        {
            System.err.println("Midi Setup error");
            System.exit(-1);
        }
    }
    
    public boolean isPlaying()
    {
        return player.isRunning();
    }
    
    public void start()
    {
        player.start();
    }
    
    public void stop()
    {
        player.stop();
    }
        
    public void addListener(ControllerEventListener listener)
    {
        player.addControllerEventListener(listener, events);
    }
    
    public void setNote(int channel, int beat)
    {
        if(track.add(notesON[channel][beat]))
        {
            track.remove(notesOFF[channel][beat]);
        }
        else
        {
            track.remove(notesON[channel][beat]);
            track.add(notesOFF[channel][beat]);
        }

    }
    
    
    public void reset()
    {
        buildTrack();
    }
    
    public void downTempo()
    {
        float tempoFactor = player.getTempoFactor();
        player.setTempoFactor((float)(tempoFactor * 0.97));
    }
    
    public void upTempo()
    {
        float tempoFactor = player.getTempoFactor();
        player.setTempoFactor((float)(tempoFactor * 1.03));
    }
    
    
    private void fillNotes()
    {
        for(int i = 0; i < 16; i++)
        {
            for(int j = 0; j < 16; j++)
            {
                notesON[i][j] = makeEvent(noteOn, 9, instruments[i], 100, j);
                notesOFF[i][j] = makeEvent(noteOff, 9, instruments[i], 100, j);
            }
            
        }
    }
    
    private void buildTrack()
    {
        seq.deleteTrack(track);
        player.setTempoFactor(1);
        track = seq.createTrack();
        addControllEvents();
        
        for(int i = 0; i < 16; i ++)
        {
            for(int j = 0; j < 16; j ++)
            {
                track.add(makeEvent(noteOff, 9, instruments[i], 100, j));
            }
        }

        try
        {
            player.setSequence(seq);
            player.setLoopCount(player.LOOP_CONTINUOUSLY);
            player.setLoopStartPoint(0);
            player.setTempoInBPM(120);
        }
        catch(Exception e)
        {
            System.err.println("Sequencer error");
            System.exit(-1);
        }
    }
    
    private void addControllEvents()
    {
        for(int i = 1; i <= 16; i++)
        {
            track.add(makeEvent(176,15,127,0,i));
        }
    }
    
    private MidiEvent makeEvent (int command, int channel, int data1, int data2, int tick)
    {
        //        command - the MIDI command represented by this message
        //        channel - the channel associated with the message. Channel 9 is just percussion
        //        data1 - the first data byte - instrument number   
        //        data2 - the second data byte - velocity
        //        tick - 
        
        MidiEvent event = null;
        try
        {
            ShortMessage a = new ShortMessage();
            a.setMessage(command, channel, data1, data2);
            event = new MidiEvent(a, tick);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return event;
    }	 
}