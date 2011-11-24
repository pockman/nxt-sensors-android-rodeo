/*
 * Unai Goñi Otegi / Università degli Studi di Padova / 2010-11
 */
package blueclean.bolas.eus;


// TODO: Auto-generated Javadoc
/**
 * The Class ScanPos.
 */
public class ScanPos {
	
	/** The mx. */
	double mx;
	
	/** The my. */
	double my;
	//boolean mpaint;
	
	/**
	 * Instantiates a new scan pos.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public ScanPos(double x,double y) {
		mx = x;
		my = y;
		//mpaint = paint;
	}
/**
 * Gets the mx.
 *
 * @return the mx
 */
public double getMx() {
		return mx;
	}
/**
 * Gets the my.
 *
 * @return the my
 */
public double getMy() {
		return my;
	}
//	public boolean isMpaint() {
//		return mpaint;
	//}
}