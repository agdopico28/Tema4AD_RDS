package Ejercicios

import javax.swing.JFrame
import java.awt.EventQueue
import java.awt.BorderLayout
import javax.swing.JPanel
import java.awt.FlowLayout
import java.sql.Connection
import java.sql.DriverManager
import javax.swing.JComboBox
import javax.swing.JButton
import javax.swing.JTextArea
import javax.swing.JLabel

class Finestra : JFrame() {

    private var con: Connection? = null;

    init {
        // Sentències per a fer la connexió a Rutes.sqlite
        try {
            val url = "jdbc:sqlite:Rutes.sqlite";
            con = DriverManager.getConnection(url);
        } catch (e: Exception) {
            println(e);
        }

        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setTitle("JDBC: Visualitzar Rutes")
        setSize(450, 450)
        setLayout(BorderLayout())

        val panell1 = JPanel(FlowLayout())
        val panell2 = JPanel(BorderLayout())
        add(panell1, BorderLayout.NORTH)
        add(panell2, BorderLayout.CENTER)

        val llistaRutes = arrayListOf<String>()
        llistaRutes.add("Elige una ruta");
        // Sentències per a omplir l'ArrayList amb el nom de les rutes
        try {
            val st = con?.createStatement()
            val rs = st?.executeQuery("SELECT RUTES.nom_r FROM RUTES")
            while (rs?.next() == true) {
                llistaRutes.add(rs.getString(1));
            }
            st?.close()
        } catch (e: Exception) {
            println(e);
        }

        val combo = JComboBox<String>(llistaRutes.toTypedArray())
        panell1.add(combo)
        val eixir = JButton("Eixir")
        panell1.add(eixir)
        val area = JTextArea()
        panell2.add(JLabel("Llista de punts de la ruta:"), BorderLayout.NORTH)
        panell2.add(area, BorderLayout.CENTER)


        combo.addActionListener() {
            // Sentèncis quan s'ha seleccionat un element del JComboBox
            // Han de consistir en omplir el JTextArea
            area.text = "";
            val rutaSeleccionada = combo.selectedItem as String;
            try {
                val st = con?.createStatement();
                val rs = st?.executeQuery(
                    "SELECT PUNTOS.num_p, PUNTOS.nom_p, PUNTOS.latitud," +
                            "PUNTOS.longitud " +
                            "FROM RUTES JOIN PUNTOS ON RUTES.num_r = PUNTOS.num_r " +
                            "WHERE RUTES.nom_r = '${rutaSeleccionada}' " +
                            "ORDER BY PUNTOS.num_p;"
                );
                while (rs?.next() == true) {
                    val num_punto = rs?.getInt(1);
                    val nom_punto = rs?.getString(2);
                    val latitud = rs?.getDouble(3);
                    val longitud = rs?.getDouble(4);
                    area.append("Nª: $num_punto, PUNTO: $nom_punto, latitud: $latitud, longitud: $longitud\n");
                }
                rs?.close();
                st?.close();
            } catch (e: Exception) {
                println(e);
            }
        }

        eixir.addActionListener() {
            // Sentències per a tancar la connexió i eixir
            try {
                con?.close();
            } catch (e: Exception) {
                println(e);
            } finally {
                dispose();

            }
        }
    }
}

fun main(args: Array<String>) {
    EventQueue.invokeLater {
        Finestra().isVisible = true
    }
}

