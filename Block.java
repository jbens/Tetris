import java.awt.*;

public class Block extends GameObject {
	final static int SIZE = 20;
	Color color;

	public Block(int x, int y) {
		super(x, y, SIZE, SIZE);
	}
	
	public void setColor(Color c) {
		this.color = c;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRoundRect(x*SIZE, y*SIZE, SIZE, SIZE, 3, 3);
//		g.fillRect(x*SIZE, y*SIZE, SIZE, SIZE);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x*SIZE, y*SIZE, SIZE, SIZE, 3, 3);
//		g.drawRect(x*SIZE, y*SIZE, SIZE, SIZE);
	}
	
	public int getSize() {
		return Block.SIZE;
	}
}
