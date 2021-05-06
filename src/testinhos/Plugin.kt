import java.awt.*
import java.util.*
import javax.swing.JButton
import javax.swing.JFrame

interface FrameSetup {
    val title: String
    val layoutManager: LayoutManager
}

interface Action {
    val name: String
    fun execute(window: Window)
    fun undo(window: Window)
}

class Window {
    private val frame = JFrame()

    @Inject
    private lateinit var setup: FrameSetup

    @InjectAdd
    private lateinit var actions: MutableList<Action>

    private var operations = mutableListOf<Action>()

    init {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.size = Dimension(300, 200)
    }

    val location get() = frame.location
    val dimension get() = frame.size

    fun open() {
        frame.title = setup.title
        frame.layout = setup.layoutManager
        actions.forEach { action ->
            val button = JButton(action.name)
            button.addActionListener { action.execute(this@Window) }
            frame.add(button)
        }
        frame.isVisible = true
    }

    fun move(x: Int, y: Int) {
        frame.location = Point(x, y)
    }

    fun setSize(width: Int, height: Int) {
        require(width > 0)
        require(height > 0)
        frame.size = Dimension(width, height)
    }

    fun saveOperation(action: Action) {
        operations.add(action)
    }

    fun undo() {
        if (operations.isNotEmpty()) {
            val lastOp = operations.last()
            if (lastOp::class != Undo::class)
                lastOp.undo(this)
            operations.removeAt(operations.size - 1)
        }
    }

}

// -------------- Actions -----------

class DefaultSetup : FrameSetup {
    override val title: String
        get() = "Test"
    override val layoutManager: LayoutManager
        get() = GridLayout(2, 1)
}

class Move : Action {
    lateinit var windowPos: Point

    override val name: String
        get() = "center"

    override fun execute(window: Window) {
        windowPos = window.location
        window.move( 500, 500)
        window.saveOperation(this)
    }

    override fun undo(window: Window) {
        window.move(windowPos.x, windowPos.y)
    }
}

class Size : Action {
    lateinit var windowSize : Dimension

    override val name: String
        get() = "change size"

    override fun execute(window: Window) {
        windowSize = window.dimension
        window.setSize(500, 500)
        window.saveOperation(this)
    }

    override fun undo(window: Window) {
        window.setSize(windowSize.getWidth().toInt(), windowSize.getHeight().toInt())
    }
}

class Undo : Action {
    override val name: String
        get() = "undo"

    override fun execute(window: Window) {
        window.undo()
    }

    override fun undo(window: Window) {
        window.undo()
    }
}


fun main () {
    val w = Injector.create(Window::class)
    w.open()
}
