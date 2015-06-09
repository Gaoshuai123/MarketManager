package Client;

import java.awt.*;
import java.awt.event.*;
import java.beans.VetoableChangeListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

import java.io.*;
import java.net.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.*;

public class UI extends JFrame implements Runnable{
	private static final FilterInputStream output = null;
	//设置一个全局变量，在VIP用户确定自己的身份之前为false，确认自己的身份之后为true
	private static boolean isVIPEntered = false;
	//定义会员编号文本框。可以输入会员编号
	private static JTextField jtfVIPNo = new JTextField("会员验证");
	//定义会员姓名的标签
	private static JLabel jlVIPNa = new JLabel();
	//定义会员的积分标签
	private static JLabel jlVIPSo = new JLabel();
	//定义输入收银员编号的文本框
	private static JTextField jtfAssistantNo = new JTextField("请确认员工身份！");
	//收银员登陆和退出按钮
	private static JButton jbtEnterOut = new JButton("登录");
	//private static JButton jbtAssistant = new JButton("先确认身份！");
	//定义收银员编号的标签
	private static JLabel jlAssistantNo = new JLabel();
	//定义录入商品编号的文本框
	private static JTextField jtfGoodsNo = new JTextField();
	//定义合计金额的标签
	private static JLabel jlbTotal1 = new JLabel("合计金额：");
	private static JLabel jlbTotal = new JLabel("0.0");
	//定义合计数量的标签
	private static JLabel jlbSum1 = new JLabel("合计数量：");
	private static JLabel jlbSum = new JLabel("0");
	
	//大标题的标签
	private static JLabel jlbl = new JLabel("超市收银管理系统");
	//设置字体的大小
	private Font font = new Font("SansSerif",Font.BOLD,30);
	private Font font1 = new Font("SansSerif",Font.BOLD,25);
	//表格的属性显示
	private String[] columnNames = {"商品编号","商品名","普通单价/元","会员单价/元","商品数量*1","金额/元"};
	//表格数据定义
	private static Object[][] data = new Object[0][6];
	//表格的各个属性的定义
	private String GoodsNo = new String();
	private String GoodsNa = new String();
	private double priceForAll;
	private double priceForVIP;
	private int numberOfgoods;
	private double totalCash;

	//定义一个vector,存储每个商品的条目信息
	private static Vector<Goods> vector = new Vector<Goods>();
	
	//private Object[] o = new Object[]{GoodsNo,GoodsNa,priceForAll,priceForVIP,numberOfgoods,totalCash};
	//二进制的输入输出流
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	//生成一个字符串存从服务器收到的数据
	private static String receiveFromServer;
	//将从服务器中收到的数据进行划分
	private static String[] receive;
	
	//创建一个模型
	private DefaultTableModel tableModel = new DefaultTableModel(data,columnNames);
	//表格定义
	private JTable jTable1 = new JTable(tableModel);
	//jTable1的行高	
	private JSpinner jspiRowHeight = new JSpinner(new SpinnerNumberModel(35,1,50,1));
	//关于时间的变量
	private static Calendar cal=Calendar.getInstance();  
	private static JLabel YMD = new JLabel("日期:" + cal.get(Calendar.YEAR)+ "" + ":"+ cal.get(Calendar.MONTH)+ "" + ":"+ cal.get(Calendar.DATE));//获取当前的日期和时分
	private static JLabel HMS = new JLabel("时分：" + cal.get(Calendar.HOUR_OF_DAY)+ "" + ":"+ cal.get(Calendar.MINUTE)+ "");
	//结算按钮
	private static JButton account = new JButton("结算");
	
	private static JFrame welcomeFrame = new JFrame();
	public UI(){
		welcomeFrame.setSize(300, 400);
		welcomeFrame.setLocationRelativeTo(null);
		welcomeFrame.setVisible(true);
		welcomeFrame.setLayout(null);
		JLabel jLabel1 = new JLabel("Welcome");
		jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		jLabel1.setFont(font1);
		jLabel1.setBounds(10, 30, 260, 50);
		jLabel1.setBorder(new LineBorder(Color.RED));
		welcomeFrame.add(jLabel1);
		JLabel jLabel2 = new JLabel("请输入口令：");
		jLabel2.setBounds(10, 100, 260, 40);
		jLabel2.setBorder(new LineBorder(Color.BLACK));
		welcomeFrame.add(jLabel2);
		jtfAssistantNo = new JPasswordField();
		jtfAssistantNo.setBounds(10, 140, 260, 40);
		welcomeFrame.add(jtfAssistantNo);
		
		
		
		/*大标题--超市收银管理系统--*/
		JPanel jpOutline = new JPanel();
		jlbl.setFont(font);
		jpOutline.add(jlbl);
		
		/*中间的主题部分*/
		JPanel jpMainField = new JPanel();
		jpMainField.setLayout(new BorderLayout(1,1));
		//主体部分的上面部分
		JPanel jpInfor = new JPanel();
		jpInfor.setLayout(new GridLayout(1,4,40,5));
		//定义4个Panel,分别是会员号，会员名，会员积分，收银员号
		JPanel jpVIPNo = new JPanel();
		jpVIPNo.setLayout(new BorderLayout(1,1));
		jpVIPNo.add(new JLabel("会员号："),BorderLayout.WEST);
		jpVIPNo.add(jtfVIPNo,BorderLayout.CENTER);
		JPanel jpVIPNa = new JPanel();
		jpVIPNa.setLayout(new BorderLayout(1,1));
		jpVIPNa.add(new JLabel("会员名："),BorderLayout.WEST);
		jpVIPNa.add(jlVIPNa,BorderLayout.EAST);
		JPanel jpVIPSo = new JPanel();
		jpVIPSo.setLayout(new BorderLayout(1,1));
		jpVIPSo.add(new JLabel("会员积分："),BorderLayout.WEST);
		jpVIPSo.add(jlVIPSo,BorderLayout.EAST);
		JPanel jpAssistantNo = new JPanel();
		jpAssistantNo.setLayout(new BorderLayout(1,1));
		//jpAssistantNo.add(new JLabel("身份验证："),BorderLayout.WEST);
//		jpAssistantNo.add(jtfAssistantNo,BorderLayout.CENTER);
		//jbtEnterOut
		jpAssistantNo.add(jbtEnterOut,BorderLayout.EAST);
		jpInfor.add(new JLabel());
		jpInfor.add(jpVIPNo);
		jpInfor.add(jpVIPNa);
		jpInfor.add(jpVIPSo);
		jpInfor.add(jpAssistantNo);
		jpInfor.add(new JLabel());
		jpMainField.add(jpInfor,BorderLayout.NORTH);
		
		
		
		
		//主体部分的中间部分
		JPanel jpGoods = new JPanel();
		jpGoods.setLayout(new BorderLayout());
		jTable1.setRowHeight(((Integer)(jspiRowHeight.getValue())).intValue());
		jpGoods.add(new JScrollPane(jTable1),BorderLayout.NORTH);
		jpMainField.add(jpGoods,BorderLayout.CENTER);
		
		//主题的下面部分
		JPanel jpButtom = new JPanel();
		jpButtom.setLayout(new GridLayout(1,2));
		//jpButtom.setLayout(new BorderLayout());
		JPanel jpInputArea = new JPanel();
		jpInputArea.setLayout(new GridLayout(2,1));
//		JPanel jpInput = new JPanel();
/*		jpInput.setLayout(new GridLayout(1,3));
		jpInput.add(new JLabel("录入框："));
		jpInput.add(jtfGoodsNo);
		jpInput.add(new JLabel("按回车键结算"));
		jpInputArea.add(jpInput);
		jpInputArea.add(new JLabel("                                           录入商品编号"));*/
//		jpInputArea.setLayout(new GridLayout(2,1));
		jpInputArea.setLayout(null);
		
		JPanel jpInput = new JPanel();
/*		jpInput.setLayout(new GridLayout(1,3));
		jpInput.add(new JLabel("录入框："));
		jpInput.add(jtfGoodsNo);
		jpInput.add(new JLabel("按回车键结算"));*/
		
		jpInput.setLayout(null);
		JLabel jl1 = new JLabel("商品编号");
		jl1.setBounds(10, 0, 70, 40);
		jl1.setBorder(new LineBorder(Color.RED));
		jl1.setHorizontalAlignment(SwingConstants.CENTER);
		jtfGoodsNo.setBounds(80, 0, 120, 40);
		jtfGoodsNo.setBorder(new LineBorder(Color.BLUE));
		JLabel jl2 = new JLabel("");
		
		jl2.setBorder(new LineBorder(Color.GRAY));
		jl2.setBounds(200, 0, 100, 40);
		jl2.setHorizontalAlignment(SwingConstants.CENTER);
		jpInput.add(jl1);
		jpInput.add(jtfGoodsNo);
		jpInput.add(jl2);
		jpInput.setBorder(new LineBorder(Color.BLACK));
		jpInput.setBounds(0, 0, 280, 80);
		
		jpInputArea.add(jpInput);
/*		JLabel jl3 = new JLabel("录入商品编号");
		jl3.setBounds(60, 40, 120, 40);
		System.out.println("jpInputArea.getHeight = " + jpInputArea.getHeight());
		jl3.setHorizontalAlignment(SwingConstants.CENTER);
		jl3.setBorder(new LineBorder(Color.BLACK));
		jpInput.add(jl3);*/
//		jpInputArea.add(jl3);
		
		JPanel jpAddition = new JPanel();
		jpAddition.setLayout(new GridLayout(2,2));
		jpAddition.add(jlbTotal1);
		jlbTotal1.setFont(font);
		jpAddition.add(jlbTotal);
		jlbTotal.setFont(font);
		jpAddition.add(jlbSum1);
		jlbSum1.setFont(font1);
		jpAddition.add(jlbSum);
		jlbSum.setFont(font1);
		jpButtom.add(jpInputArea);
		jpButtom.add(jpAddition);
		
		account.setFont(font);
		jpButtom.add(account);
		
		jpMainField.add(jpButtom,BorderLayout.SOUTH);
		
		/*下方的显示区域*/
		JPanel jpDisplay = new JPanel();
		jpDisplay.setLayout(new GridLayout(1,2));
		JPanel jpNum = new JPanel();
		jpNum.setLayout(new GridLayout(1,2));
		jpNum.add(new JLabel("收银员工号："));
		jpNum.add(jlAssistantNo);
		JPanel jpTime = new JPanel();
		jpTime.setLayout(new GridLayout(1,2));
		//jpTime.add(new JLabel("日期:"));
		 
		jpTime.add(YMD);//获取当前的日期和时分
		jpTime.add(HMS);//
		jpDisplay.add(jpNum);
		jpDisplay.add(jpTime);
		
		/*将上面的三个部分放到一个Panel中*/
		setLayout(new BorderLayout(2,1));
		add(jpOutline,BorderLayout.NORTH);
		add(jpMainField,BorderLayout.CENTER);
		add(jpDisplay,BorderLayout.SOUTH);
		
		
		/**---------------扫描商品的事件监听（回车键）---------------**/
		jtfGoodsNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(jtfGoodsNo.getText().trim().compareToIgnoreCase("") != 0 && isExist(jtfGoodsNo.getText().trim(),vector) == -1) {
					String GoodsNoToServer = "1" + ":" + "10" + ":" + jtfGoodsNo.getText().trim();
					try {
						toServer.writeUTF(GoodsNoToServer);
						System.out.println(GoodsNoToServer);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(jtfGoodsNo.getText().trim().compareToIgnoreCase("") != 0 && isExist(jtfGoodsNo.getText().trim(),vector) != -1) {
					int i = isExist(jtfGoodsNo.getText().trim(),vector);
					tableModel.setValueAt(vector.get(i).numberOfGoodsPlusOne(), i, 4);
					if(isVIPEntered == false) {
						totalCash = vector.get(i).getNumberOfGoods() * priceForAll;
					}
					else
						totalCash = vector.get(i).getNumberOfGoods() * priceForVIP;
					BigDecimal totalCash1 = new BigDecimal(totalCash);  
					double totalCash2 = totalCash1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
					tableModel.setValueAt(totalCash2, i, 5);
					//显示总的数量和金额
					double totalMoney = 0;//总的需要支付的钱数
					int totalNumber = 0;//总的购买商品的数量
					for(int j = 0; j < vector.size();j++) {
						totalMoney += vector.get(j).getTotalForAll(); 
						totalNumber += vector.get(j).getNumberOfGoods();
					}
					
					jlbTotal.setText(String.valueOf(totalMoney));
					jlbSum.setText(String.valueOf(totalNumber));
				}
			}
		});
		/**------------------------------收银员登录的事件监听--------------------------------------------------------**/
		jbtEnterOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(jbtEnterOut.getText().trim() == "登录" && jtfAssistantNo.getText().trim().compareToIgnoreCase("") != 0) {
					String toServerRegister = "0:1:" + jtfAssistantNo.getText().trim();
					try {
						toServer.writeUTF(toServerRegister);
						System.out.println(toServerRegister);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(jbtEnterOut.getText().trim() == "退出") {
					String toServerRegister = "0:9:" + jtfAssistantNo.getText().trim();
					try {
						toServer.writeUTF(toServerRegister);
						System.out.println(toServerRegister);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		jtfAssistantNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(jbtEnterOut.getText().trim() == "登录" && jtfAssistantNo.getText().trim().compareToIgnoreCase("") != 0) {
					String toServerRegister = "0:1:" + jtfAssistantNo.getText().trim();
					try {
						toServer.writeUTF(toServerRegister);
						System.out.println(toServerRegister);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(jbtEnterOut.getText().trim() == "退出") {
					String toServerRegister = "0:9:" + jtfAssistantNo.getText().trim();
					try {
						toServer.writeUTF(toServerRegister);
						System.out.println(toServerRegister);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		/**---------------------------------会员事件的监听-------------------------------------------------------------**/
		jtfVIPNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(jtfVIPNo.getText().trim().compareToIgnoreCase("") != 0 && jtfVIPNo.getText().trim().equalsIgnoreCase("会员验证") == false && jlbSum.getText() == "0") {
					String toServerVIP = "2:5:" + jtfVIPNo.getText().trim();
					try {
						toServer.writeUTF(toServerVIP);
						System.out.println(toServerVIP);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		/**----------------------------------------------结算的事件监听---------------------------------------------------------**/
		account.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//UI receiptUI = new UI("receipt");
				StringBuilder str = new StringBuilder(200);
				str.append("3:");
				str.append(jlAssistantNo.getText());
				str.append(":");
				str.append(jlbTotal.getText().trim());
				str.append(":");
				str.append( vector.size());
				str.append(":");
				for(int i = 0; i < vector.size();i++) {
					str.append(vector.get(i).goodsNo() + ":");
					str.append(vector.get(i).getNumberOfGoods() + ":");
				}
				if(isVIPEntered == true) {
					str.append(jtfVIPNo.getText());
				}
				else {
					str.append("0");
				}
				try {
					String string = str.toString();
					toServer.writeUTF(string);
					System.out.println(string);
					if(jlbSum.getText().trim() != "")
						printReceipt();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		try {
			// Create a socket to connect to the server
			Socket socket = new Socket("192.168.253.2", 8000);
			System.err.println("connected");

		    // Create an input stream to receive data from the server
		    fromServer = new DataInputStream(socket.getInputStream());

		    // Create an output stream to send data to the server
		    toServer = new DataOutputStream(socket.getOutputStream());
		    
		    // Start a new thread for receiving messages
	        new Thread(this).start();
		}
		catch (IOException ex) {
		    System.out.println(ex);
		}	
	}
	
	@Override
	public void run(){
		while(true) {
			
			try {
				cal=Calendar.getInstance();  
				YMD.setText("日期:" + cal.get(Calendar.YEAR)+ "" + ":"+ cal.get(Calendar.MONTH)+ "" + ":"+ cal.get(Calendar.DATE));//获取当前的日期和时分
				HMS.setText("时分：" + cal.get(Calendar.HOUR_OF_DAY)+ "" + ":"+ cal.get(Calendar.MINUTE)+ "" );
				/*YMD.invalidate();
				YMD.validate();
				HMS.invalidate();
				HMS.validate();*/
				String sign = fromServer.readUTF();
				
				receiveFromServer = sign;
				
				receive = receiveFromServer.split("\\:");
				System.out.println(receiveFromServer);
				/**============找不到扫描的商品==================**/
				if(receive[0].equalsIgnoreCase("1101")) {
					jtfGoodsNo.setText("没有找到商品");
				}
				/**===========从服务器获取商品的属性信息===========**/
				if(receive[0].equalsIgnoreCase("1100")) {
					GoodsNo = jtfGoodsNo.getText().trim();
					GoodsNa = receive[1];
					priceForAll = Double.valueOf(receive[2]);
					priceForVIP = Double.valueOf(receive[3]);
					numberOfgoods = 1;
					if(isVIPEntered == false) {
						totalCash = numberOfgoods*priceForAll;
					}
					else
						totalCash = numberOfgoods*priceForVIP;
					BigDecimal totalCash1 = new BigDecimal(totalCash);  
					double totalCash2 = totalCash1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
					Goods goods = new Goods(GoodsNo,GoodsNa,priceForAll,priceForVIP,numberOfgoods);
					vector.addElement(goods);
					tableModel.addRow(new Object[]{goods.goodsNo(),goods.goodsNa(),goods.getPriceForAll(),goods.getPriceForVIP(),numberOfgoods,totalCash2});
					if(jlbTotal.getText().trim() != "") {
						double totalmoney1 = Double.valueOf(jlbTotal.getText().trim()) + totalCash2;
						jlbTotal.setText(String.valueOf(totalmoney1));
					}
					if(jlbTotal.getText().trim() == "") {
						jlbTotal.setText(String.valueOf(totalCash2));
					}
					if(jlbSum.getText().trim() != ""){
						int totalNumber1 = Integer.valueOf(jlbSum.getText().trim()) + 1;
						jlbSum.setText(String.valueOf(totalNumber1));
					}
					if(jlbSum.getText().trim() == ""){
						jlbSum.setText(String.valueOf(numberOfgoods));
					}
					jtfGoodsNo.setText("已扫描成功");
				}
				/**===================从服务器获取收银员的信息=========================**/
				if(receive[0].equalsIgnoreCase("010")) {
					jbtEnterOut.setText("退出");
					jtfAssistantNo.setText(receive[1]);
					jlAssistantNo.setText(receive[1]);
					welcomeFrame.setVisible(false);
					this.setVisible(true);					
				}
				if(receive[0].equalsIgnoreCase("011")) {
					jtfAssistantNo.setText("身份验证错误");
				}
				if(receive[0].equalsIgnoreCase("012")) {
					jtfAssistantNo.setText("未身份验证");
				}
				/**=========================从服务器获取会员的确认信息==============================**/
				if(receive[0].equalsIgnoreCase("250")) {
					isVIPEntered = true;
					jlVIPNa.setText(receive[1]);
					jlVIPSo.setText(receive[2]);
				}
				if(receive[0].equalsIgnoreCase("251")) {
					jtfVIPNo.setText("无该会员");
				}
				
				/**========================收银员退出成功=========================**/
				if(receive[0].equalsIgnoreCase("090")) {
					jtfAssistantNo.setText("请确认员工身份！");
					jbtEnterOut.setText("登录");
					jlAssistantNo.setText("");
				}
				
				if(receive[0].equalsIgnoreCase("30")) {
					//对有些信息进行清除/清空
					jlbTotal.setText("0.0");
					jlbSum.setText("0");
					vector.clear();
					//jTable1.removeAll();
					//for(int i = 0;i < vector.size();i++) {
					//	tableModel.removeRow(0);
					//}
					tableModel.setRowCount(0);
					isVIPEntered = false;
					jtfVIPNo.setText("会员验证");
					jlVIPNa.setText("");
					jlVIPSo.setText("");
					
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				try {
					Thread.sleep(1000);
					 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}


	/**===================一个函数，一个字符串是否在一个字符串数组中出现过========================================**/
	public int isExist(String str,Vector<Goods> vector) {
		for(int i = 0; i < vector.size();i++) {
			if(str.equalsIgnoreCase(vector.get(i).goodsNo())) {
				return i;
			}
		}
		return -1;
	}
	
	/**==========================打印小票的函数=========================================**/
	public void printReceipt() {
		File f = new File("receipt.txt");
		try {
			PrintWriter output = new PrintWriter(f);
			output.println("\t----超市收银系统----\n");
			output.println("==================================================");
			output.print("收银员编号：\t");
			output.println(jtfAssistantNo.getText().trim());
			output.print("日期：\t");
			output.println(cal.get(Calendar.YEAR)+ "" + ":"+ cal.get(Calendar.MONTH)+ "" + ":"+ cal.get(Calendar.DATE));
//			output.println("商品名\t会员单价\t普通单价\t数量\t金额");
			output.printf("%-10s\t%-8s%-8s%-4s%-10s", "商品名", "会员单价", "普通单价", "数量", "金额");
			output.println();
			output.println("==================================================");
			for(int i = 0; i< vector.size();i++) {
/*				output.print(vector.get(i).goodsNa() + "    ");
				output.print(vector.get(i).getPriceForVIP() + "    ");
				output.print(vector.get(i).getPriceForAll() + "    ");
				output.print(vector.get(i).getNumberOfGoods() + "    ");
	*/			output.printf("%-10s\t%-12.2f%-12.2f%-6d", vector.get(i).goodsNa(), vector.get(i).getPriceForVIP(), vector.get(i).getPriceForAll(), vector.get(i).getNumberOfGoods());
				if(isVIPEntered == true) {
					//output.print(vector.get(i).getPriceForVIP() + "\t");
					//output.print(vector.get(i).getNumberOfGoods());
					output.println(vector.get(i).getPriceForVIP() * vector.get(i).getNumberOfGoods());
					//output.println("=========================");
					//output.print("会员号：");
					//output.println(jtfVIPNo.getText().trim());
					//output.println("积分：\t" + jlVIPSo.getText().trim());
					//output.println("消费额：\t" + jlbTotal.getText().trim());
				}
				else {
					//output.print(vector.get(i).getPriceForAll() + "\t");
					//output.print(vector.get(i).getNumberOfGoods() + "\t");
					output.println(vector.get(i).getPriceForAll() * vector.get(i).getNumberOfGoods());
					//output.println("非会员");
					//output.println("消费额：\t" + jlbTotal.getText().trim());
				}
			}
			if(isVIPEntered == true) {
				output.println("==================================================");
				output.print("会员号：");
				output.println(jtfVIPNo.getText().trim());
				output.println("积分：\t" + jlVIPSo.getText().trim());
				output.println("消费额：\t" + jlbTotal.getText().trim());
			}
			else{
				output.println("==================================================");
				output.println("非会员");
				output.println("消费额：\t" + jlbTotal.getText().trim());
			}
			output.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
