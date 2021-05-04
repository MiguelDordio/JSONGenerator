import Testinhos.Inject
import Testinhos.Injector
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
}

class Window {
    private val frame = JFrame()

    // 1) eliminar dependencia de DefaultSetup;
    @Inject
    private lateinit var setup: FrameSetup

    // 2) eliminar dependencias das acoes concretas (Center, Size); @InjectAdd
    private val actions = mutableListOf<Action>(Move(), Size())

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


}

// -------------- Actions -----------

class DefaultSetup : FrameSetup {
    override val title: String
        get() = "Test"
    override val layoutManager: LayoutManager
        get() = GridLayout(2, 1)
}

class Move : Action {
    override val name: String
        get() = "center"

    override fun execute(window: Window) {
        window.move( 500, 500)
    }
}

class Size : Action {
    override val name: String
        get() = "change size"

    override fun execute(window: Window) {
        window.setSize(500, 500)
    }
}


fun main () {
    val w = Injector.create(Window::class)
    w.open()
}
