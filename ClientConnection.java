package ChattingSystemWithGuiClientWorkingV1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

//client connection class that runs a thread for each client that listens to the server.
public class ClientConnection implements Runnable {

	private Socket sock;
	private ChatClient userClient;
	private BufferedReader br;
	private BufferedWriter bw;
	private boolean threadActive;
	ChatClientGui gui;
	private boolean guiActive = false;
	String userName;

	// client connection constructor
	public ClientConnection(ChatClient cClient) {
		this.userClient = cClient; // assigns the ChatClient object the client connection is assigned to
		threadActive = true;// variable determines if the thread should be running.
		guiActive =false;
	}
	


	public ClientConnection(ChatClient cClient, int port, String address) {
		this.guiActive = true;
		this.userClient = cClient;
		this.userName = userName;
		threadActive = true;
		connect(address, port);
		this.gui = new ChatClientGui("Client",this,br,bw);
		
		new Thread(this).start();

	}
	
	public ChatClient getClient() {
		return userClient;
	}

	// tries to connect the client to the server, if the connection works then the
	// method returns true and if the connection doesnt work then the method returns
	// false so that the client can handle the failed connection and can give the
	// user the option to try again. Synchronised so that mulitple client requests
	// do not interfere with eachother.
	public synchronized String connect(String address, int port) {
		try {
			sock = new Socket(address, port); // attempts to make a connection to the server using the address and port.
			br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			return "connected";
		} catch (ConnectException e) {
			return e.getMessage();
		} catch (UnknownHostException e) {
			return e.getMessage();
		} catch (IOException e) {
			return e.getMessage();
		}
	}

	// writes the client username to the datastream.
	public void setUserName(String username) {
		try {
			bw.write(username + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// changes the threadActive variable to be false so that the main loop for the
	// thread will be exited and the connection to the server will be closed.
	public synchronized void disconnect() {
		threadActive = false;
	}

	// method to send message to the server to be printed to all other clients and
	// the server.
	public synchronized void sendMsg(String message) {
		try {
			bw.write(message + "\n");
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			if (guiActive) {
				//setUserName(userName);
				String clientOut = "";
				while (threadActive) {
					clientOut = br.readLine();//maybe move this infront of the rest
					String clientOutString = gui.getMsg();

					
					//gui.recieveMsg(clientOut);
					Thread.sleep(250);
				}
			} else {
				String clientOut = "";

				while (threadActive == true) {// main loop for active connection thread
					clientOut = br.readLine();
					if (clientOut == null) {
						threadActive = false;
					}
					userClient.clientPrintln(clientOut);// prints the clientOut value to the client.
					Thread.sleep(250);
				}
			}
			bw.close();// closes the BufferedWriter once the connection thread is no longer needed.
		} catch (SocketException e) {
			userClient.clientPrintln("Connection to server has been lost");
			userClient.forcedTermination();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				bw.close();
				br.close();
				sock.close();
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
