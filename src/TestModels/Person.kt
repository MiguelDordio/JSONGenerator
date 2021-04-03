package TestModels

import JSONAnnotation

@JSONAnnotation
class Person(
        val name: String,
        val age: Int?,
        val height: Double?,
        val isWonderfulPerson: Boolean?,
        val occupation: String?,
        val contacts: Contacts?,
        val address: MutableList<*>?,
        val extras: MutableList<*>?
) {
}
