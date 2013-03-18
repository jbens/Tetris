import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;


public class Tetrimino {
	
	private int size = 1;
	private LinkedList<Block> piece;
	public int x, y;
	public int minX, maxX, minY, maxY;
	public Color color;
	public int shape, orientation;
	public int width, height;
	private int rightBound, bottomBound;
//	private TetrisCourt court;
	private Direction direction;
	
	// Shapes array
	// row is a shape, column is an orientation
	private int[][] shapes = {
			{ 0xCC00, 0xCC00, 0xCC00, 0xCC00},	// 'O'
			{ 0xF000, 0x4444, 0xF000, 0x4444},	// 'I'
			{ 0x8E00, 0x6440, 0x0E20, 0x44C0},	// 'J'
			{ 0x2E00, 0x4460, 0x0E80, 0xC440},	// 'L'
			{ 0xC600, 0x4C80, 0xC600, 0x4C80},	// 'Z'
			{ 0x6C00, 0x8C40, 0x6C00, 0x8C40},	// 'S'
			{ 0x4E00, 0x4640, 0x0E40, 0x4C40}	// 'T'
	};

	// Constructor
	public Tetrimino (Shape s, int o, int x, int y) {
		
		switch (s) {
			case O: shape = 0; color = Color.YELLOW; 	break;
			case I: shape = 1; color = Color.CYAN; 		break;
			case J: shape = 2; color = Color.BLUE; 		break;
			case L: shape = 3; color = Color.ORANGE; 	break;
			case Z: shape = 4; color = Color.RED;		break;
			case S: shape = 5; color = Color.GREEN; 	break;
			case T: shape = 6; color = Color.MAGENTA;	break;
			default: piece = null; break; // or some other default pattern
		}
		
		this.orientation = o;
		piece = make(shapes[shape][orientation], x, y);
		
		this.x = x;
		this.y = y;
		this.minX = x;
		this.minY = y;
		
		this.width = maxX() - minX();
		this.height = maxY() - minY();
		
	}
	
	// makes the tetrimino pieces at locations
	private LinkedList<Block> make (int shape, int x, int y) {
		int marker = 0x8000;
		LinkedList<Block> bb = new LinkedList<Block>();
		int i = 0;
		int j = 0;
		
		while(marker > 0) {
			if ((shape & marker) == marker)
				bb.add(new Block (j + x,i + y));
			marker >>>= 1;
			j++;
			
			if (j == 4) {
				j = 0; i++;
			}
		}
		
		// return the block array;
		return bb;
	}
	
	// set court
//	public void setCourt(TetrisCourt tc) {
//		court = tc;
//	}
	
	public void setLocation(Tetrimino t, int x, int y) {
		piece = make(shapes[t.shape][t.orientation], x, y);
		
		this.x = x;
		this.y = y;
		this.minX = x;
		this.minY = y;
		
		this.width = maxX() - minX();
		this.height = maxY() - minY();
		
	}
	
	public void setBounds(int width, int height) {
		rightBound = width;
		bottomBound = height;
	}
	
	public void clip() {
		// clip x values		
		if (minX < 0) {
			for (Block b : piece)
				b.setX(Math.abs(minX) + b.getX());
			minX = 0; maxX = width;
		}
		else if (maxX > rightBound) {
			for (Block b : piece)
				b.setX(b.getX() - Math.abs(maxX - rightBound));
			minX = rightBound - width; maxX = rightBound;
		}
		

		// clip y values
		if (maxY > bottomBound) {
			for (Block b : piece)
				b.setY(b.getY() - Math.abs(maxY - bottomBound));
			minY = bottomBound - height; maxY = bottomBound;
		}
		else if (minY < 0) {
			for (Block b : piece)
				b.setY(Math.abs(minY) + b.getY());
			minY = 0; maxY = height;
		}
		
	}
	
	// get minimum X value of piece
	public int minX() {
		minX = piece.get(0).getX();
		for (Block b : piece)
			if (minX > b.getX())
				minX = b.getX();
		return minX;
	}
	
	// get maximum X value of piece
	public int maxX() {
		maxX = piece.get(0).getX();
		for (Block b : piece)
			if (maxX < b.getX())
				maxX = b.getX();
				
		return maxX += size;
	}
	
	// get minimum Y value of piece
	public int minY() {
		minY = piece.get(0).getY();
		for (Block b : piece)
			if (minY > b.getY())
				minY = b.getY();
				
		return minY;
	}
	
	// get maximum Y value of piece
	public int maxY() {
		maxY = piece.get(0).getY();
		for (Block b : piece)
			if (maxY < b.getY())
				maxY = b.getY();
				
		return maxY += size;
	}
	
	public LinkedList<Block> getPiece() {
		return piece;
	}
	
	public void changeBounds() {
		minX(); maxX();
		minY(); maxY();
		this.width = maxX() - minX();
		this.height = maxY() - minY();
	}
	
	public Direction rotateCW() {
		this.orientation = (orientation + 1) & 0x3;
		piece = make(shapes[shape][orientation], x, y);
		changeBounds(); clip();
		direction = Direction.CW;
		return direction;
	}
	
	public Direction rotateCCW() {
		this.orientation = (orientation - 1) & 0x3;
		piece = make(shapes[shape][orientation], x, y);
		changeBounds(); clip();
		direction = Direction.CCW;
		return direction;
	}
	 
	// tetrimino move method
	public Direction move(int dx, int dy) {
				
		for (Block b : piece) {
			b.move(dx, dy);
		}

		x += dx; y += dy; 
		minX += dx; maxX += dx; 
		minY += dy; maxY += dy;
		
		clip();
		
		if (dx == 0 || dy == 0)
			direction = Direction.NONE;
		if (dx > 0)
			direction = Direction.RIGHT;
		else if (dx < 0)
			direction = Direction.LEFT;
		if (dy > 0)
			direction = Direction.DOWN;
		else if (dy < 0)
			direction = Direction.UP;
		
		return direction;
	}
	
	// tetrimino draw method
	public void draw(Graphics g) {
		// set color of tetrimino
		g.setColor(color);
		for (Block b : piece) {
			b.setColor(color);
			b.draw(g);
		}
	}
	
}
