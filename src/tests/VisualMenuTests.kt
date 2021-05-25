package tests

import magicJSON.JSONInspector
import testModels.Boss
import testModels.Employee
import testModels.Folder
import visualizer.VisualMapping

fun main() {
    val boss = Boss("Hugo", "Manager", null, null)
    val employee = Employee("Miguel", 23, boss)
    //val jsonVisitor = JSONInspector()
    //print(jsonVisitor.objectToJSONPrettyPrint(employee))
    //jsonVisitor.openVisualMenu(employee)

    val subFolder4 = Folder("Testes2.kt", null)
    val subFolder3 = Folder("Testes1.kt", null)
    val subFolder2 = Folder("testes", mutableListOf(subFolder3, subFolder4))
    val subFolder1 = Folder("Main.kt", null)
    val mainFolder = Folder("src", mutableListOf(subFolder1, subFolder2))

    val jsonVisitor = JSONInspector()
    //print(jsonVisitor.objectToJSON(mainFolder))
    //print(jsonVisitor.objectToJSONPrettyPrint(mainFolder))
    //print(jsonVisitor.objectToJSONPrettyPrint(employee))

    print(jsonVisitor.objectToJSONPrettyPrint(employee))

    //val w = Injector.create(VisualMapping::class)
    //w.initializeJSON(mainFolder)
}