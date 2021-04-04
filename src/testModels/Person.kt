package testModels

import JSONAnnotation

@JSONAnnotation
class Person(
        val name: String,
        val age: Int?,
        val height: Double?,
        val weight: Float?,
        val isWonderfulPerson: Boolean?,
        val occupation: String?,
        val contacts: Contacts?,
        val address: MutableList<*>?,
        val extras: MutableList<*>?,
        val id: Char?)

@JSONAnnotation
class Cars(
        val brand: String,
        val model: String,
        val year: Int)

@JSONAnnotation
class Contacts(val email: String,
               val phone: String)

@JSONAnnotation
class RelationShipStatus(
        val maritalStatus: String,
        val birthday: String)
