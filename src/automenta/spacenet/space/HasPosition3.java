package automenta.spacenet.space;



import automenta.spacenet.var.vector.V3;


public interface HasPosition3 {
	
	/** position relative to super-space */	
	public V3 getPosition();
	
	/** position (available after being started) of its "absolute" position */
	//public V3 getAbsolutePosition();

	/** next "absolute" position, set on next frame update */
	//public V3 getNextAbsolutePosition();

	
	/**
	 * ux, uy are relative to rectangular shadow's absolute center and absolute size.  so (1, 1) is the upper right corner.
	 */
	//public Vector3 getAbsolutePosition(double d, double upscale, Vector3 ul);

	//public void getAbsoluteNormal(Vector3 normal);

}
