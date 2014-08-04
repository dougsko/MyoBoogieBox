import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;

import javax.sound.midi.*;

import org.java_websocket.drafts.Draft;

public class Scene
{
    private int beatCount = 0;
    private MIDIplayer mplayer;
    //private MyoMidiPlayer mplayer;
    //private static int events[] = {127};
    private JFrame frame;
    private JPanel mainPanel;
    //private JLabel bg;
    private static String wshost = "myo-ws-server.herokuapp.com";
    
    private static String iconPath = "icons/";
    private static Icon selectIcon = new ImageIcon( iconPath + "select.png");
    private static Icon unselectIcon = new ImageIcon( iconPath + "unselect.png");
    private static Icon beatUnselectIcon = new ImageIcon( iconPath + "beatunselect.png");
    private static Icon beatSelectIcon = new ImageIcon( iconPath + "beatselect.png");
    private static Icon playIcon = new ImageIcon( iconPath + "play.png");
    private static Icon playIconSel = new ImageIcon( iconPath + "play_sel.png");
    private static Icon stopIcon = new ImageIcon( iconPath + "stop.png");
    private static Icon stopIconSel = new ImageIcon( iconPath + "stop_sel.png");
    private static Icon plusIcon = new ImageIcon( iconPath + "plus.png");
    private static Icon plusIconSel = new ImageIcon( iconPath + "plus_sel.png");
    private static Icon minusIcon = new ImageIcon( iconPath + "minus.png");
    private static Icon minusIconSel = new ImageIcon( iconPath + "minus_sel.png");
    private static Color bgColor = new Color(49, 49, 49, 255);
    
    private BBRadioButton[][] radioBoxList;
    private JButton start;
    private JButton stop;
    //private JButton pause;
    private JButton upTempo;
    private JButton downTempo;
    private Box buttonBox; 
    
    		
    
    public static void main(String[] args)
    {
    	if(args.length > 0) {
    		wshost = args[1];
    	}
        Scene program = new Scene();
        program.run();
    	
    }
    
    private void run()
    {
    	buildGui();
    	// set up server
    	int portNumber = 6969;
        boolean listening = true;
        mplayer = new MIDIplayer(radioBoxList);
        mplayer.addListener(new MIDIBeatListener());
        
        try {
			MyoWsClient c = new MyoWsClient( new URI( "ws://" + wshost ), mplayer);
			c.connect();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //buildGui();
        /*
        try {
        	
        	ServerSocket serverSocket = new ServerSocket(portNumber);
            while (listening) {
                new MyoMidiServerThread(serverSocket.accept(), mplayer).start();
            }
            
            serverSocket.close();
            
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
        */
    }
    
    private void buildGui ()
    {
        frame = new JFrame("Myo Boogie Box");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setBounds(50, 50, 300, 300);
        
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        background.setBackground(bgColor);
        
        radioBoxList = new BBRadioButton[16][16];
        buttonBox = new Box(BoxLayout.X_AXIS);
        
        start = new JButton(playIcon);
        start.setPressedIcon(playIconSel);
        setupButton(start);
        buttonBox.add(start);
        buttonBox.add(Box.createHorizontalGlue());
        
        stop = new JButton(stopIcon);
        stop.setPressedIcon(stopIconSel);
        setupButton(stop);
        buttonBox.add(stop);
        buttonBox.add(Box.createHorizontalGlue());
                
        upTempo = new JButton(plusIcon);
        upTempo.setPressedIcon(plusIconSel);
        setupButton(upTempo);
        buttonBox.add(upTempo);
        buttonBox.add(Box.createHorizontalGlue());
        
        downTempo = new JButton(minusIcon);
        downTempo.setPressedIcon(minusIconSel);
        setupButton(downTempo);
        buttonBox.add(downTempo);
        
        background.add(BorderLayout.SOUTH, buttonBox);
        
        frame.getContentPane().add(background);
        
        GridLayout grid = new GridLayout(16, 16);
        mainPanel = new JPanel(grid);
        mainPanel.setBackground(bgColor);
        background.add(BorderLayout.CENTER, mainPanel);
        
        for(int rows = 0; rows < 16; rows ++)
        {
            for(int cols = 0; cols < 16; cols ++)
            {
                radioBoxList[rows][cols] = new BBRadioButton(rows, cols);
                radioBoxList[rows][cols].setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
                radioBoxList[rows][cols].setIcon(unselectIcon);
                radioBoxList[rows][cols].setSelectedIcon(selectIcon);
                radioBoxList[rows][cols].setSelected(false);
                radioBoxList[rows][cols].addActionListener(new RadioButtonListener());
                radioBoxList[rows][cols].setContentAreaFilled(false);
                mainPanel.add(radioBoxList[rows][cols]);
            }
        }	
        frame.pack();
        frame.setVisible(true);
    }
    
    private void setupButton(JButton button)
    {
        button.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        button.setContentAreaFilled(false);
        button.addActionListener(new controlButtonListener());
        
    }
    
    private void resetButtons()
    {
        for(int rows = 0; rows < 16; rows ++)
        {
            for(int cols = 0; cols < 16; cols ++)
            {
                radioBoxList[rows][cols].setSelected(false);
                changeIcons(radioBoxList[rows][cols], selectIcon, unselectIcon);
            }
        }  
    }
    
    private void updateColumns (int cols)
    {
        for(int rows = 0; rows < 16; rows ++)
        {
            changeIcons(radioBoxList[rows][cols], beatSelectIcon, beatUnselectIcon);            
            if(cols != 0 )
            {
                changeIcons(radioBoxList[rows][cols-1], selectIcon, unselectIcon);
            }
            else
            {
                changeIcons(radioBoxList[rows][15], selectIcon, unselectIcon);
            }
        }
    }
    
    private void changeIcons(JRadioButton button, Icon selected, Icon unselected)
    {
        if(button.isSelected()) button.setSelectedIcon(selected);
        else button.setIcon(unselected);
    }
    
    class MIDIBeatListener implements ControllerEventListener
        {
            public void controlChange (ShortMessage mess)
            {  
                updateColumns(beatCount);
                beatCount++;
                if(beatCount == 16) beatCount = 0;
            }    
        }
    
    class RadioButtonListener implements ActionListener
        {
            public void actionPerformed (ActionEvent ev)
            {
                BBRadioButton source =  (BBRadioButton) ev.getSource();
                mplayer.setNote(source.getRow(), source.getColumn());
            }
        }
    
    class controlButtonListener implements ActionListener
        {    
            public void actionPerformed (ActionEvent ev)
            {
                Object source = ev.getSource();
                if (source == upTempo)
                {
                    mplayer.upTempo();
                }
                else if (source == downTempo)
                {
                    mplayer.downTempo();
                }
                else if (source == start)
                {
                    if(mplayer.isPlaying())
                    {
                        mplayer.stop();
                    }
                    else
                    {
                        mplayer.start();
                    }
                }
                else
                {
                    beatCount = 0;
                    mplayer.stop();
                    mplayer.reset();
                    resetButtons();
                }                  

            }
        }
}