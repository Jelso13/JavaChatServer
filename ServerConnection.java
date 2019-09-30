package ChattingSystemWithGuiClientWorkingV1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

//serverConnection class allows the server to create threads for each connection to multiple client users displaying messages sent to the server for each client.
public class ServerConnection implements Runnable {

	private Socket sock; // socket for connection
	// static ArrayList of BufferedWriters to allow access to all instances
	private static ArrayList<BufferedWriter> BWConnections = new ArrayList<BufferedWriter>();
	private static ArrayList<String> userNamesAL = new ArrayList<String>();
	private boolean threadActive;
	private BufferedWriter bw;
	private String userName; // client username

	// ServerConnection constructor that specifies the socket to be used
	public ServerConnection(Socket sock) {
		this.sock = sock;
		threadActive = true;
	}

	// public accessor method that returns the username of the client.
	public String getUserName() {
		return userName;
	}

	// method that allows the server to stop the connection thread
	public synchronized void stopThread() {
		threadActive = false;
	}
	
	//method allows the chatServer to determine what client connections are currently still running 
	public synchronized boolean getThreadStatus() {
		return threadActive;
	}

	// synchronized method that adds the buffered reader to the connections
	// arrayList
	// method is synchronized so that multiple threads trying to add to the
	// connections arrayList do not interfere with eachother
	private synchronized void addBR() {
		BWConnections.add(bw);
	}

	// method that prints a users message to the server
	private synchronized void printToEveryone(String message) {// actually prints to everyone other than the original
																// sender of the message.
		for (int i = 0; i < BWConnections.size(); i++) {
			if (!(BWConnections.get(i) == bw)) {// determines if the bufferedWriter is the writer that belongs to the
												// message sender and does not send the message to the origional sender.
				try {
					BWConnections.get(i).write(message + "\n");
					BWConnections.get(i).flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					String tempMessage = "You" + message.substring(userName.length());
					BWConnections.get(i).write(tempMessage + "\n");
					BWConnections.get(i).flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println(message);// prints the message recieved from the user to the server
	}
	
	public void endConnection() {
		threadActive = false;
	}
	//prints server messages to all clients
	private synchronized void serverPrintAll(String message) {
		for (int i = 0; i < BWConnections.size(); i++) {
			try {
				BWConnections.get(i).write("SERVER: " + message + "\n");
				BWConnections.get(i).flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("SERVER: " + message);
	}

	// method that disconnects a client found by comparing the buffered writer for
	// all users in the buffered reader arraylist to the buffered writer of the
	// current connection then closes that clients connections disconnecting them
	// from the server.
	private synchronized void disconnectClient() {
		for (int i = 0; i < BWConnections.size(); i++) {
			if (BWConnections.get(i) == bw) {// finds the correct user to disconnect
				userNamesAL.remove(i);//remove the user from the usernames arraylist so that they can re-login and their username isnt flagged as being taken.
				try {
					bw.close(); // closes the BufferedWriter and socket
					sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				BWConnections.remove(i); // removes the connection from the ArrayList
				break;
			}
		}
		serverPrintAll(String.format("%s disconnected", userName)); // displays that the user has disconnected
																	// from the server to all other users.
	}
	
	private synchronized boolean userNameVerified(String userName) {
		//stops the user from choosing usernames that are invalid such as too large or small or consist of reserved keywords.
		if(userName.equalsIgnoreCase("you") || userName.equalsIgnoreCase("server") || userName.equalsIgnoreCase("#Disconnect") || userName.equalsIgnoreCase("#EXIT") || userName.length()<3 ||userName.length()>20) {
			return false;
		}
		for(String name : userNamesAL) {//iterates through all the currently connected usernames seeeing if the requested username can be found
			if(userName.equals(name)) {
				return false;
			}
		}
		return true;
	}

	// method that runs the thread for the server connection that listens to the 
	// client messages to be broadcast through the server.
	public void run() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			addBR(); // adds BufferedReader to the BRConnections ArrayList
			userName = br.readLine(); // gets the clients username
			while(!userNameVerified(userName)) {
				bw.write("failed to set username...\nEither the username is already taken,\nusername is not of correct length (2<length<21)\nor your name is a forbidden username\n");
				bw.flush();
				userName = br.readLine();
			}
			userNamesAL.add(userName);
			serverPrintAll(String.format("%s connected", userName)); // displays that the user has connected to
																		// the server, to all other users.

			while (threadActive) {// while the thread is running...
				String userInput = br.readLine();
				// determines if the user wishes to disconnect in which case the thread will
				// eventually terminate if the user does not want to disconnect then the message
				// they type will be sent to all other users with the prefix of the senders
				// username.
				if (userInput != null && !userInput.equals("#Disconnect")) {
					printToEveryone(userName + ": " + userInput); //calls the printToEveryone method that sends the message to everyone connected to the server
				} else {
					threadActive = false; // if the user wants to disconnect then break from the while loop deactivating the thread
				}
				Thread.sleep(250);
			}

		} catch (SocketException e) {
			// Not much to do here, finally block will handle it
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			
			disconnectClient(); // Disconnects client
			this.stopThread();
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
