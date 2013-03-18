import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

@SuppressWarnings("serial")
public class Menu extends JComponent implements Runnable {
	
	private JFrame frame;
	private JLabel scoreLabel;
	public int score = 0;
	
	public Menu (JFrame frame) {
		
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.frame = frame;
		setLayout(new BorderLayout());
		JLabel title = new JLabel("Tetris", SwingConstants.CENTER);
		Font f = new Font(null, Font.PLAIN, 36); // set font to bigger size
		title.setFont(f);
		title.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		String inst = "\nThis is your classic Tetris game. \n\n" +
					  "Use the left and right arrow keys for horizontal movement.\n" +
					  "Use the down arrow key to move the piece down more quickly, " +
					  " and use the up arrow for clockwise rotations.\n\n" +
					  "Pressing 'a' rotates the piece counter-clockwise, while 's' " + 
					  "rotates the piece clockwise.\n" +
					  "Pressing 'q' quits the game.\n" +
					  "Pressing 'p' pauses the game. \n\n" +
					  "Enjoy!";
		JTextPane instructions = new JTextPane();
		instructions.setText(inst);
		instructions.setFont(new Font(null, Font.PLAIN, 15));
		instructions.setEditable(false);
		
		StyledDocument doc = instructions.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		// make buttons
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(2,1)); // sets grid layout
		buttons.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setBackground(Color.WHITE);
		buttons.setFocusable(true);
		
		// Start button
		JButton start = new JButton("Start Game");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				run();
			}
		});
		
		// Quit button
		JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible (false);
				System.exit(0);
			}
		});
		
		buttons.add(start);
		buttons.add(quit);
		
		// set layout
		add(title, BorderLayout.PAGE_START);
		add(instructions, BorderLayout.CENTER);
		add(buttons, BorderLayout.PAGE_END);
		
		this.frame.setResizable(false);
	}
	
	public int getScore() {
		return score;
	}
	
	public void setScore(int s) {
		score = s;
		scoreLabel.setText("Lines: " + score);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(320, 440);
	}
	
	@Override
	public void run() {
		frame.remove(this);
		
		// play area
		final TetrisCourt court = new TetrisCourt(this);
		frame.add(court, BorderLayout.CENTER);

		// make side panel/grid layout
		final JPanel panel = new JPanel();
		final JPanel buttonPanel = new JPanel();
		final JPanel buttonPanel2 = new JPanel();
		panel.add(buttonPanel);
//		panel.setLayout(new GridLayout(5,1));	// make layout of vertical buttons
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		frame.add(panel, BorderLayout.EAST);

		// score 
		scoreLabel = new JLabel("Lines: " + score, SwingConstants.CENTER);
		Font f = new Font(null, Font.BOLD, 15); // set font to bigger size
		scoreLabel.setFont(f);
//		scoreLabel.CENTER_ALIGNMENT;
//		scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(scoreLabel, BorderLayout.CENTER);
		
		panel.add(Box.createRigidArea(new Dimension(0,5)));
		
		// Preview panel (Possibly make into a class, so you can draw, like TetrisCourt)
		final JComponent previewPane = new JPanel();
		JLabel previewLabel = new JLabel("Preview", SwingConstants.CENTER);
		final Preview preview = new Preview(court);
		previewPane.add(previewLabel);
		previewPane.add(preview, BorderLayout.SOUTH);
//		previewPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//		previewPane.setSize(60,60);
		previewPane.setFocusable(false);
		panel.add(previewPane);
		
		// Hold 
		final JComponent holdPane = new JPanel();
		JLabel holdLabel = new JLabel("Hold", SwingConstants.CENTER);
		final Hold hold = new Hold(court);
		holdPane.add(holdLabel);
		holdPane.add(hold);
//		preview.setSize(60,60);
//		preview.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		preview.setFocusable(false);
		panel.add(holdPane);
		
		// Reset button
		final JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				court.reset();
			}
		});
		buttonPanel.add(reset);

		/** figure out how to make the two buttons stacked */

		// Pause button
		final JButton pause = new JButton("Pause");
		pause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				court.pause();
			}
		});
		buttonPanel.add(pause);

		panel.add(buttonPanel2);
		// Quit button
		JButton quit = new JButton("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible (false);
				System.exit(0);
			}
		});
		buttonPanel2.add(quit);
		
		// Put the frame on the screen
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// Start the game running
		court.reset();
	}

}
