import raytracer.cameras.{OrthographicCamera, Projection}
import raytracer.math.{Point, Transform, Vec3}

val cam = new OrthographicCamera(
  Transform.identity,
  (-1, 1, -1, 1),
  400, 300, 0.1, 100
)

val a = Projection.orthographic(0.1, 100)

1 / (100 - 0.1)

val p = Point(400, 300, 0)

a(p)
a.inverse(p)

cam.screenToRaster(Point(1, 1, 0))
cam.rasterToScreen(p)
(cam.cameraToScreen.inverse * cam.rasterToScreen)(p)

cam.generateRay(50, 0)


