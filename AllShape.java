package mycad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AllShape implements Serializable {//implemants 实现接口
	
	private List<Line> lineList = new ArrayList<Line>();//定义一个直线的链表
	private List<Circle> circleList = new ArrayList<Circle>();//定义圆的链表，并初始化
	public List<Line> getLineList() {
		return lineList;
	}
	public void setLineList(List<Line> lineList) {
		this.lineList = lineList;
	}
	public List<Circle> getCircleList() {
		return circleList;
	}
	public void setCircleList(List<Circle> circleList) {
		this.circleList = circleList;
	}
}
