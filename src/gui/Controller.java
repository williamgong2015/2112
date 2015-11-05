package gui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;

/**
 * Implementation of controller methods that interact with the view but doesn't
 * need to interact with the underlying data of critter program. 
 * 
 * Ref: http://hg.openjdk.java.net/openjfx/8u-dev/rt/file/36a59c629605
 *      /apps/scenebuilder/samples/AirportApp/src/airportapp/Controller.java
 */
public class Controller {

	
	@FXML 
    private Slider worldframe_slider;
    @FXML
    private Pane world_pane;
    @FXML
    private ScrollPane worldframe_scrollpane;
    
    Group zoomGroup;
    
    private boolean assertLoaded(Node n, String name) {
    	if (n == null) {
    		System.out.println(name + " hasn't been loaded");
    		return false;
    	}
    	return true;
    }
    

    @FXML
    void initialize() {
        System.out.println("Controller has been initialized");
        if (!assertLoaded(worldframe_slider, "worldframe_slider")) 
        	return;
        if (!assertLoaded(world_pane, "world_pane"))
        	return;
        if (!assertLoaded(worldframe_scrollpane, "worldframe_scrollpane"))
        	return;
        
        // Wrap scroll content in a Group so ScrollPane re-computes scroll bars
        Group contentGroup = new Group();
        zoomGroup = new Group();
        contentGroup.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(worldframe_scrollpane.getContent());
        worldframe_scrollpane.setContent(contentGroup);
        worldframe_slider.setValue(10);
        world_pane.setOnZoom(new EventHandler<ZoomEvent>() {
        	@Override 
        	public void handle(ZoomEvent t) { 
        		worldframe_slider.setValue(t.getZoomFactor() * 
        				worldframe_slider.getValue()); 
        	} 
        });
        
        worldframe_slider.valueProperty().addListener((o, oldVal, newVal) -> 
        zoom((Double) newVal));
    }
    
    private void zoom(double scaleValue) {
          double scrollH = worldframe_scrollpane.getHvalue();
          double scrollV = worldframe_scrollpane.getVvalue();
          zoomGroup.setScaleX(scaleValue/10);
          zoomGroup.setScaleY(scaleValue/10);
          worldframe_scrollpane.setHvalue(scrollH);
          worldframe_scrollpane.setVvalue(scrollV);
      }

}
