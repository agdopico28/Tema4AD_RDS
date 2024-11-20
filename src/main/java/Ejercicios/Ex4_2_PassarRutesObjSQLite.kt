package Ejercicios

import java.io.FileInputStream
import java.io.Serializable
import java.lang.Exception
import java.io.ObjectInputStream
import java.sql.DriverManager

/*
Crea una altre programa anomenat Ex4_2_PassarRutesObjSQLite.kt que passe les dades del fitxer Rutes.obj a les taules
de Rutes.sqlite.
 */

// CLASES COPIADAS DEL EJERCICIO T3_2.KT
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
class Ruta(var nom: String, var desnivell: Int, var desnivellAcumulat: Int, var llistaDePunts: MutableList<PuntGeo>) :
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

// AQUI EMPIEZA LA CLASE PRINCIPAL CREADA EN ESTE EJERCICIO
fun main (args: Array<String>) {
// Primero habrá que leer los datos del archivo Rutes.obj que son objetos por lo que samos ObjectInputStream
    val rutas: MutableList<Ruta> = mutableListOf(); //Creamos una lista vacía donde poner lo que se lee del archivo
    var f: ObjectInputStream? = null; //Lo declaramos aquí antes del try para que no de error al cerrarlo en finally
                                        //tiene que ser var para poder modificar su referencia posteriormente
    try{
    f = ObjectInputStream(FileInputStream("Rutes.obj")); //Creamos la referencia al archivo
    while (true) {
        try {
            val ruta = f.readObject() as Ruta;
            rutas.add(ruta);
            ruta.mostrarRuta();
        }catch (e: Exception){
            break; //Se añade este try-catch para detener el bucle cuando se acabe el archivo
        }
    }
    f.close();
}catch (e: Exception){ //Este catch es para posibles problemas al generar el file f
    println(e);
}finally{
        f?.close();
}
// Aquí ya tenemos el listado de las rutas
// Ahora automatizar la estructura sql para añadir las distintas rutas de forma adecuada.
    //Creamos la conexión al archivo sql donde guardaremos los datos de las rutas
    val url = "jdbc:sqlite:Rutes.sqlite";
    val con = DriverManager.getConnection(url);
    val strutas = "INSERT INTO RUTES (num_r, nom_r, des, des_ac) VALUES (?, ?, ?,?)";
    val stpuntos = "INSERT INTO PUNTOS(num_r,num_p,nom_p,latitud,longitud) VALUES (?, ?, ?,?,?)";

    val preparedstrutas = con.prepareStatement(strutas);
    val preparedstpuntos = con.prepareStatement(stpuntos);

    //recorrer la lista de rutas introduciendo los datos en la tabla
    con.autoCommit = false //para que no se actualice automáticamente

    try {
        var contador = 1
        for (ruta in rutas) {
            // Inserción de la ruta
            preparedstrutas.setInt(1, contador)
            preparedstrutas.setString(2, ruta.nom)
            preparedstrutas.setInt(3, ruta.desnivell)
            preparedstrutas.setInt(4, ruta.desnivellAcumulat)
            preparedstrutas.executeUpdate() // Ejecutar inserción de ruta

            var subcontador = 1
            for (punt in ruta.llistaDePunts) {
                // Inserción de puntos
                preparedstpuntos.setInt(1, contador)
                preparedstpuntos.setInt(2, subcontador)
                preparedstpuntos.setString(3, punt.nom)
                preparedstpuntos.setDouble(4, punt.coord.latitud)
                preparedstpuntos.setDouble(5, punt.coord.longitud)
                preparedstpuntos.executeUpdate() // Ejecutar inserción de punto
                subcontador++
            }
            contador++
        }
        con.commit() // Confirmar la actualización
    } catch (e: Exception) {
        con.rollback() // Revertir cambios en caso de error
        println(e)
    } finally {
        preparedstrutas.close()
        preparedstpuntos.close()
        con.close()
    }
}