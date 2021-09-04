package MindMapProject;

import javax.swing.JLabel;

import java.util.LinkedList;
import java.util.StringTokenizer;

class Controler {
	
	public MindNode root;
	public LinkedList<String> textadd = new LinkedList<>();		 // text Editor 에게 문자열 배열을 받아온다.
	public LinkedList<Integer> count = new LinkedList<>();		 // 그 문자열에 따른 레벨 단계를 가져온다.
	public LinkedList<Integer> textLength = new LinkedList<>();	 // 마인드맵에서 나타낼때 가로길이를 정해주기위해 문자열의 문자갯수도 담아둔다.
	public LinkedList<JLabel> mindLabel = new LinkedList<>();
	//---------------------------------------------------------------

	/** 트리구조를 만드는 메소드 **/ 
	public boolean setNode() { //이거 한번으로 트리구조를 형성하게 하자. 

		MindNode cur = new MindNode("",-1); // 사용하지 않을 빈노드를 생성해서 임시형태인 cur을 형성

		for(int i=0; i<count.size(); i++) {
			if(i==0) {
				cur = root = new MindNode(textadd.get(0), count.get(0));
				cur.width = (textLength.get(i)*25)+10;
			}
			else if(i>0){
				if(count.get(i) == 0) {
					System.err.println("마인드맵의 가장 꼭대기 root는 하나만 일때만 가능합니다.\n 탭을 신경써서 다시 작성해주세요.");
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
					System.out.println("택스트 에디터 에서 표준과 잘못된 입력이 있는지 확인해주세요");
					root = null;
					return false;
				}	

			}
		}
		//System.out.println("트리 완성");
		return true;
	}
	//---------------------------------------------------------------
	
	//노드가 새로 추가되었던가 사려진걸 반영한다.
	public boolean changeTree(StringTokenizer compare) {
		int num = compare.countTokens();
		StringBuilder buf;
		LinkedList<String> text = new LinkedList<>();
		LinkedList<Integer> con = new LinkedList<>();
		LinkedList<Integer> Length = new LinkedList<>();
		MindNode cur = new MindNode("", -1);
		MindNode temproot = null;
		
		//구문 분석
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
			System.err.println("입력에 관해서 오류가 났습니다.\n이 문제는 입력하신 내용에 맨 아래줄을 지울때 일어나는 경우일테니 아래줄을 완전히 지워주시면 되겠습니다.");
			System.err.println("내용상 문제가 없지만  적어주신 내용중 비어있는 아래줄을 확실히 지우시고 다시 실행해주세요 ^^");
		}
		
		// 비교 분석
		for(int i=0; i<con.size(); i++) {
			if(i==0) {
				cur = temproot = new MindNode(text.get(0), con.get(0));
				copyNode(temproot, root);
			}
			else if(i>0){
				if(con.get(i) == 0) {
					System.err.println("마인드맵의 가장 꼭대기 root는 하나만 일때만 가능합니다.\n 탭을 신경써서 다시 작성해주세요.");
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
					System.out.println("택스트 에디터 에서 표준과 잘못된 입력이 있는지 확인해주세요");
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
	
	//노드의 값을 복사해서 넘겨주는 함수
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

	/*** 트리에서 찾아내는 메소드들 ***/

	/* 순서번호에 해당되는 트리의 노드를 찾기 */
	public MindNode FindNode(int num) {
		MindNode temp = root; //0번째가 root이다.
		if(num>=0 && num<count.size()) {
			for(int i=0; i<num; i++)
				temp = temp.search_next;
			return temp;
		}
		System.err.println("찾을 수 없는 노드로 검색");
		return null;
	}

	/* 이 노드가 몇번째 순서 번호인지  (배열원소번호 로 쉽게 보게될수도있다) */
	public int whatsnum(MindNode it) {
		MindNode temp = root;
		for(int i=0; i<count.size(); i++) {
			if(temp == it)
				return i;
			temp = temp.search_next;
		}
		return -1;
	}

	/* 그 노드는 부모님은 몇번째 순서에 위치하시는지 */
	public int whoYourParent (MindNode it) {
		if(it == root) //레퍼런스값(주소값)비교 로 하는것이다.
			return -1; //it이 root라면. parent가 null이다.
		else {
			MindNode temp = root;
			for(int i=0; i<count.size()-1;i++) {
				if(temp == it.parent)
					return i; //it의 parent는 배열번호상 몇번째에 있구나 알수있다.
				temp = temp.search_next;
			}
		}
		return -1;
	}

	/* 노드의 더 윗단게 부모로 가는데 이용이 될 메소드*/
	MindNode toMyParent(MindNode son, int count) { //count가 1이면 나의 부모. 더욱이 위에 부모를 보여주는 함수.
		MindNode temp =son;
		for(int i=0; i<count; i++) {
			temp = temp.parent;
		}
		return temp;
	}

	/* 그 노드의 레벨(깊이)이 몇인지 알려주는 메소드 */
	public int whatMyLevel(MindNode it) {
		return it.depth;
	}
	//---------------------------------------------------------------

	/** 위치를 지정할때 쓰이게될 메소드 **/

	/* 노드의 위치가 정해져있지 않을때 위치를 자식관계를 고려해서  위치를 만들어주는 메소드 */
	public void MakeXY() {
		MindNode temp = root;
		int num = (count.size())-1; //root를 제외한 노드의 갯수
		if(temp == null) 
			return;
		temp.x = 370; temp.y= 290;
		//System.out.println("1번");

		temp = temp.search_next;	//root 그 다음 노드부터
		int toZero=0; int tcheck[] = new int[num];
		int tcount[] = new int[num]; //root를 제외한 노드들. 노드들의 부모가 몇번째 노드를 가리키는지 그 번호를 저장해둘것이다.


		//System.out.println("2번");
		for(int i=0; i<num; i++) {
			tcount[i]= whoYourParent(temp);
			tcheck[i] = 0;
			temp = temp.search_next;
		}

		//System.out.println("3번");
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
		//System.out.println("4번");
		for(int i=1; i<num; i++) {
			for(int k=0; k<num; k++) {
				if(tcount[k] == i) {

					MindNode tempnode = FindNode(i);
					MindNode subject = FindNode(k+1);
					//System.out.println("현재 "+ subject.text +" 내 부모: " + tempnode.text);
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
		//System.out.println("5번");
		MindNode tmp = root;
		for(int i=0; i<count.size();i++) {
			//System.out.println(i+"번쨰 x :"+FindNode(i).x+" y : "+FindNode(i).y + " "+ tmp.text); //tmp.x  tmp.y
			tmp =tmp.search_next;
		}
	}

	/* 노드의 위치를 의도적으로 바꾸었을때 트리의 노드값도 변경을 해주는 메소드*/
	public void ModifyLocation(MindNode it, int x, int y) {
		//FindNode(i)를 이용해서 찾아와서 바꾸어야 할것이다.
		if(it!=null) {
			it.x = x;
			it.y = y;
		}
		else System.err.println("오류가 된 텍스트내용을 다시 수정해주세요");
	}

	/* 노드의 사이즈를 의도적으로 바꾸었을때 트리의 노드값도 변경을 해주는 메소드*/
	public void ModifySize(MindNode it, int w, int h) {
		//FindNode(i)를 이용해서 찾아와서 바꾸어야 할것이다.
		if(it!=null) {
			it.width = w;
			it.height = h;
		}
		else System.err.println("오류가 된 텍스트내용을 다시 수정해주세요");
	}
	
	/* 노드의 설명을 바꾸었을때 트리의 설명을 바꾼다.*/
	public void ModifyExplain(MindNode it, String explain) {
		//FindNode(i)를 이용해서 찾아와서 바꾸어야 할것이다.
		if(it!=null) {
			it.explain = explain;
		}
		else System.err.println("오류가 된 텍스트내용을 다시 수정해주세요");
	}

	/* Json load를 하여 다시 노드들을 생성할때 이전에 있던 값들을 주도록 만든 메소드 */
	public void setXYWH(int it, int x, int y, int w, int h, String e) {
		MindNode temp = FindNode(it);
		temp.x = x;
		temp.y = y;
		temp.width = w;
		temp.height = h;
		temp.explain = new String(e);
	}
	public int getX(int i) { //몇번째 노드의 것을 줄 것인지 번호로 적는다.
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

	/*** JLabel 관련 메소드들 ***/

	/* 만들어둔 트리를 이용해서 Mind Pane에 쓰일 JLabel을 만들어둔다. */
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

	/* 선택된 라벨이 어떤 트리의 노드인지 알려준다. */
	public MindNode JLabeltoNode(JLabel it) {
		for(int i=0; i<count.size(); i++) {
			if( mindLabel.get(i)==it) 
				return FindNode(i);
		}
		return null;
	}
	
	/* 마인드맵에 연결선을 표시하기위해 각 '연결선의 두 점'을 노드에따라 설정해주는 메소드 */
	public int[] setLine(MindNode it) {
		int []xy=new int[4];
		MindNode temp = toMyParent(it,1);
		int x= it.x; int mx= it.x+it.width;
		int y= it.y; int my= it.y+it.height;
		int Tx=temp.x; int mTx= temp.x+temp.width;
		int Ty=temp.y; int mTy= temp.y+temp.height;
		//System.out.println("자식껀 "+x+" "+y+" "+mx+" "+my);
		//System.out.println("부모껀 "+Tx+" "+Ty+" "+mTx+" "+mTy);

		if(my<Ty) {
			if(x<Tx && mx<=Tx) {
				xy[0]= x+it.width/2; xy[1]= my;
				xy[2]= Tx; xy[3]=Ty+temp.height/2;
				//System.out.println("위 1");
				return xy;}
			else if(Tx<x&&mx<mTx ) {
				xy[0]= x+it.width/2; xy[1]= my;
				xy[2]= Tx+temp.width/2; xy[3]= Ty;
				//System.out.println("위 2");
				return xy;}
			else if(x>=mTx&&mx>mTx) {
				xy[0]= x+it.width/2; xy[1]= my;
				xy[2]= mTx; xy[3]=Ty+temp.height/2;
				//System.out.println("위 3");
				return xy;}	
			else {
				xy[0]= x+it.width/2; xy[1]= my;
				xy[2]= Tx+temp.width/2; xy[3]= Ty;
				//System.out.println("위 ");
				return xy;}
		}
		else if( Ty<y&&my<=mTy ) {
			if(x<Tx && mx<=Tx) {
				xy[0]=mx; xy[1]=y+it.height/2;
				xy[2]=Tx; xy[3]=Ty+temp.height/2;
				//System.out.println("중 1");
				return xy;}
			else if(x>=mTx) {
				xy[0]= x; xy[1]=y+it.height/2;
				xy[2]= mTx; xy[3]=Ty+temp.height/2;
				//System.out.println("중 2");
				return xy;}
		}
		else if(y>=mTy) {
			if(x<Tx && mx<=Tx) {
				xy[0]= x+it.width/2; xy[1]= y;
				xy[2]= Tx; xy[3]=Ty+temp.height/2;
				//System.out.println("아래 1");
				return xy;}
			else if(Tx<x&&mx<mTx ) {
				xy[0]= x+it.width/2; xy[1]= y;
				xy[2]= Tx+temp.width/2; xy[3]= mTy;
				//System.out.println("아래 2");
				return xy;}
			else if(x>=mTx&&mx>mTx) {
				xy[0]= x+it.width/2; xy[1]= y;
				xy[2]= mTx; xy[3]=Ty+temp.height/2;
				//System.out.println("아래 3");
				return xy;}	
			else {
				xy[0]= x+it.width/2; xy[1]= y;
				xy[2]= Tx+temp.width/2; xy[3]= mTy;
				//System.out.println("아래");
				return xy;
			}
		}
		else { //중간과 같은 효과를 낼수있게 그 사이사이선을 중간과 같은 내용을 담아두었다.
			if(x<Tx && mx<=Tx) {
				xy[0]=mx; xy[1]=y+it.height/2;
				xy[2]=Tx; xy[3]=Ty+temp.height/2;
				//System.out.println("중 1");
				return xy;}
			else if(x>=mTx) {
				xy[0]= x; xy[1]=y+it.height/2;
				xy[2]= mTx; xy[3]=Ty+temp.height/2;
				//System.out.println("중 2");
				return xy;}
		}
		//위 if문에서 return을 거치지 못하면 그저 0을 부여해준다.
		int []z=new int[4];
		for(int i=0; i<4; i++)
			z[i]=0;
		//System.out.println("0을 부여");
		return z;
	}
	//---------------------------------------------------------------



	/* 내부 클래스 MindNode 클래스  */
	class MindNode {
		//트리구조를 갖추면서 탐색은 연결리스트 형태로 가져오도록했다.
		public int x, y, width, height;
		public int depth;
		public String explain;
		public String text;
		public MindNode parent;		 // 이 노드의 부보
		public MindNode brother; 	 // 부모의 여러자식들은 '그 자식의 brother'를 통해 부모의 다음 자식으로 넘어갈수있다.
		public MindNode son;		 // 이 노드의 자식. 더 많은 자식에 대한 정보는 그 자식의 brother로 알아갈수있다.
		public MindNode search_next; // 트리구조 탐색을 쉽게 하도록 노드가 만들어진 다음 순서를 기억하도록 할 레퍼런스. 

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
