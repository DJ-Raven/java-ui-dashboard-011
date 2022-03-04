package javaswingdev.swing.titlebar;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class ComponentResizer extends MouseAdapter {

    private final static Dimension MINIMUM_SIZE = new Dimension(10, 10);
    private final static Dimension MAXIMUM_SIZE = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    private static Map<Integer, Integer> cursors = new HashMap<Integer, Integer>();

    {
        cursors.put(1, Cursor.N_RESIZE_CURSOR);
        cursors.put(2, Cursor.W_RESIZE_CURSOR);
        cursors.put(4, Cursor.S_RESIZE_CURSOR);
        cursors.put(8, Cursor.E_RESIZE_CURSOR);
        cursors.put(3, Cursor.NW_RESIZE_CURSOR);
        cursors.put(9, Cursor.NE_RESIZE_CURSOR);
        cursors.put(6, Cursor.SW_RESIZE_CURSOR);
        cursors.put(12, Cursor.SE_RESIZE_CURSOR);
    }

    private Insets dragInsets;
    private Dimension snapSize;
    private int direction;
    protected static final int NORTH = 1;
    protected static final int WEST = 2;
    protected static final int SOUTH = 4;
    protected static final int EAST = 8;
    private Cursor sourceCursor;
    private boolean resizing;
    private Rectangle bounds;
    private Point pressed;
    private boolean autoscrolls;
    private Dimension minimumSize = MINIMUM_SIZE;
    private Dimension maximumSize = MAXIMUM_SIZE;

    public ComponentResizer() {
        this(new Insets(5, 5, 5, 5), new Dimension(1, 1));
    }

    public ComponentResizer(Component... components) {
        this(new Insets(5, 5, 5, 5), new Dimension(1, 1), components);
    }

    public ComponentResizer(Insets dragInsets, Component... components) {
        this(dragInsets, new Dimension(1, 1), components);
    }

    public ComponentResizer(Insets dragInsets, Dimension snapSize, Component... components) {
        setDragInsets(dragInsets);
        setSnapSize(snapSize);
        registerComponent(components);
    }

    public Insets getDragInsets() {
        return dragInsets;
    }

    public void setDragInsets(Insets dragInsets) {
        validateMinimumAndInsets(minimumSize, dragInsets);

        this.dragInsets = dragInsets;
    }

    public Dimension getMaximumSize() {
        return maximumSize;
    }

    public void setMaximumSize(Dimension maximumSize) {
        this.maximumSize = maximumSize;
    }

    public Dimension getMinimumSize() {
        return minimumSize;
    }

    public void setMinimumSize(Dimension minimumSize) {
        validateMinimumAndInsets(minimumSize, dragInsets);

        this.minimumSize = minimumSize;
    }

    public void deregisterComponent(Component... components) {
        for (Component component : components) {
            component.removeMouseListener(this);
            component.removeMouseMotionListener(this);
        }
    }

    public void registerComponent(Component... components) {
        for (Component component : components) {
            component.addMouseListener(this);
            component.addMouseMotionListener(this);
        }
    }

    public Dimension getSnapSize() {
        return snapSize;
    }

    public void setSnapSize(Dimension snapSize) {
        this.snapSize = snapSize;
    }

    private void validateMinimumAndInsets(Dimension minimum, Insets drag) {
        int minimumWidth = drag.left + drag.right;
        int minimumHeight = drag.top + drag.bottom;
        if (minimum.width < minimumWidth
                || minimum.height < minimumHeight) {
            String message = "Minimum size cannot be less than drag insets";
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Component source = e.getComponent();
        Point location = e.getPoint();
        direction = 0;
        if (location.x < dragInsets.left) {
            direction += WEST;
        }
        if (location.x > source.getWidth() - dragInsets.right - 1) {
            direction += EAST;
        }
        if (location.y < dragInsets.top) {
            direction += NORTH;
        }
        if (location.y > source.getHeight() - dragInsets.bottom - 1) {
            direction += SOUTH;
        }
        //  Mouse is no longer over a resizable border
        if (direction == 0) {
            source.setCursor(sourceCursor);
        } else {
            int cursorType = cursors.get(direction);
            Cursor cursor = Cursor.getPredefinedCursor(cursorType);
            source.setCursor(cursor);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (!resizing) {
            Component source = e.getComponent();
            sourceCursor = source.getCursor();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (!resizing) {
            Component source = e.getComponent();
            source.setCursor(sourceCursor);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        //  The mouseMoved event continually updates this variable
        if (direction == 0) {
            return;
        }
        //  Setup for resizing. All future dragging calculations are done based
        //  on the original bounds of the component and mouse pressed location.
        resizing = true;
        Component source = e.getComponent();
        pressed = e.getPoint();
        SwingUtilities.convertPointToScreen(pressed, source);
        bounds = source.getBounds();
        //  Making sure autoscrolls is false will allow for smoother resizing
        //  of components
        if (source instanceof JComponent) {
            JComponent jc = (JComponent) source;
            autoscrolls = jc.getAutoscrolls();
            jc.setAutoscrolls(false);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        resizing = false;
        Component source = e.getComponent();
        source.setCursor(sourceCursor);
        if (source instanceof JComponent) {
            ((JComponent) source).setAutoscrolls(autoscrolls);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (resizing == false) {
            return;
        }
        Component source = e.getComponent();
        Point dragged = e.getPoint();
        SwingUtilities.convertPointToScreen(dragged, source);
        changeBounds(source, direction, bounds, pressed, dragged);
    }

    protected void changeBounds(Component source, int direction, Rectangle bounds, Point pressed, Point current) {
        //  Start with original locaton and size
        int x = bounds.x;
        int y = bounds.y;
        int width = bounds.width;
        int height = bounds.height;
        //  Resizing the West or North border affects the size and location
        if (WEST == (direction & WEST)) {
            int drag = getDragDistance(pressed.x, current.x, snapSize.width);
            int maximum = Math.min(width + x, maximumSize.width);
            drag = getDragBounded(drag, snapSize.width, width, minimumSize.width, maximum);
            x -= drag;
            width += drag;
        }

        if (NORTH == (direction & NORTH)) {
            int drag = getDragDistance(pressed.y, current.y, snapSize.height);
            int maximum = Math.min(height + y, maximumSize.height);
            drag = getDragBounded(drag, snapSize.height, height, minimumSize.height, maximum);
            y -= drag;
            height += drag;
        }
        //  Resizing the East or South border only affects the size
        if (EAST == (direction & EAST)) {
            int drag = getDragDistance(current.x, pressed.x, snapSize.width);
            Dimension boundingSize = getBoundingSize(source);
            int maximum = Math.min(boundingSize.width - x, maximumSize.width);
            drag = getDragBounded(drag, snapSize.width, width, minimumSize.width, maximum);
            width += drag;
        }

        if (SOUTH == (direction & SOUTH)) {
            int drag = getDragDistance(current.y, pressed.y, snapSize.height);
            Dimension boundingSize = getBoundingSize(source);
            int maximum = Math.min(boundingSize.height - y, maximumSize.height);
            drag = getDragBounded(drag, snapSize.height, height, minimumSize.height, maximum);
            height += drag;
        }

        source.setBounds(x, y, width, height);
        source.validate();
    }

    private int getDragDistance(int larger, int smaller, int snapSize) {
        int halfway = snapSize / 2;
        int drag = larger - smaller;
        drag += (drag < 0) ? -halfway : halfway;
        drag = (drag / snapSize) * snapSize;

        return drag;
    }

    private int getDragBounded(int drag, int snapSize, int dimension, int minimum, int maximum) {
        while (dimension + drag < minimum) {
            drag += snapSize;
        }
        while (dimension + drag > maximum) {
            drag -= snapSize;
        }
        return drag;
    }

    private Dimension getBoundingSize(Component source) {
        if (source instanceof Window) {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle bounds = env.getMaximumWindowBounds();
            return new Dimension(bounds.width, bounds.height);
        } else {
            return source.getParent().getSize();
        }
    }
}
