package cz.vut.ija.game.view;

import cz.vut.ija.game.model.GameSave;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.Consumer;

public class LoadGameView extends BorderPane {
    private ListView<GameSave> saveListView;
    private Button loadButton;
    private Button backButton;
    private Consumer<GameSave> onSaveSelected;

    public LoadGameView(List<GameSave> saves) {
        setPadding(new Insets(20));
        
        // Nadpis
        Label titleLabel = new Label("Select Saved Game");
        titleLabel.getStyleClass().add("title-label");
        setTop(titleLabel);
        BorderPane.setAlignment(titleLabel, Pos.CENTER);
        BorderPane.setMargin(titleLabel, new Insets(0, 0, 20, 0));
        
        // Seznam uložených her
        saveListView = new ListView<>();
        saveListView.setCellFactory(param -> new javafx.scene.control.ListCell<GameSave>() {
            @Override
            protected void updateItem(GameSave save, boolean empty) {
                super.updateItem(save, empty);
                if (empty || save == null) {
                    setText(null);
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date = sdf.format(save.getSaveDate());
                    String status = save.isCompleted() ? "Completed" : "In Progress";
                    setText(String.format("%s - %s - %s - %d moves", 
                            date, save.getBoardSize(), status, save.getMoves().size()));
                }
            }
        });
        saveListView.getItems().addAll(saves);
        setCenter(saveListView);
        
        // Tlačítka
        loadButton = new Button("Load");
        loadButton.setDisable(true);
        backButton = new Button("Back");
        
        HBox buttonBox = new HBox(10, backButton, loadButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        setBottom(buttonBox);
        
        // Povolit tlačítko Load po výběru hry
        saveListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadButton.setDisable(newVal == null);
        });
        
        // Nastavit akci pro tlačítko Load
        loadButton.setOnAction(e -> {
            if (onSaveSelected != null) {
                GameSave selected = saveListView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    onSaveSelected.accept(selected);
                }
            }
        });
    }
    
    public void setOnSaveSelected(Consumer<GameSave> callback) {
        this.onSaveSelected = callback;
    }
    
    public Button getBackButton() {
        return backButton;
    }
}