package mycad;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class MyPaint extends JFrame {
	private JPanel comPanel;// 新建一个面板用于放置操作控件
	private JRadioButton rbLine;// 定义画直线的单选按钮
	private JRadioButton rbCircle;// 定义画圆的单选按钮
	private JRadioButton rbSelect;//定义一个选择单选按钮
	private ButtonGroup group;// 定义按钮组合，目的是使组里面的按钮每次只能选择一个
	private DrawPanel drawPanel;// 定义一个画图的类
//	private int x1,y1;//定义直线的起点
//	private int x2,y2;//定义直线的终点
	private boolean isStarted = false;//当前的位置是赋值给第一个点还是第二个点 第一个点是true，第二个点是false
	private boolean isSlected = false;//用于判断是否选择中了某个线段，同时不是绘制 没有选中是false
	
	
	private Line line = null;//定义一条直线
	private JButton colorButton = null;//定义一个颜色按钮
	private Color currentColor = Color.black;//定义当前的颜色
	private int drawType = 0;//初始化为0，默认画直线
	
	private Circle circle = null; //定义一个圆
	private JButton widthButton = null;//定义选择的宽度的按钮
	private JButton deleteButton = null;//定义选择的宽度的按钮
	private JButton saveButton = null;//定义一个保存按钮
	private JButton openButton = null;//定义一个打开按钮
	private int currentWidth = 1;//定义当前的宽度为1
	private Line currentSelectLine = null;//当前选中的直线
	private Line currentSelectLineTemp = null;//鼠标经过后被捕捉到的直线
	private Circle currentSelectCircle = null;//当前选中的圆
	private Circle currentSelectCircleTemp = null;//鼠标经过后被捕捉到的圆
	private AllShape allShape = new AllShape();//定义所有图形的对象
//	private 

	public MyPaint() {
		// TODO Auto-generated constructor stub
		this.setSize(800, 600);// 设置窗口的大小
		this.setLocationRelativeTo(null);// 设置窗口居中
		this.setTitle("My CAD!");// 设置标题
		ImageIcon icon = new ImageIcon("");//定义并导入图像
		this.setIconImage(icon.getImage());//将图像设置为窗口的图标
		comPanel = new JPanel();
		
		rbLine = new JRadioButton("直线");// 初始化直线按钮
		rbLine.setSelected(true);
		comPanel.add(rbLine);// 添加到面板中
		
		// this.add(rbLine);//添加到窗口当中
		rbCircle = new JRadioButton("圆");
		// this.add(rbCircle);
		rbSelect = new JRadioButton("选择");//初始化选择按钮
		comPanel.add(rbSelect);//将选择按钮添加到面板中
		comPanel.add(rbCircle);
		
		colorButton = new JButton("选择颜色");
		comPanel.add(colorButton);//将颜色选择按钮添加到命令面板当中
		colorButton.setBackground(currentColor);
		deleteButton = new JButton("删除");//初始化删除按钮
		comPanel.add(deleteButton);		
		
		this.setLayout(new BorderLayout());// 将窗口设置布局为BorderLayout
		this.add(comPanel, BorderLayout.NORTH);// 将面板添加到窗口当中
		
		group = new ButtonGroup();// 初始化按钮组
		group.add(rbLine);
		group.add(rbCircle);// 将按钮添加到组
		group.add(rbSelect);//添加选择按钮
		
		drawPanel = new DrawPanel();// 初始化绘图面板
		this.add(drawPanel, BorderLayout.CENTER);// 添加一个绘图面板
//		Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);//添加鼠标的移动动作
//		drawPanel.setCursor(cursor);
		
		//颜色按钮添加事件侦听
		colorButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				currentColor = JColorChooser.showDialog(null, "选择颜色", currentColor);
				colorButton.setBackground(currentColor);//获取颜色后将颜色更新
			}
		});
		//
		rbLine.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				drawType = 0;//改变当前的状态为绘制直线
				isSlected = false;
				Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);//添加鼠标的移动动作
				drawPanel.setCursor(cursor);
			}
		});
		//
		rbCircle.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				drawType = 1;//当前的状态为绘制圆
				isSlected = false;
				Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);//添加鼠标的移动动作
				drawPanel.setCursor(cursor);
			}
		});
		rbSelect.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				drawType = 100;//当前的状态为选择线条
				isSlected = true;
				Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);//添加鼠标的移动动作 默认箭头
				drawPanel.setCursor(cursor);
				//this.setCursor(cursor);
			}
		});
		//删除按钮添加事件侦听
		deleteButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(currentSelectLine == null && currentSelectCircle == null)//如果当前没有选中直线也没有选中圆
				{
					JOptionPane.showMessageDialog(null, "请先选中图形，再进行删除！");//输出直线提示
				}
				else if(currentSelectCircle != null)
				{
					
					allShape.getCircleList().remove(currentSelectCircle);
					currentSelectCircle = null;
					drawPanel.repaint();
				}
				else if(currentSelectLine != null)
				{
					allShape.getLineList().remove(currentSelectLine);
					currentSelectLine = null;
					drawPanel.repaint();
				}
			}
		});
		
		widthButton = new JButton("宽度为：1");
		comPanel.add(widthButton);//在操作面板中添加宽度的按钮
		widthButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				final JDialog jd = new JDialog();//定义一个对话框并初始化
				jd.setSize(200,100);
				jd.setLocationRelativeTo(null);
				jd.setTitle("设置线条宽度");
				jd.getContentPane().setLayout(new GridLayout(2,2));//设置对话框的布局为网格布局
				jd.add(new JLabel("线条的宽度"));//定义宽度的标签
				final JTextField widthTF = new JTextField();//定义输入宽度的文本框
				jd.add(widthTF);
				
				JButton okButton = new JButton("确定");//定义一个确定的按钮
				jd.add(okButton);
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						String str = widthTF.getText();//获取文本框的输入宽度
						try {
							currentWidth = Integer.parseInt(str);//将字符串转化成整形
							widthButton.setText("线条宽度：" + currentWidth);
							jd.setVisible(false);
						}catch(Exception e1)
						{
							JOptionPane.showMessageDialog(null, "您输入有误，请重新输入！");
						}
					}
				});
				JButton cancelButton = new JButton("取消");//定义一个确定的按钮
				cancelButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						jd.setVisible(false);
					}
				});
				jd.add(cancelButton);
				jd.setVisible(true);
			}
		});//为按钮添加响应函数
		saveButton = new JButton("保存");//初始化保存按钮
		comPanel.add(saveButton);//将保存按钮添加到操作面板中
		saveButton.addActionListener(new ActionListener() {//添加按钮的侦听事件
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser chooser = new JFileChooser();//创建文件选择对话框
				FileNameExtensionFilter filter = new FileNameExtensionFilter("*.cad", "cad");//定义并初始化
				//文件过滤器
				chooser.setFileFilter(filter);//为文件选择器设置文件的过滤器
				chooser.showSaveDialog(null);//显示文件选择器
				File file  = chooser.getSelectedFile();//获取选择器所选择的文件
				if(!file.getPath().endsWith(".cad"))
				{
					file = new File(file.getPath()+".cad");//新建文件以.cad文件作为扩展名赋给原来文件
				}
				try//尝试保存图形
				{
					FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());//定义文件输出流
					//并以所选择的文件路径作为输出位置
					ObjectOutputStream oos = new ObjectOutputStream(fos);//对象选择路径 定义对象输出流
					oos.writeObject(allShape);//将图形对象写入输出流;
					oos.close();//关闭对象流
					fos.close();//关闭文件流
					//首先定义文件存储的位置，随后创建对象流，将所有的链表对象放到对象流上，最后关闭对象流
				}
				catch(IOException e1)//如果保存失败 抛出异常
				{
					e1.printStackTrace();
				}
			}
		});
		
		openButton = new JButton("打开文件");
		comPanel.add(openButton);
		openButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser chooser = new JFileChooser();//创建文件选择器
				FileNameExtensionFilter filter = new FileNameExtensionFilter("*.cad", "cad");//定义并初始化
				//文件过滤器
				chooser.setFileFilter(filter);//为文件选择器设置文件的过滤器
				chooser.showOpenDialog(null);//显示文件选择器
				File file = chooser.getSelectedFile();//获取所选择的文件
				try{//尝试打开文件
					FileInputStream fis = new FileInputStream(file.getAbsolutePath());//新建文件输入流
					ObjectInputStream ois = new ObjectInputStream(fis);//定义一个文件对象输入流
					allShape = (AllShape) ois.readObject();//从对象输入流里读对象
					ois.close();//关闭对象流
					fis.close();//关闭文件流
					drawPanel.repaint();//刷新面板
				}catch(IOException e1)//文件的io异常 比如 文件没找到
				{
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {//文件的对象异常 读出来的数据不是Allshape类型
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
			}
		});
		
	}

	// java 中特有还可以在一个类中定义另一个类, 用户自定义的类
	class DrawPanel extends JPanel { // 画图的类
		public DrawPanel() { // 构造函数
			// TODO Auto-generated constructor stub
			this.setBackground(Color.white);// 设置面板的背景颜色为黑色
			MouseA mouseA = new MouseA();//新创建鼠标监听器并初始化
			//可以在类的外部另一个类中创建新的对象
			this.addMouseListener(mouseA);
			//this.addMouseListener(new MouseA());//添加鼠标的基本动作的监听
			Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);//添加鼠标的移动动作
			this.setCursor(cursor);
			this.addMouseMotionListener(new MouseB());//添加鼠标移动本动作的监听
		}
		//本函数用于引出画笔
		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);
			//此处 g强制类型转换，根据函数参数来执行
			//2D画笔用于
			Graphics2D g2 = (Graphics2D) g;
			//先判断type和先判断是否开始有什么关联？
			
			for(int i = 0; i<allShape.getLineList().size(); i++){
				Line l = allShape.getLineList().get(i);
				g2.setColor(l.color);//设置g2的颜色
				BasicStroke bs = new BasicStroke(l.width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);//设置直线的宽度 第二个参数是曲线端点的属性，第三个参数相交直线的属性
				g2.setStroke(bs);//设置线条的属性 包括宽度
				g2.drawLine(l.x1, l.y1, l.x2, l.y2);
			}
			
			for(int j = 0; j<allShape.getCircleList().size(); j++){
				Circle c = allShape.getCircleList().get(j);
				g2.setColor(c.color);//设置g2的颜色
//				g2.drawOval(c.x1, c.y1, c.x2, c.y2); //画椭圆
				BasicStroke bs = new BasicStroke(c.width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);//设置直线的宽度 第二个参数是曲线端点的属性，第三个参数相交直线的属性
				g2.setStroke(bs);//设置线条的属性 包括宽度
				int r = (int) Math.sqrt((c.x1-c.x2)*(c.x1-c.x2)+(c.y1-c.y2)*(c.y1-c.y2));//计算圆的半径
				g2.drawOval(c.x1-r, c.y1-r, 2*r, 2*r); //画椭圆  第一个参数是椭圆左上角的X坐标，第二个参数是椭圆
				//左上角的Y坐标 ，第三个第四个为长和宽
			}
			if(isStarted && !isSlected)
			{
				//开始绘制橡皮筋
				if(drawType == 0)
				{
					g2.setColor(currentColor);
					BasicStroke bs = new BasicStroke(currentWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);//设置直线的宽度 第二个参数是曲线端点的属性，第三个参数相交直线的属性
					g2.setStroke(bs);//设置线条的属性 包括宽度
					g2.drawLine(line.x1, line.y1, line.x2, line.y2);//绘制橡皮筋
				}
				else if(drawType == 1)
				{
					g2.setColor(currentColor);
					BasicStroke bs = new BasicStroke(currentWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);//设置直线的宽度 第二个参数是曲线端点的属性，第三个参数相交直线的属性
					g2.setStroke(bs);//设置线条的属性 包括宽度
					int r = getDistance(circle.x1, circle.y1, circle.x2, circle.y2);
//					int r = (int) Math.sqrt((circle.x1-circle.x2)*(circle.x1-circle.x2)+(circle.y1-circle.y2)*(circle.y1-circle.y2));//计算圆的半径
					g2.drawOval(circle.x1-r, circle.y1-r,2*r, 2*r); //画椭圆???第二个参数为什么
				}
				System.out.println("绘制橡皮筋");
				
			}
			if(currentSelectLineTemp != null)//如果鼠标为选择状态 鼠标接近直线
			{
				g2.setColor(Color.red);//设置画笔的颜色为红色
				g2.drawRect(currentSelectLineTemp.x1-5, currentSelectLineTemp.y1-5, 10, 10);//在直线的端点绘制正方形
				g2.drawRect(currentSelectLineTemp.x2-5, currentSelectLineTemp.y2-5, 10, 10);//在直线的端点绘制正方形
			}
			else if(currentSelectCircleTemp != null)//如果鼠标为选择状态 鼠标接近圆
			{
				g2.setColor(Color.red);//设置画笔的颜色为红色
				int r = getDistance(currentSelectCircleTemp.x1, currentSelectCircleTemp.y1, currentSelectCircleTemp.x2, currentSelectCircleTemp.y2);
				g2.drawOval(currentSelectCircleTemp.x1-r-5, currentSelectCircleTemp.y1-5,10, 10); //在圆的上下左右绘制圆
				g2.drawOval(currentSelectCircleTemp.x1+r-5, currentSelectCircleTemp.y1-5,10, 10); 
				g2.drawOval(currentSelectCircleTemp.x1-5, currentSelectCircleTemp.y1-r-5,10, 10); 
				g2.drawOval(currentSelectCircleTemp.x1-5, currentSelectCircleTemp.y1+r-5,10, 10); 
			}
			
			if(currentSelectLine != null)//如果鼠标为选择状态 鼠标接近直线
			{
				g2.setColor(Color.blue);//设置画笔的颜色为蓝色
				g2.drawRect(currentSelectLine.x1-5, currentSelectLine.y1-5, 10, 10);//在直线的端点绘制正方形
				g2.drawRect(currentSelectLine.x2-5, currentSelectLine.y2-5, 10, 10);//在直线的端点绘制正方形
			}
			
			
			if(currentSelectCircle != null)//如果鼠标为选择状态 鼠标接近圆
			{
				g2.setColor(Color.blue);//设置画笔的颜色为蓝色
				int r = getDistance(currentSelectCircle.x1, currentSelectCircle.y1, currentSelectCircle.x2, currentSelectCircle.y2);
				g2.drawOval(currentSelectCircle.x1-r-5, currentSelectCircle.y1-5,10, 10); 
				g2.drawOval(currentSelectCircle.x1+r-5, currentSelectCircle.y1-5,10, 10); 
				g2.drawOval(currentSelectCircle.x1-5, currentSelectCircle.y1-r-5,10, 10); 
				g2.drawOval(currentSelectCircle.x1-5, currentSelectCircle.y1+r-5,10, 10); 
			}
			//g2.drawOval(200, 200, 300, 300);
		}
	}
	int getDistance(int x1,int y1,int x2, int y2)//计算距离的函数
	{
		int r = (int) Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));//计算圆的半径
		return r;//返回直线两点之间的距离
	}
	class MouseA extends MouseAdapter{//定义一个鼠标的基本适配器 监听器
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mousePressed(e);
			System.out.println("按下了鼠标 x:"+e.getX()+ ",y:"+e.getY() );
			if(!isStarted && !isSlected)//如果没有开始画直线，赋值给第一个点 同时此时必须满足 没有选中线条
			{
				if(drawType == 0)//画直线
				{
					line = new Line();
					line.width = currentWidth;//把当前线条的宽度赋给直线
					line.x1 = e.getX();
					line.y1 = e.getY();
					
				}
				else if(drawType == 1)
				{
					circle = new Circle();
					circle.x1 = e.getX();//获取圆心的坐标
					circle.y1 = e.getY();
					circle.width = currentWidth;
				}
				isStarted = true;
				
			}
			else if(isStarted && !isSlected)
			{
				if(drawType == 0)
				{
					line.x2 = e.getX();
					line.y2 = e.getY();
					//line.width = currentWidth;//把当前线条的宽度赋给直线
					line.color = currentColor;//将当前选择的颜色赋给当前的直线
					allShape.getLineList().add(line);
					System.out.println("加入线链表");
				}
				else if(drawType == 1)
				{
					circle.x2 = e.getX();
					circle.y2 = e.getY();
					circle.color = currentColor;//将当前选择的颜色赋给当前的直线
					
					allShape.getCircleList().add(circle);
					System.out.println("加入圆链表");
				}
				
				isStarted = false;
				drawPanel.repaint();//重新绘制图形
			}
			if(drawType == 100)//如果当前是选择状态
			{
				if(currentSelectLineTemp != null)//鼠标接近直线
				{
					currentSelectLine = currentSelectLineTemp;//确认选中直线
					currentSelectLineTemp = null;// 清空临时的直线
					//isSlected = true;
					drawPanel.repaint();//刷新绘图区域屏幕
				}
				else if(currentSelectCircleTemp != null)//鼠标接近圆
				{
					currentSelectCircle = currentSelectCircleTemp;//确认选中圆
					currentSelectCircleTemp = null;// 清空临时的圆
					//isSlected = true;
					drawPanel.repaint();//刷新绘图区域屏幕
				}
			}
		}
		
	}
	class MouseB extends MouseMotionAdapter{
		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			super.mouseMoved(e);
			if(isStarted)
			{
				if(drawType == 0)//判断是否已经开始绘图
				{
					line.x2 = e.getX();//获取橡皮筋的终点坐标X
					line.y2 = e.getY();//获取橡皮筋的终点坐标Y
//					drawPanel.repaint();//刷新面板
				}
				else if(drawType == 1)
				{
					circle.x2 = e.getX();//获取橡皮筋的终点坐标X
					circle.y2 = e.getY();//获取橡皮筋的终点坐标Y
					
				}
				drawPanel.repaint();//刷新面板
			}
			if(drawType == 100)//当前的状态为选择状态
			{
				for(int i = 0; i<allShape.getLineList().size(); i++)//遍历每一条直线
				{
					Line line  = allShape.getLineList().get(i);//获取直线 用三角形内部的点来判断直线
					int x1_x2 = getDistance(line.x1, line.y1, line.x2, line.y2);//计算直线两个端点之间的距离
					int ex_x1 = getDistance(e.getX(), e.getY(), line.x1, line.y1);//计算鼠标到直线一个端点的距离
					int ex_x2 = getDistance(e.getX(), e.getY(), line.x2	, line.y2);//计算鼠标到直线另一个端点的距离
					if(ex_x1 + ex_x2 < x1_x2 + 2)
					{
						currentSelectLineTemp = line;//把捕捉到的直线赋给当前的直线
						System.out.println("捕捉到了");
						break;//退出整个循环体
					}
					else{
						currentSelectLineTemp = null;//如果没有选中直线，当前选择为空
					}
				}
				for(int j = 0; j<allShape.getCircleList().size(); j++)//遍历每一个圆
				{
					Circle circle  = allShape.getCircleList().get(j);//获取直线 用三角形内部的点来判断直线
					int r = getDistance(circle.x1, circle.y1, circle.x2, circle.y2);//半径的长度
					int ex_c1 = getDistance(circle.x1, circle.y1, e.getX(), e.getY());//计算鼠标与圆心之间的长度
					if(ex_c1>=0.9*r && ex_c1<=1.1*r)
					{
						currentSelectCircleTemp = circle;//把捕捉到的直线赋给当前的直线
						System.out.println("捕捉到了");
						break;//退出整个循环体
					}
					else{
						currentSelectCircleTemp = null;
					}
				}
				drawPanel.repaint();//刷新绘图面板
			}
		}
		
	}

}
