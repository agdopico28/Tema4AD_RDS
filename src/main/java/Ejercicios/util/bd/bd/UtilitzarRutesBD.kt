package Ejercicios.util.bd.bd

fun main(args: Array<String>) {
    // Creació del gestionador
    val gRutes = GestionarRutesBD()

    //Leemos la BD completa para trabajar con el arrayList
    gRutes.leerBD()

    // Inserció d'una nova Ruta
    val noms2 = arrayOf( "Les Useres", "Les Torrocelles", "Lloma Bernat", "Xodos (Molí)", "El Marinet", "Sant Joan")
    val latituds2 = arrayOf(40.158126, 40.196046, 40.219210, 40.248003, 40.250977, 40.251221)
    val longituds2 = arrayOf(-0.166962, -0.227611, -0.263560, -0.296690, -0.316947, -0.354052)
    //Insertamos los puntos de la ruta
    val punts2 = ArrayList<PuntGeo>()
    for (i in 0 until 6){
        punts2.add(PuntGeo(noms2[i], Coordenades(latituds2[i], longituds2[i])))
    }
    //Añadimos la ruta a la lista
    gRutes.inserir(Ruta("Pelegrins de Les Useres",896,1738,punts2))

    //Vamos a generar una ruta para que ver que funciona el borrado
    val noms = arrayOf( "Punto 1", "Punto 2", "Punto 3", "Punto 4")
    val latituds = arrayOf(40.158126, 40.196046, 40.219210, 40.248003)
    val longituds = arrayOf(-0.166962, -0.227611, -0.263560, -0.296690)
    //Insertamos los puntos de la ruta de prueba
    val punts = ArrayList<PuntGeo>()
    for (i in 0 until 4){
        punts.add(PuntGeo(noms[i], Coordenades(latituds[i], longituds[i])))
    }
    //Insertamos la ruta en la lista
    gRutes.inserir(Ruta("Prueba para borrar",896,1738,punts))

    // Llistat de totes les rutes
    println("-----------------SE MUESTRAN LAS RUTAS --------------------------------------")
    gRutes.llistat()

    // Buscar una ruta determinada
    println("\nBUSCAMOS LA RUTA 2\n")
    val r2 = gRutes.buscar(2)
    if (r2 != null)
        r2.mostrarRutaOrdenada()


    println("\nBUSCAMOS LA RUTA 4 (PRUEBA A BORRAR) Y LA BORRAMOS\n")
    val r3 = gRutes.buscar(4)
    if (r3 != null) {
        r3.mostrarRutaOrdenada()
        gRutes.esborrar(4)
    }
    println("------------------------------------------")
    println("Quedan "+ gRutes.numRutas() + " rutas.")
    gRutes.cerrar();
}