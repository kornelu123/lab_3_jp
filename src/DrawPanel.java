import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static java.awt.event.KeyEvent.*;

public class DrawPanel extends JPanel implements KeyListener, FocusListener, MouseWheelListener, MouseMotionListener, MouseListener {
    private Shape curShape = null, prevShape = null;
    private int shapeIndex;
    private final ArrayList<Shape> shapeList = new ArrayList<>(), selShapes = new ArrayList<>();
    private Rectangle selBox;
    private boolean drawFocus = true, isSelected = false, shouldBeGraphics = true, colorRequest = false, undo = false, ctrlPressed = false;
    private Point startPoint, endPoint;
    private shape reqShape = shape.LINE;

    private final Set<Integer> shortcuts = new HashSet<>();

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int rot = e.getWheelRotation();
        if (isSelected) {
            if (curShape instanceof RectangularShape recShape)
                recShape.setFrame(recShape.getX(), recShape.getY(), recShape.getWidth() + rot*4, recShape.getHeight() + rot*4);
            else  {
                Rectangle boundOfEllipse = curShape.getBounds();
                curShape = new Ellipse2D.Double(boundOfEllipse.getX(), boundOfEllipse.getY(), boundOfEllipse.getWidth() + rot*4, boundOfEllipse.getHeight() + rot*4);
            }
        }
        repaint();
    }

    @Override
    public void focusGained(FocusEvent e) {

    }

    @Override
    public void focusLost(FocusEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(isSelected){
            handleKeys(e);
            if(e.getKeyCode() == VK_CONTROL)
                colorRequest = true;
        }
        repaint();
    }

    private void  handleKeys(KeyEvent e) {
        if(isSelected) {
            switch (e.getKeyCode()) {
                case VK_UP -> moveShape(curShape, 0, -5);
                case VK_DOWN -> moveShape(curShape, 0, 5);
                case VK_RIGHT -> moveShape(curShape, 5, 0);
                case VK_LEFT -> moveShape(curShape, -5, 0);
                case VK_C -> colorRequest = true;
                default -> System.out.println("Unsupported key was pressed");
            }
        }
        shortcuts.add(e.getKeyCode());
    }
    @Override
    public void keyReleased(KeyEvent e) {
        shortcuts.remove(e.getKeyCode());
        if (e.getKeyCode() == VK_C)
            colorRequest = false;
        if(e.getKeyCode() == VK_CONTROL)
            colorRequest = false;
            changeShape();

        repaint();
    }

    public void setActionType(shape sh) {
        reqShape = sh;
    }

    public enum shape{
        RECTANGLE,
        CIRCLE,
        LINE,
        ELLIPSE,
        SELECT,
        DRAG,
    }

    public void setShape(){
        if(startPoint == null || endPoint == null) return;
        Double x = Math.min(startPoint.getX(), endPoint.getX());
        Double y = Math.min(startPoint.getY(), endPoint.getY());
        Double width, height;
        if(endPoint.getX() - startPoint.getX() >= 0){
            width = endPoint.getX() - startPoint.getX();
        }else{
            width = startPoint.getX() - endPoint.getX();
        }

        if(endPoint.getY() - startPoint.getY() >= 0){
            height = endPoint.getY() - startPoint.getY();
        }else{
            height =  startPoint.getY() - endPoint.getY();
        }

        switch (reqShape){
            case RECTANGLE -> curShape = new Rectangle2D.Double(x,y,width,height);
            case LINE -> curShape = new Line2D.Double(startPoint.getX(),startPoint.getY(),endPoint.getX(),endPoint.getY());
            case CIRCLE, ELLIPSE -> curShape = new Ellipse2D.Double(x,y,width,height);
            default -> curShape = null;
        }
        shapeList.add(curShape);
    }

    public void setDraw(boolean drawFocus) {
        this.drawFocus = drawFocus;
    }

    public void setSelect(boolean selected) {
        isSelected = selected;
    }

    public DrawPanel(int width, int height){
            setPreferredSize(new Dimension(width,height));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createDashedBorder(Color.black, 4, 0));
            addFocusListener();
            setFocusable(true);
            requestFocus();
            addKeyListener(this);
            addMouseListener(this);
            addMouseMotionListener(this);
            addMouseWheelListener(this);
            setVisible(true);
        }

    public void clear(){
        shapeList.removeAll(shapeList);
        repaint();
    }


        @Override public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D gr = (Graphics2D) g;

            for(Shape sh : shapeList){
                if((sh != curShape || !colorRequest)){
                    gr.setColor(Color.magenta);
                    gr.draw(sh);
                }
            }

            if(selBox != null) {
                gr.setColor(Color.orange);
                gr.fill(selBox);
                gr.draw(selBox);
            }

            if(colorRequest && curShape != null){
                gr.setColor(Color.cyan);
                changeShape();
                gr.fill(curShape);
            }
        }

        private void moveShapes(){
            if(selShapes.isEmpty())
                return;

            int dY= (int) (endPoint.getY() - startPoint.getY());
            int dX = (int) (endPoint.getX() - startPoint.getX());
	    for(Shape sh : selShapes){
		moveShape(sh, dX, dY);
	    }

    }
	   private void moveShape(Shape sh,int dX,int dY){
		if(sh instanceof RectangularShape recShape)
			recShape.setFrame(recShape.getX() + dX, recShape.getY() - dX, recShape.getWidth(), recShape.getHeight());
        else if (sh instanceof Line2D line)
            line.setLine(line.getX1() + dX, line.getY1() + dY, line.getX2() + dX, line.getY2() + dY);
        else  {
            Rectangle boundOfEllipse = curShape.getBounds();
            curShape = new Ellipse2D.Double(boundOfEllipse.getX() + dX, boundOfEllipse.getY() + dY, boundOfEllipse.getWidth(), boundOfEllipse.getHeight());
        }
	   }

       private void addFocusListener(){
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e){
                    repaint();
                }
            });
       }


            @Override
            public void mouseClicked(MouseEvent e) {
                if(isSelected){
                    selectShape(e.getX(), e.getY());
                    requestFocusInWindow();
            }
        }

        private void selectShape(int x, int y){
            for(Shape sh : shapeList){
                if(sh.getBounds2D().contains(x,y)){
                    curShape = sh;
                    return;
                }
            }
            curShape = null;
        }

        @Override public void mousePressed(MouseEvent e) {
            startPoint = e.getPoint();
            System.out.println("button pressed");

            if(isSelected){
                selBox = new Rectangle((Point) startPoint);
                selShapes.clear();
            }
        }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("mouse released");
                endPoint = e.getPoint();
                if(isSelected){
                    moveShapes();
                    selShapes.clear();
                    selBox = null;
                }
                if(drawFocus){
                    setShape();
                }
                repaint();
            }


            private void changeShape(){
                if(shortcuts.contains(VK_CONTROL) && shortcuts.contains(VK_C)){
                    shapeIndex = shapeList.indexOf(curShape);
                    if(!ctrlPressed && undo){
                        prevShape = null;
                        undo = false;
                        shapeList.remove(shapeIndex);
                        shapeList.add(prevShape);
                        repaint();
                    } else if(shapeIndex != -1){undo = true;
                        shapeList.remove(shapeIndex);
                        prevShape = curShape;
                        curShape = modifyShape();
                        shapeList.add(curShape);
                        shapeIndex = shapeList.indexOf(curShape);
                        repaint();
                    }
                }
            }

            private Shape modifyShape(){
                if(curShape instanceof Rectangle2D) {
                    RectangularShape rect = (RectangularShape) curShape;
                    double x = rect.getX();
                    double y = rect.getY();

                    double width = rect.getWidth();
                    double height = rect.getHeight();

                    return new Rectangle2D.Double(x, y, width, height);
                }else if(curShape instanceof Ellipse2D){
                    Ellipse2D ellipse = (Ellipse2D) curShape;
                    double x = ellipse.getX();
                    double y = ellipse.getY();

                    double width = ellipse.getWidth();
                    double height = ellipse.getHeight();

                    return new Rectangle2D.Double(x, y, width, height);
                } else {
                    return curShape;
                }

            }


            private boolean isClickInArea(MouseEvent e){
                startPoint = e.getPoint();
                for(Shape shape: shapeList){
                    if(shape.contains(e.getPoint())){
                        curShape = shape;
                        return true;
                    }
                }
                return false;
            }
            private void moveShape(Point2D p){
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                System.out.println("focused on draw panel");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                System.out.println("focus on draw panel lost");
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if(isSelected){
                    Point end = e.getPoint();
                    Point endPoint = e.getPoint();
                    selBox.setBounds(
                            (int) Math.min(startPoint.getX(), end.getX()),
                            (int) Math.min(startPoint.getY(), end.getY()),
                            (int) Math.abs(end.getX() - startPoint.getX()),
                            (int) Math.abs(end.getY() - startPoint.getY())
                    );
                    selShapes.clear();
                    for(Shape sh: shapeList){
                        if(selBox.intersects(sh.getBounds()))
                            selShapes.add(sh);
                    }
                }
                repaint();
             }


            @Override
            public void mouseMoved(MouseEvent e) {

            }
}
