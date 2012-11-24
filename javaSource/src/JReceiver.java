import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;

@SuppressWarnings("serial")
public class JReceiver extends JComponent implements Runnable {
	// size of the board in blocks
	public static final int WIDTH = 10;
	public static final int HEIGHT = 20;

	// Extra blocks at the top for pieces to start.
	// If a piece is sticking up into this area
	// when it has landed -- game over!
	public static final int TOP_SPACE = 4;

	// When this is true, plays a fixed sequence of 100 pieces
	protected boolean testMode = false;
	public final int TEST_LIMIT = 100;

	// Is drawing optimized
	// (default false, so debugging is easier)
	protected boolean DRAW_OPTIMIZE = false;

	// Board data structures
	protected Board board;
	protected Piece[] pieces;


	// The current piece in play or null
	protected Piece currentPiece;
	protected int currentX;
	protected int currentY;
	protected boolean moved;	// did the player move the piece


	// The piece we're thinking about playing
	// -- set by computeNewPosition
	// (storing this in ivars is slightly questionable style)
	protected Piece newPiece;
	protected int newX;
	protected int newY;

	// State of the game
	protected boolean gameOn;	// true if we are playing
	protected int count;		 // how many pieces played so far
	protected long startTime;	// used to measure elapsed time
	protected Random random;	 // the random generator for new pieces


	// Controls
	protected JLabel countLabel;
	protected JLabel scoreLabel;
	protected int score;
	protected JLabel timeLabel;
	protected JButton startButton;
	protected JButton stopButton;
	protected javax.swing.Timer timer;
	protected JSlider speed;
	protected JCheckBox testButton;

	public final int DELAY = 1000;	// milliseconds per tick
	private Socket receiveSock;

	private ObjectInputStream recIn;
	private ObjectOutputStream recOut;
	/**
	 * Creates a new JTetris where each tetris square
	 * is drawn with the given number of pixels.
	 */
	JReceiver(int pixels, Socket receiveSock) {
		super();

		// Set component size to allow given pixels for each block plus
		// a 1 pixel border around the whole thing.
		setPreferredSize(new Dimension((WIDTH * pixels)+2,
				(HEIGHT+TOP_SPACE)*pixels+2));
		gameOn = false;

		pieces = Piece.getPieces();
		board = new Board(WIDTH, HEIGHT + TOP_SPACE);
		this.receiveSock = receiveSock;
		try {						
			recOut = new ObjectOutputStream(this.receiveSock.getOutputStream());
			recIn = new ObjectInputStream(this.receiveSock.getInputStream());
		}
		//server.setTcpNoDelay(true);
		catch (IOException e) {
			e.printStackTrace();
		}



		requestFocusInWindow();
	}



	/**
	 Sets the internal state and starts the timer
	 so the game is happening.
	 */
	public void startGame() {
		// cheap way to reset the board state
		board = new Board(WIDTH, HEIGHT + TOP_SPACE);

		repaint();

	}



	/**
	 Given a piece, tries to install that piece
	 into the board and set it to be the current piece.
	 Does the necessary repaints.
	 If the placement is not possible, then the placement
	 is undone, and the board is not changed. The board
	 should be in the committed state when this is called.
	 Returns the same error code as Board.place().
	 */
	public int setCurrent(Piece piece, int x, int y) {
		int result = board.place(piece, x, y);

		if (result <= Board.PLACE_ROW_FILLED) { // SUCESS
			// repaint the rect where it used to be
			if (currentPiece != null) repaintPiece(currentPiece, currentX, currentY);
			currentPiece = piece;
			currentX = x;
			currentY = y;
			// repaint the rect where it is now
			repaintPiece(currentPiece, currentX, currentY);
		}
		else {
			board.undo();
		}

		return(result);
	}


	/**
	 Selects the next piece to use using the random generator
	 set in startGame().
	 */


	/**
	 Updates the count/score labels with the latest values.
	 */



	/**
	 Figures a new position for the current piece
	 based on the given verb (LEFT, RIGHT, ...).
	 The board should be in the committed state --
	 i.e. the piece should not be in the board at the moment.
	 This is necessary so dropHeight() may be called without
	 the piece "hitting itself" on the way down.

	 Sets the ivars newX, newY, and newPiece to hold
	 what it thinks the new piece position should be.
	 (Storing an intermediate result like that in
	 ivars is a little tacky.)
	 */
	public void computeNewPosition(int verb) {
		// As a starting point, the new position is the same as the old
		newPiece = currentPiece;
		newX = currentX;
		newY = currentY;

		// Make changes based on the verb
		switch (verb) {
		case LEFT: newX--; break;

		case RIGHT: newX++; break;

		case ROTATE:
			newPiece = newPiece.fastRotation();

			// tricky: make the piece appear to rotate about its center
			// can't just leave it at the same lower-left origin as the
			// previous piece.
			newX = newX + (currentPiece.getWidth() - newPiece.getWidth())/2;
			newY = newY + (currentPiece.getHeight() - newPiece.getHeight())/2;
			break;

		case DOWN: newY--; break;

		case DROP:
			newY = board.dropHeight(newPiece, newX);

			// trick: avoid the case where the drop would cause
			// the piece to appear to move up
			if (newY > currentY) {
				newY = currentY;
			}
			break;

		default:
			throw new RuntimeException("Bad verb");
		}

	}




	public static final int ROTATE = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int DROP = 3;
	public static final int DOWN = 4;
	/**
	 Called to change the position of the current piece.
	 Each key press calls this once with the verbs
	 LEFT RIGHT ROTATE DROP for the user moves,
	 and the timer calls it with the verb DOWN to move
	 the piece down one square.

	 Before this is called, the piece is at some location in the board.
	 This advances the piece to be at its next location.




	/**
	 Given a piece and a position for the piece, generates
	 a repaint for the rectangle that just encloses the piece.
	 */
	public void repaintPiece(Piece piece, int x, int y) {
		if (DRAW_OPTIMIZE) {
			int px = xPixel(x);
			int py = yPixel(y + piece.getHeight() - 1);
			int pwidth = xPixel(x+piece.getWidth()) - px;
			int pheight = yPixel(y-1) - py;

			repaint(px, py, pwidth, pheight);
		}
		else {
			// Not-optimized -- rather than repaint
			// just the piece rect, repaint the whole board.
			repaint();
		}
	}


	/*
	 Pixel helpers.
	 These centralize the translation of (x,y) coords
	 that refer to blocks in the board to (x,y) coords that
	 count pixels. Centralizing these computations here
	 is the only prayer that repaintPiece() and paintComponent()
	 will be consistent.

	 The +1's and -2's are to account for the 1 pixel
	 rect around the perimeter.
	 */


	// width in pixels of a block
	private final float dX() {
		return( ((float)(getWidth()-2)) / board.getWidth() );
	}

	// height in pixels of a block
	private final float dY() {
		return( ((float)(getHeight()-2)) / board.getHeight() );
	}

	// the x pixel coord of the left side of a block
	private final int xPixel(int x) {
		return(Math.round(1 + (x * dX())));
	}

	// the y pixel coord of the top of a block
	private final int yPixel(int y) {
		return(Math.round(getHeight() -1 - (y+1)*dY()));
	}


	/**
	 Draws the current board with a 1 pixel border
	 around the whole thing. Uses the pixel helpers
	 above to map board coords to pixel coords.
	 Draws rows that are filled all the way across in green.
	 */
	@Override
	public void paintComponent(Graphics g) {

		// Draw a rect around the whole thing
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);


		// Draw the line separating the top
		int spacerY = yPixel(board.getHeight() - TOP_SPACE - 1);
		g.drawLine(0, spacerY, getWidth()-1, spacerY);


		// check if we are drawing with clipping
		//Shape shape = g.getClip();
		Rectangle clip = null;
		if (DRAW_OPTIMIZE) {
			clip = g.getClipBounds();
		}


		// Factor a few things out to help the optimizer
		final int dx = Math.round(dX()-2);
		final int dy = Math.round(dY()-2);
		final int bWidth = board.getWidth();

		int x, y;
		// Loop through and draw all the blocks
		// left-right, bottom-top
		for (x=0; x<bWidth; x++) {
			int left = xPixel(x);	// the left pixel

			// right pixel (useful for clip optimization)
			int right = xPixel(x+1) -1;

			// skip this x if it is outside the clip rect
			if (DRAW_OPTIMIZE && clip!=null) {
				if ((right<clip.x) || (left>=(clip.x+clip.width))) continue;
			}

			// draw from 0 up to the col height
			final int yHeight = board.getColumnHeight(x);
			for (y=0; y<yHeight; y++) {
				if (board.getGrid(x, y)) {
					boolean filled = (board.getRowWidth(y)==bWidth);
					if (filled) g.setColor(Color.green);

					g.fillRect(left+1, yPixel(y)+1, dx, dy);	// +1 to leave a white border

					if (filled) g.setColor(Color.black);
				}
			}
		}
	}


	/**
	 Updates the timer to reflect the current setting of the
	 speed slider.
	 */
	public void updateTimer() {
		double value = ((double)speed.getValue())/speed.getMaximum();
		timer.setDelay((int)(DELAY - value*DELAY));
	}



	@Override
	public void run() {
		System.out.println("I am started by run()");
		startGame();
		Board proBoard = null;
		Object proObj = null;
		int i = 0;
		while (true) {
			try {
				proObj = recIn.readObject();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (proObj.getClass().equals(Board.class)) {
				System.out.println("I am in the update board loop" + i);
				proBoard = (Board) proObj;
				this.board.updateBoard(proBoard);
				//this.board = proBoard;

				repaint();
	
			}	
			i = i + 1;
		}
		
	}
}