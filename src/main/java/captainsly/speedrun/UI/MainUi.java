package captainsly.speedrun.UI;

import static captainsly.speedrun.UI.API.INTERNATIONAL;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.tsunderebug.speedrun4j.Speedrun4J;
import com.tsunderebug.speedrun4j.game.Game;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainUi extends Application {

	// UI Elements
	private BorderPane root;
	private GridPane grid;
	private GridPane contentGrid;

	private Scene scene; // The Main Scene of the application

	private Game currentGame;

	private Button previousListBtn, nextListBtn;
	private ListView<Game> gameListView; 
	
	// ContentGrid Elements
	private Image currentGameBoxArt;
	private Label currentGameIntName, currentGameReleaseDate;
	private List<Label> currentGamePlatforms;

	private List<Game> gameList;

	@Override
	public void start(Stage primaryStage) throws Exception {
		gameList = new ArrayList<Game>();

		root = new BorderPane();
		grid = new GridPane();
		contentGrid = new GridPane();

		previousListBtn = new Button();
		nextListBtn = new Button();

		gameListView = new ListView<Game>();

		// -------------------
		
		currentGameBoxArt = new Image("unavailable64.png");
		currentGameIntName = new Label("");
		currentGameReleaseDate = new Label("");
		
		currentGamePlatforms = new ArrayList<Label>();
	
		
		contentGrid.add(new ImageView(currentGameBoxArt), 0, 0);
		
		grid.add(previousListBtn, 0, 0);
		grid.add(nextListBtn, 1, 0);
		grid.add(gameListView, 0, 1);

		setupGameListView();

		root.setLeft(gameListView);
		root.setCenter(contentGrid);

		scene = new Scene(root);

		primaryStage.setScene(scene);
		primaryStage.show();

	}

	private void setupGameListView() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				long time = System.currentTimeMillis();

				for (int i = 0; i < 22000; i = i + 200) {
					System.out.println(i);
					for (Game game : getGames(i)) {
						gameList.add(game);
					}
				}

				System.out.println((System.currentTimeMillis() - time));

			}
		}).run();

		gameListView.setItems(FXCollections.observableArrayList(gameList));
		gameListView.setCellFactory(new Callback<ListView<Game>, ListCell<Game>>() {

			@Override
			public ListCell<Game> call(ListView<Game> param) {
				ListCell<Game> cell = new ListCell<Game>() {

					@Override
					protected void updateItem(Game item, boolean empty) {
						super.updateItem(item, empty);

						if (!empty) this.setText(item.getNames().get("international"));
						else this.setText("");

					}

				};

				return cell;
			}
		});

		gameListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Game>() {

			@Override
			public void changed(ObservableValue<? extends Game> observable, Game oldValue, Game newValue) {
				if (oldValue == newValue) return;
				System.out.println(newValue.getId());
				
				
				setupContentGrid(newValue);
				
			}
		});

	}
	
	private void setupContentGrid(Game game) {
//		currentGameBoxArt = new Image(game.getAssets().getCoverMedium().getUri());
		
		currentGameIntName.setText(game.getNames().get(INTERNATIONAL.getId()));
		currentGameReleaseDate.setText(game.getReleaseDate());
		
		if (game.getPlatforms() != null) {
			for (int i = 0; i < game.getPlatforms().length; i++) {
				System.out.println(i);
				currentGamePlatforms.add(new Label(game.getPlatforms()[i]));	
			}
		}
		int i = 0;
		
		for (Label l : currentGamePlatforms) {
			contentGrid.add(l, 0, i);
			i++;
		}
		
		
	}

	private Game[] getGames(int offset) {
		try {
			Gson g = new Gson();
			URL u = new URL(Speedrun4J.API_ROOT + "games?&max=200&offset=" + offset);
			HttpURLConnection c = (HttpURLConnection) u.openConnection();
			c.setRequestProperty("User-Agent", Speedrun4J.USER_AGENT);
			InputStreamReader r = new InputStreamReader(c.getInputStream());

			GameData games = g.fromJson(r, GameData.class);
			r.close();

			return games.data;

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private class GameData {
		Game[] data;
	}


}
