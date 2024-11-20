package Ejercicios

/*
Crea un programa Kotlin anomenat Ex4_1_CreaTaulesRuta.kt que cree les taules necessàries per a guardar les dades de les
rutes en una Base de Dades SQLite anomenada Rutes.sqlite.

Han de ser 2 taules:

    RUTES : que contindrà tota la informació del conjunt de la ruta. La clau principal s'anomenarà num_r (entera).
    També guardarà el nom de la ruta (nom_r), desnivell *(*desn) i desnivell acumulat (desn_ac). Els tipus d'aquestos
    tres camps últims seran de text, enter i enter respectivament.
    PUNTS : que contindrà la informació dels punts individuals de les rutes. Contindrà els camps num_r (número de ruta:
    enter) , num_p (número de punt: enter), nom_p (nom del punt: text) , latitud (número real) i longitud (número real).
    La clau principal serà la combinació num_r + num_p . Tindrà una clau externa (num_r) que apuntarà a la clau
    principal de RUTES.

Adjunta tot el projecte, i també la Base de Dades Rutes.sqlite(normalment estarà dins del projecte)
 */

import java.sql.DriverManager
import java.sql.SQLException

fun main (args: Array<String>){

    val url = "jdbc:sqlite:Rutes.sqlite";
    val con = DriverManager.getConnection(url);
    val st = con.createStatement();

    val crearRutes = "CREATE TABLE RUTES("+
            "num_r INTEGER CONSTRAINT cp_num_r PRIMARY KEY," +
            "nom_r TEXT," +
            "des INTEGER," +
            "des_ac INTEGER)";
    val crearPuntos = "CREATE TABLE PUNTOS(" +
            "num_r INTEGER," +
            "num_p INTEGER," +
            "nom_p TEXT," +
            "latitud REAL," +
            "longitud REAL," +
            " PRIMARY KEY (num_r, num_p)," +
            "FOREIGN KEY (num_r) REFERENCES RUTES(num_r))";

    //como no se devuelven datos con un ResultSet se puede utilizar la misma st con ambas creaciones
    try {
        st.executeUpdate(crearRutes);
        st.executeUpdate(crearPuntos);
    }catch(e: SQLException){
        println("Error al crear la tabla: ${e.message}");
    }finally {
        st.close();
        con.close();
    }
}