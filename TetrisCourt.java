import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;


import javax.swing.*;

@SuppressWarnings("serial")
public class TetrisCourt extends JComponent {
	private Tetrimino tetrimino, hold;
	public Tetrimino next;
	
	private int interval = 20; // Milliseconds between updates.
	private Timer timerTick;       // Each time timer fires we animate one step.
	private Timer timerTock;	   // Timer for vertical movement

	private final int COURTWIDTH  = 200;
	private final int COURTHEIGHT = 400;
	
	private final int cellW = 10;
	private final int cellH = 20;
	
	private Block[][] cells = new Block[cellH][cellW];
//	private Graphics graphics;
	private Direction dir = Direction.NONE;
	private boolean cleared = false;
	private boolean collision = false;
	private boolean gameOver = false;
	private int score = 0;
	
	private Menu menu;
	
	public TetrisCourt(Menu m) {
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setFocusable(true);
		
		menu = m;

		// Have two timers to allow discrepancy in vertical v.s. horizontal
		// motion
		timerTick = new Timer(600, new ActionListener() {
			public void actionPerformed(ActionEvent e) { tick(); }
		});
		timerTick.start();
		
		timerTock = new Timer(interval, new ActionListener() {
			public void actionPerformed(ActionEvent e) { tock(); }
		});
		timerTock.start();
		
		// map keys to functionality
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT)
					collisionMove(-1, 0);
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
					collisionMove(1, 0);
				else if (e.getKeyCode() == KeyEvent.VK_DOWN)
					collisionMove(0, 1);
				else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					collisionMove(0,spaceLeft(tetrimino));
					newPiece();
				}
				else if (e.getKeyCode() == KeyEvent.VK_X)
//					collisionRotate(Direction.CW);
					dir = tetrimino.rotateCW();
				else if (e.getKeyCode() == KeyEvent.VK_UP)
//					collisionRotate(Direction.CW);
					dir = tetrimino.rotateCW();
				else if (e.getKeyCode() == KeyEvent.VK_Z)
//					collisionRotate(Direction.CCW);
					dir = tetrimino.rotateCCW();
				else if (e.getKeyCode() == KeyEvent.VK_P)
					pause();
				else if (e.getKeyCode() == KeyEvent.VK_R)
					reset();
				else if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
//					timerTick.stop();
					if (hold == null) {
						hold = tetrimino;
//						hold.setLocation(tetrimino, 4, 0);
						tetrimino = next;
						next = getRandom();
						next.setBounds(cellW, cellH);
						tetrimino.setLocation(tetrimino,4,0);
						tetrimino.setBounds(cellW, cellH);
					}
					else {
						Tetrimino temp = hold;
						hold = tetrimino;
						tetrimino = temp;
						tetrimino.setLocation(tetrimino, 4, 0);
						tetrimino.setBounds(cellW, cellH);
					}
//					timerTick.start();
				}
				else if (e.getKeyCode() == KeyEvent.VK_Q) {
					setVisible (false);
					System.exit(0);
				}
					
			}
		});
		// After a PongCourt object is built and installed in a container
		// hierarchy, somebody should invoke reset() to get things started... 
	}

	/** Set the state of the state of the game to its initial value and 
	    prepare the game for keyboard input. */
	public void reset() {
		tetrimino = getRandom();
		next = getRandom();
		hold = null;
		
		tetrimino.setBounds(cellW, cellH);
//		next.setBounds(cellW, cellH);
//		next.setLocation(next,4,0);
		
		cells = new Block[cellH][cellW];
		score = 0;
		menu.setScore(score);
		
		if (isPaused) {
			timerTick.start();
			timerTock.start();
		}
		
		requestFocusInWindow();
	}

   /** Update the game one timestep by moving the ball and the paddle. */
	void tick() {
		isPaused = false;
		
		// make new piece, stores old piece in cells[][]
		if (collisionMove(0,1) && dir == Direction.DOWN)
			newPiece();
	}
	
	void tock() {
		repaint();
	}
	
	public void newPiece() {
		// try to get delay of making this piece
		for (Block b : tetrimino.getPiece()) {
			cells[b.getY()][b.getX()] = b;
		}
		clearLine();
		tetrimino = next;
		next = getRandom();
		tetrimino.setBounds(cellW, cellH);
		tetrimino.setLocation(tetrimino,4,0);
	}
	
	public int spaceLeft (Tetrimino t) {
		
		LinkedList<Block> block = t.getPiece();
		
		int min = t.minX;
		int max = t.maxX;
		
		int[] mins = new int[t.width];
		
		//calculate minimum y's for discrete x's
		for (Block b: block) {
			mins[b.getX() - t.minX] = b.getY();
		}
		
		int spaceLeft = cellH - mins[0] - 1;
		
		for (int i = t.minY; i < cellH; i++) {
			for (int j = min; j < max; j++) {
				if (cells[i][j] != null) {
					int temp = i - mins[j - min] - 1;
					if (temp < spaceLeft)
						spaceLeft = temp; 
				}
			}
		}
		
		return spaceLeft;
	}
	
	public void clearLine() {
		int fill_row, row;
		int full = 1;
		int i = 0, j = 0;

		cleared = false;
		int clearedRows = 0;
		
		fill_row = (cellH - 1);
		row = (cellH - 1);

		while (row >= 0) {
			
			for (j = 0; j < cellW; j++) {
				cells[fill_row][j] = cells[row][j];
				
				if (cleared && cells[row][j] != null)
					cells[row][j].move(0,clearedRows);
			}
						
			for (j = 0, full = 1; (j < cellW) && (full > 0); j++) {
				if (cells[row][j] == null) {
					--fill_row;
					full = 0;
				}
			}
			
			if (full == 1 && j == 10) {
				cleared = true;
				clearedRows++;
			}
			
			--row;
			// this handles scoring
			if (full == 1) {
				score++;
				menu.setScore(menu.getScore() + 1);
			}
		}		
		
		for (i = 0; i < fill_row; i++) {
			for (j = 0; j < cellW; j++) {
				cells[i][j] = null;
			}
		}
		
	}
	
	private boolean isPaused = false;
	
	// Pause functionality
	void pause() {
		isPaused = !isPaused;
		if (isPaused) {
			timerTick.stop();
			timerTock.stop();
		}
		else {
			timerTick.start();
			timerTock.start();
			requestFocusInWindow();
		}
	}
	
	public Tetrimino getNext() {
		return next;
	}
	
	public Tetrimino getHold() {
		return hold;
	}
	
	// Get random pieces
	public Tetrimino getRandom() {
		int rand = (int) (Math.random()*7); // random number from 0 to 6
		Shape shape = Shape.O;
		
		switch (rand) {
			case 0: shape = Shape.O; break;
			case 1: shape = Shape.I; break;
			case 2: shape = Shape.J; break;
			case 3: shape = Shape.L; break;
			case 4: shape = Shape.Z; break;
			case 5: shape = Shape.S; break;
			case 6: shape = Shape.T; break;
			default: break;
		}
		                            // random number from 0 to 4
		return new Tetrimino(shape, (int)(Math.random()*4), 4, 0);
	}
	
	// collision test for up/down, left/right moves
	public boolean collisionMove(int dx, int dy) {
		gameOver = false;
		collision = false;

		LinkedList<Block> blocks = tetrimino.getPiece();
		
		if (dir == Direction.CW || dir == Direction.CCW) {
			for (Block b: blocks) {
				if (cells[b.getY()][b.getX()] != null) {
					switch (dir) {
						case CCW:
							tetrimino.rotateCW();
							break;
						case CW:
							tetrimino.rotateCCW();
							break;
						default: break;
					}
					collision = true;
				}
			}
		}
		repaint();
		
		if (collision)
			return collision;
		
		dir = tetrimino.move(dx,dy);
		/*LinkedList<Block>*/ blocks = tetrimino.getPiece();
		
		for (Block b: blocks) {
			if (cells[b.getY()][b.getX()] != null) {
				switch (dir) {
					case DOWN: 
						if (tetrimino.minY <= 0) {
							gameOver = true;
							break;
						}
						tetrimino.move(0,-1); 
						break;
					case LEFT:
						tetrimino.move(dx,0); break;
					case RIGHT:
						tetrimino.move(-dx,0); break;
					default: break;
				}
				collision = true;
			}
		}
		
		if (tetrimino.maxY >= cellH) {
			for (Block b: blocks) {
			if (cells[b.getY()][b.getX()] == null) {
				collision = true;
			}
			}
			collision = true;
		}
		
		if (gameOver && collision) {
			pause();
			System.out.println("Game over");
		}
		
		repaint();

		return collision;
	}
	
	// collision for rotational moves
//	public boolean collisionRotate(Direction d) {
//		
//		collision = false;
//		
//		if (d == Direction.CCW)
//			dir = tetrimino.rotateCCW();
//		else dir = tetrimino.rotateCW();
//		
//		LinkedList<Block> blocks = tetrimino.getPiece();
//
//		timerTick.stop();
//		for (Block b: blocks) {
//			if (cells[b.getY()][b.getX()] != null) {
//				switch (dir) {
//					case CCW:
//						tetrimino.rotateCW();
//						break;
//					case CW:
//						tetrimino.rotateCCW();
//						break;
//					default: break;
//				}
//				collision = true;
//			}
//		}
//		repaint();
//		timerTick.start();
//
//		return collision;
//	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // Paint background, border
		
		g.setColor(Color.lightGray);
		
		// paint horizontal grid lines
		for (int j = 20; j < COURTHEIGHT; j += 20) {
			g.drawLine(0, j, COURTWIDTH, j); 
		}
		
		// paint vertical gridlines
		for (int i = 20; i < COURTWIDTH; i += 20) {
			g.drawLine(i, 0, i, COURTHEIGHT);
		}
		
		tetrimino.draw(g);
		
		// maybe store min y value to make check efficient
		// start with bottom row in redrawing
//		for (int i = cells.length - 1; i >= 0; i--) {
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells[i].length; j++) {
				if (cells[i][j] != null)
					cells[i][j].draw(g);
			}
		}


	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(COURTWIDTH, COURTHEIGHT);
	}
}
