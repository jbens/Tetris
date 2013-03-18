import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.Timer;


@SuppressWarnings("serial")
public class Preview extends JComponent {
	
	private Tetrimino next, temp;
	private TetrisCourt court;
	private int interval = 30; // Milliseconds between updates.
	private Timer timerTock;
	private int h = 80, w = 80;
	
	public Preview(TetrisCourt tc) {
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		court = tc;
		next = court.getNext();
		temp = next;
		
		if (temp != null) {
			temp.setBounds(4, 4);
			temp.setLocation(temp,0,0);
		}
		
		timerTock = new Timer(interval, new ActionListener() {
			public void actionPerformed(ActionEvent e) { getNext(); }
		});
		timerTock.start();
		
	}
	
	void getNext() {
		next = court.getNext();
		temp = next;
		temp.setBounds(4, 4);
		temp.setLocation(temp,0,0);
		repaint();
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // Paint background, border
		
		g.setColor(Color.lightGray);
		
		for (int j = 20; j < h; j += 20) {
			g.drawLine(0, j, h, j); 
		}
		
		// paint vertical gridlines
		for (int i = 20; i < w; i += 20) {
			g.drawLine(i, 0, i, h);
		}
		
		if (temp != null)
			temp.draw(g);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(w, h);
	}
}
