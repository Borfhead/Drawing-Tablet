package cs2410.assn6;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

/**
 * @author Dylan Cox
 * @version 1
 */
public class AdvancedDrawingTablet extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Scene mainScene;
    private Pane mainPane;
    private ToolPane toolPane;
    private Pane drawPane;
    private EventHandler<MouseEvent> pressedHandler;
    private EventHandler<MouseEvent> dragDetectedHandler;
    private EventHandler<MouseEvent> draggedHandler;
    private Ellipse ell;
    private Rectangle rec;
    private Path free;
    private Rectangle clip;
    private Shape selected;
    private double startX;
    private double startY;
    private double origX;
    private double origY;

    public void start(Stage primaryStage) throws Exception {

        initHandlers();
        toolPane = new ToolPane();
        toolPane.setFillPickerAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(toolPane.editBtnSelected() && selected != null)
                {
                    selected.setFill(toolPane.getFillPickerValue());
                }
            }
        });

        toolPane.setStrokePickerAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(toolPane.editBtnSelected() && selected != null)
                {
                    selected.setStroke(toolPane.getStrokePickerValue());
                }
            }
        });

        toolPane.setStrokeSizeAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(toolPane.editBtnSelected() && selected != null)
                {
                    selected.setStrokeWidth(toolPane.getStrokeSizeValue());
                }
            }
        });

        drawPane = new Pane();
        drawPane.setPrefHeight(500);
        drawPane.setPrefWidth(500);
        drawPane.setOnMousePressed(pressedHandler);
        drawPane.setOnDragDetected(dragDetectedHandler);
        drawPane.setOnMouseDragged(draggedHandler);
        drawPane.prefWidthProperty().bind(primaryStage.widthProperty());
        drawPane.prefHeightProperty().bind(primaryStage.heightProperty());

        clip = new Rectangle();
        clip.widthProperty().bind(drawPane.widthProperty());
        clip.heightProperty().bind(drawPane.heightProperty());
        drawPane.setClip(clip);

        mainPane = new VBox();
        mainPane.getChildren().add(toolPane);
        mainPane.getChildren().add(drawPane);

        mainScene = new Scene(mainPane);
        primaryStage.setScene(mainScene);
        primaryStage.setWidth(650);
        primaryStage.setHeight(650);
        primaryStage.setTitle("Advanced Drawing Tablet");
        primaryStage.show();
    }

    public void initHandlers()
    {
        pressedHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                startX = event.getX();
                startY = event.getY();

                if(toolPane.editBtnSelected())
                {
                    if(event.getSource() instanceof Shape)
                    {
                        selected = (Shape)event.getSource();
                        toolPane.setFillPickerValue((Color)selected.getFill());
                        toolPane.setStrokePickerValue((Color)selected.getStroke());
                        toolPane.setStrokeSizeValue((int)selected.getStrokeWidth());
                        origX = event.getX();
                        origY = event.getY();
                    }
                }
                else if(toolPane.eraseBtnSelected())
                {
                    if(event.getSource() instanceof Shape)
                    {
                        drawPane.getChildren().remove(event.getSource());
                    }
                }
                else if(toolPane.ellBtnSelected())
                {
                    ell = new Ellipse();
                }
                else if(toolPane.rectBtnSelected())
                {
                    rec = new Rectangle();
                }
                else if(toolPane.freeBtnSelected())
                {
                    free = new Path();
                    free.getElements().add(new MoveTo(event.getX(), event.getY()));
                }


            }
        };

        dragDetectedHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {


                if(toolPane.ellBtnSelected())
                {
                    ell.setCenterX(event.getX());
                    ell.setCenterY(event.getY());
                    ell.setRadiusX(2);
                    ell.setRadiusY(2);
                    ell.setFill(toolPane.getFillPickerValue());
                    ell.setStroke(toolPane.getStrokePickerValue());
                    ell.setStrokeWidth(toolPane.getStrokeSizeValue());
                    ell.setOnMousePressed(pressedHandler);
                    ell.setOnMouseDragged(draggedHandler);
                    drawPane.getChildren().add(ell);
                }
                else if(toolPane.rectBtnSelected())
                {
                    rec.setX(event.getX());
                    rec.setY(event.getY());
                    rec.setFill(toolPane.getFillPickerValue());
                    rec.setStroke(toolPane.getStrokePickerValue());
                    rec.setStrokeWidth(toolPane.getStrokeSizeValue());
                    rec.setOnMousePressed(pressedHandler);
                    rec.setOnMouseDragged(draggedHandler);
                    drawPane.getChildren().add(rec);
                }
                else if(toolPane.freeBtnSelected())
                {
                    free.setStrokeWidth(toolPane.getStrokeSizeValue());
                    free.setStroke(toolPane.getStrokePickerValue());
                    free.setOnMousePressed(pressedHandler);
                    free.setOnMouseDragged(draggedHandler);
                    drawPane.getChildren().add(free);
                }
            }
        };

        draggedHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if(toolPane.editBtnSelected())
                {
                    if(event.getSource() instanceof Shape)
                    {
                        Shape temp = (Shape) event.getSource();
                        temp.setTranslateX(temp.getTranslateX() + event.getX() - origX);
                        temp.setTranslateY(temp.getTranslateY() + event.getY() - origY);
                    }
                }
                else if(toolPane.ellBtnSelected())
                {
                    ell.setRadiusY(Math.abs(startY - event.getY())/2);
                    ell.setRadiusX(Math.abs(startX - event.getX())/2);
                    ell.setCenterX((startX + event.getX()) / 2);
                    ell.setCenterY((startY + event.getY()) / 2);
                }
                else if(toolPane.rectBtnSelected())
                {
                    if(startX > event.getX())
                    {
                        rec.setX(event.getX());
                    }
                    if(startY > event.getY())
                    {
                        rec.setY(event.getY());
                    }
                    rec.setWidth(Math.abs(startX - event.getX()));
                    rec.setHeight(Math.abs(startY - event.getY()));

                }
                else if(toolPane.freeBtnSelected())
                {
                    free.getElements().add(new LineTo(event.getX(), event.getY()));
                }
            }
        };
    }


}
