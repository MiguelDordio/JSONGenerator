package tests

import magicJSON.JSONInspector
import magicJSON.JSONPrimitive
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assertions
import testModels.Contacts


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
}