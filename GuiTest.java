package ChattingSystemWithGuiClientWorkingV1;

import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;

public class GuiTest extends JFrame{
	
	private JLabel item1;
	private JTextField tf1;
	private JTextField tf2;
	private JTextField tf3;
	private JPasswordField pwdField;
	
	private JButton reg;
	private JButton custom;
	
	private JTextField textField;
	private JCheckBox boldBox;
	private JCheckBox italicBox;
	
	private JButton lb;
	private JButton cb;
	private JButton rb;
	private FlowLayout layout;
	private Container container;
	
	private JList colourList;
	private static String[] colourNames = {"red","pink","green","blue","white"};
	//american spelling of colour
	private static Color[] colors = {Color.RED, Color.PINK,Color.GREEN,Color.BLUE,Color.WHITE}; 
	
	public GuiTest() {
		super("The title bar");
		layout = new FlowLayout();
		container = getContentPane();
		setLayout(layout);
		
		colourList = new JList(colourNames);
		colourList.setVisibleRowCount(4);
		colourList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//can only select 1 thing at a time
		add(new JScrollPane(colourList));
		
		colourList.addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						//sets the background colour to be the index of the selected list item
						getContentPane().setBackground(colors[colourList.getSelectedIndex()]);
					}
				});
		
		reg = new JButton("this is a button");
		add(reg);
		
		item1 = new JLabel("this is a sentence");
		item1.setToolTipText("this is gonna show up on hover over");
		add(item1);
		
		textField = new JTextField("This is a text box ting",20);
		textField.setFont(new Font("Serif", Font.PLAIN,14));
		textField.setSize(300, 300);
		add(textField);
		
		boldBox = new JCheckBox("checkBox description bold");
		add(boldBox);
		italicBox = new JCheckBox("checkbox description italic");
		add(italicBox);
		
		tf1 = new JTextField(10);
		add(tf1);
		tf2 = new JTextField("Enter text here");
		add(tf2);
		tf3 = new JTextField("uneditable",20);
		tf3.setSize(300, 300);
		tf3.setEditable(false);
		add(tf3);
		
		pwdField = new JPasswordField("mypassword");
		add(pwdField);
		
		secondHandler handler2 = new secondHandler();
		boldBox.addItemListener(handler2);
		italicBox.addItemListener(handler2);
		
		lb = new JButton("Left");
		add(lb);
		lb.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						layout.setAlignment(FlowLayout.LEFT);//left alignment
						layout.layoutContainer(container);
					}
				});
		
		cb = new JButton("Center");
		add(cb);
		cb.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						layout.setAlignment(FlowLayout.CENTER);//left alignment
						layout.layoutContainer(container);
					}
				});
		
		rb = new JButton("Right");
		add(rb);
		rb.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						layout.setAlignment(FlowLayout.RIGHT);//left alignment
						layout.layoutContainer(container);
					}
				});
		
		thehandler handler = new thehandler();
		reg.addActionListener(handler);
		tf1.addActionListener(handler);
		tf2.addActionListener(handler);
		tf3.addActionListener(handler);
		pwdField.addActionListener(handler);
		
	}
	
	private class secondHandler implements ItemListener{
		public void itemStateChanged(ItemEvent event) {
			Font font = null;
			
			if(boldBox.isSelected() && italicBox.isSelected()) {
				font = new Font("Serif", Font.BOLD + Font.ITALIC, 14);
			}
			else if(boldBox.isSelected()) {
				font = new Font("Serif", Font.BOLD, 14);
			}
			else if(italicBox.isSelected()) {
				font = new Font("Serif", Font.ITALIC, 14);
			}
			else {
				font = new Font("Serif",Font.PLAIN,14);
			}
			
			textField.setFont(font);
		}
	}
	
	private class thehandler implements ActionListener{
		
		//method that handles the event (MUST BE CALLED actionPerformed)
		public void actionPerformed(ActionEvent event) {
			
			String outputVal = "";
			
			if(event.getSource() == tf1) {
				outputVal = String.format("field 1: %s",event.getActionCommand());
			}
			else if(event.getSource() == tf2) {
				outputVal = String.format("field 2: %s",event.getActionCommand());
			}
			else if(event.getSource() == tf3) {
				outputVal = String.format("field 3s: %s",event.getActionCommand());
			}
			else if(event.getSource() == pwdField) {
				outputVal = String.format("password is: %s", event.getActionCommand());
			}
			else if(event.getSource() == reg) {
				outputVal = String.format("button pressed is: %s", event.getActionCommand());
			}
			
			JOptionPane.showMessageDialog(null, outputVal);
		}
		
	}
	
	//delete this
	public static void main(String[] args) {
		
		String fn = JOptionPane.showInputDialog("Enter first number");
		String sn = JOptionPane.showInputDialog("Enter second number");
		
		int num1 = Integer.parseInt(fn);
		int num2 = Integer.parseInt(sn);
		int sum = num1+num2;
		
		if(sum==6) {
			GuiTest ccg = new GuiTest();
			ccg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			ccg.setSize(350,100);
			ccg.setVisible(true);
			
		}
		
		JOptionPane.showMessageDialog(null, "The answer is: "+sum, "the title", JOptionPane.PLAIN_MESSAGE);
		
	}
}