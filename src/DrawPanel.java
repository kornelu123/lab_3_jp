import javax.sound.sampled.Line;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class DrawPanel extends JPanel implements MouseMotionListener, MouseListener {
    private Shape curShape = null;
    private final ArrayList<Shape> shapeList = new ArrayList<>();
    private boolean drawFocus = true, isSelected = false, shouldBeGraphics = true;
    private Point startPoint, endPoint;
    private shape reqShape = shape.LINE;
    public enum shape{
        RECTANGLE,
        CIRCLE,
        LINE,
        ELLIPSE,
        SELECT,
        DRAG,
    }


    public void setActionType(shape s) {
        reqShape = s;
    }

    public void setDraw(boolean drawFocus) {
        this.drawFocus = drawFocus;
    }

    public void setSelect(boolean selected) {
        isSelected = selected;
    }

    public DrawPanel(int width, int height){
            setFocusable(true);
            requestFocus();
            setPreferredSize(new Dimension(width,height));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createDashedBorder(Color.black, 4, 0));
            addMouseListener(this);
            addMouseMotionListener(this);
            setVisible(true);
        }
    public void clear(){
        setBackground(Color.white);
        getGraphics().clearRect(0,0,getWidth(),getHeight());
        getGraphics().dispose();
    }


        @Override public void paintComponent(Graphics g){
            Graphics2D gr = (Graphics2D) g;
            if(isSelected){
                for (Shape sh : shapeList){
                    if(sh == curShape){
                        continue;
                    }
                    gr.draw(sh);
                }
            }
            if(shouldBeGraphics) {
                setShape();
                gr.setColor(Color.black);
                if (curShape != null) {
                    gr.draw(curShape);
                }
                shouldBeGraphics = true;
            }
        }

            @Override
            public void mouseClicked(MouseEvent e) {
            if(!drawFocus){
                isSelected = isClickInArea(e);
                if(!isSelected) {
                    drawFocus = !drawFocus;
                }
            }
        }

        @Override public void mousePressed(MouseEvent e) {
            startPoint = e.getPoint();
        }

            @Override
            public void mouseReleased(MouseEvent e) {
            clear();
            endPoint = e.getPoint();
            paintComponent(getGraphics());
        }

        private void setShape(){
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

            switch(reqShape){
                case ELLIPSE, CIRCLE -> curShape = new Ellipse2D.Double(x,y,width,height);
                case LINE -> curShape = new Line2D.Double(startPoint.getX(),startPoint.getY(),endPoint.getX(),endPoint.getY());
                case RECTANGLE -> curShape = new Rectangle2D.Double(x,y,width,height);
                case  SELECT-> {
                    curShape = null;
                    break;
                    }
                case DRAG ->
                }
            shapeList.add(curShape);
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
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

            @Override
            public void mouseDragged(MouseEvent e) {

             }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
}
