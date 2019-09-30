package ChattingSystemWithGuiClientWorkingV1;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ChatClientGui {

	private JFrame frame;

	private ClientConnection connection;

	private JTextField tf2;

	private JTextArea display;
	private FlowLayout layout;
	private Container container;
	private JPanel panel;
	private JScrollPane scroll;

	private JList<String> colourList;
	private static String[] colourNames = { "white", "light gray", "gray", "dark gray", "black" };
	private static Color[] colors = { Color.WHITE, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY, Color.BLACK };

	private boolean msgSent = false;
	private String outPutVal = "";
	private String msg = "";

	private BufferedReader br;
	private BufferedWriter bw;

	public ChatClientGui(String title, ClientConnection connection, BufferedReader BR, BufferedWriter BW) {
		this.connection = connection;
		this.br = BR;
		this.bw = BW;
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				connection.getClient().forcedTermination();
				connection.disconnect();
				System.exit(0);

			}
		});
		frame.setVisible(true);
		layout = new FlowLayout(FlowLayout.CENTER);
		frame.setLayout(layout);
		frame.setLocationRelativeTo(null);
		container = frame.getContentPane();

		panel = new JPanel();
		panel.setBorder(new EtchedBorder());

		// create the middle panel components

		display = new JTextArea(16, 58);
		display.setEditable(false); // set textArea non-editable
		JScrollPane scroll = new JScrollPane(display);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		// Add Textarea in to middle panel
		panel.add(scroll);

		frame.add(panel);

		colourList = new JList<String>(colourNames);
		colourList.setVisibleRowCount(4);
		colourList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// can only select 1 thing at a time
		panel.add(new JScrollPane(colourList));

		colourList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				// sets the background colour to be the index of the selected list item
				frame.getContentPane().setBackground(colors[colourList.getSelectedIndex()]);
				display.setBackground(colors[colourList.getSelectedIndex()]);
				panel.setBackground(colors[colourList.getSelectedIndex()]);
				if (colourList.getSelectedIndex() > 2) {// if the background is dark make text white and if the
														// background is light make the text black
					display.setForeground(colors[0]);
					panel.setForeground(colors[0]);
				} else {
					display.setForeground(colors[4]);
					panel.setForeground(colors[4]);
				}
			}
		});

		tf2 = new JTextField(14);
		tf2.setFont(new Font("Serif", Font.BOLD, 20));
		frame.add(tf2, FlowLayout.CENTER);

		thehandler handler = new thehandler();
		tf2.addActionListener(handler);

		frame.pack();

		display.append("Welcome to the chat server\nPlease choose a username...\n");
		int count = 1;//count used to keep track of the number of inputs given so that after the username is selected other messages can be given.
		try {
			while (true) {
				if (count < 3) {
					if (count == 1) {
						count++;
					} else if (count == 2) {
						display.append("You can disconnect by typing #Disconnect or by closing the window\n\n");
						count+=34;
					}
				}
				display.append(br.readLine() + "\n");

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getMsg() {// waits for an object to pick up the sent value
		if (!msgSent) {
			return msg;
		}
		return "";
	}

	private class thehandler implements ActionListener {

		// method that handles the event
		public synchronized void actionPerformed(ActionEvent event) {

			outPutVal = "";
			if (event.getSource() == tf2) {
				outPutVal = String.format("%s", event.getActionCommand());
				tf2.setText("");
				msg = outPutVal;
				try {
					if (msg.equals("#Disconnect")) {
						bw.write(msg + "\n");
						bw.flush();
						connection.getClient().forcedTermination();
						connection.disconnect();
						System.exit(0);
					} else {
						bw.write(msg + "\n");
						bw.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}