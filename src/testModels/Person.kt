package testModels

import magicJSON.JSONClass
import magicJSON.JSONExcludeItem
import magicJSON.JSONObjectItem

class Person(
        val name: String,
        val age: Int?,
        val height: Double?,
        val weight: Float?,
        val isWonderfulPerson: Boolean?,
        val occupation: String?,
        @JSONObjectItem
        val contacts: Contacts?,
        val address: MutableList<*>?,
        val extras: MutableList<*>?,
        val id: Char?)

@JSONClass
class Cars(
        val brand: String,
        val model: String,
        val year: Int)

class Contacts(val email: String,
               val phone: String)

@JSONClass
class RelationShipStatus(
        val maritalStatus: String,
        val birthday: String)

class PetOwner(
        val owner: String,
        val pets: MutableList<*>)

class Boss(
        val bossName: String,
        @JSONExcludeItem
        val role: String,
        @JSONObjectItem
        val contacts: Contacts?,
        val extras: MutableList<*>?
)
