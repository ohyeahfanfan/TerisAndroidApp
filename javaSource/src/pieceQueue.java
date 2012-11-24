import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

import javax.swing.JComponent;


public class pieceQueue extends JComponent {
    final static int QUEUE_LENGTH = 5;
	Piece[] pcQueue = new Piece[QUEUE_LENGTH];
	protected Piece[] pieces;
	protected Random random;
	//protected int[] pieceId = new int[QUEUE_LENGTH];
	
	public pieceQueue (int pixels, int[] temp) {
		super();
		setPreferredSize(new Dimension((3 * pixels)+2,
				(24)*pixels+2));
		pieces = Piece.getPieces();
		for (int i = 0; i < QUEUE_LENGTH; i++) {
			pcQueue[i] = pieces[temp[i]];
		}
		random = new Random();

	}
	
	public Piece getNext(int pieceNum) {
		Piece temp = pcQueue[0];
		int i;
		for (i = 0; i < QUEUE_LENGTH -1; i++) {
			pcQueue[i] = pcQueue[i + 1];
			//pieceId[i] = pieceId[i + 1];
		}
		pcQueue[i] = pieces[pieceNum];
		//pieceId[i] = pieceNum;
		return temp;
	}
	
	public void updatePieces(int[] sharedInt) {
		int i;
		for (i = 0; i < QUEUE_LENGTH; i++) {
			pcQueue[i] = pieces[sharedInt[i]];
		}
		repaint();
	}
	
	public Piece[] getAll() {
		return pcQueue;
	}
	
	private final float dX() {
		return( ((float)(getWidth()-2)) / 3 );
	}

	// height in pixels of a block
	private final float dY() {
		return( ((float)(getHeight()-2)) / 24 );
	}

	// the x pixel coord of the left side of a block
	private final int xPixel(int x) {
		return(Math.round(1 + (x * dX())));
	}

	// the y pixel coord of the top of a block
	private final int yPixel(int y) {
		return(Math.round(getHeight() -1 - (y+1)*dY()));
	}
	@Override
	public void paintComponent(Graphics g) {

		// Draw a rect around the whole thing
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);


		// Draw the line separating the top
		int spacerY = yPixel(19);
		g.drawLine(0, spacerY, getWidth()-1, spacerY);


		// check if we are drawing with clipping
		//Shape shape = g.getClip();
		final int dx = Math.round(dX()-2);
		final int dy = Math.round(dY()-2);

		int i, x, y;
		y = 0;
		// Loop through and draw all the blocks
		// left-right, bottom-top
		for (i = 0; i < QUEUE_LENGTH; i++) {
			TPoint[] tbody = pcQueue[i].getBody();	// the left pixel
			for (x = 0; x < tbody.length; x++) {
				int left = xPixel(tbody[x].x);
				// right pixel (useful for clip optimization)
				int right = xPixel(tbody[x].x+1) -1;
                g.fillRect(left+1, yPixel(tbody[x].y + y)+1, dx, dy);                
			}
			y = y + pcQueue[i].getHeight() + 1;
		}
	}

}
