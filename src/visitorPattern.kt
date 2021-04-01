fun countLeafs(c: Composite): Int {
    var count = 0
    c.children.forEach {
        if (it is Leaf)
            count++
        else if (it is Composite)
            count += countLeafs(it)
    }
    return count
}

fun countComposites(c: Composite): Int {
    var count = 1
    c.children.forEach {
        if (it is Composite) {
            count++
            count += countLeafs(it)
        }
    }
    return count
}

abstract class Element(val name: String, val parent: Composite?) {
    init {
        parent?.children?.add(this)
    }
    abstract fun accept(v: Visitor)
}

class Leaf(name: String, parent: Composite) : Element(name, parent) {
    override fun accept(v: Visitor) {
        v.visit(this)
    }
}

class Composite(name: String, parent: Composite? = null) : Element(name,parent) {
    val children = mutableListOf<Element>()
    val size get() = children.size
    override fun accept(v: Visitor) {
        if (v.visit(this))
            children.forEach {
                it.accept(v)
            }
    }
}

interface Visitor {
    fun visit(c: Composite): Boolean = true
    fun visit(l: Leaf) {}
}


fun main() {
    val a = Composite("a")
    Leaf("1",a)
    Leaf("2",a)
    Leaf("3",a)

    val b = Composite(".b", a)
    Leaf("4",a)

    val c = Composite("c", a)
    Leaf("5",c)

    val v = object : Visitor {
        var count = 0
        override fun visit(l: Leaf) {
            count++
        }

        override fun visit(c: Composite): Boolean {
            return !c.name.startsWith(".")
        }
    }
    a.accept(v)
    println(v.count)

}
