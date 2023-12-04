import javax.swing.*;
import java.awt.*;

public class App {
    private final JFrame frame = new JFrame();
    private JPanel  butPan = new JPanel();
    private DrawPanel drawPan;
    private static final String[] butNames = {"RECTANGLE", "CIRCLE", "LINE", "ELLIPSE", "SELECT", "CLEAR"};

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
        });
    }

    public App(){
        panelInit();
        buttonsInit();
        frameInit();
        frame.add(butPan, BorderLayout.NORTH);
        frame.add(drawPan, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void frameInit(){
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("rysowac");
        frame.setSize(600,600);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.add(drawPan);
    }

    private void panelInit(){
        drawPan = new DrawPanel(500,500);
    }

    private void buttonsInit(){
        butPan.setPreferredSize(new Dimension(500,75));
        butPan.setLayout(new GridLayout(1,0));
        for(int i=0; i<butNames.length; i++){
            JButton but = new JButton(butNames[i]);
            switch (butNames[i]){
                case "RECTANGLE":{
                    but.addActionListener(e -> {
                        drawPan.setActionType(DrawPanel.shape.RECTANGLE);
                        drawPan.setSelect(false);
                        drawPan.setDraw(true);
                    });
                    break;
                }
                case "CIRCLE":{
                    but.addActionListener(e->{
                        drawPan.setActionType(DrawPanel.shape.CIRCLE);
                        drawPan.setSelect(false);
                        drawPan.setDraw(true);
                    });
                    break;
                }
                case "LINE":{
                    but.addActionListener(e ->{
                        drawPan.setActionType(DrawPanel.shape.LINE);
                        drawPan.setSelect(false);
                        drawPan.setDraw(true);
                    });
                    break;
                }
                case "ELLIPSE":{
                    but.addActionListener(e-> {
                        drawPan.setActionType(DrawPanel.shape.ELLIPSE);
                        drawPan.setSelect(false);
                        drawPan.setDraw(true);
                    });
                    break;
                }
                case "SELECT": {
                    but.addActionListener(e->{
                        drawPan.setActionType(DrawPanel.shape.SELECT);
                        drawPan.setDraw(false);
                        drawPan.setSelect(true);
                    });
                    break;
                }
                case "CLEAR": {
                    but.addActionListener(e-> {
                        drawPan.clear();
                    });
                }
            }
            butPan.add(but);
        }

    }
}