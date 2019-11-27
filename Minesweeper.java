/*
 * 
 */
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Minesweeper extends JFrame
{
	private final int WINDOWSTARTWIDTH = 700;
	private final int WINDOWSTARTHEIGHT = 350;
	private final int WINDOWGAMEWIDTH = 550;
	private final int WINDOWGAMEHEIGHT = 600;
	private final int TITLESIZE=50;
    private final int GRIDSIZE = 9; 
    private final int STARTBUTTONSWIDTH = 150;
    private final int STARTBUTTONSHEIGHT = 100;
    private final int STARTBUTTONFONT=20;
    private final int GAMEBUTTONSWIDTH = 100;
    private final int GAMEBUTTONSHEIGHT = 50;
    private final int GAMEBUTTONFONT=10;
    private final int GRIDBUTTONSIZE = 50;
    private final Color SELECTEDSPACECOLOR=Color.CYAN;
    private final Color MINECOLOR=Color.RED;
    private final Color NOTSELECTEDSPACECOLOR=Color.BLUE;
    private final Color FLAGCOLOR=Color.GREEN;
    private final int EASY=20;
    private final int MED=30;
    private final int HARD=40;
    private int mineCount;
    private int placedFlags;
    private int grid[][];
    private boolean firstSpace;
    private FileReader file;
	private BufferedReader reader;
	private BufferedWriter writer;
    private JButton easyButton,medButton,hardButton,loadButton,saveButton,restartButton;       
    private JLabel title, mineTotalLabel;
    private JTextField loadGameName,saveGameName;
    private JPanel titlePanel,difficultyPanel,loadPanel,headerPanel,gridPanel;
    private JButton[][] gridButtons;  
    private Image mineImage;
    private ImageIcon mineIcon;
    Actions actionHandler;
                      
    /*
     * Constructor begins the game
     */
    public Minesweeper()
    {
     super("CMPSC 221 MINESWEEPER PROJECT");
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     actionHandler=new Actions();
     firstSpace=true;									//shows that its the first space
     
     //Prepares the mine icon
     mineIcon=new ImageIcon("mineImage.PNG");
     mineImage=mineIcon.getImage();
     mineImage=mineImage.getScaledInstance(GRIDBUTTONSIZE, GRIDBUTTONSIZE, java.awt.Image.SCALE_SMOOTH);
     mineIcon=new ImageIcon(mineImage);
     
     //Creates the beginning window
     setSize(WINDOWSTARTWIDTH,WINDOWSTARTHEIGHT);				//sets the dimensions of the start window
     setResizable(false);										//prevents the user from resizing the window
     setLayout(new FlowLayout());
     //Creates title panel
     titlePanel=new JPanel();
     title=new JLabel("Welcome To Minesweeper!");
     title.setFont(new Font(title.getName(),Font.PLAIN,TITLESIZE));
     titlePanel.add(title);
     //Creates difficulty panel
     difficultyPanel=new JPanel();
     easyButton=new JButton("Easy");
     easyButton.setFont(new Font(title.getName(),Font.PLAIN,STARTBUTTONFONT));
     easyButton.setPreferredSize(new Dimension(STARTBUTTONSWIDTH,STARTBUTTONSHEIGHT));
     easyButton.addActionListener(actionHandler);
     medButton=new JButton("Medium");
     medButton.setFont(new Font(title.getName(),Font.PLAIN,STARTBUTTONFONT));
     medButton.setPreferredSize(new Dimension(STARTBUTTONSWIDTH,STARTBUTTONSHEIGHT));
     medButton.addActionListener(actionHandler);
     hardButton=new JButton("Hard");
     hardButton.setFont(new Font(title.getName(),Font.PLAIN,STARTBUTTONFONT));
     hardButton.setPreferredSize(new Dimension(STARTBUTTONSWIDTH,STARTBUTTONSHEIGHT));
     hardButton.addActionListener(actionHandler);
     difficultyPanel.add(easyButton);
     difficultyPanel.add(medButton);
     difficultyPanel.add(hardButton);
     //Creates load game panel
     loadPanel=new JPanel();
     loadGameName=new JTextField();
     loadGameName.setPreferredSize(new Dimension(305,30));
     loadGameName.addActionListener(actionHandler);
     loadButton=new JButton("Load");
     loadButton.setFont(new Font(title.getName(),Font.PLAIN,STARTBUTTONFONT));
     loadButton.setPreferredSize(new Dimension(STARTBUTTONSWIDTH,STARTBUTTONSHEIGHT));
     loadButton.addActionListener(actionHandler);
     loadPanel.add(loadGameName);
     loadPanel.add(loadButton);
     
     //Adds all the panels in the correct order, but only the beginning panels are visible
     titlePanel.setVisible(true);
     difficultyPanel.setVisible(true);
     loadPanel.setVisible(true);
     add(titlePanel);
     add(difficultyPanel);
     add(loadPanel);
     setVisible(true);				//makes window visible
    }
    
    /*
     * Creates the board, starting the game
     */
    private void start()
    {
    	//Removes the beginning options
    	titlePanel.setVisible(false);
        difficultyPanel.setVisible(false);
        loadPanel.setVisible(false);
        //Readjusts window size
        setSize(WINDOWGAMEWIDTH,WINDOWGAMEHEIGHT);
        
    	//Creates the header options
        headerPanel=new JPanel();
        mineTotalLabel=new JLabel(String.format("%s Mines", mineCount));
        mineTotalLabel.setFont(new Font(title.getName(),Font.PLAIN,30));
        restartButton=new JButton("Restart");
        restartButton.setFont(new Font(title.getName(),Font.PLAIN,GAMEBUTTONFONT));
        restartButton.setPreferredSize(new Dimension(GAMEBUTTONSWIDTH,GAMEBUTTONSHEIGHT));
        restartButton.addActionListener(actionHandler);
        saveGameName=new JTextField();
        saveGameName.setPreferredSize(new Dimension(150,30));
        saveGameName.addActionListener(actionHandler);
        saveButton=new JButton("Save");
        saveButton.setFont(new Font(title.getName(),Font.PLAIN,GAMEBUTTONFONT));
        saveButton.setPreferredSize(new Dimension(GAMEBUTTONSWIDTH,GAMEBUTTONSHEIGHT));
        saveButton.addActionListener(actionHandler);
        headerPanel.add(mineTotalLabel);
        headerPanel.add(restartButton);
        headerPanel.add(saveGameName);
        headerPanel.add(saveButton);
        
        //Creates the grid
        gridPanel=new JPanel(new GridLayout(9,9));
        gridButtons=new JButton[GRIDSIZE][GRIDSIZE];
        grid=new int[GRIDSIZE][GRIDSIZE];
        placedFlags=0;
        //Runs through every button in the grid
        for(int y=0;y<GRIDSIZE;y++)
        {
        	for(int x=0;x<GRIDSIZE;x++)
        	{
        		gridButtons[y][x]=new JButton();
        		gridButtons[y][x].setName(String.format("%s%s",y,x));
        		gridButtons[y][x].setBackground(NOTSELECTEDSPACECOLOR);
        		gridButtons[y][x].setPreferredSize(new Dimension(GRIDBUTTONSIZE,GRIDBUTTONSIZE));
        		gridButtons[y][x].addActionListener(actionHandler);
        		gridButtons[y][x].addMouseListener(actionHandler);
        		grid[y][x]=0;
        		gridPanel.add(gridButtons[y][x]);
        	}
        }
        //Makes panels visible and adds them
        headerPanel.setVisible(true);
        gridPanel.setVisible(true);
        add(headerPanel);
        add(gridPanel);
        //Welcomes user and explains the game
        JOptionPane.showMessageDialog(null, "To play the game, use left click to reveal a space. Right click to flag a spot as a mine. You win after flagging every mine.");
    }
    
    /*
     * Gets a random number
     * @param Minimum value and maximum value
     * @return int random number based on inputed range
     */
    private int getRandNum(int max)
    {
    	Random random = new Random();
        return random.nextInt(max);
    }
    
    /*
     * Restarts the game
     */
    private void restart()
    {
    	//Resets every button and the grid
    	for(int y=0;y<GRIDSIZE;y++)
    	{
        	for(int x=0;x<GRIDSIZE;x++)
        	{
        		gridButtons[y][x].setBackground(NOTSELECTEDSPACECOLOR);
        		gridButtons[y][x].setIcon(null);
        		gridButtons[y][x].setText("");
        		firstSpace=true;
        	}
    	}
    	//Indicates to the user that the mines were reset
    	JOptionPane.showMessageDialog(null,"The mines have been reset.");
    }
    
    /*
     * Tests if the inputed coordinates are empty in the int grid array
     * @param int y and int x for the 2x2 array
     */
    private boolean isValidPoint(int y,int x)
    {
    	//Determines whether the point is valid
    	if(y > -1 && y < GRIDSIZE && x > -1 && x < GRIDSIZE)
    		return true;
    	else
    		return false;
    }
    
    /*
     * Counts the mines and increments spaces
     */
    private void countMines()
    {
    	//Runs through each space in the grid
    	for(int y=0;y<GRIDSIZE;y++)
    	{
    		for(int x=0;x<GRIDSIZE;x++)
    		{
    			//Tests  to ensure the current space isn't a bomb and increments this space if there is a bomb around it
    			if(grid[y][x] != 9 && isValidPoint(y-1,x-1) && grid[y-1][x-1]==9)
    				grid[y][x]++;
    			if(grid[y][x] != 9 && isValidPoint(y-1,x) && grid[y-1][x]==9)
    				grid[y][x]++;
    			if(grid[y][x] != 9 && isValidPoint(y-1,x+1) && grid[y-1][x+1]==9)
    				grid[y][x]++;
    			if(grid[y][x] != 9 && isValidPoint(y,x-1) && grid[y][x-1]==9)
    				grid[y][x]++;
    			if(grid[y][x] != 9 && isValidPoint(y,x+1) && grid[y][x+1]==9)
    				grid[y][x]++;
    			if(grid[y][x] != 9 && isValidPoint(y+1,x-1) && grid[y+1][x-1]==9)
    				grid[y][x]++;
    			if(grid[y][x] != 9 && isValidPoint(y+1,x) && grid[y+1][x]==9)
    				grid[y][x]++;
    			if(grid[y][x] != 9 && isValidPoint(y+1,x+1) && grid[y+1][x+1]==9)
    				grid[y][x]++;
    		}
    	}
    }
    
    /*
     * Randomly places the mines in blank spaces
     * @param Y and X coordinates that correspond to the pressed space
     */
    private void placeMines(int selectY, int selectX)
    {
    	int randY;		//random y point
    	int randX;		//random x point
    	boolean valid;	//used to run until a valid point is found
    	
    	//Initializes the mine grid
    	grid=new int[GRIDSIZE][GRIDSIZE];
    	//Sets the entire grid to 0
    	for(int y=0;y<GRIDSIZE;y++)
    		for(int x=0;x<GRIDSIZE;x++)
    			grid[y][x]=0;
    	//Runs through each mine and places it
    	for(int i=0;i<mineCount;i++)
    	{
    		//Runs until valid point is found
    		do
    		{
    			//Gets a random coordinate
    			randY=getRandNum(GRIDSIZE+1);
    			randX=getRandNum(GRIDSIZE+1);
    			//Tests whether the point generated is valid and ensures the first selected space isn't a mine
    			if(isValidPoint(randY,randX) && grid[randY][randX] != 9 && gridButtons[randY][randX].getBackground() != SELECTEDSPACECOLOR)
    				valid=true;
    			else
    				valid=false;
    		}while(!valid);
    		//Adds the mine
    		grid[randY][randX]=9;
    		valid=false;
    	}
    }
    
    /*
	 * Places a flag
	 */
	private void placeFlag(JButton space)
	{
		//If the spaces was already green, change it back
		if(space.getBackground() == FLAGCOLOR)
		{
			space.setBackground(NOTSELECTEDSPACECOLOR);
			placedFlags--;
		}
		else if(placedFlags < mineCount)
		{
			space.setBackground(FLAGCOLOR);
			placedFlags++;
		}
		else//the placed flags surpasses the mine amount
			JOptionPane.showMessageDialog(null, "You cannot place more flags then there are mines.");
		testWin();
	}

	/*
     * Determines the correct action based on the space selected
     * @param the selected space
     */
    private void spaceSelected(JButton space)
    {
    	//Saves the coordinates of the button given
    	int selectedY=Character.getNumericValue(space.getName().charAt(0));
    	int selectedX=Character.getNumericValue(space.getName().charAt(1));;
    	
    	//Used to determine the correct option based on the space
    	if(firstSpace)//prevents the first space from being a bomb
    	{
    		space.setBackground(SELECTEDSPACECOLOR);
    		placeMines(selectedY,selectedX);
    		countMines();
    		//Only checks for blanks if the space selected does not have a number
    		gridButtons[selectedY][selectedX].setText(String.format("%s",grid[selectedY][selectedX]));
    		firstSpace=false;
    	}
    	else if(grid[selectedY][selectedX] == 9)//bomb was selected, user lost
    	{
    		lost();
    	}
    	else//normal space selected
    	{
    		space.setBackground(SELECTEDSPACECOLOR);
    		gridButtons[selectedY][selectedX].setText(String.format("%s",grid[selectedY][selectedX]));
    		testWin();
    	}
    }
    
    /*
     * Loads the game
     * @param String thats the fileName
     */
    private void load(String fileName)
    {
    	//Catches IO exceptions
    	try
    	{
    		int tempMines=0;					//counts the amount of mines
    		int temp=0;							//used traverse each code array
    		String eachCode[] = null;			//used to separate each code
    		String fullCode="";					//the full file code		
    		int input;							//saves the raw input
    		file=new FileReader(fileName);		//opens file
    		reader=new BufferedReader(file);	//opens buffered reader
    		firstSpace=false;
    		
    		//Adds each code to the string
    		input=reader.read();
    		while(input != -1) 
            { 
                fullCode+=(char)input;
                input=reader.read();
            }
    		eachCode=fullCode.split("\\s+");//splits the full string into individual codes
    		start();//starts the game
    		//Loads the file into the game
        	for(int y=0;y<GRIDSIZE;y++)
        	{
        		for(int x=0;x<GRIDSIZE;x++)
        		{
        			if(Character.getNumericValue(eachCode[temp].charAt(1))==9)
        				tempMines++;
        			grid[y][x]=Character.getNumericValue(eachCode[temp].charAt(1));	//saves the number value
        			//Tests the correct values for the board
        			if(eachCode[temp].charAt(0)=='1')
        			{
        				gridButtons[y][x].setBackground(SELECTEDSPACECOLOR);
        				gridButtons[y][x].setText(String.format("%s", eachCode[temp].charAt(1)));
        			}
        			//Tests if it was a flagged value
        			if(eachCode[temp].charAt(0)=='2')
        				gridButtons[y][x].setBackground(FLAGCOLOR);
        			temp++;//next slot in each code
        		}
        	}
        	mineCount=tempMines;
        	mineTotalLabel.setText(String.format("%s Mines", tempMines));
    		file.close();
        	reader.close();
    	}catch(Exception e)
    	{
    		JOptionPane.showMessageDialog(null, "The file could not be found.");
    	}
    }
    
    /*
     * Saves the game
     * @param String thats the fileName
     */
    private void save(String fileName)
    {
    	//Catches all IO exceptions
    	try
    	{
    		writer=new BufferedWriter(new FileWriter(fileName));
    		//Runs through each button 
    		for(int y=0;y<GRIDSIZE;y++)
        	{
        		for(int x=0;x<GRIDSIZE;x++)
        		{
        			//Saves codes based on 00, where first 0 is whether it was selected (2 is flagged) and second 0 represents the grid's value
        			if(gridButtons[y][x].getBackground()==NOTSELECTEDSPACECOLOR)
        			{
        				writer.write("0");
        				writer.write(String.format("%s", grid[y][x]));
        			}
        			else if(gridButtons[y][x].getBackground()==FLAGCOLOR)
        			{
        				writer.write("2");
        				writer.write(String.format("%s", grid[y][x]));
        			}
        			else
        			{
        				writer.write("1");
        				writer.write(String.format("%s", grid[y][x]));
        			}
        			//Determines if there is still more to save
            		if(x+1 < GRIDSIZE)
            			writer.write(" ");
        		}
        		//Determines if there is still more to save
        		if(y+1 < GRIDSIZE)
        			writer.write("\n");
        	}
    		JOptionPane.showMessageDialog(null, "The file was saved successfully.");
    		writer.close();
    	}catch(Exception e)
    	{
    		JOptionPane.showMessageDialog(null, "The file entered could not be created.");
    	}
    }
    
    /*
     * Tests whether the user won
     * @param String thats the fileName
     */
    private void testWin()
    {
    	int temp=0;	//used to determine the count of flags
    	//Tests if all the numbers are revealed
    	for(int y=0;y<GRIDSIZE;y++)
    	{
    		for(int x=0;x<GRIDSIZE;x++)
    		{
    			//Tests if this current space has a number and if its revealed 
    			if(grid[y][x] == 9 && gridButtons[y][x].getBackground() == FLAGCOLOR)
    				temp++;
    		}
    	}
    	//Tests if the temp number matches the amount of mines, indicating a win
    	if(temp==mineCount)
    	{
    		revealMines();
    		JOptionPane.showMessageDialog(null, "Congratulations! You won!");
    		System.exit(0);
    	}
    }
    
    /*
     * The user has lost the game. Reveals all bombs and gives the user the option to play again
     */
    private void lost()
    {
    	revealMines();
    	//Determines correct answer based on prompt answer
    	if(JOptionPane.showConfirmDialog(null, "You Lose. Play Again?")==0)
			restart();
    	else
    		System.exit(0);
    }
    
    /*
     * Reveals all the mines
     */
    private void revealMines()
    {
    	//Runs through all the buttons and reveals them as mines
    	for(int y=0;y<GRIDSIZE;y++)
    	{
    		for(int x=0;x<GRIDSIZE;x++)
    		{
    			if(grid[y][x]==9)//is a mine
    			{
    				gridButtons[y][x].setIcon(mineIcon);
    				gridButtons[y][x].setBackground(MINECOLOR);
    			}
    		}
    	}
    }
    
    /*
	 *	Actions class handles all the actions based on the user's actions
	 */
	private class Actions implements ActionListener,MouseListener
	{
		public void actionPerformed(ActionEvent event) 
		{
			//User selects difficulty
			if(event.getSource() == easyButton)
			{
				mineCount=EASY;
				start();
			}
			if(event.getSource() == medButton)
			{
				mineCount=MED;
				start();
			}
			if(event.getSource() == hardButton)
			{
				mineCount=HARD;
				start();
			}
			//User selects load button
			if(event.getSource() == loadButton || event.getSource() == loadGameName)
				load(loadGameName.getText());
			//User selects restart
			if(event.getSource() == restartButton)
				restart();
			//User selects save
			if(event.getSource() == saveButton || event.getSource() == saveGameName)
			{
				//Stops save if the user did not do anything yet
	    		if(firstSpace)
	    			JOptionPane.showMessageDialog(null, "Complete an action before saving.");
	    		else
	    			save(saveGameName.getText());
			}
			//User selects a grid button
			//Only selects it if the title is not visible and therefore the game began
			if(!titlePanel.isVisible())
			{
				//Goes through each grid button to test if it was clicked
				for(int y=0;y<GRIDSIZE;y++)
		        	for(int x=0;x<GRIDSIZE;x++)
		        		if(event.getSource() == gridButtons[y][x])
		    				spaceSelected((JButton)event.getSource());
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) 
		{
			//Determines if it was a right click
			if(e.getButton()==MouseEvent.BUTTON3)
				placeFlag((JButton)e.getSource());
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}
}

