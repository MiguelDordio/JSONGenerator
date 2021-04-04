package magicJSON

import testModels.Cars
import testModels.Contacts
import testModels.Person
import testModels.RelationShipStatus


/**
 * The client application that has objects in hierarchical object structure and make
 * use of different ConcreteVisitor(s) to perform operations on those objects.
 */
fun main() {

    val contacts = Contacts("mig@gmail.com", "999888777")
    val relationShipStatus = RelationShipStatus("Married", "09-12-1978")
    val car = Cars("Cloudy", "Biggy", 2008)
    val extras: MutableList<Any> = mutableListOf(relationShipStatus, car)
    val address: MutableList<Any> = mutableListOf("Rua da vida", "Aqui não passas")
    val person = Person("Miguel", 23, 1.79, 65.4f, true,null,
            contacts, address, extras, 'c')

    val jsonVisitor = JSONInspector()
    println(jsonVisitor.objectToJSONPrettyPrint(person))

    println("All Strings")
    println(jsonVisitor.getAllStrings())
}