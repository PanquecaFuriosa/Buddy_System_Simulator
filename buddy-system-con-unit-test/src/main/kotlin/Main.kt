import java.util.LinkedList
import java.util.Scanner
import kotlin.math.*
/**
 * Implementación de una clase que representa a lo bloques de la memoria,
 * sigue la idea de los nodos de un árbol binario.
 * Autor: Gabriela Panqueva 06/2022.
 */
class Bloque {
    var proceso: String = ""
    var padre: Bloque? = null
    var hijoDer: Bloque? = null
    var hijoIzq: Bloque? = null
    var disp: Boolean = true
    var ord: Int = 0

    // Función que retorna true si el bloque está disponible y false si no.
    fun estaDisponible(): Boolean = disp

    // Función que retorna el orden del bloque.
    fun orden(): Int = ord
}

/**
 * Clase que implementa el algoritmo de asignación de memoria Buddy System.
 * Argumentos:
 * n (Int): Es el orden máximo que puede tener un bloque, es decir, el bloque
 * más grande corresponda al tamaño 2^n.
 * Autor: Gabriela Panqueva 06/2022.
 */
class BuddySystem(n: Int) {
    private var orden : Int
    private var memoria : Array<LinkedList<Bloque>>
    var procesosNombres : LinkedList<String> = LinkedList()
    var procesosOrden : LinkedList<Int> = LinkedList()

    init {
        orden = n
        memoria = Array<LinkedList<Bloque>>((orden + 1), {LinkedList<Bloque>()})
        memoria[orden].add(Bloque())
        memoria[orden].getFirst().ord = orden

    }

    /**
     * Función que busca un espacio para asginar de manera recursiva:
     * si no consigue en un nivel dado, busca en el nivel superior.
     * Argumentos:
     * n (Int): Es el orden del bloque a buscar.
     * Retorna:
     * El bloque a asignar, si no hay retorna null.
     * Autor: Gabriela Panqueva 06/2022.
     */
    private fun buscarEspacio(n: Int): Bloque? {
        var bloqueB : Bloque? = null

        if (n > orden) {
            return null
        }

        for (i in memoria[n]) {
            if (i.estaDisponible()) {
                bloqueB = i
                break
            }
        }
        if (bloqueB == null) {
            bloqueB = buscarEspacio(n+1)
        }

        return bloqueB
    }

    /**
     * Función que divide bloques a la mitad desde un bloque dado hasta
     * encontrar un bloque del orden deseado.
     * Argumentos:
     * bloque (Bloque): Bloque a dividir.
     * n (Int): Orden deseado de los bloques resultantes.
     * Retorna:
     * Bloque con el orden deseado para asignar
     * Autor: Gabriela Panqueva 06/2022.
     */
    private fun dividir(bloque: Bloque, n: Int) : Bloque {
        var bloqueA = bloque

        if (bloqueA.orden() > n) {
            val hijoDer = Bloque()
            val hijoIzq = Bloque()
            hijoDer.padre = bloqueA
            hijoIzq.padre = bloqueA
            hijoDer.ord = bloqueA.orden()-1
            hijoIzq.ord = bloqueA.orden()-1
            bloqueA.hijoDer = hijoDer
            bloqueA.hijoIzq = hijoIzq
            bloqueA.disp = false
            memoria[bloqueA.orden()-1].add(hijoDer)
            memoria[bloqueA.orden()-1].add(hijoIzq)
            bloqueA = dividir(hijoIzq, n)
        }

        return bloqueA
    }

    /**
     * Función que combina bloques para formar un bloque más grande hasta
     * que no sea posible combinar más bloques.
     * Argumentos:
     * bloqueA (Bloque): Uno de los bloques a combinar.
     * bloqueB (Bloque): El otro bloque a combinar.
     * Autor: Gabriela Panqueva 06/2022.
     */
    private fun combinar(bloqueA: Bloque, bloqueB: Bloque) {

        if (bloqueA.estaDisponible() && bloqueB.estaDisponible()) {
            memoria[bloqueA.ord].remove(bloqueA)
            memoria[bloqueA.ord].remove(bloqueB)
            bloqueA.padre!!.disp = true
            if (bloqueA.padre!!.padre == null) { return }
            combinar(bloqueA.padre!!.padre!!.hijoDer!!, bloqueA.padre!!.padre!!.hijoIzq!!)
        }
    }

    /**
     * Función que determina si se puede reservar un bloque del orden dado
     * para un proceso con el nombre dado.
     * Argumentos:
     * nombre (String): Nombre del proceso que pide la reserva.
     * n (Int): Orden del bloque que se debe reservar.
     * Retorna:
     * Booleano, true si se pudo hacer la reserva del espacio, false si no.
     * Autor: Gabriela Panqueva 06/2022.
     */
    fun reservar(nombre: String, n: Int) : Boolean {
        var bloqueA : Bloque?
        var i : Int = orden

        while (i > 0) {
            if ((2.0.pow((i-1).toDouble())) < n && n <= (2.0.pow(i.toDouble()))) {
                break
            }
            --i
        }
        if (2.0.pow(i.toDouble()) < n) { return false }

        bloqueA = buscarEspacio(i)

        if (bloqueA != null && bloqueA.orden() > i) {
            bloqueA = dividir(bloqueA, i)
        } else if (bloqueA == null) { return false }

        bloqueA.proceso = nombre
        bloqueA.disp = false
        procesosNombres.add(nombre)
        procesosOrden.add(i)

        return true
    }

    /**
     * Función que se encarga de liberar el espacio reservado por un proceso dado.
     * Argumentos:
     * nombre (String): Nombre del proceso que solicita liberar el espacio reservado.
     * Retorna:
     * Booleano, true si se encontró el bloque reservado por el proceso dado. false si no.
     * Autor: Gabriela Panqueva 06/2022.
     */
    fun liberar(nombre: String) : Boolean {
        val i : Int  = procesosNombres.indexOf(nombre)
        var e = false
        if (i != -1) {
            e = true
            val orden : Int = procesosOrden.get(i)
            for (j in memoria[orden]) {
                if (j.proceso == nombre) {
                    j.proceso = ""
                    j.disp = true
                    if (j.padre != null) {
                        combinar(j.padre!!.hijoDer!!, j.padre!!.hijoIzq!!)
                    }
                }
            }
            procesosOrden.remove(procesosOrden.get(procesosNombres.indexOf(nombre)))
            procesosNombres.remove(nombre)
        }

        return e
    }

    /**
     * Función que obtiene el número de bloque disponibles de un orden dado.
     * Argumentos:
     * n (Int): Orden de los bloques buscados.
     * Retorna:
     * Entero, el número de bloques disponibles del orden dado.
     * Autor: Gabriela Panqueva 06/2022.
     */
    fun obtenerNumBloquesDisp(n: Int): Int {
        var num = 0

        if (n <= orden) {
            for (i in memoria[n]) {
                if (i.disp) {
                    num++
                }
            }
        }

        return num
    }
}

/**
 * Función principal, le pide repetidamente al usuario una acción para ejecutar.
 */
fun main(args: Array<String>) {
    val particiones : LinkedList<BuddySystem> = LinkedList()
    val lector = Scanner(System.`in`)
    var linea : String
    var palabras : List<String>
    var j : Int = args[0].toInt()
    val bin : String = j.toString(2)
    j = 0

    for (i in bin.length-1 downTo 0) {
        if (bin[i] == '1') {
            particiones.add(BuddySystem(j))
        }
        j++
    }

    println("Bienvenido al simulador de reservador de memoria Buddy System :).")
    println("Recuerde que las acciones permitidas son: RESERVAR, LIBERAR, MOSTRAR y SALIR.")

    while (true) {
        print(">$")
        linea = lector.nextLine()
        palabras = linea.split(" ")

        when (palabras[0]) {
            "RESERVAR" -> {
                var resultado = false
                for (i in particiones) {
                    resultado = i.reservar(palabras[1], palabras[2].toInt())
                    if (resultado) { break }
                }
                if (!(resultado)) {
                    println("Hubo un error al reservar los bloques para el proceso, no hay suficientes bloques disponibles de manera contigua.")
                }
            }
            "LIBERAR" -> {
                var resultado = false
                for (i in particiones) {
                    resultado = i.liberar(palabras[1])
                    if (resultado) { break }
                }
                if (!(resultado)) {
                    println("Hubo un error en la liberación del espacio reservado con el nombre especificado, el mismo no está asociado a ningún espacio.")
                }
            }
            "MOSTRAR" -> {
                var texto = ""
                var memDisp : Int
                for (i in 0 until j) {
                    var bloqDisp = 0
                    memDisp = (2.0.pow(i)).toInt()
                    for (k in particiones) {
                        bloqDisp += k.obtenerNumBloquesDisp(i)
                    }
                    texto = "${texto}\nBloques de tamaño $memDisp disponibles:\nUsted tiene $bloqDisp bloques disponibles.\n"
                }
                texto = "${texto}\n-------------------------------------------------------------------\n"
                texto = "${texto}\nA continuación se le proporcionará una lista con los procesos que tiene bloques de memoria reservados.\n"

                for (k in particiones) {
                    for (i in 0 until k.procesosNombres.size) {
                        val espacio : Int = (2.0.pow(k.procesosOrden[i])).toInt()
                        texto = "${texto}Nombre: ${k.procesosNombres[i]}        Tamaño del bloque asignado: $espacio\n"
                    }
                }
                println(texto)
            }
            "SALIR" -> break
            else -> println("Error, la acción intorucida es equivocada, por favor intente nuevamente.")
        }
    }
}