import raytracer.math.{Point, Transform, Vec3}

def axisRotate(angle: Double, axis: Vec3): Transform = {
  val a = axis.nor

  // Rotate into xy plane
  val t1 = Transform.rotateY(math.toDegrees(math.atan2(a.z, a.x)))

  // Rotate onto x axis
  val l1 = math.sqrt(a.x*a.x + a.z*a.z)
  val t2 = Transform.rotateZ(360-math.toDegrees(math.atan2(a.y, l1)))
  val t3 = Transform.rotateX(angle)
  t1.inverse * t2.inverse * t3 * t2 * t1
}

val t = Transform.rotate(90, Vec3(0,1,0))
val k = Transform.rotateY(90)
val tst = axisRotate(90, Vec3(0,1,0))

val testLook = Transform.lookAt(Point.ZERO, Point(1,1,0), Vec3(0,1,0))

val p = Vec3(2,0,0)
//(t(p)) (k(p)) tst(p)
testLook(p)

