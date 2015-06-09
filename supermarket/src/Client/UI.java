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
	//����һ��ȫ�ֱ�������VIP�û�ȷ���Լ������֮ǰΪfalse��ȷ���Լ������֮��Ϊtrue
	private static boolean isVIPEntered = false;
	//�����Ա����ı��򡣿��������Ա���
	private static JTextField jtfVIPNo = new JTextField("��Ա��֤");
	//�����Ա�����ı�ǩ
	private static JLabel jlVIPNa = new JLabel();
	//�����Ա�Ļ��ֱ�ǩ
	private static JLabel jlVIPSo = new JLabel();
	//������������Ա��ŵ��ı���
	private static JTextField jtfAssistantNo = new JTextField("��ȷ��Ա����ݣ�");
	//����Ա��½���˳���ť
	private static JButton jbtEnterOut = new JButton("��¼");
	//private static JButton jbtAssistant = new JButton("��ȷ����ݣ�");
	//��������Ա��ŵı�ǩ
	private static JLabel jlAssistantNo = new JLabel();
	//����¼����Ʒ��ŵ��ı���
	private static JTextField jtfGoodsNo = new JTextField();
	//����ϼƽ��ı�ǩ
	private static JLabel jlbTotal1 = new JLabel("�ϼƽ�");
	private static JLabel jlbTotal = new JLabel("0.0");
	//����ϼ������ı�ǩ
	private static JLabel jlbSum1 = new JLabel("�ϼ�������");
	private static JLabel jlbSum = new JLabel("0");
	
	//�����ı�ǩ
	private static JLabel jlbl = new JLabel("������������ϵͳ");
	//��������Ĵ�С
	private Font font = new Font("SansSerif",Font.BOLD,30);
	private Font font1 = new Font("SansSerif",Font.BOLD,25);
	//����������ʾ
	private String[] columnNames = {"��Ʒ���","��Ʒ��","��ͨ����/Ԫ","��Ա����/Ԫ","��Ʒ����*1","���/Ԫ"};
	//������ݶ���
	private static Object[][] data = new Object[0][6];
	//���ĸ������ԵĶ���
	private String GoodsNo = new String();
	private String GoodsNa = new String();
	private double priceForAll;
	private double priceForVIP;
	private int numberOfgoods;
	private double totalCash;

	//����һ��vector,�洢ÿ����Ʒ����Ŀ��Ϣ
	private static Vector<Goods> vector = new Vector<Goods>();
	
	//private Object[] o = new Object[]{GoodsNo,GoodsNa,priceForAll,priceForVIP,numberOfgoods,totalCash};
	//�����Ƶ����������
	private DataInputStream fromServer;
	private DataOutputStream toServer;
	//����һ���ַ�����ӷ������յ�������
	private static String receiveFromServer;
	//���ӷ��������յ������ݽ��л���
	private static String[] receive;
	
	//����һ��ģ��
	private DefaultTableModel tableModel = new DefaultTableModel(data,columnNames);
	//�����
	private JTable jTable1 = new JTable(tableModel);
	//jTable1���и�	
	private JSpinner jspiRowHeight = new JSpinner(new SpinnerNumberModel(35,1,50,1));
	//����ʱ��ı���
	private static Calendar cal=Calendar.getInstance();  
	private static JLabel YMD = new JLabel("����:" + cal.get(Calendar.YEAR)+ "" + ":"+ cal.get(Calendar.MONTH)+ "" + ":"+ cal.get(Calendar.DATE));//��ȡ��ǰ�����ں�ʱ��
	private static JLabel HMS = new JLabel("ʱ�֣�" + cal.get(Calendar.HOUR_OF_DAY)+ "" + ":"+ cal.get(Calendar.MINUTE)+ "");
	//���㰴ť
	private static JButton account = new JButton("����");
	
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
		JLabel jLabel2 = new JLabel("��������");
		jLabel2.setBounds(10, 100, 260, 40);
		jLabel2.setBorder(new LineBorder(Color.BLACK));
		welcomeFrame.add(jLabel2);
		jtfAssistantNo = new JPasswordField();
		jtfAssistantNo.setBounds(10, 140, 260, 40);
		welcomeFrame.add(jtfAssistantNo);
		
		
		
		/*�����--������������ϵͳ--*/
		JPanel jpOutline = new JPanel();
		jlbl.setFont(font);
		jpOutline.add(jlbl);
		
		/*�м�����ⲿ��*/
		JPanel jpMainField = new JPanel();
		jpMainField.setLayout(new BorderLayout(1,1));
		//���岿�ֵ����沿��
		JPanel jpInfor = new JPanel();
		jpInfor.setLayout(new GridLayout(1,4,40,5));
		//����4��Panel,�ֱ��ǻ�Ա�ţ���Ա������Ա���֣�����Ա��
		JPanel jpVIPNo = new JPanel();
		jpVIPNo.setLayout(new BorderLayout(1,1));
		jpVIPNo.add(new JLabel("��Ա�ţ�"),BorderLayout.WEST);
		jpVIPNo.add(jtfVIPNo,BorderLayout.CENTER);
		JPanel jpVIPNa = new JPanel();
		jpVIPNa.setLayout(new BorderLayout(1,1));
		jpVIPNa.add(new JLabel("��Ա����"),BorderLayout.WEST);
		jpVIPNa.add(jlVIPNa,BorderLayout.EAST);
		JPanel jpVIPSo = new JPanel();
		jpVIPSo.setLayout(new BorderLayout(1,1));
		jpVIPSo.add(new JLabel("��Ա���֣�"),BorderLayout.WEST);
		jpVIPSo.add(jlVIPSo,BorderLayout.EAST);
		JPanel jpAssistantNo = new JPanel();
		jpAssistantNo.setLayout(new BorderLayout(1,1));
		//jpAssistantNo.add(new JLabel("�����֤��"),BorderLayout.WEST);
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
		
		
		
		
		//���岿�ֵ��м䲿��
		JPanel jpGoods = new JPanel();
		jpGoods.setLayout(new BorderLayout());
		jTable1.setRowHeight(((Integer)(jspiRowHeight.getValue())).intValue());
		jpGoods.add(new JScrollPane(jTable1),BorderLayout.NORTH);
		jpMainField.add(jpGoods,BorderLayout.CENTER);
		
		//��������沿��
		JPanel jpButtom = new JPanel();
		jpButtom.setLayout(new GridLayout(1,2));
		//jpButtom.setLayout(new BorderLayout());
		JPanel jpInputArea = new JPanel();
		jpInputArea.setLayout(new GridLayout(2,1));
//		JPanel jpInput = new JPanel();
/*		jpInput.setLayout(new GridLayout(1,3));
		jpInput.add(new JLabel("¼���"));
		jpInput.add(jtfGoodsNo);
		jpInput.add(new JLabel("���س�������"));
		jpInputArea.add(jpInput);
		jpInputArea.add(new JLabel("                                           ¼����Ʒ���"));*/
//		jpInputArea.setLayout(new GridLayout(2,1));
		jpInputArea.setLayout(null);
		
		JPanel jpInput = new JPanel();
/*		jpInput.setLayout(new GridLayout(1,3));
		jpInput.add(new JLabel("¼���"));
		jpInput.add(jtfGoodsNo);
		jpInput.add(new JLabel("���س�������"));*/
		
		jpInput.setLayout(null);
		JLabel jl1 = new JLabel("��Ʒ���");
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
/*		JLabel jl3 = new JLabel("¼����Ʒ���");
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
		
		/*�·�����ʾ����*/
		JPanel jpDisplay = new JPanel();
		jpDisplay.setLayout(new GridLayout(1,2));
		JPanel jpNum = new JPanel();
		jpNum.setLayout(new GridLayout(1,2));
		jpNum.add(new JLabel("����Ա���ţ�"));
		jpNum.add(jlAssistantNo);
		JPanel jpTime = new JPanel();
		jpTime.setLayout(new GridLayout(1,2));
		//jpTime.add(new JLabel("����:"));
		 
		jpTime.add(YMD);//��ȡ��ǰ�����ں�ʱ��
		jpTime.add(HMS);//
		jpDisplay.add(jpNum);
		jpDisplay.add(jpTime);
		
		/*��������������ַŵ�һ��Panel��*/
		setLayout(new BorderLayout(2,1));
		add(jpOutline,BorderLayout.NORTH);
		add(jpMainField,BorderLayout.CENTER);
		add(jpDisplay,BorderLayout.SOUTH);
		
		
		/**---------------ɨ����Ʒ���¼��������س�����---------------**/
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
					//��ʾ�ܵ������ͽ��
					double totalMoney = 0;//�ܵ���Ҫ֧����Ǯ��
					int totalNumber = 0;//�ܵĹ�����Ʒ������
					for(int j = 0; j < vector.size();j++) {
						totalMoney += vector.get(j).getTotalForAll(); 
						totalNumber += vector.get(j).getNumberOfGoods();
					}
					
					jlbTotal.setText(String.valueOf(totalMoney));
					jlbSum.setText(String.valueOf(totalNumber));
				}
			}
		});
		/**------------------------------����Ա��¼���¼�����--------------------------------------------------------**/
		jbtEnterOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(jbtEnterOut.getText().trim() == "��¼" && jtfAssistantNo.getText().trim().compareToIgnoreCase("") != 0) {
					String toServerRegister = "0:1:" + jtfAssistantNo.getText().trim();
					try {
						toServer.writeUTF(toServerRegister);
						System.out.println(toServerRegister);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(jbtEnterOut.getText().trim() == "�˳�") {
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
				if(jbtEnterOut.getText().trim() == "��¼" && jtfAssistantNo.getText().trim().compareToIgnoreCase("") != 0) {
					String toServerRegister = "0:1:" + jtfAssistantNo.getText().trim();
					try {
						toServer.writeUTF(toServerRegister);
						System.out.println(toServerRegister);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if(jbtEnterOut.getText().trim() == "�˳�") {
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
		/**---------------------------------��Ա�¼��ļ���-------------------------------------------------------------**/
		jtfVIPNo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(jtfVIPNo.getText().trim().compareToIgnoreCase("") != 0 && jtfVIPNo.getText().trim().equalsIgnoreCase("��Ա��֤") == false && jlbSum.getText() == "0") {
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
		
		/**----------------------------------------------������¼�����---------------------------------------------------------**/
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
				YMD.setText("����:" + cal.get(Calendar.YEAR)+ "" + ":"+ cal.get(Calendar.MONTH)+ "" + ":"+ cal.get(Calendar.DATE));//��ȡ��ǰ�����ں�ʱ��
				HMS.setText("ʱ�֣�" + cal.get(Calendar.HOUR_OF_DAY)+ "" + ":"+ cal.get(Calendar.MINUTE)+ "" );
				/*YMD.invalidate();
				YMD.validate();
				HMS.invalidate();
				HMS.validate();*/
				String sign = fromServer.readUTF();
				
				receiveFromServer = sign;
				
				receive = receiveFromServer.split("\\:");
				System.out.println(receiveFromServer);
				/**============�Ҳ���ɨ�����Ʒ==================**/
				if(receive[0].equalsIgnoreCase("1101")) {
					jtfGoodsNo.setText("û���ҵ���Ʒ");
				}
				/**===========�ӷ�������ȡ��Ʒ��������Ϣ===========**/
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
					jtfGoodsNo.setText("��ɨ��ɹ�");
				}
				/**===================�ӷ�������ȡ����Ա����Ϣ=========================**/
				if(receive[0].equalsIgnoreCase("010")) {
					jbtEnterOut.setText("�˳�");
					jtfAssistantNo.setText(receive[1]);
					jlAssistantNo.setText(receive[1]);
					welcomeFrame.setVisible(false);
					this.setVisible(true);					
				}
				if(receive[0].equalsIgnoreCase("011")) {
					jtfAssistantNo.setText("�����֤����");
				}
				if(receive[0].equalsIgnoreCase("012")) {
					jtfAssistantNo.setText("δ�����֤");
				}
				/**=========================�ӷ�������ȡ��Ա��ȷ����Ϣ==============================**/
				if(receive[0].equalsIgnoreCase("250")) {
					isVIPEntered = true;
					jlVIPNa.setText(receive[1]);
					jlVIPSo.setText(receive[2]);
				}
				if(receive[0].equalsIgnoreCase("251")) {
					jtfVIPNo.setText("�޸û�Ա");
				}
				
				/**========================����Ա�˳��ɹ�=========================**/
				if(receive[0].equalsIgnoreCase("090")) {
					jtfAssistantNo.setText("��ȷ��Ա����ݣ�");
					jbtEnterOut.setText("��¼");
					jlAssistantNo.setText("");
				}
				
				if(receive[0].equalsIgnoreCase("30")) {
					//����Щ��Ϣ�������/���
					jlbTotal.setText("0.0");
					jlbSum.setText("0");
					vector.clear();
					//jTable1.removeAll();
					//for(int i = 0;i < vector.size();i++) {
					//	tableModel.removeRow(0);
					//}
					tableModel.setRowCount(0);
					isVIPEntered = false;
					jtfVIPNo.setText("��Ա��֤");
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


	/**===================һ��������һ���ַ����Ƿ���һ���ַ��������г��ֹ�========================================**/
	public int isExist(String str,Vector<Goods> vector) {
		for(int i = 0; i < vector.size();i++) {
			if(str.equalsIgnoreCase(vector.get(i).goodsNo())) {
				return i;
			}
		}
		return -1;
	}
	
	/**==========================��ӡСƱ�ĺ���=========================================**/
	public void printReceipt() {
		File f = new File("receipt.txt");
		try {
			PrintWriter output = new PrintWriter(f);
			output.println("\t----��������ϵͳ----\n");
			output.println("==================================================");
			output.print("����Ա��ţ�\t");
			output.println(jtfAssistantNo.getText().trim());
			output.print("���ڣ�\t");
			output.println(cal.get(Calendar.YEAR)+ "" + ":"+ cal.get(Calendar.MONTH)+ "" + ":"+ cal.get(Calendar.DATE));
//			output.println("��Ʒ��\t��Ա����\t��ͨ����\t����\t���");
			output.printf("%-10s\t%-8s%-8s%-4s%-10s", "��Ʒ��", "��Ա����", "��ͨ����", "����", "���");
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
					//output.print("��Ա�ţ�");
					//output.println(jtfVIPNo.getText().trim());
					//output.println("���֣�\t" + jlVIPSo.getText().trim());
					//output.println("���Ѷ\t" + jlbTotal.getText().trim());
				}
				else {
					//output.print(vector.get(i).getPriceForAll() + "\t");
					//output.print(vector.get(i).getNumberOfGoods() + "\t");
					output.println(vector.get(i).getPriceForAll() * vector.get(i).getNumberOfGoods());
					//output.println("�ǻ�Ա");
					//output.println("���Ѷ\t" + jlbTotal.getText().trim());
				}
			}
			if(isVIPEntered == true) {
				output.println("==================================================");
				output.print("��Ա�ţ�");
				output.println(jtfVIPNo.getText().trim());
				output.println("���֣�\t" + jlVIPSo.getText().trim());
				output.println("���Ѷ\t" + jlbTotal.getText().trim());
			}
			else{
				output.println("==================================================");
				output.println("�ǻ�Ա");
				output.println("���Ѷ\t" + jlbTotal.getText().trim());
			}
			output.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
