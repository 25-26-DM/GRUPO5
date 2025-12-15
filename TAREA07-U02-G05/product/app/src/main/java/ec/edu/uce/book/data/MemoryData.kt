package ec.edu.uce.book.data

import ec.edu.uce.book.model.Product
import ec.edu.uce.book.model.User

object MemoryData {

    val users = mutableListOf(
        User("Kevin", "Celi"),
        User("Diego", "Casagallo"),
        User("Dylan", "Lema"),
        User("Jhonny", "Ninabanda"),
        User("Luis", "Perenguez"),
        User("Milton","Moncayo")
    )

    val products = mutableListOf(
        Product(
            "L001",
            "Cien años de soledad",
            "Gabriel García Márquez",
            "Novela",
            "1967",
            15.50,
            true
        ),
        Product(
            "L001",
            "Cien años de soledad",
            "Gabriel García Márquez",
            "Novela",
            "1967",
            15.50,
            true
        ),
        Product(
            "L002",
            "El principito",
            "Antoine de Saint-Exupéry",
            "Fábula",
            "1943",
            10.00,
            true
        ),
        Product(
            "L003",
            "1984",
            "George Orwell",
            "Distopía",
            "1949",
            12.75,
            false
        ),
        Product(
            "L004",
            "Don Quijote de la Mancha",
            "Miguel de Cervantes",
            "Clásico",
            "1605",
            20.00,
            true
        )
    )

    var currentUser: User? = null
}
