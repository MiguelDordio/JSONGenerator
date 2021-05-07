package testModels

import magicJSON.JSONCustomField
import magicJSON.JSONExcludeItem

enum class Direction {
        NORTH, SOUTH, WEST, EAST
}

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

class Cars(
        val brand: String,
        val model: String,
        val year: Int)

class Contacts(val email: String,
               val phone: String)

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
        @JSONCustomField("connections")
        val contacts: Contacts?,
        val extras: MutableList<*>?
)

class Compass(
        val name: String,
        val direction: Direction
)

class Employee(
        val employeeName: String,
        val age: Int,
        val boss: Boss
)

class Folder(
        val name: String,
        val children: MutableList<Folder>?
)
