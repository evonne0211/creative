
int rows=0;//
int height=180;
int rowHeight=0;
String[][] rec=null;
String[][] data=null;
Vector v=new Vector();
jcx.jform.bResultSet rs=getResultSet();	
rs.setRow(1);
if (POSITION==4 && rs.getRow()==1){
	Hashtable ht=getCache();//new Hashtable();
	put("table","1");//980226
	put("row","0");
	put("page","1");//第1頁

	for (int i=1;i<=4;i++){
		rec=(String[][])get("table"+i);
		if (rec!=null & rec.length>0)
			setTableData("table"+i,rec);
	}	


	setVisible("boxREMARK",true);
	setVisible("boxQT",true);	
	setVisible("boxQT1",true);	
	setVisible("REMARK",true);
	setVisible("QT_REMARK",true);	
	/*
	setVisible("APPLY_QTY",true);
	setVisible("AMT",true);
	setVisible("CAR_PRICE",true);				
	setVisible("VERSION_PRICE",true);
	setVisible("DESIGN_PRICE",true);					
	setVisible("KNIFE_PRICE",true);
	setVisible("tax",true);		
	setVisible("total",true);
	setVisible("verify",true);				
	setVisible("SALES",true);	
	setVisible("COST",true);	
	setVisible("COST1",true);	
	setVisible("INV_DELIVER",true);		
	setVisible("textCoun",true);
	setVisible("textOutNo",true);				
	setVisible("textInv",true);	
	*/	
	//setPrintable("REMARK",true);	
	//setPrintable("QT_REMARK",true);	
	//setPrintable("boxREMARK",true);	
	//setPrintable("boxQT",true);		
	//setPrintable("f1",false);	
	//setPrintable("f2",false);
	JTable table=null;
	String[] head=null;	
	int cnt=0;//table.getColumnCount();		
		
	for (int i=1;i<=4;i++){//第1到第4個表格
		setVisible("table"+i,true);
		getcLabel("table"+i).setLocation(0,height);			
		rec=(String[][])get("table"+i);
		rows=rec.length;
		height=height+30;
		rowHeight=45;
		v.clear();
		
		//表頭資料
		table=getTable("table"+i);
		cnt=table.getColumnCount();
		if (rec.length>0){
			head=new String[rec[0].length] ;
		}
		else{
			if(i==1)
				head=new String[37] ;
			else if(i==2)
				head=new String[41] ;	
			else if(i==3)
				head=new String[40] ;			
			else
				head=new String[16] ;			
		}
		v.add(head);			

		for (int j=0;j<rows;j++){//每個表格的行數
			//ht.put("table",i+"");
			//ht.put("row",j+"");
			height=height+getTable("table"+i).getRowHeight(j);
			rowHeight=rowHeight+getTable("table"+i).getRowHeight(j);

			v.add(rec[j]);
			if (height>650 || (height>560 && i==4 && j==rows-1)){//990427//
				setVisible("boxREMARK",false);
				setVisible("boxQT",false);	
				setVisible("boxQT1",false);			
				setVisible("REMARK",false);
				setVisible("QT_REMARK",false);	
				setVisible("APPLY_QTY",false);
				setVisible("AMT",false);
				setVisible("CAR_PRICE",false);				
				setVisible("VERSION_PRICE",false);
				setVisible("DESIGN_PRICE",false);					
				setVisible("KNIFE_PRICE",false);
				setVisible("tax",false);		
				setVisible("total",false);
				setVisible("verify",false);				
				setVisible("SALES",false);		
				//setVisible("COST",false);
				//setVisible("COST1",false);
				setVisible("INV_DELIVER",false);		
				setVisible("textCoun",false);
				setVisible("textOutNo",false);				
				setVisible("textInv",false);
								
				for(int k=i+1;k<=4;k++){
					setVisible("table"+k,false);
					setTableData("table"+k,new String[0][0]);
				}	
				setTableData("table"+i,(new String[0][0]));
				setTableData("table"+i,(String[][])v.toArray(new String[0][0]));	
				for (int k=0;k<cnt;k++){
					table.setValueAt(table.getColumnName(k),0,k) ;//表頭資料
				}	
				
				ht.put("table",i+"");
				ht.put("row",j+"");
				put("page","2");//頁數
				getcLabel("table"+i).setSize(995,rowHeight);	//
				break;
			}
			//else if (i!=4 && j!=getTableData("table4").length){
			else {
				put("table",i+"");
				put("row",j+"");		
				put("page","2");//頁數
			}			
			/*
			else
				setTableData("table"+i,(String[][])v.toArray(new String[0][0]));
			*/	
		}
		if (height>650){
			setPrintable("boxREMARK",false);	
			setPrintable("boxQT",false);
			break;			
		}	
		getcLabel("table"+(i)).setSize(995,rowHeight);	
		
		setTableData("table"+i,(String[][])v.toArray(new String[0][0]));	
		for (int k=0;k<cnt;k++){
			table.setValueAt(table.getColumnName(k),0,k) ;//表頭資料
		}			
	}
	/*
	//如果印到表格的最後一行就換頁
	String tb=(String)get("table");
	String ro=(String)get("row");
	int len=( (String[][])get("table"+tb) ).length;
	if (!tb.equals("4") && Integer.parseInt(ro)==(len-1)){
		put("table",(Integer.parseInt(tb)+1)+"");
		put("row","0");
	}		
	*/
	if (height<650){
		String REMARK=getValue("REMARK");
		String QT_REMARK=getValue("QT_REMARK");
		String[] length1=REMARK.split("\n");
		String[] length2=QT_REMARK.split("\n");
		int length3=0;
		int heightRow=20;
		
		if (length1.length>length2.length){
			heightRow=heightRow * length1.length;
			length3= length1.length;
		}
		else{
			heightRow=heightRow * length2.length;
			 length3= length2.length;		
		}	 
		//if (length1.length>length2.length)  length3= length1.length;
		//else    length3= length2.length;		

		if (height+heightRow>650){
		
			setVisible("boxREMARK",false);
			setVisible("boxQT",false);	
			setVisible("boxQT1",false);				
			setVisible("REMARK",false);
			setVisible("QT_REMARK",false);	
			setVisible("APPLY_QTY",false);
			setVisible("AMT",false);
			setVisible("CAR_PRICE",false);				
			setVisible("VERSION_PRICE",false);
			setVisible("DESIGN_PRICE",false);					
			setVisible("KNIFE_PRICE",false);
			setVisible("tax",false);		
			setVisible("total",false);
			setVisible("verify",false);				
			setVisible("SALES",false);		
			//setVisible("COST",false);	
			//setVisible("COST1",false);	
			setVisible("INV_DELIVER",false);		
			setVisible("textCoun",false);
			setVisible("textOutNo",false);				
			setVisible("textInv",false);
					
			setEditable("btnSet1",true);
			setEditable("btnSet2",true);
			ht.put("table",ht.get("table"));
			//為了下頁會顯示
			if (ht.get("row")!=null){
				String row=(String)ht.get("row");
				//row=Integer.parseInt(row)-1+"";
				ht.put("row",row);		
			}
			//put("page","2");//頁數	
			//ht.put("table","4");
			//ht.put("row",getTableData("table4").length+"");								
			return value;
		}
		/*
		getcLabel("REMARK").setLocation(4,height-2);
		getcLabel("QT_REMARK").setLocation(494,height-2);
		getcLabel("boxREMARK").setLocation(0,height-6);
		getcLabel("boxQT").setLocation(490,height-6);
		
		//售價等欄位
		int x=getcLabel("boxQT").getX()+getcLabel("boxQT").getWidth()-(getcLabel("AMT").getWidth());
		int y=height-2;
		getcLabel("AMT").setLocation(x,y);		

		int x1=x;		
		int x2=x1+getcLabel("CAR_PRICE").getWidth()-9;
		y=getcLabel("AMT").getY()+getcLabel("AMT").getHeight()-9;				
		getcLabel("CAR_PRICE").setLocation(x1,y);		
		getcLabel("VERSION_PRICE").setLocation(x2,y);	
		
		y=getcLabel("CAR_PRICE").getY()+getcLabel("CAR_PRICE").getHeight()-9;	
		getcLabel("DESIGN_PRICE").setLocation(x1,y);	
		getcLabel("KNIFE_PRICE").setLocation(x2,y);			

		y=getcLabel("DESIGN_PRICE").getY()+getcLabel("DESIGN_PRICE").getHeight()-9;				
		getcLabel("tax").setLocation(x1,y);			
		getcLabel("total").setLocation(x2,y);				
	
		y=getcLabel("tax").getY()+getcLabel("tax").getHeight()-9;				
		getcLabel("verify").setLocation(x1,y);			
		getcLabel("SALES").setLocation(x2,y);	
		*/
		getcLabel("REMARK").setLocation(4,height-2);
		getcLabel("QT_REMARK").setLocation(373,height-2);
		getcLabel("boxREMARK").setLocation(0,height-6);
		getcLabel("boxQT").setLocation(366,height-6);
		getcLabel("boxQT1").setLocation(366,height-6);
		
		/*
		//售價等欄位
		int x1=getcLabel("boxQT").getX()+getcLabel("boxQT").getWidth()
					-(getcLabel("APPLY_QTY").getWidth()+getcLabel("AMT").getWidth())+7;//+5
		int x2=x1+getcLabel("APPLY_QTY").getWidth()-9;
		int y=height-2;
		getcLabel("APPLY_QTY").setLocation(x1,y);//請款數
		getcLabel("AMT").setLocation(x2,y);//售價
		getcLabel("COST").setLocation(x1-getcLabel("COST").getWidth()+8,y);//發票遞交欄位

		//int x1=x;		
		x2=x1+getcLabel("CAR_PRICE").getWidth()-9;
		y=getcLabel("AMT").getY()+getcLabel("AMT").getHeight()-9;				
		getcLabel("CAR_PRICE").setLocation(x1,y);		
		getcLabel("VERSION_PRICE").setLocation(x2,y);	
		//getcLabel("textCoun").setLocation(getcLabel("INV_DELIVER").getX()+8,y);//結帳月份
		
		y=getcLabel("CAR_PRICE").getY()+getcLabel("CAR_PRICE").getHeight()-9;	
		getcLabel("DESIGN_PRICE").setLocation(x1,y);	
		getcLabel("KNIFE_PRICE").setLocation(x2,y);	
		//getcLabel("textOutNo").setLocation(getcLabel("INV_DELIVER").getX()+8,y);//出貨單號		

		y=getcLabel("DESIGN_PRICE").getY()+getcLabel("DESIGN_PRICE").getHeight()-9;				
		getcLabel("tax").setLocation(x1,y);			
		getcLabel("total").setLocation(x2,y);		
		//getcLabel("textInv").setLocation(getcLabel("INV_DELIVER").getX()+8,y);//發票號碼				
	
		y=getcLabel("tax").getY()+getcLabel("tax").getHeight()-9;				
		getcLabel("verify").setLocation(x1,y);			
		getcLabel("SALES").setLocation(x2,y);	

		//預估總成本以下4個欄位位置設定
		//getcLabel("COST").setLocation(x1-getcLabel("COST").getWidth()+8, y);//預估總成本		
		getcLabel("COST1").setLocation(getcLabel("COST").getX(), getcLabel("COST").getY()+22);//實際總成本
		getcLabel("INV_DELIVER").setLocation(getcLabel("COST").getX()-8, getcLabel("COST1").getY()+22);//發票遞交欄位
		getcLabel("textCoun").setLocation(getcLabel("COST").getX(), getcLabel("INV_DELIVER").getY()+22);//結帳月份	
		getcLabel("textOutNo").setLocation(getcLabel("COST").getX(), getcLabel("textCoun").getY()+22);//出貨單號			
		getcLabel("textInv").setLocation(getcLabel("COST").getX(), getcLabel("textOutNo").getY()+22);//發票號碼	
		*/		
		setEditable("btnSet1",false);
		setEditable("btnSet2",false);
		height=650-height;
		/*
		getcLabel("REMARK").setSize(498,heightRow);//height+
		getcLabel("QT_REMARK").setSize(496,heightRow);//height+
		getcLabel("boxREMARK").setSize(503,getcLabel("REMARK").getHeight());
		getcLabel("boxQT").setSize(503,getcLabel("REMARK").getHeight());			
		*/
		//getcLabel("boxQT").setSize(500,height+getcLabel("QT_REMARK").getHeight());
	}

	/*
	String table=(String)get("table");
	String row=(String)get("row");
	//System.out.println("table="+ table);
	//System.out.println("row="+ row);		
	if (table.equals("4")){//最後一個表格
		rec=(String[][])get("table4");
		if (Integer.parseInt(row)==rec.length-1){//最後一個表格的最後一行,工單只有一頁
			getcLabel("REMARK").setLocation(4,height-2);
			getcLabel("QT_REMARK").setLocation(494,height-2);
			getcLabel("boxREMARK").setLocation(0,height-6);
			getcLabel("boxQT").setLocation(490,height-6);
			//setEditable("btnSet1",false);
			//setEditable("btnSet2",false);

			height=650-height;
			getcLabel("REMARK").setSize(498,height);
			getcLabel("QT_REMARK").setSize(496,height);
			getcLabel("boxREMARK").setSize(503,height);
			getcLabel("boxQT").setSize(500,height);			
			System.out.println("height="+ height);
			//rs.setRow(2);
			//rs.delete_current();
		}
	}	
	*/
}	

return value;