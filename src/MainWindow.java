import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.journaldev.design.observer.EventType;
import com.journaldev.design.observer.Observer;
import com.journaldev.design.observer.Subject;
import util.GameUtil;
import util.Music;

/*
 * Created by Abraham Campbell on 15/01/2020.
 *   Copyright (c) 2020  Abraham Campbell

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
   
   (MIT LICENSE ) e.g do what you want with this :-) 
 */ 

/*
// Tutorial //
Observer Design Pattern in Java
https://www.digitalocean.com/community/tutorials/observer-design-pattern-in-java
*/

/**
 * @author huirong wang
 * 2023.3 assignment for COMP30540 Game Development
 */

public class MainWindow implements Observer {
	private Subject subject;

	@Override
	public void update() {
		EventType msg = (EventType) subject.getUpdate(this);
		//System.out.println("MAINWINDOW RECIEVE MSG: "+msg.toString());
		switch(msg)
		{
			case GO_MENU -> SetMenuPage();
			case GAME_OVER -> SetGameOverPage();
			case GAME_WIN -> onGameWin();
		}
	}

	@Override
	public void setSubject(Subject sub) {
		subject = model;
	}
	 private static JFrame frame = new JFrame("The Enchanted Journey");

	 private static Model model;

	 static{
		try {
			model = new Model();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	 private static GameUtil gameUtil= new GameUtil();
	 private static Viewer viewer;

	static {
		try {
			viewer = new Viewer(model);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private KeyListener Controller =new Controller();
	 private int width;
	 private int height;
	 private static int TargetFPS = 100;
	 private static boolean startGame= false; 
	 private JLabel MenuLabel;
	 private JLabel GameOverLabel;
	 private JButton NewGameBtn;
	 private JButton ContinueBtn;
	 private JButton HistoryBtn;
	 private JButton level1Btn;
	 private JButton level2Btn;
	 private JButton level3Btn;
	 Music bgm = new Music();

	public MainWindow() throws IOException {
		subject = model;
		model.setObservers(this);
		model.setObservers(viewer);
		history = new History();
		width = gameUtil.getWindowWidth();
		height = gameUtil.getWindowHeight();
		  frame.setSize(width, height);  // you can customise this later and adapt it to change on size.
	      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   //If exit // you can modify with your way of quitting , just is a template.
	      frame.setLayout(null);
	      frame.add(viewer);
	      viewer.setBounds(0, 0, width, height);
		  viewer.setBackground(new Color(255,255,255)); //white background  replaced by Space background but if you remove the background method this will draw a white screen
		  viewer.setVisible(false);

		File menuFile = new File(gameUtil.getBgPath("menu"));  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
		File gameoverFile = new File(gameUtil.getBgPath("over"));  //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
		try {
			BufferedImage myPicture = ImageIO.read(menuFile);
			MenuLabel = new JLabel(new ImageIcon(myPicture));
			MenuLabel.setSize(width,height);
			frame.add(MenuLabel);

			BufferedImage GameOverImg = ImageIO.read(gameoverFile);
			GameOverLabel = new JLabel(new ImageIcon(GameOverImg));
			GameOverLabel.setSize(width,height);
			frame.add(GameOverLabel);
		}  catch (IOException e) {
			e.printStackTrace();
		}

		PlayMusic("menu");

		SetLevelButton();
		setBtnStatus();
		setBtnVisible(true);
		GameOverLabel.setVisible(false);
		frame.setVisible(true);
	}

	public static void main(String[] args) throws Exception {
		MainWindow hello = new MainWindow();  //sets up environment 
		while(true)   //not nice but remember we do just want to keep looping till the end.  // this could be replaced by a thread but again we want to keep things simple 
		{ 
			//swing has timer class to help us time this but I'm writing my own, you can of course use the timer, but I want to set FPS and display it 
			int TimeBetweenFrames =  800 / TargetFPS;
			long FrameCheck = System.currentTimeMillis() + (long) TimeBetweenFrames;
			
			//wait till next time step
			Thread.sleep(TimeBetweenFrames);
			//while (FrameCheck > System.currentTimeMillis()){}

			if(startGame)
			{
				gameloop();
			}
			//UNIT test to see if framerate matches 
		    //UnitTests.CheckFrameRate(System.currentTimeMillis(),FrameCheck, TargetFPS);
		}
	}

	//Basic Model-View-Controller pattern 
	private static void gameloop() throws Exception {
		// GAMELOOP
		// controller input  will happen on its own thread
		// So no need to call it explicitly
		// model update
		model.gamelogic();
		viewer.updateview();
	}

	void SetMenuPage() {
		startGame = false;
		frame.setTitle("The Enchanted Journey");
		viewer.setVisible(false);
		MenuLabel.setVisible(true);
		GameOverLabel.setVisible(false);
		frame.setVisible(true);
		SetLevelButton();
		setBtnStatus();
		setBtnVisible(true);
		StopMusic();
		PlayMusic("menu");
	}

	void SetGameOverPage()
	{
		startGame = false;
		frame.setTitle("Game Over");
		viewer.setVisible(false);
		MenuLabel.setVisible(false);
		GameOverLabel.setVisible(true);
		frame.setVisible(true);
		setBtnStatus();
		setBtnVisible(true);
		StopMusic();
		PlayMusic("menu");
	}

	void SetLevelButton() {
		NewGameBtn = new JButton("New Game");
		NewGameBtn.setBounds(110, 240, 120, 40);
		ContinueBtn = new JButton("Continue");
		ContinueBtn.setBounds(260, 240, 120, 40);
		HistoryBtn = new JButton("History");
		HistoryBtn.setBounds(410, 240, 120, 40);
		NewGameBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				setBtnVisible(false);
				try {
					NewGameBtnOnClick();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}});
		ContinueBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				if(model.getLevel()==1) return;
				setBtnVisible(false);
				try {
					LevelBtnOnClick();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}});
		HistoryBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				setBtnVisible(false);
				try {
					HistoryBtnOnClick();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}});

		frame.add(NewGameBtn);
		frame.add(ContinueBtn);
		frame.add(HistoryBtn);

		level1Btn = new JButton("LEVEL 1");
		level1Btn.setBounds(110, 300, 120, 40);
		level2Btn = new JButton("LEVEL 2");
		level2Btn.setBounds(260, 300, 120, 40);
		level3Btn = new JButton("LEVEL 3");
		level3Btn.setBounds(410, 300, 120, 40);
		level1Btn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				if(model.getLevel()!=1) return;
				setBtnVisible(false);
				try {
					LevelBtnOnClick();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}});

		level2Btn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				if(model.getLevel()!=2) return;
				setBtnVisible(false);
				try {
					LevelBtnOnClick();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}});

		level3Btn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				if(model.getLevel()!=3) return;
				setBtnVisible(false);
				try {
					LevelBtnOnClick();
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}});

		frame.add(level1Btn);
		frame.add(level2Btn);
		frame.add(level3Btn);
	}

	void LevelBtnOnClick() throws Exception {
		model.StartNewGame();
		MenuLabel.setVisible(false);
		GameOverLabel.setVisible(false);
		viewer.setVisible(true);
		viewer.addKeyListener(Controller);    //adding the controller to the Canvas
		viewer.requestFocusInWindow();
		startGame=true;
		frame.setTitle("LEVEL "+ model.getLevel());
		StopMusic();
		PlayMusic("bgm");
	}

	void NewGameBtnOnClick() throws Exception {
		MenuLabel.setVisible(false);
		GameOverLabel.setVisible(false);
		viewer.setVisible(true);
		viewer.addKeyListener(Controller);    //adding the controller to the Canvas
		viewer.requestFocusInWindow();
		model.getCurrSave().NewGameSave();
		model.StartNewGame();
		startGame=true;
		frame.setTitle("LEVEL "+ model.getLevel());
		StopMusic();
		PlayMusic("bgm");
	}

	private History history;
	void HistoryBtnOnClick() throws IOException {
		model.showHistory(history.getHistoryList());
		MenuLabel.setVisible(false);
		GameOverLabel.setVisible(false);
		viewer.setVisible(true);
		viewer.addKeyListener(Controller);    //adding the controller to the Canvas
		viewer.requestFocusInWindow();
		startGame=true;
		frame.setTitle("History");

//		JPanel panel = new JPanel();
//		panel.setBounds(0,0,width,height);
//		LayoutManager overlay = new OverlayLayout(panel);
//		panel.setLayout(overlay);

//		JLabel label1 = new JLabel(history.getString());
//		label1.setAlignmentX(0.5f);
//		label1.setAlignmentY(0.5f);
//		panel.add(label1);
//		File menuFile = new File(gameUtil.getBgPath("menu"));
//		BufferedImage myPicture = ImageIO.read(menuFile);
//		JLabel label2 = new JLabel(new ImageIcon(myPicture));
//		label2.setSize(width, height);
//		label2.setAlignmentY(0.5f);
//		panel.add(label2);
//		frame.add(panel);
//
//		startGame = false;
//		frame.setTitle("History Rank");
//		viewer.setVisible(false);
//		MenuLabel.setVisible(false);
//		GameOverLabel.setVisible(false);
//
//		frame.pack();
//		frame.setVisible(true);
	}

	void onGameWin() {
		if(history==null) return;
		history.SaveHistory(model);
	}

	void setBtnStatus()
	{
		NewGameBtn.setBackground(Color.WHITE);
		HistoryBtn.setBackground(Color.WHITE);
		ContinueBtn.setBackground(model.getLevel()==1 ? Color.gray: Color.WHITE);
		level1Btn.setBackground(model.getLevel()!=1 ? Color.gray: Color.WHITE);
		level2Btn.setBackground(model.getLevel()!=2 ? Color.gray: Color.WHITE);
		level3Btn.setBackground(model.getLevel()!=3 ? Color.gray: Color.WHITE);
	}

	void setBtnVisible(boolean value)
	{
		NewGameBtn.setVisible(value);
		ContinueBtn.setVisible(value);
		level1Btn.setVisible(value);
		level2Btn.setVisible(value);
		level3Btn.setVisible(value);
		HistoryBtn.setVisible(value);
		if(value)
		{
			ContinueBtn.requestFocus();
			NewGameBtn.requestFocus();
			level1Btn.requestFocus();
			level2Btn.requestFocus();
			level3Btn.requestFocus();
			HistoryBtn.requestFocus();
		}
	}

	void PlayMusic(String name)
	{
		bgm.setFile(name);
		bgm.play();
	}

	void StopMusic()
	{
		bgm.setFile(null);
		try {
			bgm.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

/*
 * 
 * 

Hand shake agreement 
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,=+++
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,:::::,=+++????
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,:++++????+??
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,:,,:,:,,,,,,,,,,,,,,,,,,,,++++++?+++++????
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,=++?+++++++++++??????
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,~+++?+++?++?++++++++++?????
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:::,,,,,,,,,,,,,,,,,,,,,,,,,,,~+++++++++++++++????+++++++???????
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,,,,,,,,,,,,,,,,,,,,:===+=++++++++++++++++++++?+++????????????????
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,~=~~~======++++++++++++++++++++++++++????????????????
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,::::,,,,,,=~.,,,,,,,+===~~~~~~====++++++++++++++++++++++++++++???????????????
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,,,~~.~??++~.,~~~~~======~=======++++++++++++++++++++++++++????????????????II
:::::::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,,:=+++??=====~~~~~~====================+++++++++++++++++++++?????????????????III
:::::::::::::::::::::::::::::::::::::::::::::::::::,:,,,++~~~=+=~~~~~~==~~~::::~~==+++++++==++++++++++++++++++++++++++?????????????????IIIII
::::::::::::::::::::::::::::::::::::::::::::::::,:,,,:++++==+??+=======~~~~=~::~~===++=+??++++++++++++++++++++++++?????????????????I?IIIIIII
::::::::::::::::::::::::::::::::::::::::::::::::,,:+????+==??+++++?++====~~~~~:~~~++??+=+++++++++?++++++++++??+???????????????I?IIIIIIII7I77
::::::::::::::::::::::::::::::::::::::::::::,,,,+???????++?+?+++???7?++======~~+=====??+???++++++??+?+++???????????????????IIIIIIIIIIIIIII77
:::::::::::::::::::::::::::::::::::::::,,,,,,=??????IIII7???+?+II$Z77??+++?+=+++++=~==?++?+?++?????????????III?II?IIIIIIIIIIIIIIIIIIIIIIIIII
::::::::::::::::::::::::::::::,,,,,,~=======++++???III7$???+++++Z77ZDZI?????I?777I+~~+=7+?II??????????????IIIIIIIIIIIIIIIIIIIIII??=:,,,,,,,,
::::::::,:,:,,,,,,,:::~==+=++++++++++++=+=+++++++???I7$7I?+~~~I$I??++??I78DDDO$7?++==~I+7I7IIIIIIIIIIIIIIIIII777I?=:,,,,,,,,,,,,,,,,,,,,,,,,
++=++=++++++++++++++?+????+??????????+===+++++????I7$$ZZ$I+=~$7I???++++++===~~==7??++==7II?~,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
+++++++++++++?+++?++????????????IIIII?I+??I???????I7$ZOOZ7+=~7II?+++?II?I?+++=+=~~~7?++:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
+?+++++????????????????I?I??I??IIIIIIII???II7II??I77$ZO8ZZ?~~7I?+==++?O7II??+??+=====.,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
?????????????III?II?????I?????IIIII???????II777IIII7$ZOO7?+~+7I?+=~~+???7NNN7II?+=+=++,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
????????????IIIIIIIIII?IIIIIIIIIIII????II?III7I7777$ZZOO7++=$77I???==+++????7ZDN87I??=~,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
IIII?II??IIIIIIIIIIIIIIIIIIIIIIIIIII???+??II7777II7$$OZZI?+$$$$77IIII?????????++=+.,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII?+++?IIIII7777$$$$$$7$$$$7IIII7I$IIIIII???I+=,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII???????IIIIII77I7777$7$$$II????I??I7Z87IIII?=,,,,,,,,,,,:,,::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
777777777777777777777I7I777777777~,,,,,,,+77IIIIIIIIIII7II7$$$Z$?I????III???II?,,,,,,,,,,::,::::::::,,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
777777777777$77777777777+::::::::::::::,,,,,,,=7IIIII78ZI?II78$7++D7?7O777II??:,,,:,,,::::::::::::::,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
$$$$$$$$$$$$$77=:,:::::::::::::::::::::::::::,,7II$,,8ZZI++$8ZZ?+=ZI==IIII,+7:,,,,:::::::::::::::::,:::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
$$$I~::::::::::::::::::::::::::::::::::::::::::II+,,,OOO7?$DOZII$I$I7=77?,,,,,,:::::::::::::::::::::,,,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
::::::::::::::::::::::::::::::::::::::::::::::::::::::+ZZ?,$ZZ$77ZZ$?,,,,,::::::::::::::::::::::::::,::::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::I$:::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,:,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,,,
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,,,,
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,
::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::,,,,,,,,,,,,,,,,,,,,,,
                                                                                                                             GlassGiant.com
 * 
 * 
 */
