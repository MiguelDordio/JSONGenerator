package tests

import magicJSON.JSONInspector
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions
import testModels.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JSONInspectorTest {
    /**
     * Object types
     */

    @Test
    fun testWriteObjectWithStrings() {
        val contacts = Contacts("mig@gmail.com", "999888777")
        val expectedJSON = "{\"email\":\"mig@gmail.com\",\"phone\":\"999888777\"}"
        val jsonVisitor = JSONInspector()
        Assertions.assertEquals(expectedJSON, jsonVisitor.objectToJSON(contacts))
    }

    @Test
    fun testWriteObjectWithLists() {
        val petOwner = PetOwner("John", mutableListOf("Dog", "Cat", "Turtle"))
        val expectedJSON = "{\"owner\":\"John\",\"pets\":[\"Dog\",\"Cat\",\"Turtle\"]}"
        val jsonVisitor = JSONInspector()
        Assertions.assertEquals(expectedJSON, jsonVisitor.objectToJSON(petOwner))
    }

    @Test
    fun testWriteComplexObject() {
        val contacts = Contacts("mig@hotmail.com", "999888777")
        val relationShipStatus = RelationShipStatus("Married", "09-12-1979")
        val car = Cars("Cloudy", "Biggy", 2009)
        val extras: MutableList<Any> = mutableListOf(relationShipStatus, car)
        val address: MutableList<Any> = mutableListOf("Rua da vida", "Aqui não passas")
        val person = Person("Miguel", 23, 1.79, 65.4f, true,null,
                contacts, address, extras, 'c')
        val expectedJSON = "{\"address\":[\"Rua da vida\",\"Aqui não passas\"],\"age\":23,\"Contacts\":{\"email\":\"mig@hotmail.com\"," +
                "\"phone\":\"999888777\"},\"extras\":[{\"birthday\":\"09-12-1979\",\"maritalStatus\":\"Married\"}," +
                "{\"brand\":\"Cloudy\",\"model\":\"Biggy\",\"year\":2009}],\"height\":1.79,\"id\":\"c\"," +
                "\"isWonderfulPerson\":\"true\",\"name\":\"Miguel\",\"occupation\":\"null\",\"weight\":65.4}"
        val jsonVisitor = JSONInspector()
        Assertions.assertEquals(expectedJSON, jsonVisitor.objectToJSON(person))
    }

    @Test
    fun testWriteObjectWithMaps() {
        val parts = mapOf(
                "Electrical"
                        to Part("Electrical", "Part1", "Heating Element", "B293"),
                "Exterior"
                        to Part("Exterior", "Part2", "Lever", "18A"))
        val machine = Machine("Toaster", parts)
        val expectedJSON = "{\"name\":\"Toaster\",\"parts\":{" +
                "\"Electrical\":{\"compat\":\"B293\",\"description\":\"Heating Element\",\"group\":\"Electrical\",\"id\":\"Part1\"}," +
                "\"Exterior\":{\"compat\":\"18A\",\"description\":\"Lever\",\"group\":\"Exterior\",\"id\":\"Part2\"}" +
                "}}"
        val jsonVisitor = JSONInspector()
        Assertions.assertEquals(expectedJSON, jsonVisitor.objectToJSON(machine))
    }

    @Test
    fun testWriteObjectWithComplexMaps() {
        val parts = mapOf(
                PartDescriptor("Electrical", "Descriptor1")
                to Part("Electrical", "Part1", "Heating Element", "B293"),
                PartDescriptor("Exterior", "Descriptor2")
                to Part("Exterior", "Part2", "Lever", "18A"))
        val machine = Machine("Toaster", parts)
        val expectedJSON = "{\"name\":\"Toaster\",\"parts\":{" +
                "\"Electrical|Descriptor1\":{\"compat\":\"B293\",\"description\":\"Heating Element\",\"group\":\"Electrical\",\"id\":\"Part1\"}," +
                "\"Exterior|Descriptor2\":{\"compat\":\"18A\",\"description\":\"Lever\",\"group\":\"Exterior\",\"id\":\"Part2\"}" +
                "}}"
        val jsonVisitor = JSONInspector()
        Assertions.assertEquals(expectedJSON, jsonVisitor.objectToJSON(machine))
    }

    @Test
    fun testJSONPrettyPrint() {
        val contacts = Contacts("mig@gmail.com", "999888777")
        val relationShipStatus = RelationShipStatus("Married", "09-12-1978")
        val car = Cars("Cloudy", "Biggy", 2008)
        val extras: MutableList<Any> = mutableListOf(relationShipStatus, car)
        val address: MutableList<Any> = mutableListOf("Rua da vida", "Aqui não passas")
        val person = Person("Miguel", 23, 1.79, 65.4f, true,null,
                contacts, address, extras, 'c')
        val expectedJSON = "{\n" +
                "\t\"address\": [\n" +
                "\t\t\"Rua da vida\",\n" +
                "\t\t\"Aqui não passas\"\n" +
                "\t],\n" +
                "\t\"age\": 23,\n" +
                "\t\"Contacts\": {\n" +
                "\t\t\"email\": \"mig@gmail.com\",\n" +
                "\t\t\"phone\": \"999888777\"\n" +
                "\t},\n" +
                "\t\"extras\": [" +
                "\n\t\t{\n" +
                "\t\t\t\"birthday\": \"09-12-1978\",\n" +
                "\t\t\t\"maritalStatus\": \"Married\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"brand\": \"Cloudy\",\n" +
                "\t\t\t\"model\": \"Biggy\",\n" +
                "\t\t\t\"year\": 2008\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"height\": 1.79,\n" +
                "\t\"id\": \"c\",\n" +
                "\t\"isWonderfulPerson\": \"true\",\n" +
                "\t\"name\": \"Miguel\",\n" +
                "\t\"occupation\": \"null\",\n" +
                "\t\"weight\": 65.4\n" +
                "}"
        val jsonVisitor = JSONInspector()
        Assertions.assertEquals(expectedJSON, jsonVisitor.objectToJSONPrettyPrint(person))
    }


    /**
     * Utility methods
     */

    @Test
    fun testGetAllStrings() {
        val contacts = Contacts("mig@hotmail.com", "999888777")
        val relationShipStatus = RelationShipStatus("Married", "09-12-1979")
        val car = Cars("Cloudy", "Biggy", 2009)
        val extras: MutableList<Any> = mutableListOf(relationShipStatus, car)
        val address: MutableList<Any> = mutableListOf("Rua da vida", "Aqui não passas")
        val person = Person("Miguel", 23, 1.79, 65.4f, true,null,
                contacts, address, extras, 'c')
        val expectedStrings = mutableListOf("Rua da vida", "Aqui não passas", "mig@hotmail.com", "999888777",
                "09-12-1979", "Married", "Cloudy", "Biggy", "Miguel")
        val jsonVisitor = JSONInspector()
        Assertions.assertEquals(expectedStrings, jsonVisitor.getAllStrings(person))
    }

    @Test
    fun testChangeFieldNameAndExclusionAnnotation() {
        val contacts = Contacts("mig@gmail.com", "999888777")
        val relationShipStatus = RelationShipStatus("Married", "09-12-1979")
        val car = Cars("Cloudy", "Biggy", 2009)
        val extras: MutableList<Any> = mutableListOf(relationShipStatus, car)
        val boss = Boss("Pedro", "Project Manager", contacts, extras)
        val jsonObject = JSONInspector()
        val expectedJSON = "{\"bossName\":\"Pedro\",\"Connections\":{\"email\":\"mig@gmail.com\",\"phone\":\"999888777\"},\"extras\":[{\"birthday\":\"09-12-1979\",\"maritalStatus\":\"Married\"},{\"brand\":\"Cloudy\",\"model\":\"Biggy\",\"year\":2009}]}"
        Assertions.assertEquals(expectedJSON, jsonObject.objectToJSON(boss))
    }

    @Test
    fun testEnum() {
        val compass = Compass("Yo-yo", Direction.NORTH)
        val jsonObject = JSONInspector()
        val expectedJSON = "{\"direction\":\"NORTH\",\"name\":\"Yo-yo\"}"
        Assertions.assertEquals(expectedJSON, jsonObject.objectToJSON(compass))
    }
}