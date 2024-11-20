package Ejercicios.util.bd.bd

import java.io.Serializable

// Clase Coordenades: Representa una coordenada geográfica (latitud y longitud)

class Coordenades(var latitud: Double, var longitud: Double) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1
    }
}

// Clase PuntGeo: Representa un punto geográfico con un nombre y unas coordenadas
class PuntGeo(var nom: String, var coord: Coordenades) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1
    }
}

// Clase Ruta: Representa una ruta con nombre, desniveles y una lista de puntos geográficos
open class Ruta(var nom: String, var desnivell: Int, var desnivellAcumulat: Int, var llistaDePunts: MutableList<PuntGeo>) :
    Serializable {
    companion object {
        private const val serialVersionUID: Long = 1
    }

    // Método para añadir un punto geográfico a la ruta
    fun addPunt(p: PuntGeo) {
        llistaDePunts.add(p)
    }

    // Métodos para obtener información de puntos específicos
    fun getPunt(i: Int): PuntGeo = llistaDePunts[i]
    fun getPuntNom(i: Int): String = llistaDePunts[i].nom
    fun getPuntLatitud(i: Int): Double = llistaDePunts[i].coord.latitud
    fun getPuntLongitud(i: Int): Double = llistaDePunts[i].coord.longitud
    fun size(): Int = llistaDePunts.size

    // Método para mostrar la ruta completa
    fun mostrarRuta() {
        println("Ruta: $nom")
        println("Desnivell: $desnivell")
        println("Desnivell Acumulat: $desnivellAcumulat")
        println("Té ${size()} punts")
        for (i in 0 until size()) {
            println("Punt ${i + 1}: ${getPuntNom(i)} (${getPuntLatitud(i)}, ${getPuntLongitud(i)})")
        }
    }
}

class Ruta_Ordenada (
    var num_r: Int,
    nom: String,
    desnivell: Int,
    desnivellAcumulat: Int,
    llistaDePunts: MutableList<PuntGeo>
    ) : Ruta(nom, desnivell, desnivellAcumulat, llistaDePunts) {

    constructor(ruta: Ruta, num_r: Int) : this(
        num_r,
        ruta.nom,
        ruta.desnivell,
        ruta.desnivellAcumulat,
        ruta.llistaDePunts.toMutableList() // Convertimos a MutableList
    )

    fun mostrarRutaOrdenada() {
        println("Ruta: $num_r, $nom")
        println("Desnivell: $desnivell")
        println("Desnivell Acumulat: $desnivellAcumulat")
        println("Té ${size()} punts")
        for (i in 0 until size()) {
            println("Punt ${i + 1}: ${getPuntNom(i)} (${getPuntLatitud(i)}, ${getPuntLongitud(i)})")
        }
    }
}