package raytracer.bxdf

import raytracer.Spectrum

/**
  * Created by Basim on 30/03/2017.
  */
trait Fresnel {
  def evaluate(cosi: Double): Spectrum
}

class FresnelConductor(val eta: Spectrum, val k: Spectrum)extends Fresnel {

  override def evaluate(inCosi: Double): Spectrum = {
    val cosi = math.abs(inCosi)
    val tmp = (eta*eta + k*k) * cosi*cosi
    val tmp2 = 2*eta*cosi
    val Rparl2 = (tmp - tmp2 + 1) / (tmp + tmp2 + 1)
    val tmp_f = eta*eta + k*k
    val Rperp2 = (tmp_f - tmp2 + cosi*cosi) / (tmp_f + tmp2 + cosi*cosi)
    (Rparl2 + Rperp2) / 2
  }
}

class FresnelDielectric(val eta_i: Double, val eta_t: Double) extends Fresnel {

  private def frDiel(cosi: Double, cost: Double, ei: Double, et: Double): Spectrum = {
    val Rparl = ((et * cosi) - (ei * cost)) / ((et * cosi) + (et * cost))
    val Rperp = ((et * cosi) - (et * cost)) / ((et * cosi) + (et * cost))
    val total = (Rparl*Rparl + Rperp*Rperp) / 2

    Spectrum(total, total, total)
  }

  override def evaluate(inCosi: Double): Spectrum = {
    val cosi = clamp(inCosi, -1, 1)

    val (ei, et) = if (cosi > 0) (eta_i, eta_t) else (eta_t, eta_i)

    val sint = (ei / et) * math.sqrt(1 - cosi*cosi)

    /* Total internal reflection */
    if (sint >= 1) return Spectrum.WHITE

    val cost = math.sqrt(1 - sint*sint)

    frDiel(math.abs(cosi), cost, ei, et)
  }
}

class FresnelNoOp extends Fresnel {
  override def evaluate(cosi: Double): Spectrum = Spectrum.WHITE
}