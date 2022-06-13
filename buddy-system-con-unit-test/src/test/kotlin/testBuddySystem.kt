import kotlin.test.Test
import kotlin.test.assertEquals

class TestBuddySystem {
    val testBuddySystem: BuddySystem = BuddySystem(6)

    @Test
    fun testReservar() {
        val nombre = "Procesos"
        testBuddySystem.reservar(nombre, 2)
        testBuddySystem.reservar("Otro", 200)
        testBuddySystem.reservar("Otro1", 32)
        testBuddySystem.reservar("Otro2", 16)
        testBuddySystem.reservar("Otro6", 8)
        testBuddySystem.reservar("Otro3", 4)
        testBuddySystem.reservar("Otro7", 2)
        testBuddySystem.reservar("Otro4", 2)
        val orden = testBuddySystem.procesosOrden.get(testBuddySystem.procesosNombres.indexOf(nombre))

        assertEquals(true, testBuddySystem.procesosOrden.indexOf(orden) == testBuddySystem.procesosNombres.indexOf(nombre) && orden == 1)
    }
    @Test
    fun testLiberar() {
        val nombre = "Procesos"
        testBuddySystem.reservar(nombre, 2)
        testBuddySystem.liberar(nombre)

        assertEquals(true, !(testBuddySystem.procesosNombres.contains(nombre))  && testBuddySystem.obtenerNumBloquesDisp(6) == 1)
    }

}
