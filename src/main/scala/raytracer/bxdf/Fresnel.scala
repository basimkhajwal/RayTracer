package raytracer.bxdf

import raytracer.Spectrum

/**
  * Created by Basim on 30/03/2017.
  */
trait Fresnel {
  def evaluate(cosi: Double): Spectrum
}

class FresnelConductor(val eta: Spectrum, val k: Spectrum)extends Fresnel {

  override def evaluate(cosi: Double): Spectrum = {
    // TODO: Make this more efficient (once tested)
    val tmp = (eta*eta + k*k) * cosi*cosi
    val Rparl2 = (tmp - ((2 * cosi) * eta) + 1) / (tmp + ((2 * cosi) * eta) + 1)

    val tmp_f = eta*eta + k*k
    val Rperp2 = (tmp_f - ((2 * cosi) * eta) + cosi*cosi) / (tmp_f + ((2 * cosi) * eta) + cosi*cosi)

    (Rparl2 + Rperp2) / 2
  }
}

class FresnelDielectric(val eta_i: Double, val eta_t: Double) extends Fresnel {

  override def evaluate(inCosi: Double): Spectrum = {
    val cosi = math.min(1, math.max(-1, cosi))

    val (ei, et) = if (cosi > 0) (eta_i, eta_t) else (eta_t, eta_i)

    val sint = (ei / et) * math.sqrt(math.max(0, 1 - cosi*cosi))

    /* Total internal reflection */
    if (sint >= 1) return Spectrum.WHITE

    val cost = math.sqrt(math.max(0, 1 - sint*sint))

    val Rparl = (et*cosi - ei*cost) / (et*cosi + ei*cost)
    val Rperp = (ei*cosi - et*cost) / (ei*cosi + et*cost)

    val rTotal = (Rparl*Rparl + Rperp*Rperp) / 2
    Spectrum(rTotal, rTotal, rTotal)
  }
}

class FresnelNoOp extends Fresnel {
  override def evaluate(cosi: Double): Spectrum = Spectrum.WHITE
}