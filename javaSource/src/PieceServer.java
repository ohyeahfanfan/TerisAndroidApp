import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Random;

public class PieceServer {
	final static int PORT = 2222;

	public static void main(String args[]) throws IOException {
		intQueue intQue = new intQueue();
		ServerSocket server = null;
		Socket client1 = null;
		Socket client2 = null;
		Socket client11 = null;
		Socket client22 = null;


		server = new ServerSocket(PORT);
		client1 = server.accept();
		client11 = server.accept();
		client2 = server.accept();
		client22 = server.accept();
		//client11 = server.accept();
		//System.out.println("I have accepted client1");
		//client2 = server.accept();
		//System.out.println("I have accepted");
		//client.setTcpNoDelay(true);

		//InputStreamReader input = null;
		//OutputStreamWriter output = null;
			

		//output = new OutputStreamWriter(client.getOutputStream());
		ServerThread t1 = new ServerThread(client1, client22, intQue);
		ServerThread t2 = new ServerThread(client2, client11, intQue);
		t1.start();
		t2.start();
	}
	
	
}


class ServerThread extends Thread {
	private Socket receiver;
	private Socket sender;

	public static final int CODE_LENGTH = 6;
	private intQueue intQue;
	public static final int QUEUE_LENGTH = 5;

	
	public ServerThread(Socket receiver, Socket sender, intQueue queue) {
		this.receiver = receiver;
		this.sender = sender;	
		this.intQue = queue;
	}




	public void run() {
		int i;
		ObjectInputStream recIn = null;
		ObjectOutputStream recOut = null;
		ObjectOutputStream seOut = null;
		try {
			recIn = new ObjectInputStream(receiver.getInputStream());
			recOut = new ObjectOutputStream(receiver.getOutputStream());
			seOut = new ObjectOutputStream(sender.getOutputStream());
		} catch (IOException e) {			
			//e.printStackTrace();
		}
		//System.out.println("I am in server thread");
		char[] initTemp;
		String initString = null;
		try {
			initString = (String)recIn.readObject();
			System.out.println(initString);

		} catch (IOException e) {
			
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} 
		initTemp = initString.toCharArray();
		System.out.println(initTemp);
		String initMsg = null;
		if (initTemp[0] == 'a') {
			System.out.println("I received aaa");
			initMsg = 'b' + Arrays.toString(intQue.intQue);

			System.out.println(initMsg);
			try {
				recOut.writeObject(initMsg);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}
		int j;
		Object proObj = null;
		String proString = null;
		int[] proIntArray = new int[CODE_LENGTH];
		String sendMsg = null;
		Board proBoard = null;
		while (true) {
			try {
				proObj = recIn.readObject();
			} catch (ClassNotFoundException | IOException e1) {}
			if (proObj.getClass().equals(String.class)) {
				proString = (String)proObj;

				if (proString.charAt(0) == 'c') {
					proIntArray[0] = intQue.getNextInt();
					for (i = 0; i < QUEUE_LENGTH; i ++) {
						proIntArray[i + 1] = intQue.intQue[i];
					}
					sendMsg = Arrays.toString(proIntArray);
					try {
						recOut.writeObject(sendMsg);
					} catch (IOException e) {}
				}
			}
			else if (proObj.getClass().equals(Board.class)) {
				proBoard = (Board)proObj;
				try {
					seOut.writeObject(proBoard);
				} catch (IOException e) {}				
			}

		}	
	}		
}
	


class intQueue {
	protected int[] intQue;
	private Random random;
	private int QUEUE_LENGTH = 5;
	
	public intQueue () {
		int pieceNum;
		this.intQue = new int[QUEUE_LENGTH];
		this.random = new Random();
        for (int i = 0; i < QUEUE_LENGTH; i ++) {
    		pieceNum = (int) (7 * random.nextDouble());
    		this.intQue[i] = pieceNum;
        }
	}
	
	public synchronized int getNextInt() {
		int i;
		int temp = intQue[0];
		for (i = 0; i < QUEUE_LENGTH - 1; i++) {
			intQue[i] = intQue[i + 1];
		}
		intQue[i] = (int) (7 * random.nextDouble());
		return temp;
	}
	
}