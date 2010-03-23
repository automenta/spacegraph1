/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.graph.ubigraph;

/**
 *
 * @author seh
 */
public class DemoUbigraph1 {

    public static void main(String[] args) {
        UbigraphClient graph = new UbigraphClient();
        
        int N = 10;
        int[] vertices = new int[N];

        for (int i = 0; i < N; ++i) {
            vertices[i] = graph.newVertex();
        }

        for (int i = 0; i < N; ++i) {
            graph.newEdge(vertices[i], vertices[(i + 1) % N]);
        }

        graph.newWindow();
    }
}
