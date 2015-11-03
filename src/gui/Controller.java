package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

/**
 * Actually not used yet
 * @author yuxin
 *
 */
public class Controller {

	// root element
	@FXML
    private VBox root_vbox;
	
	// elements under root_vbox
    @FXML
    private HBox secondary_hbox;
    @FXML
    private SplitPane secondary_splitpane;
    
    // elements under secondary_hbox
    @FXML
    private MenuButton newworld_manubutton;
    @FXML
    private MenuItem newworld_default_manuitem;
    @FXML
    private MenuItem newworld_custom_manuitem;
    @FXML
    public Button loadworld_button;
    @FXML
    private Button addcritter_button;
    @FXML
    private TextField critternum_textfield;
    @FXML
    private Button insertcritter_button;
    @FXML
    private Slider simulationspeed_slider;
    @FXML
    private Button run_button;
    @FXML
    private Button stop_button;
    @FXML
    private Button step_button;
    // elements under secondar_hbox end here
    
    // elements under secondary_splitpane
    @FXML
    private VBox thirdary_vbox;
    @FXML
    private ScrollPane worldinfoframe_scrollpane;
    
    // elements under thirdary_vbox
    @FXML
    private SplitPane underthirdaryvbox_splitpane;
    @FXML
    private AnchorPane worldframe_anchorpane;
    @FXML
    private ScrollPane worldframe_scrollpane;
    @FXML 
    private Slider worldframe_slider;
    @FXML
    private AnchorPane world_anchorpane;
    @FXML
    private ScrollPane critterinfoframe_scrollpane;
    @FXML
    private AnchorPane critterinfoframe_anchorpane;
    @FXML
    private TextFlow critterinfo_textflow;
    // elements under thirdary_vbox end here
    
    // elements under worldinfoframe_scrollpane
    @FXML
    private AnchorPane worldinfoframe_anchorpane;
    @FXML
    private TextFlow worldinfo_textflow;
    // elements under thirdary_scrollpane end here
}
