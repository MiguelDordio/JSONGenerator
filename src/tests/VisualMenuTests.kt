package tests

import magicJSON.JSONInspector
import testModels.Boss
import testModels.Employee
import visualizer.VisualMapping

fun main() {
    val boss = Boss("Hugo", "Manager", null, null)
    val employee = Employee("Miguel", 23, boss)
    //val jsonVisitor = JSONInspector()
    //print(jsonVisitor.objectToJSONPrettyPrint(employee))
    //jsonVisitor.openVisualMenu(employee)

    val w = Injector.create(VisualMapping::class)
    w.initializeJSON(employee)
}