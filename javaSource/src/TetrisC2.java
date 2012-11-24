import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class TetrisC2 {
	private static int PORT = 2222;
	private static int LISTEN_PORT = 2223;
	private static String HOST = "localhost";
	public static final int CODE_LENGTH = 6;
	public static final int QUEUE_LENGTH = 5;
	
	public static void main(String args[]) throws IOException {
		Socket server = null;
		Socket otherSock = null;

		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		
		try {
			server = new Socket(HOST, PORT);
			System.out.println("TetrisC2 1 successfully connect to server");
			otherSock = new Socket(HOST, PORT);
			System.out.println("TetrisC2 2 successfully connect to server");
			out = new ObjectOutputStream(server.getOutputStream());
			in = new ObjectInputStream(server.getInputStream());
		}
		catch(UnknownHostException e) {
			System.err.println(e);
		}
		//server.setTcpNoDelay(true);
		String initReq = "aaaaaa";		
		//System.out.println("I have sent sendtemp");
		System.out.println(initReq);
		out.writeObject(initReq);
		//System.out.println("send request sucessfully");
		
		String world = null;
		try {
			world = (String)in.readObject();
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(world);
		char recChar = world.charAt(0);
		String[] receivetemp = world.substring(2, 15).split(", ");
		int[] sharedInt = new int[QUEUE_LENGTH];
		if (recChar == 'b') {
			for (int i = 0; i < QUEUE_LENGTH; i++) {
				sharedInt[i] = Integer.parseInt(receivetemp[i]);
				System.out.println(sharedInt[i]);
			}
		}
		


//		String inputLine = null;
//		BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
//		in.readLine();
//		System.out.println(in);

		// Set GUI Look And Feel Boilerplate.
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		JTetrisOther tetris = new JTetrisOther(16, in, out, sharedInt);
		JReceiver receiver = new JReceiver(16, otherSock);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		JFrame frame = new JFrame("Tetris");
		JComponent container = (JComponent)frame.getContentPane();
		container.setLayout(new BorderLayout());
				
		
		
		container.add(tetris, BorderLayout.WEST);
        container.add(receiver, BorderLayout.EAST);
		container.add(tetris.sharedQueue, BorderLayout.CENTER);
		// Create and install the panel of controls.
		JComponent controls = tetris.createControlPanel();
		container.add(controls, BorderLayout.SOUTH);

		// Add the quit button last so it's at the bottom
		controls.add(Box.createVerticalStrut(12));
		JButton quit = new JButton("Quit");
		controls.add(quit);
		quit.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();

		frame.setVisible(true);
		
  	     
	    Thread t1 = new Thread(tetris);
	    t1.start();
	    Thread t2 = new Thread(receiver);
	    t2.start();
	}
}



