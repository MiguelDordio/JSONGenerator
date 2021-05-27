# 1. Standart JSON Generator library 

A JSON generator library to serialize objects into JSON formated text implemented in Kotlin

## Custom Annotations

@JJSONCustomField: Used to apply a custom property name in the final JSON string

@JSONExcludeItem: Used to exclude a given class property is to be ignored when generating the JSON string

## How to use

1. Create a JSONInspector instance
2. (Optional) Add one of the library`s annotations to the desired object
3. Invoke either the .objectToJSON or .objectToJSONPrettyPrint acording to the desired results

## Examples

### Example 1:

Classes:
```kt
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
        val contacts: Contacts?,
        val extras: MutableList<*>?
)
```

Code:
```kt
val contacts = Contacts("mig@gmail.com", "999888777")
val relationShipStatus = RelationShipStatus("Married", "09-12-1979")
val car = Cars("Cloudy", "Biggy", 2009)
val extras: MutableList<Any> = mutableListOf(relationShipStatus, car)
val boss = Boss("Pedro", "Project Manager", contacts, extras)
val jsonObject = JSONInspector()
```

JSON output:
```
{
	"bossName": "Pedro",
	"Contacts": {
		"email": "mig@gmail.com",
		"phone": "999888777"
	},
	"extras": [{
			"birthday": "09-12-1979",
			"maritalStatus": "Married"
		},
		{
			"brand": "Cloudy",
			"model": "Biggy",
			"year": 2009
		}
	]
}
```

### Example 2:

```kt
val contacts = Contacts("mig@gmail.com", "999888777")
val relationShipStatus = RelationShipStatus("Married", "09-12-1978")
val car = Cars("Cloudy", "Biggy", 2008)
val extras: MutableList<Any> = mutableListOf(relationShipStatus, car)
val address: MutableList<Any> = mutableListOf("Rua da vida", "Aqui não passas")
val person = Person("Miguel", 23, 1.79, 65.4f, true,null, contacts, address, extras, 'c')

val jsonVisitor = JSONInspector()
jsonVisitor.objectToJSONPrettyPrint(person)
```

JSON output:
```
{
	"address": [
		"Rua da vida",
		"Aqui não passas"
	],
	"age": 23,
	"Contacts": {
		"email": "mig@gmail.com",
		"phone": "999888777"
	},
	"extras": [{
			"birthday": "09-12-1978",
			"maritalStatus": "Married"
		},
		{
			"brand": "Cloudy",
			"model": "Biggy",
			"year": 2008
		}
	],
	"height": 1.79,
	"id": "c",
	"isWonderfulPerson": "true",
	"name": "Miguel",
	"occupation": "null",
	"weight": 65.4
}
```

# 2. GUI

In order to enhance the visual experience of a json string, a simple GUI was developed to re-create the json generated from a given object

## Functionalities

* Get node depth
* Search for a keyword
* Interact with the json created


# 3. JSON Plugin

To allow customization, the GUI enables the creation of custom plugins

## GUI customizations

* Name the GUI window
* Define the GUI window size and layout
* Define a custom rule to customize each visual json node


## Plugin Actions

Custom actions can be created to add extra functionalities, like:

* Editing a node name
* Export the json generated as a file


## Plugin example

![image](https://user-images.githubusercontent.com/32375361/119889584-a86c4200-bf2e-11eb-9eef-f166b247871c.png)


