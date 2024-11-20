package Ejercicios.util.bd.bd

import java.sql.Connection
import java.sql.DriverManager

class GestionarRutesBD {
    private var con: Connection? = null
    private val lista: ArrayList<Ruta_Ordenada> = ArrayList() // Variable global

    init {
        // Ruta donde se encuentra la base de datos
        val url = "jdbc:sqlite:Rutes.sqlite"

        try {
            // Se crea la conexión, controlada por el try-catch
            con = DriverManager.getConnection(url)
            println("Conexión establecida a la base de datos")
            // Se crean las dos sentencias necesarias para crear las tablas si no existen
            val crearRutes = """CREATE TABLE IF NOT EXISTS RUTES (
                num_r INTEGER PRIMARY KEY,
                nom_r TEXT NOT NULL,
                des INTEGER NOT NULL,
                des_ac INTEGER NOT NULL);""".trimIndent()
            val crearPuntos = """CREATE TABLE IF NOT EXISTS PUNTOS (
                num_r INTEGER PRIMARY KEY,
                num_p INTEGER NOT NULL,
                nom_p TEXT NOT NULL,
                latitud REAL NOT NULL,
                longitud REAL NOT NULL,
                FOREIGN KEY(num_r) REFERENCES RUTES(num_r) ON DELETE CASCADE ON UPDATE CASCADE
                );""".trimIndent() // Se usa con comillas triples para garantizar indentación correcta

            // Si no es nula la conexión, se ejecutan las sentencias SQL y se crean las tablas si no existen
            con?.createStatement()?.use { statement ->
                statement.execute(crearRutes)
                statement.execute(crearPuntos)
                println("Tablas creadas o verificadas exitosamente.")
            }
        } catch (e: Exception) {
            println("Error de conexión: ${e.message}")
        }
    }

    fun inserir(r: Ruta) {
        // Crea una nueva ruta ordenada y la agrega a la lista
        val r_ordenada = Ruta_Ordenada(r, lista.size + 1)
        lista.add(r_ordenada)
    }

    fun esborrar(orden: Int) {
        val rutaBorrada = lista.removeIf { it.num_r == orden }
        if (rutaBorrada) {
            println("Borrada la ruta completamente.")
        } else {
            println("Ruta no encontrada, no borrada")
        }
        // Los puntos al formar parte de la ruta borrada se borran automáticamente
    }

    fun buscar(num: Int): Ruta_Ordenada? {
        return lista.find { it.num_r == num }
    }

    // Leer todas las rutas de la base de datos y cargarlas en el arrayList lista
    fun leerBD(): ArrayList<Ruta_Ordenada> {

        val st = con!!.createStatement()
        val rs = st.executeQuery("SELECT * FROM RUTES")
        try {
            while (rs.next()) {
                val numR = rs.getInt("num_r")
                val nombreRuta = rs.getString("nom_r")
                val desnivel = rs.getInt("des")
                val des_acum = rs.getInt("des_ac")

                // Listado de puntos asociados a la ruta
                val listaPuntos = ArrayList<PuntGeo>()
                val stPuntos = con!!.createStatement()
                val rsPuntos = stPuntos.executeQuery("SELECT * FROM PUNTOS WHERE num_r =  $numR")
                try {
                    while (rsPuntos.next()) {
                        val nombrePunto = rsPuntos.getString("nom_p")
                        val latitud = rsPuntos.getDouble("latitud")
                        val longitud = rsPuntos.getDouble("longitud")
                        listaPuntos.add(PuntGeo(nombrePunto, Coordenades(latitud, longitud)))
                    }
                } finally {
                    rsPuntos.close()
                    stPuntos.close()
                }

                lista.add(Ruta_Ordenada(numR, nombreRuta, desnivel, des_acum, listaPuntos))
            }
        } catch (e: Exception) {
            println("Error en la consulta de rutas: ${e.message}")
        } finally {
            rs.close()
            st.close()
            return lista
        }
    }

    // Cerrar la conexión
    fun cerrar() {

        // Limpiar las tablas antes de volcar los datos que tenemos
        con?.createStatement()?.use { statement ->
            statement.executeUpdate("DELETE FROM PUNTOS")
            statement.executeUpdate("DELETE FROM RUTES")
        }
        //Una vez limpio llenamos las dos tablas de cero con los datos ordenados
        var numRuta =
            1 // Empezamos el num_r de ruta en 1 y obviamos el que tenga el arrayList porque pueden haber borrado
        // datos

        lista.forEach { rutaOrdenada ->
            try {
                // Inserta la ruta en la tabla RUTES con num_r proporcionado
                con?.prepareStatement("INSERT INTO RUTES (num_r, nom_r, des, des_ac) VALUES (?, ?, ?, ?)")?.use { ps ->
                    ps.setInt(1, numRuta) // Proporcionamos el valor de num_r manualmente
                    ps.setString(2, rutaOrdenada.nom)
                    ps.setInt(3, rutaOrdenada.desnivell)
                    ps.setInt(4, rutaOrdenada.desnivellAcumulat)
                    ps.executeUpdate()
                }

                // Inserta cada punto asociado en la tabla PUNTS con el mismo num_r
                rutaOrdenada.llistaDePunts.forEachIndexed { index, punt ->
                    con?.prepareStatement("INSERT INTO PUNTOS (num_r, num_p, nom_p, latitud, longitud) VALUES (?, ?, ?, ?, ?)")
                        ?.use { ps ->
                            ps.setInt(1, numRuta) // Reutilizamos numRuta como num_r para puntos
                            ps.setInt(2, index+1) // num_p (orden del punto en la ruta)
                            ps.setString(3, punt.nom) // nom_p
                            ps.setDouble(4, punt.coord.latitud) // latitud
                            ps.setDouble(5, punt.coord.longitud) // longitud
                            ps.executeUpdate()
                        }
                }

                println("Ruta '${rutaOrdenada.nom}' y sus puntos fueron volcados exitosamente a la base de datos.")
                numRuta++ // Incrementamos manualmente el valor para la siguiente ruta

            } catch (e: Exception) {
                println("Error al volcar la ruta '${rutaOrdenada.nom}' a la base de datos: ${e.message}")
            }
        }

        // Cierra la conexión después de volcar todos los datos
        con?.close()
        println("Conexión cerrada")
    }

    //mostrar todas las rutas tal y como se encuentran en el arrayList lista
    fun llistat() {
        for (r in lista) {
            println("------------------------------------------------------------")
            r.mostrarRutaOrdenada()
        }
    }

    fun numRutas(): Int {
        return lista.size
    }
}