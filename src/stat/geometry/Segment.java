package stat.geometry;

import stat.Constants;

public class Segment {
	private Point point1 ;
	private Point point2 ;
	
	public Segment(Point point1, Point point2) {
		this.point1 = point1 ;
		this.point2 = point2 ;
		// TODO Auto-generated constructor stub
	}

	public Point getPoint1() {
		return point1;
	}

	public void setPoint1(Point point1) {
		this.point1 = point1;
	}
	
	public Point getPoint2() {
		return point2;
	}

	public void setPoint2(Point point2) {
		this.point2 = point2;
	}
	
	public boolean inSegment(Point point){
		return inSegment(point.getX()) ;
	}
	
	public boolean inSegment(double x){
		if (point1.getX() <= x && point2.getX() >= x)
			return true ;
		else
			return false ;
	}

	public double getSegmentLengthInX(){
		return point2.getX() - point1.getX();
	}
	
	public double getSegmentLengthInY(){
		return point2.getY() - point1.getY();
	}
		
	public Point inSegmentInersection(double x){
		double lengthX = this.getSegmentLengthInX() ;
		double lengthY = this.getSegmentLengthInY() ;
		
		if (lengthX< Constants.SMALL_EPSILON)
			return new Point(x, lengthY/2) ;
		else 
			return new Point(x, point1.getY() + (x - point1.getX()) * 
				(lengthY/lengthX)) ;
	}
}
