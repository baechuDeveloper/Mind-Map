package MindMapProject;

import javax.swing.JLabel;

import java.util.LinkedList;
import java.util.StringTokenizer;

class Controler {
	
	public MindNode root;
	public LinkedList<String> textadd = new LinkedList<>();		 // text Editor ���� ���ڿ� �迭�� �޾ƿ´�.
	public LinkedList<Integer> count = new LinkedList<>();		 // �� ���ڿ��� ���� ���� �ܰ踦 �����´�.
	public LinkedList<Integer> textLength = new LinkedList<>();	 // ���ε�ʿ��� ��Ÿ���� ���α��̸� �����ֱ����� ���ڿ��� ���ڰ����� ��Ƶд�.
	public LinkedList<JLabel> mindLabel = new LinkedList<>();
	//---------------------------------------------------------------

	/** Ʈ�������� ����� �޼ҵ� **/ 
	public boolean setNode() { //�̰� �ѹ����� Ʈ�������� �����ϰ� ����. 

		MindNode cur = new MindNode("",-1); // ������� ���� ���带 �����ؼ� �ӽ������� cur�� ����

		for(int i=0; i<count.size(); i++) {
			if(i==0) {
				cur = root = new MindNode(textadd.get(0), count.get(0));
				cur.width = (textLength.get(i)*25)+10;
			}
			else if(i>0){
				if(count.get(i) == 0) {
					System.err.println("���ε���� ���� ����� root�� �ϳ��� �϶��� �����մϴ�.\n ���� �Ű�Ἥ �ٽ� �ۼ����ּ���.");
					root = null;
					return false;
				}
				else if(count.get(i)==count.get(i-1)+1) {
					cur.son = new MindNode(textadd.get(i),count.get(i));
					(cur.son).parent = cur;
					cur.son.width = (textLength.get(i)*25)+10;
					cur.search_next= cur.son;
					cur = cur.son;
				}
				else if(count.get(i)==count.get(i-1)) {
					cur.brother = new MindNode(textadd.get(i),count.get(i));
					(cur.brother).parent = cur.parent;
					cur.brother.width = (textLength.get(i)*25)+10;
					cur.search_next = cur.brother;
					cur = cur.brother;
				}
				else if(count.get(i)<count.get(i-1)) {
					MindNode temp2;
					temp2 = toMyParent(cur, count.get(i-1)-count.get(i));
					temp2.brother = new MindNode(textadd.get(i), count.get(i));
					(temp2.brother).parent = temp2.parent;
					temp2.brother.width = (textLength.get(i)*25)+10;
					cur.search_next = temp2.brother;
					cur = temp2.brother;
				}
				else {
					System.out.println("�ý�Ʈ ������ ���� ǥ�ذ� �߸��� �Է��� �ִ��� Ȯ�����ּ���");
					root = null;
					return false;
				}	

			}
		}
		//System.out.println("Ʈ�� �ϼ�");
		return true;
	}
	//---------------------------------------------------------------
	
	//��尡 ���� �߰��Ǿ����� ������� �ݿ��Ѵ�.
	public boolean changeTree(StringTokenizer compare) {
		int num = compare.countTokens();
		StringBuilder buf;
		LinkedList<String> text = new LinkedList<>();
		LinkedList<Integer> con = new LinkedList<>();
		LinkedList<Integer> Length = new LinkedList<>();
		MindNode cur = new MindNode("", -1);
		MindNode temproot = null;
		
		//���� �м�
		try {
			for(int i=0; i<num; i++) {
				int search=0;
				int count=0;

				buf = new StringBuilder(compare.nextToken());
				while(buf.charAt(search)=='\t') {
					count++;	
					search++;
				}

				buf.delete(0, search);
				con.add(count);
				Length.add(buf.length());
				text.add(buf.toString());
			}
		}catch(IndexOutOfBoundsException err) {
			System.err.println("�Է¿� ���ؼ� ������ �����ϴ�.\n�� ������ �Է��Ͻ� ���뿡 �� �Ʒ����� ���ﶧ �Ͼ�� ������״� �Ʒ����� ������ �����ֽø� �ǰڽ��ϴ�.");
			System.err.println("����� ������ ������  �����ֽ� ������ ����ִ� �Ʒ����� Ȯ���� ����ð� �ٽ� �������ּ��� ^^");
		}
		
		// �� �м�
		for(int i=0; i<con.size(); i++) {
			if(i==0) {
				cur = temproot = new MindNode(text.get(0), con.get(0));
				copyNode(temproot, root);
			}
			else if(i>0){
				if(con.get(i) == 0) {
					System.err.println("���ε���� ���� ����� root�� �ϳ��� �϶��� �����մϴ�.\n ���� �Ű�Ἥ �ٽ� �ۼ����ּ���.");
					temproot = null;
					return false;
				}
				else if(con.get(i)==con.get(i-1)+1) {
					cur.son = new MindNode(text.get(i),con.get(i));
					(cur.son).parent = cur;
					cur.son.width = (Length.get(i)*25)+10;
					cur.search_next= cur.son;
					cur = cur.son;
				}
				else if(con.get(i)==con.get(i-1)) {
					cur.brother = new MindNode(text.get(i),con.get(i));
					(cur.brother).parent = cur.parent;
					cur.brother.width = (Length.get(i)*25)+10;
					cur.search_next = cur.brother;
					cur = cur.brother;
				}
				else if(con.get(i)<con.get(i-1)) {
					MindNode temp2;
					temp2 = toMyParent(cur, con.get(i-1)-con.get(i));
					temp2.brother = new MindNode(text.get(i), con.get(i));
					(temp2.brother).parent = temp2.parent;
					temp2.brother.width = (Length.get(i)*25)+10;
					cur.search_next = temp2.brother;
					cur = temp2.brother;
				}
				else {
					System.out.println("�ý�Ʈ ������ ���� ǥ�ذ� �߸��� �Է��� �ִ��� Ȯ�����ּ���");
					temproot = null;
					return false;
				}	

			}
		}

		cur = temproot.search_next;
		for(int i=1; i<con.size(); i++) {
			MindNode oritemp = root;
			for(int j=0; j<count.size(); j++) {
				if(oritemp.text.equals(cur.text) && oritemp.depth==cur.depth && oritemp.parent.text.equals(cur.parent.text) ) {
					copyNode(cur, oritemp);
					break;
				}
				oritemp = oritemp.search_next;
			}
			cur = cur.search_next;
		}
		root = temproot;
		textadd = text;
		count = con;
		textLength = Length;
		return true;
	}//---------------------------------------------------------------
	
	//����� ���� �����ؼ� �Ѱ��ִ� �Լ�
	public void copyNode(MindNode dest, MindNode orgin) {
		dest.x = orgin.x;
		dest.y = orgin.y;
		dest.width = orgin.width;
		dest.height = orgin.height;
		dest.depth = orgin.depth;
		dest.explain = orgin.explain;
		dest.text = orgin.text;
	}
	//---------------------------------------------------------------

	/*** Ʈ������ ã�Ƴ��� �޼ҵ�� ***/

	/* ������ȣ�� �ش�Ǵ� Ʈ���� ��带 ã�� */
	public MindNode FindNode(int num) {
		MindNode temp = root; //0��°�� root�̴�.
		if(num>=0 && num<count.size()) {
			for(int i=0; i<num; i++)
				temp = temp.search_next;
			return temp;
		}
		System.err.println("ã�� �� ���� ���� �˻�");
		return null;
	}

	/* �� ��尡 ���° ���� ��ȣ����  (�迭���ҹ�ȣ �� ���� ���Եɼ����ִ�) */
	public int whatsnum(MindNode it) {
		MindNode temp = root;
		for(int i=0; i<count.size(); i++) {
			if(temp == it)
				return i;
			temp = temp.search_next;
		}
		return -1;
	}

	/* �� ���� �θ���� ���° ������ ��ġ�Ͻô��� */
	public int whoYourParent (MindNode it) {
		if(it == root) //���۷�����(�ּҰ�)�� �� �ϴ°��̴�.
			return -1; //it�� root���. parent�� null�̴�.
		else {
			MindNode temp = root;
			for(int i=0; i<count.size()-1;i++) {
				if(temp == it.parent)
					return i; //it�� parent�� �迭��ȣ�� ���°�� �ֱ��� �˼��ִ�.
				temp = temp.search_next;
			}
		}
		return -1;
	}

	/* ����� �� ���ܰ� �θ�� ���µ� �̿��� �� �޼ҵ�*/
	MindNode toMyParent(MindNode son, int count) { //count�� 1�̸� ���� �θ�. ������ ���� �θ� �����ִ� �Լ�.
		MindNode temp =son;
		for(int i=0; i<count; i++) {
			temp = temp.parent;
		}
		return temp;
	}

	/* �� ����� ����(����)�� ������ �˷��ִ� �޼ҵ� */
	public int whatMyLevel(MindNode it) {
		return it.depth;
	}
	//---------------------------------------------------------------

	/** ��ġ�� �����Ҷ� ���̰Ե� �޼ҵ� **/

	/* ����� ��ġ�� ���������� ������ ��ġ�� �ڽİ��踦 ����ؼ�  ��ġ�� ������ִ� �޼ҵ� */
	public void MakeXY() {
		MindNode temp = root;
		int num = (count.size())-1; //root�� ������ ����� ����
		if(temp == null) 
			return;
		temp.x = 370; temp.y= 290;
		//System.out.println("1��");

		temp = temp.search_next;	//root �� ���� ������
		int toZero=0; int tcheck[] = new int[num];
		int tcount[] = new int[num]; //root�� ������ ����. ������ �θ� ���° ��带 ����Ű���� �� ��ȣ�� �����صѰ��̴�.


		//System.out.println("2��");
		for(int i=0; i<num; i++) {
			tcount[i]= whoYourParent(temp);
			tcheck[i] = 0;
			temp = temp.search_next;
		}

		//System.out.println("3��");
		int a=0; int b=0;
		for(int i=0; i<num; i++) {
			if(tcount[i] == 0) {
				MindNode tempnode = FindNode(i+1);

				switch (toZero%4) {

				case 0:	
					tempnode.x = root.x + a*60; 
					tempnode.y = root.y-50;
					//System.out.println("00000");
					break;
				case 1:	
					tempnode.x = root.x -tempnode.width -40; 
					tempnode.y = root.y- a*50;
					//System.out.println("11111");
					break;
				case 2:	
					tempnode.x = root.x+ 40 - a*60;  
					tempnode.y = root.y+50;
					//System.out.println("22222");
					break;	
				case 3:	
					tempnode.x = root.x+ root.width +30; 
					tempnode.y = root.y+ a*50;
					//System.out.println("33333");
					break;
				}
				tcheck[0]++;
				toZero++; //--
				b++;
				if(b%4==0)
					a++;
			}
		}
		//System.out.println("4��");
		for(int i=1; i<num; i++) {
			for(int k=0; k<num; k++) {
				if(tcount[k] == i) {

					MindNode tempnode = FindNode(i);
					MindNode subject = FindNode(k+1);
					//System.out.println("���� "+ subject.text +" �� �θ�: " + tempnode.text);
					if(tempnode.x>=root.x && tempnode.y<root.y) { //0
						subject.x = tempnode.x -subject.width -20 + 45*tcheck[i];
						subject.y = tempnode.y -10 - 45*tcheck[i];
						//System.out.println("-0-0-0-0-");
					}
					else if(tempnode.x<root.x && tempnode.y<=root.y) { //1
						subject.x = tempnode.x -subject.width -20 - 45*tcheck[i];
						subject.y = tempnode.y - 45*tcheck[i];	
						//System.out.println("-1-1-1-1-");
					}
					else if(tempnode.x<=root.x + 40 && tempnode.y>root.y) { //2
						subject.x = tempnode.x -20 - 45*tcheck[i];
						subject.y = tempnode.y+ 50 + 45*tcheck[i];
						//System.out.println("-2-2-2-2-");
					}
					else if(tempnode.x>root.x && tempnode.y>=root.y) { //3
						subject.x = tempnode.x +110 +45*tcheck[i];;
						subject.y = tempnode.y + 45*tcheck[i];
						//System.out.println("-3-3-3-3-");
					}
					tcheck[i]++;
				}
			}
		}
		//System.out.println("5��");
		MindNode tmp = root;
		for(int i=0; i<count.size();i++) {
			//System.out.println(i+"���� x :"+FindNode(i).x+" y : "+FindNode(i).y + " "+ tmp.text); //tmp.x  tmp.y
			tmp =tmp.search_next;
		}
	}

	/* ����� ��ġ�� �ǵ������� �ٲپ����� Ʈ���� ��尪�� ������ ���ִ� �޼ҵ�*/
	public void ModifyLocation(MindNode it, int x, int y) {
		//FindNode(i)�� �̿��ؼ� ã�ƿͼ� �ٲپ�� �Ұ��̴�.
		if(it!=null) {
			it.x = x;
			it.y = y;
		}
		else System.err.println("������ �� �ؽ�Ʈ������ �ٽ� �������ּ���");
	}

	/* ����� ����� �ǵ������� �ٲپ����� Ʈ���� ��尪�� ������ ���ִ� �޼ҵ�*/
	public void ModifySize(MindNode it, int w, int h) {
		//FindNode(i)�� �̿��ؼ� ã�ƿͼ� �ٲپ�� �Ұ��̴�.
		if(it!=null) {
			it.width = w;
			it.height = h;
		}
		else System.err.println("������ �� �ؽ�Ʈ������ �ٽ� �������ּ���");
	}
	
	/* ����� ������ �ٲپ����� Ʈ���� ������ �ٲ۴�.*/
	public void ModifyExplain(MindNode it, String explain) {
		//FindNode(i)�� �̿��ؼ� ã�ƿͼ� �ٲپ�� �Ұ��̴�.
		if(it!=null) {
			it.explain = explain;
		}
		else System.err.println("������ �� �ؽ�Ʈ������ �ٽ� �������ּ���");
	}

	/* Json load�� �Ͽ� �ٽ� ������ �����Ҷ� ������ �ִ� ������ �ֵ��� ���� �޼ҵ� */
	public void setXYWH(int it, int x, int y, int w, int h, String e) {
		MindNode temp = FindNode(it);
		temp.x = x;
		temp.y = y;
		temp.width = w;
		temp.height = h;
		temp.explain = new String(e);
	}
	public int getX(int i) { //���° ����� ���� �� ������ ��ȣ�� ���´�.
		MindNode temp = FindNode(i);
		return temp.x;
	}
	public int getY(int i) {
		MindNode temp = FindNode(i);
		return temp.y;
	}
	public int getW(int i) {
		MindNode temp = FindNode(i);
		return temp.width;
	}
	public int getH(int i) {
		MindNode temp = FindNode(i);
		return temp.height;
	}
	public String getExplain(int i) {
		MindNode temp = FindNode(i);
		return temp.explain;
	}

	//---------------------------------------------------------------

	/*** JLabel ���� �޼ҵ�� ***/

	/* ������ Ʈ���� �̿��ؼ� Mind Pane�� ���� JLabel�� �����д�. */
	public void MakeJLabelNode() {
		MindNode temp = root;
		mindLabel = new LinkedList<JLabel>();
		for(int i=0; i<count.size(); i++) {
			mindLabel.add(new JLabel());//mindLabel[i] = new JLabel();
			mindLabel.get(i).setText(textadd.get(i));
			MindNode dest = FindNode(i);
			
			if(dest.width==0) {
				mindLabel.get(i).setSize((textLength.get(i)*25)+10,30);
				temp.width = (textLength.get(i)*25)+10;
			}
			else mindLabel.get(i).setSize(dest.width, dest.height);
			mindLabel.get(i).setLocation(dest.x, dest.y);
			temp = temp.search_next;
		}
	}

	/* ���õ� ���� � Ʈ���� ������� �˷��ش�. */
	public MindNode JLabeltoNode(JLabel it) {
		for(int i=0; i<count.size(); i++) {
			if( mindLabel.get(i)==it) 
				return FindNode(i);
		}
		return null;
	}
	
	/* ���ε�ʿ� ���ἱ�� ǥ���ϱ����� �� '���ἱ�� �� ��'�� ��忡���� �������ִ� �޼ҵ� */
	public int[] setLine(MindNode it) {
		int []xy=new int[4];
		MindNode temp = toMyParent(it,1);
		int x= it.x; int mx= it.x+it.width;
		int y= it.y; int my= it.y+it.height;
		int Tx=temp.x; int mTx= temp.x+temp.width;
		int Ty=temp.y; int mTy= temp.y+temp.height;
		//System.out.println("�ڽĲ� "+x+" "+y+" "+mx+" "+my);
		//System.out.println("�θ� "+Tx+" "+Ty+" "+mTx+" "+mTy);

		if(my<Ty) {
			if(x<Tx && mx<=Tx) {
				xy[0]= x+it.width/2; xy[1]= my;
				xy[2]= Tx; xy[3]=Ty+temp.height/2;
				//System.out.println("�� 1");
				return xy;}
			else if(Tx<x&&mx<mTx ) {
				xy[0]= x+it.width/2; xy[1]= my;
				xy[2]= Tx+temp.width/2; xy[3]= Ty;
				//System.out.println("�� 2");
				return xy;}
			else if(x>=mTx&&mx>mTx) {
				xy[0]= x+it.width/2; xy[1]= my;
				xy[2]= mTx; xy[3]=Ty+temp.height/2;
				//System.out.println("�� 3");
				return xy;}	
			else {
				xy[0]= x+it.width/2; xy[1]= my;
				xy[2]= Tx+temp.width/2; xy[3]= Ty;
				//System.out.println("�� ");
				return xy;}
		}
		else if( Ty<y&&my<=mTy ) {
			if(x<Tx && mx<=Tx) {
				xy[0]=mx; xy[1]=y+it.height/2;
				xy[2]=Tx; xy[3]=Ty+temp.height/2;
				//System.out.println("�� 1");
				return xy;}
			else if(x>=mTx) {
				xy[0]= x; xy[1]=y+it.height/2;
				xy[2]= mTx; xy[3]=Ty+temp.height/2;
				//System.out.println("�� 2");
				return xy;}
		}
		else if(y>=mTy) {
			if(x<Tx && mx<=Tx) {
				xy[0]= x+it.width/2; xy[1]= y;
				xy[2]= Tx; xy[3]=Ty+temp.height/2;
				//System.out.println("�Ʒ� 1");
				return xy;}
			else if(Tx<x&&mx<mTx ) {
				xy[0]= x+it.width/2; xy[1]= y;
				xy[2]= Tx+temp.width/2; xy[3]= mTy;
				//System.out.println("�Ʒ� 2");
				return xy;}
			else if(x>=mTx&&mx>mTx) {
				xy[0]= x+it.width/2; xy[1]= y;
				xy[2]= mTx; xy[3]=Ty+temp.height/2;
				//System.out.println("�Ʒ� 3");
				return xy;}	
			else {
				xy[0]= x+it.width/2; xy[1]= y;
				xy[2]= Tx+temp.width/2; xy[3]= mTy;
				//System.out.println("�Ʒ�");
				return xy;
			}
		}
		else { //�߰��� ���� ȿ���� �����ְ� �� ���̻��̼��� �߰��� ���� ������ ��Ƶξ���.
			if(x<Tx && mx<=Tx) {
				xy[0]=mx; xy[1]=y+it.height/2;
				xy[2]=Tx; xy[3]=Ty+temp.height/2;
				//System.out.println("�� 1");
				return xy;}
			else if(x>=mTx) {
				xy[0]= x; xy[1]=y+it.height/2;
				xy[2]= mTx; xy[3]=Ty+temp.height/2;
				//System.out.println("�� 2");
				return xy;}
		}
		//�� if������ return�� ��ġ�� ���ϸ� ���� 0�� �ο����ش�.
		int []z=new int[4];
		for(int i=0; i<4; i++)
			z[i]=0;
		//System.out.println("0�� �ο�");
		return z;
	}
	//---------------------------------------------------------------



	/* ���� Ŭ���� MindNode Ŭ����  */
	class MindNode {
		//Ʈ�������� ���߸鼭 Ž���� ���Ḯ��Ʈ ���·� �����������ߴ�.
		public int x, y, width, height;
		public int depth;
		public String explain;
		public String text;
		public MindNode parent;		 // �� ����� �κ�
		public MindNode brother; 	 // �θ��� �����ڽĵ��� '�� �ڽ��� brother'�� ���� �θ��� ���� �ڽ����� �Ѿ���ִ�.
		public MindNode son;		 // �� ����� �ڽ�. �� ���� �ڽĿ� ���� ������ �� �ڽ��� brother�� �˾ư����ִ�.
		public MindNode search_next; // Ʈ������ Ž���� ���� �ϵ��� ��尡 ������� ���� ������ ����ϵ��� �� ���۷���. 

		MindNode(String str, int count){
			x=0; y=0; width=0; height=30;
			depth = count;
			explain = new String();
			text = str;
			parent=null;brother=null;son=null;
			search_next=null;
		}

	}
	//---------------------------------------------------------------
}
