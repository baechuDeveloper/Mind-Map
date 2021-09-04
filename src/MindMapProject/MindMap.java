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

/* MindMap 클래스 안 멤버로는...
 * 9개의 멤버 변수,
 * 자바의 GUI로 사용되는 5개의 멤버 클래스, 
 * 이벤트리스너로 사용할 7개의 멤버 클래스,
 * JSON 저장, 불러오기를 하는 메소드 2개,
 * 마인드맵을 실행하는 생성자 1개,
 * main 메소드 1개로 구성되어있습니다. */
public class MindMap extends JFrame {

	private Controler mTree = new Controler();
	private MyTextArea tEditor;
	private MyAtrpanel atrPane;
	private MindPanel mMapPane;  
	private MyMenuBar menuBar;
	private MyToolBar toolBar;

	private Container c; 		 		  // MindMap 본인을 지칭 하도록 만든 컨테이너.
	private String EditorText = null;	 // TextArea의 완성된 문자열 상태를 가지고 있는 문자열.
	private String ourPath = null; 		// JSON 저장을 하지 않은 상태라면 그 경로는 null이고, 있다면 그 경로 문자열이다.
	//----------------------------------------------
	
	public MindMap() {
		setTitle("마인드 맵");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.out.println("마인드맵을 실행합니다.");

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

			JButton tButton = new JButton("적용");
			tButton.setSize(280,40);
			tButton.setLocation(10,585);
			tButton.addActionListener(new applyButton());
			c.add(tButton);
		}
	}

	/* 	이 이벤트리스너는 TextArea 영역의 버튼에서 뿐만아니라  Menubar와 Toolbar에서의 버튼으로도 사용이 된다.
	 	그래서 MindMap 클래스의 멤버 클래스로 두었다.*/
	/* '적용'버튼에 해당되는 이벤트 리스너*/
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
						mMapPane.repaint(); //이게 호출될때마다 저 안에 paintComponent가 호출이 될수있다. 
					}
				}catch(IndexOutOfBoundsException err) {
					System.err.println("입력에 관해서 오류가 났습니다.\n이 문제는 입력하신 내용에 맨 아래줄을 지울때 일어나는 경우일테니 아래줄을 완전히 지워주시면 되겠습니다.");
					System.err.println("내용상 문제가 없지만  적어주신 내용중 비어있는 아래줄을 확실히 지우시고 다시 실행해주세요 ^^");
				}
			}
			else {
				if(mTree.changeTree(st)) {
					EditorText = origin;
					//mTree.MakeXY();
					mTree.MakeJLabelNode();	
					mMapPane.setMyJLabel();
					mMapPane.repaint(); //이게 호출될때마다 저 안에 paintComponent가 호출이 될수있다. 
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
			/*제목*/
			JLabel headline = new JLabel();
			headline.setText("Attribute Pane"); 
			headline.setLocation(1205, 90);    
			headline.setSize(220,50);
			headline.setFont(new Font("Attribute Pane", Font.BOLD, 30));
			headline.setOpaque(false);
			headline.setForeground(Color.WHITE);
			c.add(headline);

			/*텍스트라벨 부분*/
			JLabel textLabel = new JLabel();
			textLabel.setText("TEXT:");
			textLabel.setFont(new Font("TEXT", Font.BOLD, 20));
			textLabel.setBounds(10, 30, 100, 30);
			add(textLabel);

			tfield = new JTextField("텍스트", 5);
			tfield.setBounds(150, 30, 120, 30);
			tfield.setFont(new Font("", Font.BOLD, 15));
			tfield.setEditable(false);
			add(tfield);

			/*X라벨 부분*/
			JLabel xLabel = new JLabel();
			xLabel.setText("X:");
			xLabel.setFont(new Font("X", Font.BOLD, 20));
			xLabel.setBounds(10, 70, 30, 30);
			add(xLabel);

			xfield = new JTextField("x의 값", 5);
			xfield.setBounds(150, 70, 120, 30);
			xfield.setFont(new Font("", Font.BOLD, 18));
			add(xfield);

			/*Y라벨 부분*/
			JLabel yLabel = new JLabel();
			yLabel.setText("Y:");
			yLabel.setFont(new Font("Y", Font.BOLD, 20));
			yLabel.setBounds(10, 110, 30, 30);
			add(yLabel);

			yfield = new JTextField("y의 값", 5);
			yfield.setBounds(150, 110, 120, 30);
			yfield.setFont(new Font("", Font.BOLD, 18));
			add(yfield);

			/*Width라벨 부분*/
			JLabel wLabel = new JLabel();
			wLabel.setText("W:");
			wLabel.setFont(new Font("W", Font.BOLD, 20));
			wLabel.setBounds(7, 150, 40, 30);
			add(wLabel);

			wfield = new JTextField("가로길이", 5);
			wfield.setBounds(150, 150, 120, 30);
			wfield.setFont(new Font("", Font.BOLD, 18));
			add(wfield);

			/*Height라벨 부분*/
			JLabel hLabel = new JLabel();
			hLabel.setText("H:");
			hLabel.setFont(new Font("H", Font.BOLD, 20));
			hLabel.setBounds(10, 190, 35, 30);
			add(hLabel);

			hfield = new JTextField("세로길이", 5);
			hfield.setBounds(150, 190, 120, 30);
			hfield.setFont(new Font("", Font.BOLD, 18));
			add(hfield);

			/*Color라벨 부분*/
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

			/*Explain라벨 부분*/
			atrText = new JTextArea();
			atrText.setLocation(10, 270);
			atrText.setSize(290, 150);

			JScrollPane scrollPane = new JScrollPane(atrText);
			scrollPane.setLocation(10,270);
			scrollPane.setSize(290,150);
			add(scrollPane);

			/*변경 버튼  부분 */
			JButton atrButton = new JButton("변경");
			atrButton.setSize(310,40);
			atrButton.setLocation(1160,585); 
			atrButton.addActionListener(new ModifyButton());
			c.add(atrButton);
		}

	}

	/* 	이 이벤트리스너는 Attribute Pane 영역의 버튼에서 뿐만아니라  Menubar와 Toolbar에서의 버튼으로도 사용이 된다.
	 	그래서 MindMap 클래스의 멤버 클래스로 두었다.*/
	/* '변경'버튼에 해당되는  이벤트 리스너 */
	class ModifyButton implements ActionListener{
		private JLabel b;
		private int x, y, w, h;
		private String explain;
		@Override
		public void actionPerformed (ActionEvent a) {
			if(mMapPane.currentJLabel == null)
				System.out.println("아직 노드를 선택하지 않았습니다.");
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
						System.err.println("이 입력값만으론 적당한 사이즈 혹은 장소로 존재할수가 없으므로 다시 입력을 해주세요.");
					else{
						try {
							mTree.ModifyLocation(mTree.JLabeltoNode(b), x, y);
							mTree.ModifySize(mTree.JLabeltoNode(b), w, h);
							b.setLocation(x, y);
							b.setSize(w, h);
							mMapPane.repaint();
						}
						catch(NullPointerException e) {
							System.err.println("새로히 만들어진 상태라서, 사실상 현재 선택된게 없습니다!");
						}
					}
				}
				catch(NumberFormatException e) {
					System.err.println("현재 입력칸에 문자가 들어간듯 합니다. 숫자만 다시 입력해주세요. ");
				}
			}

		}

	}

	//--------------------------------------------------------------------------------


	/*   Mind Panel  */
	class MindPanel extends JPanel{
		public JLabel currentJLabel = null; //현재 선택된 라벨을 말한다. 
		public JLabel prevJLabel = null; // 이전에 선택된 라벨이며 ,색상반전을 위해서 설정해주었다.
		public Color prevColor=null; //그 이전선택된 노드의 색깔은 레퍼런스값이며 float형이라 깊은 복사를 통해 가져가도록한다.

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

		/* mMapPane에 선을 그어주는 메소드가 된다. */
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if(mTree.root !=null) {
				int[] xy = new int[4];
				for(int i=1; i<mTree.count.size(); i++) {
					xy = mTree.setLine(mTree.FindNode(i));
					g.setColor(Color.BLACK);
					g.drawLine(xy[0], xy[1], xy[2], xy[3]); //각 순서 첫번째가 본인  , 두번째가 가야하는 대상
				}
			}
		}

		/* 마인드 맵에 완성된 트리들을 JLabel 컴포넌트 형태로 나타내 mMapPane에 붙여두는 메소드*/
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

		/* 새로 마인드맵을 둘때 이전에 있던 JLabel들을 뺴놓는 메소드이다. */
		public void DeletePrevLabel() {
			if(mTree.root !=null) {
				try{for(int i=0; i<mTree.count.size(); i++) 
					mMapPane.remove(mTree.mindLabel.get(i));
				}
				catch(ArrayIndexOutOfBoundsException e) {
					System.err.println("맨 아래줄 오류가 있을수 있으니 앞으로도 지울때는 확실하게 지우시면 됩니다.^.^");
				}
			}
		}

		/* 노드의 사이즈변경, 노드의 위치변경을 해주는 내부 이벤트 리스너이다.*/
		class MoveOrSize extends MouseAdapter{

			private JLabel b;
			private int isit = 0;

			public void mousePressed(MouseEvent a) {
				b = (JLabel)a.getSource(); 
				mMapPane.currentJLabel = b;//현재 선택되고있는 라벨임을 알려주어서 나중에 변경을 할때도 사용이 가능하게 한다.

				Color Rgb = (b.getBackground());

				if(mMapPane.prevJLabel != null && mMapPane.prevJLabel != mMapPane.currentJLabel) 
					mMapPane.prevJLabel.setBackground(prevColor); //색상을 원래대로 

				if(prevColor == null || prevJLabel != currentJLabel) 
					prevColor = new Color(Rgb.getRGB());

				b.setBackground(Color.yellow);// 선택 되었을 때 색상.

				atrPane.tfield.setText(b.getText());
				atrPane.xfield.setText(Integer.toString(b.getX()));
				atrPane.yfield.setText(Integer.toString(b.getY()));
				atrPane.wfield.setText(Integer.toString(b.getWidth()));
				atrPane.hfield.setText(Integer.toString(b.getHeight()));//문자열로 바꾸어서 넣어주어야 text에 입력이 된다.
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
					System.out.println("사이즈변경 "); //if 와 else if 조건에는 크기설정에서 변경하면 사라지게되는 사이즈일때 조건으로 두어서 일정크기로만 바꾸고 그 이외에만 정상적으로 원하는 사이즈로 변경이 되도록 하였습니다. 
					if(a.getX()>=b.getWidth()-25 && a.getY()<=b.getHeight()-12) {				
						System.err.println("크기 변경중..크기가 너무 작았습니다. 일정크기로 두겠습니다.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);	
					}
					else if(a.getX()<=b.getWidth()-25 && a.getY()>=b.getHeight()-12) {
						System.err.println("크기 변경중..크기가 너무 작았습니다. 일정크기로 두겠습니다.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()>=b.getWidth()-25 && a.getY()>=b.getHeight()-12) {
						System.err.println("크기 변경중..크기가 너무 작았습니다. 일정크기로 두겠습니다.");
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
					System.out.println("사이즈변경 ");

					if(a.getX()<=25 && a.getY()<=b.getHeight()-12) {				
						System.err.println("크기 변경중..크기가 너무 작았습니다. 일정크기로 두겠습니다.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()>=25 && a.getY()>=b.getHeight()-12) {
						System.err.println("크기 변경중..크기가 너무 작았습니다. 일정크기로 두겠습니다.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()<=25 && a.getY()>=b.getHeight()-12) {
						System.err.println("크기 변경중..크기가 너무 작았습니다. 일정크기로 두겠습니다.");
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
					System.out.println("사이즈변경 ");

					if(a.getX()>=b.getWidth()-25 && a.getY()>=12) {				
						System.err.println("크기 변경중..크기가 너무 작았습니다. 일정크기로 두겠습니다.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()<=b.getWidth()-25 && a.getY()<=12) {
						System.err.println("크기 변경중..크기가 너무 작았습니다. 일정크기로 두겠습니다.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()>=b.getWidth()-25 && a.getY()<=12) {
						System.err.println("크기 변경중..크기가 너무 작았습니다. 일정크기로 두겠습니다.");
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
					System.out.println("사이즈변경 ");

					if(a.getX()<=25 && a.getY()>=12) {				
						System.err.println("크기 변경중..크기가 너무 작았습니다. 일정크기로 두겠습니다.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()>=25 && a.getY()<=12) {
						System.err.println("크기 변경중..크기가 너무 작았습니다. 일정크기로 두겠습니다.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else if(a.getX()<=25 && a.getY()<=12) {
						System.err.println("크기 변경중..크기가 너무 작았습니다. 일정크기로 두겠습니다.");
						mTree.ModifySize(mTree.JLabeltoNode(b), 60, 30);
						b.setSize(60, 30);
					}
					else {
						mTree.ModifySize(mTree.JLabeltoNode(b), a.getX(), a.getY());
						b.setSize(a.getX(), a.getY());
					}
				}
				else if(!(a.getX()<=b.getWidth() && a.getX()>=0 && a.getY()<=b.getHeight() && a.getY()>=0)) {
					//그외에는 잡아서 위치를 변경하는게 작동이 된다.
					int x = b.getX();
					int y = b.getY();
					b.setLocation(x+a.getX()-b.getWidth()/2, y+a.getY()-b.getHeight()/2);
					if(x<= -40 || x>=835 || y<=-18 || y>=590) {
						System.err.println("마인드 맵 범위를벗어나는 이동을 하셨습니다.\n원래위치로 두겠습니다.");
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

			JMenu fileMenu = new JMenu("메뉴");
			fileMenu.setSize(300,300);
			fileMenu.setFont(new Font("", Font.BOLD, 20));

			JMenuItem newFile = new JMenuItem("새로 만들기");
			JMenuItem openFile = new JMenuItem("열기");
			JMenuItem saveFile = new JMenuItem("저장");
			JMenuItem saveAsFile = new JMenuItem("다른 이름으로 저장");
			JMenuItem applyAction = new JMenuItem("적용");
			JMenuItem modifyAction = new JMenuItem("변경");
			JMenuItem exitFile = new JMenuItem("닫기");

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
			applyAction.addActionListener(new applyButton());	//TextArea와 같은 이벤트 리스너를 적용.
			modifyAction.addActionListener(new ModifyButton()); //Attribute Panel와 같은 이벤트 리스너로 적용.
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
			add(new JLabel("도구"));
			addSeparator();

			JButton newBtn = new JButton("새로 만들기");
			JButton openBtn = new JButton("열기");
			JButton saveBtn = new JButton("저장");
			JButton saveAsBtn = new JButton("다른 이름으로 저장");
			JButton applyBtn = new JButton("적용");
			JButton modifyBtn = new JButton("변경");
			JButton exitBtn = new JButton("닫기");

			newBtn.setToolTipText("1. 새로운 마인드맵 편집을 시작합니다.  2. 불러낸 내용이 있다면, 프로그램이 처음 시작 했을 때의 상태로 되돌립니다.");
			openBtn.setToolTipText("파일 로드");
			saveBtn.setToolTipText("파일 저장");
			saveAsBtn.setToolTipText("새로운 파일 생성");
			applyBtn.setToolTipText("텍스트 편집 내용을 마인드 맵 페인에 적용");
			modifyBtn.setToolTipText("속성 페인 변경 내용을 마인드 맵 페인에 적용");
			exitBtn.setToolTipText("프로그램 종료");

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
			applyBtn.addActionListener(new applyButton());   //TextArea와 같은 이벤트 리스너를 적용.
			modifyBtn.addActionListener(new ModifyButton()); //Attribute Panel와 같은 이벤트 리스너로 적용.
			exitBtn.addActionListener(new exitAction());

		}

	}
	//--------------------------------------------------------------------------------


	/* 새로 만들기 이벤트 리스너*/
	class newMap implements ActionListener{

		public void actionPerformed(ActionEvent e) {

			if(ourPath == null) {
				int result = JOptionPane.showConfirmDialog(null, "저장을 하지 않으셨습니다.\n저장을 해야 다시 불러올 수 있습니다...\n새로 만드시겠습니까?", 
						"새로 만들기", JOptionPane.YES_NO_OPTION); 
				if(result == JOptionPane.YES_OPTION) {

					mMapPane.DeletePrevLabel();
					mTree=new Controler();
					mMapPane.repaint();
					tEditor.setText("");
					atrPane.tfield.setText("텍스트");
					atrPane.xfield.setText("x의 값");
					atrPane.yfield.setText("y의 값");
					atrPane.wfield.setText("가로길이");
					atrPane.hfield.setText("세로길이");
					atrPane.rfield.setText("R");
					atrPane.gfield.setText("G");
					atrPane.bfield.setText("B");
					atrPane.atrText.setText("설명");
					ourPath=null;
				}
			}
			else if(ourPath !=null) {

				int result = JOptionPane.showConfirmDialog(null, "새로운 파일을 만든다면 \"예\""
						+ "\n현재 파일의 마지막 저장상태로 가시려면 \"아니요\"\n취소하시려면 \"취소\"를 눌러주세요",
						"새로만들기 or 돌려놓기",JOptionPane.YES_NO_CANCEL_OPTION);
				if(result ==  JOptionPane.YES_OPTION) {
					mMapPane.DeletePrevLabel();
					mTree=new Controler();
					mMapPane.repaint();
					tEditor.setText("");
					atrPane.tfield.setText("텍스트");
					atrPane.xfield.setText("x의 값");
					atrPane.yfield.setText("y의 값");
					atrPane.wfield.setText("가로길이");
					atrPane.hfield.setText("세로길이");
					atrPane.rfield.setText("R");
					atrPane.gfield.setText("G");
					atrPane.bfield.setText("B");
					atrPane.atrText.setText("설명");
					ourPath=null;
				}
				else if(result ==  JOptionPane.NO_OPTION) {
					loadJson(ourPath);
				}

			}
		}
	}

	/* 열기 이벤트 리스너 */
	class openAction implements ActionListener{
		private JFileChooser chooser;

		openAction(){
			chooser = new JFileChooser();
		}
		public void actionPerformed(ActionEvent e) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Json 파일","json");
			chooser.setFileFilter(filter);

			int ret = chooser.showOpenDialog(null);
			if(ret != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다.","경고",JOptionPane.WARNING_MESSAGE);
				return;
			}

			String filePath = chooser.getSelectedFile().getPath();
			loadJson(filePath); //loadJson();을 실행
		}
	}

	/* 저장 이벤트 리스너 */
	class saveAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(mTree.root!=null) 
				saveJson(ourPath);
			else {
				JOptionPane.showMessageDialog(null, "적용을 눌러서 마인드맵을 만들어주세요.",
						"경고",JOptionPane.WARNING_MESSAGE);
				System.err.println("적용을 눌러서 마인드맵을 만들어주세요.");
			}
		}
	}
	/* 다른 이름으로 저장 이벤트 리스너*/
	class saveAsAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(mTree.root!=null) 
				saveJson(null);
			else {
				JOptionPane.showMessageDialog(null, "적용을 눌러서 마인드맵을 만들어주세요.",
						"경고",JOptionPane.WARNING_MESSAGE);
				System.err.println("적용을 눌러서 마인드맵을 만들어주세요.");
			}
		}
	}

	/* 종료  이벤트 리스너*/
	class exitAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			int result = JOptionPane.showConfirmDialog(null, "프로그램을 종료하시겠습니까?", 
					"Confirm", JOptionPane.YES_NO_OPTION); 
			if(result == JOptionPane.YES_OPTION) {
				System.out.println("프로그램을 종료합니다!");
				System.exit(0);
			}
		}

	}

	/* JSON 방식으로 저장하기  */
	void saveJson(String Path) { //Path가 null 인 경우 다른이름으로 저장 , null이 아닐경우 주어진 Path 경로로 저장

		JFileChooser chooser=new JFileChooser();
		String filePath;  
		StringTokenizer temp1; 

		if(Path==null) {
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Json 파일","json");

			chooser.setFileFilter(filter);
			int ret = chooser.showSaveDialog(null);
			if(ret != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "파일을 저장하지 않으셨어요...",
						"경고",JOptionPane.WARNING_MESSAGE);
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
				//이것 이후부터는 의도적으로 .을 만든것으로 판단하고 이름으로 넣어주겠다.
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
			System.out.println("save 경로:"+ourPath);
		} 
		catch(IOException e) {
			e.printStackTrace();
			System.err.println("저장을 하기에 접근이 허용이 되지 않은곳입니다. 콘솔창을 봐주세요!");
			JOptionPane.showMessageDialog(null, "파일을 저장하실때 C:\\Users\\사용자컴퓨터이름 인곳에 저장해보세요 ",
					"경고",JOptionPane.WARNING_MESSAGE);
		}

	}

	/* JSON 방식으로 불러오기  */
	void loadJson(String path) {

		JSONParser parser = new JSONParser();

		try {
			// path 경로의 파일을 읽어와 Object로 파싱
			Object obj = parser.parse(new FileReader(path));
			System.out.println("load의 경로"+path);
			JSONObject jsonObject =(JSONObject) obj;

			// list가져오기
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

			// list를 풀어준다.
			String nowTextArea = Edititerator.next();

			mMapPane.DeletePrevLabel();

			mTree = new Controler(); //새로 만들것이다. 

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
				atrPane.tfield.setText("텍스트");
				atrPane.xfield.setText("x의 값");
				atrPane.yfield.setText("y의 값");
				atrPane.wfield.setText("가로길이");
				atrPane.hfield.setText("세로길이");
				atrPane.rfield.setText("R");
				atrPane.gfield.setText("G");
				atrPane.bfield.setText("B");
				atrPane.atrText.setText("설명");
				ourPath = path;
			}
		} 
		catch (Exception e) {
			System.err.println("Json 불러오기에 문제가 발생했습니다. 정확한 파일이 맞는지 확인해주세요.");
			JOptionPane.showMessageDialog(null, "Json 불러오기에 문제가 발생했습니다. 정확한 파일이 맞는지 확인해주세요.",
					"경고",JOptionPane.WARNING_MESSAGE);
		}
	}

	//---------------------------------------------------------------------

	/* main 메소드 */
	public static void main(String[] args) {
		new MindMap();
	}

}
