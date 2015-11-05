package main;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class UserInterface extends JFrame{

	private javax.swing.JTextArea displayArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField msgField;
    private javax.swing.JButton sendButton;
    
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
        displayArea = new javax.swing.JTextArea();
        msgField = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();
        JLabel jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        displayArea.setEditable(false);
        displayArea.setColumns(20);
        displayArea.setFont(new java.awt.Font("Arial", 0, 16));
        displayArea.setRows(5);
        displayArea.setMargin(new Insets(4, 4, 4, 4));
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
	}

	private void initKatie() {
		showText("Hi!");		
	}

	protected void sendMessage(String text) {
		if(text.isEmpty()) return;
		
		displayArea.setText(displayArea.getText() + "\n" + user_name+": " + text);
		msgField.setText("");
		process(text);
	}

	private void process(String text) {
		//tokenize
		process.tokenize(text);
		
		String line = process.processFromDB(text);
				
		System.out.println(line);

		//if(!tok.isEmpty())
		showText(line);

		String output = process.getSentiment(text);
		
		//showText("You look "+output+".");
	}

	private void showText(String text) {
		displayArea.setText(displayArea.getText() + "\nKatie: " + text);
	}
	
	
	
}
