package automenta.spacenet.var.graph.patterns;

import automenta.spacenet.var.graph.MemGraph;
import java.util.UUID;

public class MeshGraph extends MemGraph<String,String> {

	String[][] n;
	private int width;
	private int height;
	
	public MeshGraph(final int x, final int y, boolean isTorus) {
		super();

		this.width = x;
		this.height = y;

		n = new String[x][y];
		for (int i = 0; i < x; i++)
			for (int j = 0; j < y; j++) {
				n[i][j] = newNode();
                addNode(n[i][j]);
			}

		for (int i = 0; i < x; i++)
			for (int j = 0; j < y; j++) {
				if (!isTorus) {
					if (i < x-1)
						addEdge(newEdge(), n[i][j], n[i+1][j]);
				}
				else {
					addEdge(newEdge(), n[i][j], n[(i+1)%x][j]);
				}
				
				if (j < y-1)
					addEdge(newEdge(), n[i][j], n[i][j+1]);
			}


	}
	
	public Object get(int x, int y) { return n[x][y]; }

	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}

    public String newNode() { return UUID.randomUUID().toString(); }
    public String newEdge() { return UUID.randomUUID().toString(); }
}

