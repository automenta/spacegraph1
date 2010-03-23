/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.geom;

import com.ardor3d.bounding.OrientedBoundingBox;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.util.geom.BufferUtils;
import java.nio.FloatBuffer;

/**
 * trapezoidal box
 */
public class TrapBox extends Box {
    //TODO compute correct side (left, right, top, bottom) normals when flare<>1.0

    public static class TrapBoxGeom extends Mesh {

        private double _xExtent, _yExtent, _zExtent;
        private final Vector3 _center = new Vector3(0, 0, 0);
        private final double flare;

        /**
         * Constructs a new 1x1x1 <code>Box</code>.
         */
        public TrapBoxGeom(double flare) {
            super("");
            this.flare = flare;
            setData(Vector3.ZERO, 0.5, 0.5, 0.5);
        }

        /**
         * @return the current center of this box.
         */
        public ReadOnlyVector3 getCenter() {
            return _center;
        }

        /**
         * @return the current X extent of this box.
         */
        public double getXExtent() {
            return _xExtent;
        }

        /**
         * @return the current Y extent of this box.
         */
        public double getYExtent() {
            return _yExtent;
        }

        /**
         * @return the current Z extent of this box.
         */
        public double getZExtent() {
            return _zExtent;
        }

        /**
         * Updates the center point and extents of this box to match an axis-aligned box defined by the two given opposite
         * corners.
         *
         * @param pntA
         *            the first point
         * @param pntB
         *            the second point.
         */
        public void setData(final ReadOnlyVector3 pntA, final ReadOnlyVector3 pntB) {
            _center.set(pntB).addLocal(pntA).multiplyLocal(0.5);

            final double x = Math.abs(pntB.getX() - _center.getX());
            final double y = Math.abs(pntB.getY() - _center.getY());
            final double z = Math.abs(pntB.getZ() - _center.getZ());
            setData(_center, x, y, z);
        }

        /**
         * Updates the center point and extents of this box using the defined values.
         *
         * @param center
         *            The center of the box.
         * @param xExtent
         *            x extent of the box
         * @param yExtent
         *            y extent of the box
         * @param zExtent
         *            z extent of the box
         */
        public void setData(final ReadOnlyVector3 center, final double xExtent, final double yExtent, final double zExtent) {
            if (center != null) {
                _center.set(center);
            }

            _xExtent = xExtent;
            _yExtent = yExtent;
            _zExtent = zExtent;

            setVertexData();
            setNormalData();
            setTextureData();
            setIndexData();

        }

        /**
         * <code>setVertexData</code> sets the vertex positions that define the box using the center point and defined
         * extents.
         */
        protected void setVertexData() {
            if (_meshData.getVertexBuffer() == null) {
                _meshData.setVertexBuffer(BufferUtils.createVector3Buffer(24));
            }

            final Vector3[] vert = computeVertices(); // returns 8

            // Back
            BufferUtils.setInBuffer(vert[0], _meshData.getVertexBuffer(), 0);
            BufferUtils.setInBuffer(vert[1], _meshData.getVertexBuffer(), 1);
            BufferUtils.setInBuffer(vert[2], _meshData.getVertexBuffer(), 2);
            BufferUtils.setInBuffer(vert[3], _meshData.getVertexBuffer(), 3);

            // Right
            BufferUtils.setInBuffer(vert[1], _meshData.getVertexBuffer(), 4);
            BufferUtils.setInBuffer(vert[4], _meshData.getVertexBuffer(), 5);
            BufferUtils.setInBuffer(vert[6], _meshData.getVertexBuffer(), 6);
            BufferUtils.setInBuffer(vert[2], _meshData.getVertexBuffer(), 7);

            // Front
            BufferUtils.setInBuffer(vert[4], _meshData.getVertexBuffer(), 8);
            BufferUtils.setInBuffer(vert[5], _meshData.getVertexBuffer(), 9);
            BufferUtils.setInBuffer(vert[7], _meshData.getVertexBuffer(), 10);
            BufferUtils.setInBuffer(vert[6], _meshData.getVertexBuffer(), 11);

            // Left
            BufferUtils.setInBuffer(vert[5], _meshData.getVertexBuffer(), 12);
            BufferUtils.setInBuffer(vert[0], _meshData.getVertexBuffer(), 13);
            BufferUtils.setInBuffer(vert[3], _meshData.getVertexBuffer(), 14);
            BufferUtils.setInBuffer(vert[7], _meshData.getVertexBuffer(), 15);

            // Top
            BufferUtils.setInBuffer(vert[2], _meshData.getVertexBuffer(), 16);
            BufferUtils.setInBuffer(vert[6], _meshData.getVertexBuffer(), 17);
            BufferUtils.setInBuffer(vert[7], _meshData.getVertexBuffer(), 18);
            BufferUtils.setInBuffer(vert[3], _meshData.getVertexBuffer(), 19);

            // Bottom
            BufferUtils.setInBuffer(vert[0], _meshData.getVertexBuffer(), 20);
            BufferUtils.setInBuffer(vert[5], _meshData.getVertexBuffer(), 21);
            BufferUtils.setInBuffer(vert[4], _meshData.getVertexBuffer(), 22);
            BufferUtils.setInBuffer(vert[1], _meshData.getVertexBuffer(), 23);
        }

        /**
         * <code>setNormalData</code> sets the normals of each of the box's planes.
         */
        private void setNormalData() {
            if (_meshData.getNormalBuffer() == null) {
                _meshData.setNormalBuffer(BufferUtils.createVector3Buffer(24));

                // back
                for (int i = 0; i < 4; i++) {
                    _meshData.getNormalBuffer().put(0).put(0).put(-1);
                }

                // right
                for (int i = 0; i < 4; i++) {
                    _meshData.getNormalBuffer().put(1).put(0).put(0);
                }

                // front
                for (int i = 0; i < 4; i++) {
                    _meshData.getNormalBuffer().put(0).put(0).put(1);
                }

                // left
                for (int i = 0; i < 4; i++) {
                    _meshData.getNormalBuffer().put(-1).put(0).put(0);
                }

                // top
                for (int i = 0; i < 4; i++) {
                    _meshData.getNormalBuffer().put(0).put(1).put(0);
                }

                // bottom
                for (int i = 0; i < 4; i++) {
                    _meshData.getNormalBuffer().put(0).put(-1).put(0);
                }
            }
        }

        /**
         * <code>setTextureData</code> sets the points that define the texture of the box. It's a one-to-one ratio, where
         * each plane of the box has it's own copy of the texture. That is, the texture is repeated one time for each six
         * faces.
         */
        private void setTextureData() {
            if (_meshData.getTextureCoords(0) == null) {
                _meshData.setTextureBuffer(BufferUtils.createVector2Buffer(24), 0);
                final FloatBuffer tex = _meshData.getTextureBuffer(0);

                for (int i = 0; i < 6; i++) {
                    tex.put(1).put(0);
                    tex.put(0).put(0);
                    tex.put(0).put(1);
                    tex.put(1).put(1);
                }
            }
        }

        /**
         * <code>setIndexData</code> sets the indices into the list of vertices, defining all triangles that constitute the
         * box.
         */
        private void setIndexData() {
            if (_meshData.getIndexBuffer() == null) {
                final int[] indices = {2, 1, 0, 3, 2, 0, 6, 5, 4, 7, 6, 4, 10, 9, 8, 11, 10, 8, 14, 13, 12, 15, 14, 12,
                    18, 17, 16, 19, 18, 16, 22, 21, 20, 23, 22, 20};
                _meshData.setIndexBuffer(BufferUtils.createIntBuffer(indices));
            }
        }


        /**
         * @return a size 8 array of Vectors representing the 8 points of the box.
         */
        public Vector3[] computeVertices() {

            final Vector3 rVal[] = new Vector3[8];
            rVal[0] = _center.add(-_xExtent, -_yExtent, -_zExtent, null);
            rVal[1] = _center.add(_xExtent, -_yExtent, -_zExtent, null);
            rVal[2] = _center.add(_xExtent, _yExtent, -_zExtent, null);
            rVal[3] = _center.add(-_xExtent, _yExtent, -_zExtent, null);

            rVal[4] = _center.add(_xExtent*flare, -_yExtent*flare, _zExtent, null);
            rVal[5] = _center.add(-_xExtent*flare, -_yExtent*flare, _zExtent, null);
            rVal[6] = _center.add(_xExtent*flare, _yExtent*flare, _zExtent, null);
            rVal[7] = _center.add(-_xExtent*flare, _yExtent*flare, _zExtent, null);

            return rVal;
        }
    }

    public TrapBox(double flare) {
        super(BoxShape.Empty);

        TrapBoxGeom trapBox = add(new TrapBoxGeom(flare));
        trapBox.setModelBound(new OrientedBoundingBox());
    }
}
