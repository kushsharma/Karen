package main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class UserInterface extends JFrame{

	private javax.swing.JTextPane displayArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField msgField;
    private javax.swing.JButton sendButton;
    //private javax.swing.JTextPane screenText;
    
    String user_name = "You";
    Processor process;
    
	public UserInterface(Processor pro){
		process = pro;
		
		initEverything();
		this.setTitle("Karen");
		this.setVisible(true);		
	}

	private void initEverything() {
		jScrollPane1 = new javax.swing.JScrollPane();
        displayArea = new javax.swing.JTextPane();
        //screenText =  new javax.swing.JTextPane();
        msgField = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();
        JLabel jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        //displayArea.setEditable(false);
        displayArea.setFont(new java.awt.Font("Arial", 0, 16));
        displayArea.setMargin(new Insets(4, 4, 4, 4));
        displayArea.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        //displayArea.setContentType("text/html");
        //tPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        //screenText.setMargin(new Insets(5, 5, 5, 5));
        
        jScrollPane1.setViewportView(displayArea);

        msgField.requestFocusInWindow(); 
        msgField.setMargin(new Insets(2, 2, 2, 2));
        msgField.addKeyListener(new KeyListener(){

			public void keyTyped(KeyEvent e) {				
			}

			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					sendMessage(msgField.getText());
				}
			}

			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
        	
        });

        sendButton.setText("Send");
        sendButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				sendMessage(msgField.getText());				
			}
        	
        });
        
        jLabel1.setText("Message");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 702, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(msgField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(msgField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(27, 27, 27))
        );

        pack();
		
        
        initKatie();
        
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				msgField.grabFocus();
				msgField.requestFocus();// or inWindow
			}
		});
	}

	private void initKatie() {
		showText("Hi!","Karen");		
	}

	protected void sendMessage(String text) {
		if(text.isEmpty()) return;
		
		//displayArea.setText(displayArea.getText() + "\n" + user_name+": " + text);
		showText(text, user_name);
		
		msgField.setText("");
		process(text);
		
		msgField.grabFocus();
		msgField.requestFocus();
	}

	private void process(String text) {
		//tokenize
		process.tokenize(text);
		
		String line = process.processFromDB(text);
				
		System.out.println(line);

		//if(!tok.isEmpty())
		String senti = process.getSentiment(text);

		showText(line, "Karen",senti);

		//showText("You look "+output+".");
	}

	private void showText(String text, String name) {
		showText(text, name, "Neutral");
	}

	private void showText(String text, String name, String senti) {

		if(senti.equals("Positive")){
			appendToPane(displayArea, text, name, Color.GREEN);			
		}else if(senti.equals("Negative")){
			appendToPane(displayArea, text, name, Color.RED);
		}else if(senti.equals("Very Positive")){
			appendToPane(displayArea, text, name, Color.ORANGE);
		}else if(senti.equals("Very Negative")){
			appendToPane(displayArea, text, name, Color.PINK);
		}
		else
		{
			appendToPane(displayArea, text, name, Color.BLACK);
		}
		
		
		//neutral
		//displayArea.setText(displayArea.getText() + "\nKatie: " + text);
	}
	
	private void appendToPane(JTextPane tp, String msg, String name, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        
        aset = sc.addAttribute(aset, StyleConstants.Bold, true);
        
        if(name.equals("Karen"))
            aset = sc.addAttribute(aset, StyleConstants.Italic, true);
        else
            aset = sc.addAttribute(aset, StyleConstants.Italic, false);

        
        //set name
        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection("\n"+name+": ");
        
        aset = sc.addAttribute(aset, StyleConstants.Bold, false);
        aset = sc.addAttribute(aset, StyleConstants.Italic, false);
        //set msg
        len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
	
	
}
