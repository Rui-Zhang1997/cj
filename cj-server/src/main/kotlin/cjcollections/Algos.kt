package cjcollections

fun createLogistic(x_0: Double, L: Double = 1.0, k: Double = 1.0): (Double) -> Double {
    return { n -> L / (1 + Math.pow(Math.E, -1 * k * (n - x_0))) }
}

/*

    f(x) = -coeff * e ^ ((-(expcoeff * x) / div) ^ exp) + mod

 */
fun createModifiedLogistic(coeff: Double = 1.0, exp: Int = 2,
                           expcoeff: Double = 1.0, div: Double = 1.0, mod: Double = 1.0): (Double) -> Double {
    return { n -> -1 * coeff * Math.pow(Math.E, Math.pow(-1 * (expcoeff * n / div), exp.toDouble())) + mod }
}
