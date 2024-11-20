package Ejemplos

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/*
Està fet sobre MySQL , ja que com hem comentat abans, PostgreSQL utilitza ara un altre codi d'error per a la
contrasenya invàlida. Tindrem 3 contrasenyes possibles per a la connexió. Si qualsevol de les 3 és bona, es connectarà.
Sinó indicarà que hi ha un error en la contrasenya.
 */
fun main(args: Array<String>) {

    var connectat = false
    var con: Connection? = null
    println("tractamentErrorConnexio()")

    try {

        val url = "jdbc:postgresql://89.36.214.106:5432/geo_ad"

        val usuari = "geo_ad"
        val contrasenyes = arrayOf("geo0", "geo1", "geo_ad")

        for (i in 0 until contrasenyes.size) {
            try {
                con = DriverManager.getConnection(url, usuari, contrasenyes[i])
                connectat = true
                break
            } catch (ex: SQLException) {
                if (!ex.getSQLState().equals("28P01")) {
                    // NO és un error d'autenticació
                    /*
                    El throw ex se utiliza para volver a lanzar una excepción después de que se ha capturado en un
                    bloque catch. Esto permite que la excepción sea manejada en un nivel superior de la pila de
                    llamadas, o que interrumpa el flujo normal del programa.
                     */
                    throw ex

                }
            }
        }
        if (connectat)
            println("Connexió efectuada correctament")
        else
            println("Error en la contrasenya")
    } catch (ex: SQLException) {
        if (ex.getSQLState().equals("08001")) {
            println(
                "S'ha detectat un problema de connexió. Reviseu els cables de xarxa i assegureu-vos que el SGBD està operatiu."
                        + " Si continua sense connectar, aviseu el servei tècnic"
            )

        } else {
            println(
                "S'ha produït un error inesperat. Truqueu al servei tècnic indicant el següent codi d'error SQL:"
                        + ex.getSQLState()
            )
        }
    } catch (ex: ClassNotFoundException) {
        println("No s'ha trobat el controlador JDBC (" + ex.message + "). Truqueu al servei tècnic")
    } finally {
        try {
            if (con != null && !con.isClosed()) {
                con.close()
            }
        } catch (ex: SQLException) {
            throw ex
        }
    }
}