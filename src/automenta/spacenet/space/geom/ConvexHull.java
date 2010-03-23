package automenta.spacenet.space.geom;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.util.geom.BufferUtils;
import com.bulletphysics.linearmath.convexhull.HullDesc;
import com.bulletphysics.linearmath.convexhull.HullFlags;
import com.bulletphysics.linearmath.convexhull.HullLibrary;
import com.bulletphysics.linearmath.convexhull.HullResult;
import java.nio.FloatBuffer;
import java.util.List;
import javax.vecmath.Vector3f;

public class ConvexHull extends Box {

    Mesh mesh = new Mesh();
    Vector3 center = new Vector3();
    HullLibrary hl = new HullLibrary();

    public ConvexHull(List<Vector3f> vertices) {
        super(BoxShape.Empty);
        mesh.setModelBound(new BoundingSphere());
        add(mesh);
        setPoints(vertices);
        
    }

    public void setPoints(List<Vector3f> vertices) {

        center.set(0, 0, 0);
        for (Vector3f v : vertices) {
            center.addLocal(v.x, v.y, v.z);
        }
        center.multiplyLocal(1.0 / vertices.size());
        
        HullDesc hullDesc = new HullDesc(HullFlags.TRIANGLES, vertices.size(), vertices);
        HullResult result = new HullResult();
        hl.createConvexHull(hullDesc, result);

        MeshData meshData = new MeshData();
        final int verts = result.numOutputVertices;
        meshData.setVertexBuffer(BufferUtils.createVector3Buffer(verts));
        meshData.setNormalBuffer(BufferUtils.createVector3Buffer(verts));
        final FloatBuffer tbuf = BufferUtils.createVector2Buffer(verts);
        meshData.setTextureBuffer(tbuf, 0);
        final int tris = result.numFaces;
        meshData.setIndexBuffer(BufferUtils.createIntBuffer(tris * 3));
        meshData.getVertexBuffer().clear();
        Vector3f n = new Vector3f();
        for (Vector3f v : result.outputVertices) {
            meshData.getVertexBuffer().put(v.x);
            meshData.getVertexBuffer().put(v.y);
            meshData.getVertexBuffer().put(v.z);
            float dx = v.x - center.getXf();
            float dy = v.y - center.getYf();
            float dz = v.z - center.getZf();
            n.set(dx, dy, dz);
            n.normalize();
            meshData.getNormalBuffer().put((float) n.x);
            meshData.getNormalBuffer().put((float) n.y);
            meshData.getNormalBuffer().put((float) n.z);
        }
        for (int f = 0; f < result.numIndices; f++) {
            meshData.getIndexBuffer().put(result.indices.get(f));
        }

        meshData.setIndexMode(IndexMode.Triangles);
        mesh.setMeshData(meshData);



        hl.releaseResult(result);
    }
    
}
