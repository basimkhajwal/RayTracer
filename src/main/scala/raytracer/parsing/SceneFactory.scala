package raytracer.parsing

import raytracer._
import raytracer.materials.{Material, MatteMaterial}
import raytracer.math.{Point, Transform}
import raytracer.shapes.{Shape, Sphere, Triangle, TriangleMesh}
import raytracer.textures.{ConstantTexture, Texture}

/**
  * Created by Basim on 12/02/2017.
  */
object SceneFactory {

  private def reportUnused[T](tp: TextureParams)(b: => T): T = {
    val returnValue = b
    tp.reportUnused
    returnValue
  }

  private def reportUnused[T](ps: ParamSet)(b: => T): T = {
    val returnValue = b
    ps.reportUnused
    returnValue
  }

  def makeMaterial(matType: String, textureParams: TextureParams): Material = reportUnused(textureParams){
    matType match {

      case "matte" => {
        new MatteMaterial(textureParams.getSpectrumTexture("kd", Spectrum(0.5, 0.5, 0.5)))
      }

      case _ => throw new IllegalArgumentException(s"Un-implemented material type $matType")
    }
  }

  def makeSpectrumTexture(textureClass: String, params: TextureParams): Texture[Spectrum] = reportUnused(params){
    textureClass match {
      case "constant" => {
        new ConstantTexture[Spectrum](params.getOneOr("value", Spectrum(0.5, 0.5, 0.5)))
      }
      case _ => throw new IllegalArgumentException(s"Unknown spectrum texture class $textureClass")
    }
  }

  def makeFloatTexture(textureClass: String, params: TextureParams): Texture[Double] = reportUnused(params){
    textureClass match {
      case "constant" => {
        new ConstantTexture[Double](params.getOneOr("value", 1.0))
      }
      case _ => throw new IllegalArgumentException(s"Unknown float texture class $textureClass")
    }
  }

  def makeShape(name: String, objToWorld: Transform, params: ParamSet): Shape = reportUnused(params){
    name match {

      case "sphere" => {
        val radius = params.getOneOr[Double]("radius", 1)

        Sphere(radius, objToWorld(Point.ZERO))
      }

      case "trianglemesh" => {

        val indices = params.get[Int]("indices")
          .getOrElse(throw new IllegalArgumentException("Indices parameter required for triangle mesh"))

        val points = params.get[Point]("P")
          .getOrElse(throw new IllegalArgumentException("Point (P) parameter required for triangle mesh"))

        require(indices.length % 3 == 0, "Indices must specify 3 points for each triangle")

        TriangleMesh(indices.toArray, points.toArray)
      }

      case _ => throw new IllegalArgumentException(s"Unimplemented shape type $name")
    }
  }
}
