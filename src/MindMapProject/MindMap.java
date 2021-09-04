package MindMapProject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.LinkedList;

/* MindMap Ŭ���� �� ����δ�...
 * 9���� ��� ����,
 * �ڹ��� GUI�� ���Ǵ� 5���� ��� Ŭ����, 
 * �̺�Ʈ�����ʷ� ����� 7���� ��� Ŭ����,
 * JSON ����, �ҷ����⸦ �ϴ� �޼ҵ� 2��,
 * ���ε���� �����ϴ� ������ 1��,
 * main �޼ҵ� 1���� �����Ǿ��ֽ��ϴ�. */
public class MindMap extends JFrame {

	private Controler mTree = new Controler();
	private MyTextArea tEditor;
	private MyAtrpanel atrPane;
	private MindPanel mMapPane;  
	private MyMenuBar menuBar;
	private MyToolBar toolBar;

	private Container c; 		 		  // MindMap ������ ��Ī �ϵ��� ���� �����̳�.
	private String EditorText = null;	 // TextArea�� �ϼ��� ���ڿ� ���¸� ������ �ִ� ���ڿ�.
	private String ourPath = null; 		// JSON ������ ���� ���� ���¶�� �� ��δ� null�̰�, �ִٸ� �� ��� ���ڿ��̴�.
	//----------------------------------------------
	
	public MindMap() {
		setTitle("���ε� ��");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.out.println("���ε���� �����մϴ�.");

		c = getContentPane(); 
		c.setBackground(Color.darkGray);
		c.setLayout(null);			

		tEditor = new MyTextArea();
		JScrollPane scrollPane = new JScrollPane(tEditor);
		scrollPane.setLocation(10,140);
		scrollPane.setSize(280,440);
		add(scrollPane);

		atrPane = new MyAtrpanel();
		add(atrPane); 

		mMapPane = new MindPanel();
		add(mMapPane); 

		menuBar = new MyMenuBar();
		setJMenuBar(menuBar);
		toolBar = new MyToolBar();
		add(toolBar);

		setSize(1500,850); 
		setVisible(true);
	}
	
	//-------------------------------------------------------------------------------

	/*   Text Editor  */
	class MyTextArea extends JTextArea{

		MyTextArea() {
			setTabSize(2);
			setVisible(true);
			setFont(new Font("",Font.BOLD,20));

			JLabel textlabel = new JLabel();
			textlabel.setText("Text Editor");
			textlabel.setLocation(75, 90);
			textlabel.setSize(160,55);
			textlabel.setFont(new Font("Text Editor", Font.BOLD, 30));
			textlabel.setOpaque(false);
			textlabel.setForeground(Color.WHITE);
			c.add(textlabel);

			JButton tButton = new JButton("����");
			tButton.setSize(280,40);
			tButton.setLocation(10,585);
			tButton.addActionListener(new applyButton());
			c.add(tButton);
		}
	}

	/* 	�� �̺�Ʈ�����ʴ� TextArea ������ ��ư���� �Ӹ��ƴ϶�  Menubar�� Toolbar������ ��ư���ε� ����� �ȴ�.
	 	�׷��� MindMap Ŭ������ ��� Ŭ������ �ξ���.*/
	/* '����'��ư�� �ش�Ǵ� �̺�Ʈ ������*/
	class applyButton implements ActionListener{

		String origin;
		StringTokenizer st;
		StringBuffer buf;
		boolean isfirst = false;
		boolean ischange = false;
		@Override
		public void actionPerformed(ActionEvent e) {
			mMapPane.DeletePrevLabel();
			origin = tEditor.getText();

			st = new StringTokenizer(origin, "\n");
			int num = st.countTokens();

			//if(mTree.isChange(origin)) {

			if(mTree.root == null) {
				mTree.count = new LinkedList<>();
				mTree.textadd = new LinkedList<>();
				mTree.textLength = new LinkedList<>();
				try {
					for(int i=0; i<num; i++) {
						int search=0;
						int count=0;

						buf = new StringBuffer(st.nextToken());
						while(buf.charAt(search)=='\t') {
							count++;	
							search++;
						}

						buf.delete(0, search);
						mTree.count.add(count);
						mTree.textLength.add(buf.length());
						mTree.textadd.add(buf.toString());
					}

					if(mTree.setNode()) {
						EditorText = origin;
						mTree.MakeXY();
						mTree.MakeJLabelNode();	
						mMapPane.setMyJLabel();
						mMapPane.repaint(); //�̰� ȣ��ɶ����� �� �ȿ� paintComponent�� ȣ���� �ɼ��ִ�. 
					}
				}catch(IndexOutOfBoundsException err) {
					System.err.println("�Է¿� ���ؼ� ������ �����ϴ�.\n�� ������ �Է��Ͻ� ���뿡 �� �Ʒ����� ���ﶧ �Ͼ�� ������״� �Ʒ����� ������ �����ֽø� �ǰڽ��ϴ�.");
					System.err.println("����� ������ ������  �����ֽ� ������ ����ִ� �Ʒ����� Ȯ���� ����ð� �ٽ� �������ּ��� ^^");
				}
			}
			else {
				if(mTree.changeTree(st)) {
					EditorText = origin;
					//mTree.MakeXY();
					mTree.MakeJLabelNode();	
					mMapPane.setMyJLabel();
					mMapPane.repaint(); //�̰� ȣ��ɶ����� �� �ȿ� paintComponent�� ȣ���� �ɼ��ִ�. 
				}
			}
			
		}
	}

	//-----------------------------------------------------------------------------------------

	/*     Attribute Pane    */
	class MyAtrpanel extends JPanel{

		public JTextField tfield;
		public JTextField xfield;
		public JTextField yfield;
		public JTextField hfield;
		public JTextField wfield;
		public JTextField rfield, gfield, bfield;
		public JTextField efield;
		public JTextArea atrText;

		MyAtrpanel(){
			setSize(310,440);
			setLocation(1160,140); 
			setVisible(true);
			setLayout(null);
			/*����*/
			JLabel headline = new JLabel();
			headline.setText("Attribute Pane"); 
			headline.setLocation(1205, 90);    
			headline.setSize(220,50);
			headline.setFont(new Font("Attribute Pane", Font.BOLD, 30));
			headline.setOpaque(false);
			headline.setForeground(Color.WHITE);
			c.add(headline);

			/*�ؽ�Ʈ�� �κ�*/
			JLabel textLabel = new JLabel();
			textLabel.setText("TEXT:");
			textLabel.setFont(new Font("TEXT", Font.BOLD, 20));
			textLabel.setBounds(10, 30, 100, 30);
			add(textLabel);

			tfield = new JTextField("�ؽ�Ʈ", 5);
			tfield.setBounds(150, 30, 120, 30);
			tfield.setFont(new Font("", Font.BOLD, 15));
			tfield.setEditable(false);
			add(tfield);

			/*X�� �κ�*/
			JLabel xLabel = new JLabel();
			xLabel.setText("X:");
			xLabel.setFont(new Font("X", Font.BOLD, 20));
			xLabel.setBounds(10, 70, 30, 30);
			add(xLabel);

			xfield = new JTextField("x�� ��", 5);
			xfield.setBounds(150, 70, 120, 30);
			xfield.setFont(new Font("", Font.BOLD, 18));
			add(xfield);

			/*Y�� �κ�*/
			JLabel yLabel = new JLabel();
			yLabel.setText("Y:");
			yLabel.setFont(new Font("Y", Font.BOLD, 20));
			yLabel.setBounds(10, 110, 30, 30);
			add(yLabel);

			yfield = new JTextField("y�� ��", 5);
			yfield.setBounds(150, 110, 120, 30);
			yfield.setFont(new Font("", Font.BOLD, 18));
			add(yfield);

			/*Width�� �κ�*/
			JLabel wLabel = new JLabel();
			wLabel.setText("W:");
			wLabel.setFont(new Font("W", Font.BOLD, 20));
			wLabel.setBounds(7, 150, 40, 30);
			add(wLabel);

			wfield = new JTextField("���α���", 5);
			wfield.setBounds(150, 150, 120, 30);
			wfield.setFont(new Font("", Font.BOLD, 18));
			add(wfield);

			/*Height�� �κ�*/
			JLabel hLabel = new JLabel();
			hLabel.setText("H:");
			hLabel.setFont(new Font("H", Font.BOLD, 20));
			hLabel.setBounds(10, 190, 35, 30);
			add(hLabel);

			hfield = new JTextField("���α���", 5);
			hfield.setBounds(150, 190, 120, 30);
			hfield.setFont(new Font("", Font.BOLD, 18));
			add(hfield);

			/*Color�� �κ�*/
			JLabel cLabel = new JLabel();
			cLabel.setText("COLOR:");
			cLabel.setFont(new Font("COLOR", Font.BOLD, 20));
			cLabel.setBounds(10, 230, 120, 30);
			add(cLabel);

			rfield = new JTextField("R", 3);
			rfield.setBounds(150, 230, 40, 30);
			rfield.setFont(new Font("", Font.BOLD, 15));
			rfield.setEditable(false);
			add(rfield);

			gfield = new JTextField("G", 3);
			gfield.setBounds(190, 230, 40, 30);
			gfield.setFont(new Font("", Font.BOLD, 15));
			gfield.setEditable(false);
			add(gfield);

			bfield = new JTextField("B", 3);
			bfield.setBounds(230, 230, 40, 30);
			bfield.setFont(new Font("", Font.BOLD, 15));
			bfield.setEditable(false);
			add(bfield);

			/*Explain�� �κ�*/
			atrText = new JTextArea();
			atrText.setLocation(10, 270);
			atrText.setSize(290, 150);

			JScrollPane scrollPane = new JScrollPane(atrText);
			scrollPane.setLocation(10,270);
			scrollPane.setSize(290,150);
			add(scrollPane);

			/*���� ��ư  �κ� */
			JButton atrButton = new JButton("����");
			atrButton.setSize(310,40);
			atrButton.setLocation(1160,585); 
			atrButton.addActionListener(new ModifyButton());
			c.add(atrButton);
		}

	}

	/* 	�� �̺�Ʈ�����ʴ� Attribute Pane ������ ��ư���� �Ӹ��ƴ϶�  Menubar�� Toolbar������ ��ư���ε� ����� �ȴ�.
	 	�׷��� MindMap Ŭ������ ��� Ŭ������ �ξ���.*/
	/* '����'��ư�� �ش�Ǵ�  �̺�Ʈ ������ */
	class ModifyButton implements ActionListener{
		private JLabel b;
		private int x, y, w, h;
		private String explain;
		@Override
		public void actionPerformed (ActionEvent a) {
			if(mMapPane.currentJLabel == null)
				System.out.println("���� ��带 �������� �ʾҽ��ϴ�.");
			else {
				b= mMapPane.currentJLabel;
				try{
					x = Integer.parseInt(atrPane.xfield.getText());
					y = Integer.parseInt(atrPane.yfield.getText());
					w = Integer.parseInt(atrPane.wfield.getText());
					h = Integer.parseInt(atrPane.hfield.getText());
					explain = atrPane.atrText.getText();
					mTree.ModifyExplain(mTree.JLabeltoNode(b), explain);

					if( x<0 || x>830|| y<0 || y> 580|| w<40 || h<30 )
						System.err.println("�� �Է°������� ������ ������ Ȥ�� ��ҷ� �����Ҽ��� �����Ƿ� �ٽ� �Է��� ���ּ���.");
					else{
						try {
							mTree.ModifyLocation(mTree.JLabeltoNode(b), x, y);
							mTree.ModifySize(mTree.JLabeltoNode(b), w, h);
							b.setLocation(x, y);
							b.setSize(w, h);
							mMapPane.repaint();
						}
						catch(NullPointerException e) {
							System.err.println("������ ������� ���¶�, ��ǻ� ���� ���õȰ� �����ϴ�!");
						}
					}
				}
				catch(NumberFormatException e) {
					System.err.println("���� �Է�ĭ�� ���ڰ� ���� �մϴ�. ���ڸ� �ٽ� �Է����ּ���. ");
				}
			}

		}

	}

	//--------------------------------------------------------------------------------


	/*   Mind Panel  */
	class MindPanel extends JPanel{
		public JLabel currentJLabel = null; //���� ���õ� ���� ���Ѵ�. 
		public JLabel prevJLabel = null; // ������ ���õ� ���̸� ,��������� ���ؼ� �������־���.
		public Color prevColor=null; //�� �������õ� ����� ������ ���۷������̸� float���̶� ���� ���縦 ���� �����������Ѵ�.

		MindPanel(){
			setSize(850,600); 
			setLocation(300,100); 
			setVisible(true);
			setBackground(Color.cyan);
			setLayout(null);

			JLabel a = new JLabel();
			a.setText("Mind Map Pane");
			a.setLocation(320, 0); 
			a.setSize(230,50); 
			a.setFont(new Font("Mind Map Pane", Font.HANGING_BASELINE, 28));
			a.setOpaque(false);
			a.setForeground(Color.BLACK);
			add(a);
		}

		/* mMapPane�� ���� �׾��ִ� �޼ҵ尡 �ȴ�. */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if(mTree.root !=null) {
				int[] xy = new int[4];
				for(int i=1; i<mTree.count.size(); i++) {
					xy = mTree.setLine(mTree.FindNode(i));
					g.setColor(Color.BLACK);
					g.drawLine(xy[0], xy[1], xy[2], xy[3]); //�� ���� ù��°�� ����  , �ι�°�� �����ϴ� ���
				}
			}
		}

		/* ���ε� �ʿ� �ϼ��� Ʈ������ JLabel ������Ʈ ���·� ��Ÿ�� mMapPane�� �ٿ��δ� �޼ҵ�*/
		public void setMyJLabel() {
			for(int i=0; i<mTree.count.size(); i++) {
				int temp = mTree.whatMyLevel(mTree.FindNode(i));
				JLabel tempLabel = mTree.mindLabel.get(i);
				mMapPane.add(tempLabel);
				tempLabel.setFont(new Font("", Font.BOLD, 25));
				tempLabel.setOpaque(true);
				tempLabel.setBackground(Color.getHSBColor(100+ 30*temp, 200 , 200+ 30*temp));
				tempLabel.addMouseListener(new MoveOrSize());
			}
		}

		/* ���� ���ε���� �Ѷ� ������ �ִ� JLabel���� ������ �޼ҵ��̴�. */
		public void DeletePrevLabel() {
			if(mTree.root !=null) {
				try{for(int i=0; i<mTree.count.size(); i++) 
					mMapPane.remove(mTree.mindLabel.get(i));
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.err.println("�� �Ʒ��� ������ ������ ������ �����ε� ���ﶧ�� Ȯ���ϰ� ����ø� �˴ϴ�.^.^");
				}
			}
		}

		/* ����� �������, ����� ��ġ������ ���ִ� ���� �̺�Ʈ �������̴�.*/
		class MoveOrSize extends MouseAdapter{

			private JLabel b;
			private int isit = 0;

			public void mousePressed(MouseEvent a) {
				b = (JLabel)a.getSource(); 
				mMapPane.currentJLabel = b;//���� ���õǰ��ִ� ������ �˷��־ ���߿� ������ �Ҷ��� ����� �����ϰ� �Ѵ�.

				Color Rgb = (b.getBackground());

				if(mMapPane.prevJLabel != null && mMapPane.prevJLabel != mMapPane.currentJLabel) 
					mMapPane.prevJLabel.setBackground(prevColor); //������ ������� 

				if(prevColor == null || prevJLabel != currentJLabel) 
					prevColor = new Color(Rgb.getRGB());

				b.setBackground(Color.yellow);// ���� �Ǿ��� �� ����.

				atrPane.tfield.setText(b.getText());
				atrPane.xfield.setText(Integer.toString(b.getX()));
				atrPane.yfield.setText(Integer.toString(b.getY()));
				atrPane.wfield.setText(Integer.toString(b.getWidth()));
				atrPane.hfield.setText(Integer.toString(b.getHeight()));//���ڿ��� �ٲپ �־��־�� text�� �Է��� �ȴ�.
				atrPane.rfield.setText(Integer.toString(prevColor.getRed()));
				atrPane.gfield.setText(Integer.toString(prevColor.getBlue()));
				atrPane.bfield.setText(Integer.toString(prevColor.getGreen()));
				atrPane.atrText.setText(mTree.JLabeltoNode(b).explain);

				if(a.getX()>=0 && a.getX()<=15 && a.getY()>=0 && a.getY()<=15) 
					isit = 1;
				else if(a.getX()>b.getWidth()-15 && a.getX()<=b.getWidth() && a.getY()>=0 && a.getY()<=15) 
					isit = 2;
				else if(a.getX()>=0 && a.getX()<=15 && a.getY()>=b.getHeight()-15 && a.getY()<=b.getHeight()) 
					isit = 3;
				else if(a.getX()>=b.getWidth()-15 && a.getX()<=b.getWidth() && a.getY()>=b.getHeight()-15 && a.getY()<=b.getHeight()) 
					isit = 4;

				mMapPane.prevJLabel = mMapPane.currentJLabel;
			}
			public void mouseReleased(MouseEvent a) {

				b = (JLabel)a.getSource();

				if(isit==1) {
					System.out.println("������� "); //if �� else if ���ǿ��� ũ�⼳������ �����ϸ� ������ԵǴ� �������϶� �������� �ξ ����ũ��θ� �ٲٰ� �� �̿ܿ��� ���������� ���ϴ� ������� ������ �ǵ��� �Ͽ����ϴ�. 
					if(a.getX()>=b.getWidth()-25 && a.getY()<=b.getHeight()-12) {				
						System.err.println("ũ�� ������..ũ�Ⱑ �ʹ� �۾ҽ��ϴ�. ����ũ��� �ΰڽ��ϴ�.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);	
					}
					else if(a.getX()<=b.getWidth()-25 && a.getY()>=b.getHeight()-12) {
						System.err.println("ũ�� ������..ũ�Ⱑ �ʹ� �۾ҽ��ϴ�. ����ũ��� �ΰڽ��ϴ�.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()>=b.getWidth()-25 && a.getY()>=b.getHeight()-12) {
						System.err.println("ũ�� ������..ũ�Ⱑ �ʹ� �۾ҽ��ϴ�. ����ũ��� �ΰڽ��ϴ�.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else{
						mTree.ModifyLocation(mTree.JLabeltoNode(b), b.getX()+a.getX(),b.getY()+a.getY());
						mTree.ModifySize(mTree.JLabeltoNode(b), b.getWidth()-a.getX(), b.getHeight()-a.getY());
						b.setLocation(b.getX()+a.getX(),b.getY()+a.getY());
						b.setSize(b.getWidth()-a.getX(), b.getHeight()-a.getY());
					}
				}
				else if(isit==2) {
					System.out.println("������� ");

					if(a.getX()<=25 && a.getY()<=b.getHeight()-12) {				
						System.err.println("ũ�� ������..ũ�Ⱑ �ʹ� �۾ҽ��ϴ�. ����ũ��� �ΰڽ��ϴ�.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()>=25 && a.getY()>=b.getHeight()-12) {
						System.err.println("ũ�� ������..ũ�Ⱑ �ʹ� �۾ҽ��ϴ�. ����ũ��� �ΰڽ��ϴ�.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()<=25 && a.getY()>=b.getHeight()-12) {
						System.err.println("ũ�� ������..ũ�Ⱑ �ʹ� �۾ҽ��ϴ�. ����ũ��� �ΰڽ��ϴ�.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else {
						mTree.ModifyLocation(mTree.JLabeltoNode(b), b.getX(),b.getY()+a.getY());
						mTree.ModifySize(mTree.JLabeltoNode(b), a.getX(), b.getHeight()-a.getY());
						b.setLocation(b.getX(),b.getY()+a.getY());
						b.setSize(a.getX(), b.getHeight()-a.getY());
					}
				}
				else if(isit==3) {
					System.out.println("������� ");

					if(a.getX()>=b.getWidth()-25 && a.getY()>=12) {				
						System.err.println("ũ�� ������..ũ�Ⱑ �ʹ� �۾ҽ��ϴ�. ����ũ��� �ΰڽ��ϴ�.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()<=b.getWidth()-25 && a.getY()<=12) {
						System.err.println("ũ�� ������..ũ�Ⱑ �ʹ� �۾ҽ��ϴ�. ����ũ��� �ΰڽ��ϴ�.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()>=b.getWidth()-25 && a.getY()<=12) {
						System.err.println("ũ�� ������..ũ�Ⱑ �ʹ� �۾ҽ��ϴ�. ����ũ��� �ΰڽ��ϴ�.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else {
						mTree.ModifyLocation(mTree.JLabeltoNode(b), b.getX()+a.getX(),b.getY());
						mTree.ModifySize(mTree.JLabeltoNode(b), b.getWidth()-a.getX(), a.getY());
						b.setLocation(b.getX()+a.getX(),b.getY());
						b.setSize(b.getWidth()-a.getX(), a.getY());
					}
				}
				else if(isit==4) {
					System.out.println("������� ");

					if(a.getX()<=25 && a.getY()>=12) {				
						System.err.println("ũ�� ������..ũ�Ⱑ �ʹ� �۾ҽ��ϴ�. ����ũ��� �ΰڽ��ϴ�.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()>=25 && a.getY()<=12) {
						System.err.println("ũ�� ������..ũ�Ⱑ �ʹ� �۾ҽ��ϴ�. ����ũ��� �ΰڽ��ϴ�.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()<=25 && a.getY()<=12) {
						System.err.println("ũ�� ������..ũ�Ⱑ �ʹ� �۾ҽ��ϴ�. ����ũ��� �ΰڽ��ϴ�.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else {
						mTree.ModifySize(mTree.JLabeltoNode(b), a.getX(), a.getY());
						b.setSize(a.getX(), a.getY());
					}
				}
				else if(!(a.getX()<=b.getWidth() && a.getX()>=0 && a.getY()<=b.getHeight() && a.getY()>=0)) {
					//�׿ܿ��� ��Ƽ� ��ġ�� �����ϴ°� �۵��� �ȴ�.
					int x = b.getX();
					int y = b.getY();
					b.setLocation(x+a.getX()-b.getWidth()/2, y+a.getY()-b.getHeight()/2);
					if(x<= -40 || x>=835 || y<=-18 || y>=590) {
						System.err.println("���ε� �� ����������� �̵��� �ϼ̽��ϴ�.\n������ġ�� �ΰڽ��ϴ�.");
						b.setLocation(x, y);
					}
					else {
						mTree.ModifyLocation(mTree.JLabeltoNode(b), x+a.getX()-b.getWidth()/2, y+a.getY()-b.getHeight()/2);
					}
				}

				mMapPane.repaint();
				Color Rgb = (b.getBackground());
				atrPane.tfield.setText(b.getText());
				atrPane.xfield.setText(Integer.toString(b.getX()));
				atrPane.yfield.setText(Integer.toString(b.getY()));
				atrPane.wfield.setText(Integer.toString(b.getWidth()));
				atrPane.hfield.setText(Integer.toString(b.getHeight()));
				atrPane.rfield.setText(Integer.toString(prevColor.getRed()));
				atrPane.gfield.setText(Integer.toString(prevColor.getBlue()));
				atrPane.bfield.setText(Integer.toString(prevColor.getGreen()));

				isit = 0;
			}
		}

	}
	//--------------------------------------------------------------------------------

	/*    Menu Bar   */
	class MyMenuBar extends JMenuBar {

		public MyMenuBar() {

			setVisible(true);
			setBackground(Color.ORANGE);	
			setFont(new Font("", Font.BOLD, 15));

			JMenu fileMenu = new JMenu("�޴�");
			fileMenu.setSize(300,300);
			fileMenu.setFont(new Font("", Font.BOLD, 20));

			JMenuItem newFile = new JMenuItem("���� �����");
			JMenuItem openFile = new JMenuItem("����");
			JMenuItem saveFile = new JMenuItem("����");
			JMenuItem saveAsFile = new JMenuItem("�ٸ� �̸����� ����");
			JMenuItem applyAction = new JMenuItem("����");
			JMenuItem modifyAction = new JMenuItem("����");
			JMenuItem exitFile = new JMenuItem("�ݱ�");

			fileMenu.add(newFile);
			newFile.setBackground(Color.LIGHT_GRAY);
			fileMenu.add(openFile);
			openFile.setBackground(Color.WHITE);
			fileMenu.add(saveFile);
			saveFile.setBackground(Color.LIGHT_GRAY);
			fileMenu.add(saveAsFile);
			saveAsFile.setBackground(Color.WHITE);
			fileMenu.add(applyAction);
			applyAction.setBackground(Color.LIGHT_GRAY);
			fileMenu.add(modifyAction);
			modifyAction.setBackground(Color.WHITE);
			fileMenu.add(exitFile);
			exitFile.setBackground(Color.LIGHT_GRAY);

			newFile.addActionListener(new newMap());
			openFile.addActionListener(new openAction());
			saveFile.addActionListener(new saveAction());
			saveAsFile.addActionListener(new saveAsAction());
			applyAction.addActionListener(new applyButton());	//TextArea�� ���� �̺�Ʈ �����ʸ� ����.
			modifyAction.addActionListener(new ModifyButton()); //Attribute Panel�� ���� �̺�Ʈ �����ʷ� ����.
			exitFile.addActionListener(new exitAction());

			add(fileMenu);

		}
	}
	//--------------------------------------------------------------------------------

	/*   Tool Bar   */
	class MyToolBar extends JToolBar {

		public MyToolBar() {
			setSize(1500,30);
			setVisible(true);

			setBackground(Color.PINK);		
			add(new JLabel("����"));
			addSeparator();

			JButton newBtn = new JButton("���� �����");
			JButton openBtn = new JButton("����");
			JButton saveBtn = new JButton("����");
			JButton saveAsBtn = new JButton("�ٸ� �̸����� ����");
			JButton applyBtn = new JButton("����");
			JButton modifyBtn = new JButton("����");
			JButton exitBtn = new JButton("�ݱ�");

			newBtn.setToolTipText("1. ���ο� ���ε�� ������ �����մϴ�.  2. �ҷ��� ������ �ִٸ�, ���α׷��� ó�� ���� ���� ���� ���·� �ǵ����ϴ�.");
			openBtn.setToolTipText("���� �ε�");
			saveBtn.setToolTipText("���� ����");
			saveAsBtn.setToolTipText("���ο� ���� ����");
			applyBtn.setToolTipText("�ؽ�Ʈ ���� ������ ���ε� �� ���ο� ����");
			modifyBtn.setToolTipText("�Ӽ� ���� ���� ������ ���ε� �� ���ο� ����");
			exitBtn.setToolTipText("���α׷� ����");

			add(newBtn);
			newBtn.setBackground(Color.LIGHT_GRAY);
			add(openBtn);
			openBtn.setBackground(Color.WHITE);
			add(saveBtn);
			saveBtn.setBackground(Color.LIGHT_GRAY);
			add(saveAsBtn);
			saveAsBtn.setBackground(Color.WHITE);
			add(applyBtn);
			applyBtn.setBackground(Color.LIGHT_GRAY);
			add(modifyBtn);
			modifyBtn.setBackground(Color.WHITE);
			add(exitBtn);
			exitBtn.setBackground(Color.LIGHT_GRAY);

			newBtn.addActionListener(new newMap());
			openBtn.addActionListener(new openAction());
			saveBtn.addActionListener(new saveAction());
			saveAsBtn.addActionListener(new saveAsAction());
			applyBtn.addActionListener(new applyButton());   //TextArea�� ���� �̺�Ʈ �����ʸ� ����.
			modifyBtn.addActionListener(new ModifyButton()); //Attribute Panel�� ���� �̺�Ʈ �����ʷ� ����.
			exitBtn.addActionListener(new exitAction());

		}

	}
	//--------------------------------------------------------------------------------


	/* ���� ����� �̺�Ʈ ������*/
	class newMap implements ActionListener{

		public void actionPerformed(ActionEvent e) {

			if(ourPath == null) {
				int result = JOptionPane.showConfirmDialog(null, "������ ���� �����̽��ϴ�.\n������ �ؾ� �ٽ� �ҷ��� �� �ֽ��ϴ�...\n���� ����ðڽ��ϱ�?", 
						"���� �����", JOptionPane.YES_NO_OPTION); 
				if(result == JOptionPane.YES_OPTION) {

					mMapPane.DeletePrevLabel();
					mTree=new Controler();
					mMapPane.repaint();
					tEditor.setText("");
					atrPane.tfield.setText("�ؽ�Ʈ");
					atrPane.xfield.setText("x�� ��");
					atrPane.yfield.setText("y�� ��");
					atrPane.wfield.setText("���α���");
					atrPane.hfield.setText("���α���");
					atrPane.rfield.setText("R");
					atrPane.gfield.setText("G");
					atrPane.bfield.setText("B");
					atrPane.atrText.setText("����");
					ourPath=null;
				}
			}
			else if(ourPath !=null) {

				int result = JOptionPane.showConfirmDialog(null, "���ο� ������ ����ٸ� \"��\""
						+ "\n���� ������ ������ ������·� ���÷��� \"�ƴϿ�\"\n����Ͻ÷��� \"���\"�� �����ּ���",
						"���θ���� or ��������",JOptionPane.YES_NO_CANCEL_OPTION);
				if(result ==  JOptionPane.YES_OPTION) {
					mMapPane.DeletePrevLabel();
					mTree=new Controler();
					mMapPane.repaint();
					tEditor.setText("");
					atrPane.tfield.setText("�ؽ�Ʈ");
					atrPane.xfield.setText("x�� ��");
					atrPane.yfield.setText("y�� ��");
					atrPane.wfield.setText("���α���");
					atrPane.hfield.setText("���α���");
					atrPane.rfield.setText("R");
					atrPane.gfield.setText("G");
					atrPane.bfield.setText("B");
					atrPane.atrText.setText("����");
					ourPath=null;
				}
				else if(result ==  JOptionPane.NO_OPTION) {
					loadJson(ourPath);
				}

			}
		}
	}

	/* ���� �̺�Ʈ ������ */
	class openAction implements ActionListener{
		private JFileChooser chooser;

		openAction(){
			chooser = new JFileChooser();
		}
		public void actionPerformed(ActionEvent e) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Json ����","json");
			chooser.setFileFilter(filter);

			int ret = chooser.showOpenDialog(null);
			if(ret != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "������ �������� �ʾҽ��ϴ�.","���",JOptionPane.WARNING_MESSAGE);
				return;
			}

			String filePath = chooser.getSelectedFile().getPath();
			loadJson(filePath); //loadJson();�� ����
		}
	}

	/* ���� �̺�Ʈ ������ */
	class saveAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(mTree.root!=null) 
				saveJson(ourPath);
			else {
				JOptionPane.showMessageDialog(null, "������ ������ ���ε���� ������ּ���.",
						"���",JOptionPane.WARNING_MESSAGE);
				System.err.println("������ ������ ���ε���� ������ּ���.");
			}
		}
	}
	/* �ٸ� �̸����� ���� �̺�Ʈ ������*/
	class saveAsAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(mTree.root!=null) 
				saveJson(null);
			else {
				JOptionPane.showMessageDialog(null, "������ ������ ���ε���� ������ּ���.",
						"���",JOptionPane.WARNING_MESSAGE);
				System.err.println("������ ������ ���ε���� ������ּ���.");
			}
		}
	}

	/* ����  �̺�Ʈ ������*/
	class exitAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			int result = JOptionPane.showConfirmDialog(null, "���α׷��� �����Ͻðڽ��ϱ�?", 
					"Confirm", JOptionPane.YES_NO_OPTION); 
			if(result == JOptionPane.YES_OPTION) {
				System.out.println("���α׷��� �����մϴ�!");
				System.exit(0);
			}
		}

	}

	/* JSON ������� �����ϱ�  */
	void saveJson(String Path) { //Path�� null �� ��� �ٸ��̸����� ���� , null�� �ƴҰ�� �־��� Path ��η� ����

		JFileChooser chooser=new JFileChooser();
		String filePath;  
		StringTokenizer temp1; 

		if(Path==null) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Json ����","json");

			chooser.setFileFilter(filter);
			int ret = chooser.showSaveDialog(null);
			if(ret != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "������ �������� �����̾��...",
						"���",JOptionPane.WARNING_MESSAGE);
				return;}

			filePath = chooser.getSelectedFile().getPath();

			temp1 = new StringTokenizer(filePath,".");
			if(temp1.countTokens()>1) {
				String temp2 = new String(temp1.nextToken());
				String temp3 = new String(temp2+".json");
				if(temp3.equals(filePath))	//.json
					filePath = temp2;	
				else if(temp3.equals(filePath+".json")) //.json.json 
					filePath = temp2;	
				//�̰� ���ĺ��ʹ� �ǵ������� .�� ��������� �Ǵ��ϰ� �̸����� �־��ְڴ�.
			}
		}
		else 
			filePath = Path;


		JSONObject jobj = new JSONObject();

		JSONArray xArray = new JSONArray();
		JSONArray yArray = new JSONArray();
		JSONArray wArray = new JSONArray();
		JSONArray hArray = new JSONArray();
		JSONArray textArray = new JSONArray();
		JSONArray countArray = new JSONArray();
		JSONArray tlengthArray = new JSONArray();
		JSONArray totalnum = new JSONArray();
		JSONArray Etext = new JSONArray();
		JSONArray explaintext = new JSONArray();

		for(int i=0; i<mTree.count.size(); i++) {
			xArray.add(Integer.toString(mTree.getX(i)));
			yArray.add(Integer.toString(mTree.getY(i)));
			wArray.add(Integer.toString(mTree.getW(i)));
			hArray.add(Integer.toString(mTree.getH(i)));
			textArray.add((mTree.textadd.get(i)));
			countArray.add(Integer.toString(mTree.count.get(i)));
			tlengthArray.add(Integer.toString(mTree.textLength.get(i)));
			explaintext.add(mTree.getExplain(i));
		}
		totalnum.add(Integer.toString(mTree.count.size()));
		Etext.add(EditorText);

		jobj.put("X", xArray);
		jobj.put("Y", yArray);
		jobj.put("W", wArray);
		jobj.put("H", hArray);
		jobj.put("Text", textArray);
		jobj.put("Count", countArray);
		jobj.put("tLength", tlengthArray);
		jobj.put("Totalnum",totalnum);
		jobj.put("EditText", Etext);
		jobj.put("explainText", explaintext);


		try {
			FileWriter file;
			if( Path==null && ourPath!=filePath) 
				file = new FileWriter(filePath+".json");
			else 
				file = new FileWriter(filePath);
			file.write(jobj.toJSONString());
			file.flush();
			file.close();
			if(Path==null) ourPath = filePath+".json";
			System.out.println("save ���:"+ourPath);
		} 
		catch(IOException e) {
			e.printStackTrace();
			System.err.println("������ �ϱ⿡ ������ ����� ���� �������Դϴ�. �ܼ�â�� ���ּ���!");
			JOptionPane.showMessageDialog(null, "������ �����ϽǶ� C:\\Users\\�������ǻ���̸� �ΰ��� �����غ����� ",
					"���",JOptionPane.WARNING_MESSAGE);
		}

	}

	/* JSON ������� �ҷ�����  */
	void loadJson(String path) {

		JSONParser parser = new JSONParser();

		try {
			// path ����� ������ �о�� Object�� �Ľ�
			Object obj = parser.parse(new FileReader(path));
			System.out.println("load�� ���"+path);
			JSONObject jsonObject =(JSONObject) obj;

			// list��������
			JSONArray xList =(JSONArray) jsonObject.get("X");
			JSONArray yList =(JSONArray) jsonObject.get("Y");
			JSONArray wList =(JSONArray) jsonObject.get("W");
			JSONArray hList =(JSONArray) jsonObject.get("H");
			JSONArray tList =(JSONArray) jsonObject.get("Text");
			JSONArray cList =(JSONArray) jsonObject.get("Count");
			JSONArray lengthList =(JSONArray) jsonObject.get("tLength");
			JSONArray jsonlength =(JSONArray) jsonObject.get("Totalnum");
			JSONArray EditText =(JSONArray) jsonObject.get("EditText");
			JSONArray explainText =(JSONArray) jsonObject.get("explainText");

			Iterator<String>xiterator = xList.iterator();
			Iterator<String>yiterator = yList.iterator();
			Iterator<String>witerator = wList.iterator();
			Iterator<String>hiterator = hList.iterator();
			Iterator<String>titerator = tList.iterator();
			Iterator<String>citerator = cList.iterator();
			Iterator<String>literator = lengthList.iterator();
			Iterator<String>totalLength = jsonlength.iterator();
			Iterator<String>Edititerator = EditText.iterator();
			Iterator<String>explainiterator = explainText.iterator();

			// list�� Ǯ���ش�.
			String nowTextArea = Edititerator.next();

			mMapPane.DeletePrevLabel();

			mTree = new Controler(); //���� ������̴�. 

			int num=0; 
			int [] x, y, w, h;
			String[] explain;

			num = Integer.parseInt(totalLength.next());

			/*mTree.textadd = new String[num];
			mTree.count = new int[num];
			mTree.textLength = new int[num]; */
			mTree.textadd = new LinkedList<>();
			mTree.count = new LinkedList<>();
			mTree.textLength = new LinkedList<>();

			x= new int[num]; y= new int[num]; w= new int[num]; h= new int[num];
			explain = new String[num];

			for(int i =0; i<num; i++)
				mTree.textadd.add(titerator.next()); //mTree.textadd[i]  = titerator.next();
			for(int i =0; i<num; i++)
				mTree.count.add( Integer.parseInt(citerator.next()) );//mTree.count[i] = Integer.parseInt(citerator.next());
			for(int i =0; i<num; i++)
				mTree.textLength.add( Integer.parseInt(literator.next()) );//mTree.textLength[i] = Integer.parseInt(literator.next());
			for(int i =0; i<num; i++)
				x[i] = Integer.parseInt(xiterator.next());
			for(int i =0; i<num; i++)
				y[i] = Integer.parseInt(yiterator.next());
			for(int i =0; i<num; i++)
				w[i] = Integer.parseInt(witerator.next());
			for(int i =0; i<num; i++)
				h[i] = Integer.parseInt(hiterator.next());
			for(int i =0; i<num; i++)
				explain[i] = explainiterator.next();

			if(mTree.setNode()) {
				EditorText = nowTextArea;
				for(int i=0; i<num; i++) 
					mTree.setXYWH(i, x[i], y[i], w[i], h[i], explain[i]);
				mTree.MakeJLabelNode();	
				mMapPane.setMyJLabel();
				mMapPane.repaint(); 

				tEditor.setText(nowTextArea);
				atrPane.tfield.setText("�ؽ�Ʈ");
				atrPane.xfield.setText("x�� ��");
				atrPane.yfield.setText("y�� ��");
				atrPane.wfield.setText("���α���");
				atrPane.hfield.setText("���α���");
				atrPane.rfield.setText("R");
				atrPane.gfield.setText("G");
				atrPane.bfield.setText("B");
				atrPane.atrText.setText("����");
				ourPath = path;
			}
		} 
		catch (Exception e) {
			System.err.println("Json �ҷ����⿡ ������ �߻��߽��ϴ�. ��Ȯ�� ������ �´��� Ȯ�����ּ���.");
			JOptionPane.showMessageDialog(null, "Json �ҷ����⿡ ������ �߻��߽��ϴ�. ��Ȯ�� ������ �´��� Ȯ�����ּ���.",
					"���",JOptionPane.WARNING_MESSAGE);
		}
	}

	//---------------------------------------------------------------------

	/* main �޼ҵ� */
	public static void main(String[] args) {
		new MindMap();
	}

}
