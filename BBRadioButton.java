import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

//
//  BBRadioButton
//  
//
//  Created by Vytis Sibonis on 2008-02-20.
//  Copyright 2008 __MyCompanyName__. All rights reserved.
//

public class BBRadioButton extends JRadioButton
    {
        private int row;
        private int column;
        private JRadioButton but;
        
        BBRadioButton(int x, int y)
        {
            row = x;
            column = y;
            but = new JRadioButton();
        }
        
        public int getRow ()
        {
            return row;
        }
        
        public int getColumn ()
        {
            return column;
        }
    }
