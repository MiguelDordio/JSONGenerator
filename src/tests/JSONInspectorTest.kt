package tests

import magicJSON.JSONInspector
import magicJSON.JSONPrimitive
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions
import testModels.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JSONInspectorTest {

    /**
     * Primitive types
     */

    @Test
    fun testWriteNull() {
        Assertions.assertEquals("\"nothing\":\"null\"", JSONPrimitive("nothing", null).generateJSON())
    }

    @Test
    fun testWriteBoolean() {
        Assertions.assertEquals("\"boolean\":\"true\"", JSONPrimitive("boolean", true).generateJSON())
    }

    @Test
    fun testWriteInt() {
        Assertions.assertEquals("\"number\":20", JSONPrimitive("number", 20).generateJSON())
    }

    @Test
    fun testWriteFloat() {
        Assertions.assertEquals("\"number\":20.3", JSONPrimitive("number", 20.3f).generateJSON())
    }

    @Test
    fun testWriteDouble() {
        Assertions.assertEquals("\"number\":20.56", JSONPrimitive("number", 20.56).generateJSON())
    }

    @Test
    fun testWriteString() {
        Assertions.assertEquals("\"text\":\"big text\"", JSONPrimitive("text", "big text").generateJSON())
    }

    @Test
    fun testWriteChar() {
        val charItem = 'c'
        Assertions.assertEquals("\"text\":\"c\"", JSONPrimitive("text", charItem).generateJSON())
    }

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
}