package dev.war.openfight.port;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import dev.war.openfight.api.animation.Joint;
import dev.war.openfight.api.animation.JointTransform;
import dev.war.openfight.api.utils.math.OpenMatrix4f;
import dev.war.openfight.api.utils.math.Vec3f;

/** Independent numerical checks for the original joint/matrix code against JOML. */
public final class TransformVerification {
    public static void main(String[] args) {
        var rotation=new Quaternionf().rotationY((float)Math.PI/2.0F);
        var transform=new JointTransform(new Vec3f(2,3,4),rotation,new Vec3f(2,3,4));
        // Epic Fight's original fromQuaternion uses the inverse rotation convention relative to JOML.
        var expected=new Matrix4f().translationRotateScale(new Vector3f(2,3,4),new Quaternionf(rotation).conjugate(),new Vector3f(2,3,4));
        compare(OpenMatrix4f.exportToMojangMatrix(transform.toMatrix()),expected,"Joint translation/rotation/scale");
        var roundtrip=OpenMatrix4f.importFromMojangMatrix(expected);
        compare(OpenMatrix4f.exportToMojangMatrix(roundtrip),expected,"Matrix layout import/export");
        var local=new OpenMatrix4f().translate(0,5,0);
        var parent=new OpenMatrix4f().translate(7,0,0);
        var joint=new Joint("hand",0,local);
        var animated=JointTransform.empty();
        animated.translation().set(0,0,2);
        var bound=animated.getAnimationBoundMatrix(joint,parent);
        compare(OpenMatrix4f.exportToMojangMatrix(bound),new Matrix4f().translation(7,5,2),"Parent/local/animation composition");
        var before=JointTransform.empty();
        var after=new JointTransform(new Vec3f(4,8,12),new Quaternionf().rotationY((float)Math.PI/2.0F),new Vec3f(3,5,7));
        var mid=JointTransform.interpolateTransform(before,after,0.5F,null);
        near(mid.translation().x,2,"Translation interpolation");
        near(mid.scale().y,3,"Scale interpolation");
        var midRotation=new Quaternionf().slerp(after.rotation(),0.5F);
        near(Math.abs(mid.rotation().dot(midRotation)),1,"Quaternion shortest-path interpolation");
        var copy=after.copy();
        copy.translation().set(99,99,99);
        near(after.translation().x,4,"Copied joint transform must not alias mutable vectors");
        var readonly=local.unmodifiable();
        try { readonly.setIdentity(); throw new AssertionError("Immutable local bind matrix was mutable"); }
        catch (UnsupportedOperationException expectedFailure) {}
        System.out.println("PORT_TRANSFORM_VERIFICATION_PASSED");
    }
    private static void compare(Matrix4f actual,Matrix4f expected,String label) {
        float[] a=new float[16],b=new float[16]; actual.get(a); expected.get(b);
        for (int i=0;i<16;i++) near(a[i],b[i],label+" element "+i);
    }
    private static void near(float actual,float expected,String label) {
        if (Math.abs(actual-expected)>0.0001F) throw new AssertionError(label+": "+actual+" != "+expected);
    }
}
