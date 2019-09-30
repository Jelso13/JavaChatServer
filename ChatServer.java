package ChattingSystemWithGuiClientWorkingV1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ChatServer implements Runnable {

	private ServerConnection sConnection;
	private static ArrayList<ServerConnection> serverConnectionsAL; // connections arraylist for disconnecting clients
	private static ServerSocket sock; // ServerSocket object used to create the socket
	private static boolean threadActive;// value to determine whether the server thread is running
	int port; // the integer value of the port

	// ChatServer constructor specifies the port number
	public ChatServer(int port) {
		this.port = port;
		try {
			sock = new ServerSocket(port); // instantiates ServerSocket object with the required port
			new Thread(this).start();// calls the run method of the ChatServer object as a new thread.
			threadActive = true;
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	// closes connections to the clients, synchonized so that different threads
	// closing connections dont interfere with eachother
	private synchronized void closeConnections() {
		for (int i = 0; i < serverConnectionsAL.size(); i++) {
			serverConnectionsAL.get(i).endConnection();//makes the connections threadActive variable false so that it can stop itself running.
			//serverConnectionsAL.get(i).stopThread();
		}
		try {
			sock.close(); // closes the socket
			//sConnection.
			System.exit(0); // terminates the chat server
		} catch (IOException e) {
			e.printStackTrace();
		}
		threadActive = false;
	}

	// method used to determine if the 'EXIT' command from the server is actually
	// desired before terminating the server and closing all connections.
	public static boolean confirmServerExit(BufferedReader br) {
		String exitChoice = "";
		try {
			System.out.println("Are you sure you want to exit and terminate the server...?");
			while (true) {
				System.out.println("Type 'Y' for yes and type 'N' for no");
				exitChoice = br.readLine();
				if (exitChoice.equals("Y")) {
					return true;
				} else if (exitChoice.equals("N")) {
					return false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void run() {
		serverConnectionsAL = new ArrayList<ServerConnection>(); // initialise the connections ArrayList
		try {
			while (threadActive == true) {
				Socket connectionSocket = sock.accept();
				// Server displays that the connection to the server was successful
				System.out.printf("Server connection accepted on sockets: %s to %s\n", sock.getLocalPort(),
						connectionSocket.getPort());
				sConnection = new ServerConnection(connectionSocket); // creates a new ServerConnection object assigning
																		// the value to the sConnection variable
				// addConnection(sConnection); // ServerConnection object to arraylist so
				// threads can be handled
				serverConnectionsAL.add(sConnection);
				new Thread(sConnection).start(); // calls the run method in the ServerConnection object to start a new
													// thread to control that connection.
				Thread.sleep(250);
			}
		} catch (SocketException e) {
			System.out.println("Server terminated");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			closeConnections(); // closes the connection to the clients once the chatServer no longer exists.
		}
	}

	// main method checks first checks for changes to the port number, creates the
	// chatServer object with the desired port
	// then waits until the server command 'EXIT' is used that indicates the server
	// should be closed, if the 'EXIT' command is found
	// then the connections to the server are closed and then the ChatServer object
	// is terminated.
	public static void main(String[] args) {
		int port = 14001;// default port value
		if (args.length == 2) {// determines if the change server port command has been chosen and changes the
								// port value as required.
			if (args[0].equals("-csp")) {
				port = Integer.parseInt(args[1]);
			}
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try {
			String serverInput = "";
			ChatServer cs = new ChatServer(port); // Creates ChatServer object
			serverConnectionsAL = new ArrayList<ServerConnection>();
			System.out.println("Server started...\nPlease type '#EXIT' to close the server");
			System.out.println("Available commands:\nshowClients");

			while (!serverInput.equals("#EXIT")) {
				serverInput = br.readLine();
				if (serverInput.equals("#EXIT")) {
					if (confirmServerExit(br) == false) {
						serverInput = "";// resets the value of serverInput to "" so that it doesnt exit the while loop
					}
					else {
						for(ServerConnection sc : serverConnectionsAL) {
							sc.endConnection();
						}
					}
				}
				// displays all users currently connected
				if (serverInput.equals("showClients")) {
					for (ServerConnection sc : serverConnectionsAL) {
						if (sc.getThreadStatus()) {
							System.out.println(sc.getUserName());
						}
					}
				}
			}
			cs.closeConnections();// close the connections to the server once the user has

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
