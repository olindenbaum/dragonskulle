/* (C) 2021 DragonSkulle */
package org.dragonskulle.components;

import org.dragonskulle.core.GameObject;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Base Transform class
 *
 * @author Harry Stoltz
 *     <p>All GameObjects will have a Transform object which stores the position, rotation and scale
 *     of the object (As right, up, forward and position in a 4x4 Matrix). The Transform can be used
 *     to get 3D position, scale and rotation. Or can be cast to HexTransform to get the position,
 *     scale and rotation in Hex coordinates.
 */
public class Transform extends Component {
    private static final float DEG_TO_RAD = (float) Math.PI / 180.f;

    private final Matrix4f mLocalMatrix;
    private Matrix4f mWorldMatrix;
    private boolean mShouldUpdate = true;

    /** Default constructor. mLocalMatrix is just the identity matrix */
    public Transform() {
        mLocalMatrix = new Matrix4f().identity();
        mWorldMatrix = new Matrix4f().identity();
    }

    /**
     * Constructor
     *
     * @param position Starting position for the object
     */
    public Transform(Vector3f position) {
        mLocalMatrix = new Matrix4f().identity().translate(position);
        mWorldMatrix = new Matrix4f().identity();
    }

    /**
     * Constructor
     *
     * @param matrix Matrix to be used for mLocalMatrix
     */
    public Transform(Matrix4f matrix) {
        mLocalMatrix = new Matrix4f(matrix);
        mWorldMatrix = new Matrix4f().identity();
    }

    /**
     * Set the position of the object relative to the parent
     *
     * @param position Vector3f containing the desired position
     */
    public void setPosition(Vector3f position) {
        mLocalMatrix.setColumn(3, new Vector4f(position, 1.f));
        setUpdateFlag();
    }

    /**
     * Rotate the object with euler angles
     *
     * @param eulerAngles Vector containing euler angles to rotate object with
     */
    public void rotateRad(Vector3f eulerAngles) {
        mLocalMatrix.rotateXYZ(eulerAngles);
        setUpdateFlag();
    }

    /**
     * Rotate the object with euler angles
     *
     * @param x Rotation in X-axis in radians
     * @param y Rotation in Y-axis in radians
     * @param z Rotation in Z-axis in radians
     */
    public void rotateRad(float x, float y, float z) {
        mLocalMatrix.rotateXYZ(x, y, z);
        setUpdateFlag();
    }

    /**
     * Rotate the object with euler angles
     *
     * @param x Rotation in X-axis in degrees
     * @param y Rotation in Y-axis in degrees
     * @param z Rotation in Z-axis in degrees
     */
    public void rotateDeg(float x, float y, float z) {
        mLocalMatrix.rotateXYZ(DEG_TO_RAD * x, DEG_TO_RAD * y, DEG_TO_RAD * z);
        setUpdateFlag();
    }

    /**
     * Rotate the object with a quaternion
     *
     * @param quaternion Quaternion to rotate object with
     */
    public void rotate(Quaternionf quaternion) {
        mLocalMatrix.rotate(quaternion);
        setUpdateFlag();
    }

    /**
     * Translate the object
     *
     * @param translation Vector translation to perform
     */
    public void translate(Vector3f translation) {
        mLocalMatrix.translate(translation);
        setUpdateFlag();
    }

    /**
     * Translate the object
     *
     * @param x Translation in X-axis
     * @param y Translation in Y-axis
     * @param z Translation in Z-axis
     */
    public void translate(float x, float y, float z) {
        mLocalMatrix.translate(x, y, z);
        setUpdateFlag();
    }

    /**
     * Scale the object
     *
     * @param scale Vector to scale object with
     */
    public void scale(Vector3f scale) {
        mLocalMatrix.scale(scale);
        setUpdateFlag();
    }

    /**
     * Scale the object
     *
     * @param x Scale in the X-axis
     * @param y Scale in the Y-axis
     * @param z Scale in the Z-axis
     */
    public void scale(float x, float y, float z) {
        mLocalMatrix.scale(x, y, z);
        setUpdateFlag();
    }

    /** Set mShouldUpdate to true in all children transforms */
    private void setUpdateFlag() {
        if (mShouldUpdate) {
            return;
        }
        mShouldUpdate = true;
        for (GameObject obj : mGameObject.getChildren()) {
            obj.getTransform().setUpdateFlag();
        }
    }

    /**
     * Get the transformation matrix relative to the parent transform
     *
     * @return A copy of the local matrix
     */
    public Matrix4f getLocalMatrix() {
        Matrix4f dest = new Matrix4f();
        mLocalMatrix.get(dest);
        return dest;
    }

    /**
     * Get the transformation matrix relative to the parent transform
     *
     * @param dest Matrix to store a copy of the local matrix
     */
    public void getLocalMatrix(Matrix4f dest) {
        dest.set(mLocalMatrix);
    }

    /**
     * Get the normalised rotation of the transform
     *
     * @return Rotation of the transform as Quaternion
     */
    public Quaternionf getLocalRotation() {
        Quaternionf rotation = new Quaternionf();
        mLocalMatrix.getNormalizedRotation(rotation);
        return rotation;
    }

    /**
     * Get the normalised rotation of the transform, relative to the parent transform
     *
     * @param dest Quaternion to store the rotation
     */
    public void getLocalRotation(Quaternionf dest) {
        mLocalMatrix.getNormalizedRotation(dest);
    }

    /**
     * Get the rotation of the transform per axis, in radians, relative to the parent transform
     *
     * @return Rotation of the transform as AxisAngle
     */
    public AxisAngle4f getLocalRotationAngles() {
        AxisAngle4f rotation = new AxisAngle4f();
        mLocalMatrix.getRotation(rotation);
        return rotation;
    }

    /**
     * Get the rotation of the transform per axis, in radians, relative to the parent transform
     *
     * @param dest AxisAngle4f to store the rotation
     */
    public void getLocalRotationAngles(AxisAngle4f dest) {
        mLocalMatrix.getRotation(dest);
    }

    /**
     * Get the position of the transform
     *
     * @return Vector3f containing the XYZ position of the object
     */
    public Vector3f getLocalPosition() {
        Vector3f position = new Vector3f();
        mLocalMatrix.getColumn(3, position);
        return position;
    }

    /**
     * Get the position of the transform relative to the parent transform
     *
     * @param dest Vector3f to store the position
     */
    public void getLocalPosition(Vector3f dest) {
        mLocalMatrix.getColumn(3, dest);
    }

    /**
     * Get the scale of the transform
     *
     * @return Vector3f containing the XYZ scale of the object
     */
    public Vector3f getLocalScale() {
        Vector3f scale = new Vector3f();
        mLocalMatrix.getScale(scale);
        return scale;
    }

    /**
     * Get the scale of the transform
     *
     * @param dest Vector3f to store the scale
     */
    public void getLocalScale(Vector3f dest) {
        mLocalMatrix.getScale(dest);
    }

    /**
     * Get the world matrix for this transform. If the transform is on a root object, mLocalMatrix
     * is used as the worldMatrix. If it isn't a root object and mShouldUpdate is true, recursively
     * call getWorldMatrix up to the first Transform that has mShouldUpdate as false
     *
     * @return mWorldMatrix
     */
    public Matrix4f getWorldMatrix() {
        if (mShouldUpdate) {
            mShouldUpdate = false;
            if (mGameObject.isRootObject()) {
                mWorldMatrix = mLocalMatrix;
            } else {
                // Store our local matrix in mWorldMatrix
                mWorldMatrix.set(mLocalMatrix);

                // Then multiply by parent's world matrix
                // Which gives us the matrix multiplication mLocalMatrix * parentWorldMatrix
                // so when doing mWorldMatrix * (vector)
                // It is the same as doing mLocalMatrix * parentWorldMatrix * (vector)
                // so that any parent transformations are done prior to the local transformation
                mWorldMatrix.mul(mGameObject.getParentTransform().getWorldMatrix());
            }
        }
        return mWorldMatrix;
    }

    /**
     * Get the rotation of the transform in the world as a Quaternion
     *
     * @return Quaternionf containing the rotation of the transform
     */
    public Quaternionf getRotation() {
        Quaternionf rotation = new Quaternionf();
        getWorldMatrix().getNormalizedRotation(rotation);
        return rotation;
    }

    /**
     * Get the rotation of the transform in the world as a Quaternion
     *
     * @param dest Quaternionf to store the rotation of the transform
     */
    public void getRotation(Quaternionf dest) {
        getWorldMatrix().getNormalizedRotation(dest);
    }

    /**
     * Get the rotation of the transform in the world as axis angles
     *
     * @return AxisAngle4f containing the rotation of the transform
     */
    public AxisAngle4f getRotationAngles() {
        AxisAngle4f rotation = new AxisAngle4f();
        getWorldMatrix().getRotation(rotation);
        return rotation;
    }

    /**
     * Get the rotation of the transform in the world as axis angles
     *
     * @param dest AxisAnglef to store the rotation of the transform
     */
    public void getRotationAngles(AxisAngle4f dest) {
        getWorldMatrix().getRotation(dest);
    }

    /**
     * Get the position of the transform in the world
     *
     * @return Vector3f containing the world position
     */
    public Vector3f getPosition() {
        Vector3f position = new Vector3f();
        getWorldMatrix().getColumn(3, position);
        return position;
    }

    /**
     * Get the position of the transform in the world
     *
     * @param dest Vector3f to store the position
     */
    public void getPosition(Vector3f dest) {
        getWorldMatrix().getColumn(3, dest);
    }

    /**
     * Get the scale of the transform in the world
     *
     * @return Vector3f containing the scale of the transform
     */
    public Vector3f getScale() {
        Vector3f scale = new Vector3f();
        getWorldMatrix().getScale(scale);
        return scale;
    }

    /**
     * Get the scale of the transform in the world
     *
     * @param dest Vector3f to store the scale
     */
    public void getScale(Vector3f dest) {
        getWorldMatrix().getScale(dest);
    }

    @Override
    protected void onDestroy() {}
}