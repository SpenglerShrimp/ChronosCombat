package _2dhif.thegamereal.view;

import _2dhif.thegamereal.model.TheGameModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;

public class TheGameView {
    private TheGameModel model;
    private Scene scene;
    private StackPane root;

    private VBox startScreenPane;
    private Label startPromptLabel;

    private StackPane introPane;
    private ImageView introEnemyView;
    private Label introDialog;

    private StackPane endScreenPane;

    private BorderPane battlePane;
    private Pane battleArena;
    private Label dialogText;

    private ImageView playerImageView, enemyImageView, explosionView;
    private Label playerHpLabel, enemyHpLabel, turnTimerLabel, parryStatusLabel;

    private Pane classroomPane, hallwayPane;
    private ImageView overworldPlayerView;
    private ImageView classroomEnemyView;
    private Label healPromptLabel;
    private Label healIndicatorLabel;
    private Label upgradePromptLabel;

    private VBox upgradeMenuPane;
    private Label spLabel;
    private Button btnUpSpd, btnUpJmp, btnUpStr, btnUpHp, btnUpPry;
    private Button btnDnSpd, btnDnJmp, btnDnStr, btnDnHp, btnDnPry;
    private Button btnCloseMenu;
    private Label lblSpd, lblJmp, lblStr, lblHp, lblPry;

    private GridPane actionMenu;
    private Button btnPunch, btnKick, btnUppercut, btnItem;

    private List<Node> drawnBulletNodes = new ArrayList<>();
    private List<Label> drawnDamageTexts = new ArrayList<>();
    private List<ImageView> drawnItems = new ArrayList<>();

    private Image imgStand, imgWalkR1, imgWalkR2, imgWalkL1, imgWalkL2, imgJump, imgBack;
    private Image imgPunch, imgKick, imgUppercut, imgThrow, imgParry;
    private Image imgExplosion, imgGerin, imgPaul, imgBook;

    private Image[] itemImages = new Image[7];

    private double lastPlayerX = 100;
    private boolean facingRight = true;
    private double lastOverworldX = 400;
    private double lastOverworldY = 300;

    private MediaPlayer menuMusicPlayer;

    public TheGameView(TheGameModel model) {
        this.model = model;

        root = new StackPane();
        root.setStyle("-fx-background-color: black;");

        StackPane gameContainer = new StackPane();
        gameContainer.setMaxSize(800, 600);

        scene = new Scene(root, 800, 600);

        loadImages();
        loadAudio();
        buildStartScreen();
        buildIntroScene();
        buildBattleScene();
        buildClassroomScene();
        buildHallwayScene();
        buildUpgradeMenu();
        buildEndScreen();

        gameContainer.getChildren().addAll(classroomPane, hallwayPane, battlePane, upgradeMenuPane, startScreenPane, introPane, endScreenPane);
        root.getChildren().add(gameContainer);
    }

    private void loadAudio() {
        try {
            Media media = new Media(getClass().getResource("/main_menu_music.mp3").toExternalForm());
            menuMusicPlayer = new MediaPlayer(media);
            menuMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        } catch (Exception e) {}
    }

    public void playMenuMusic() {
        if (menuMusicPlayer != null) menuMusicPlayer.play();
    }

    public void stopMenuMusic() {
        if (menuMusicPlayer != null) menuMusicPlayer.stop();
    }

    private void buildStartScreen() {
        startScreenPane = new VBox(30);
        startScreenPane.setAlignment(Pos.CENTER);
        startScreenPane.setStyle("-fx-background-color: #121212;");

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER);

        Label lblChronos = new Label("Chronos");
        lblChronos.setFont(Font.font("Impact", FontWeight.BOLD, 54));
        lblChronos.setTextFill(Color.WHITE);
        lblChronos.setEffect(new DropShadow(15, Color.WHITE));

        Label lblCombat = new Label("Combat");
        lblCombat.setFont(Font.font("Impact", FontWeight.BOLD, 54));
        lblCombat.setTextFill(Color.web("#FF4444"));
        lblCombat.setEffect(new DropShadow(15, Color.web("#FF4444")));

        titleBox.getChildren().addAll(lblChronos, lblCombat);

        startPromptLabel = new Label("Drücken sie Enter");
        startPromptLabel.setFont(Font.font("Trebuchet MS", FontWeight.BOLD, 22));
        startPromptLabel.setTextFill(Color.WHITE);

        startScreenPane.getChildren().addAll(titleBox, startPromptLabel);
    }

    private void loadImages() {
        try {
            imgStand = new Image(getClass().getResource("/player_stand.png").toExternalForm());
            imgBack = new Image(getClass().getResource("/player_back.png").toExternalForm());
            imgWalkR1 = new Image(getClass().getResource("/player_walk_r1.png").toExternalForm());
            imgWalkR2 = new Image(getClass().getResource("/player_walk_r2.png").toExternalForm());
            imgWalkL1 = new Image(getClass().getResource("/player_walk_l1.png").toExternalForm());
            imgWalkL2 = new Image(getClass().getResource("/player_walk_l2.png").toExternalForm());
            imgJump = new Image(getClass().getResource("/player_jump.png").toExternalForm());

            imgPunch = new Image(getClass().getResource("/player_punch.png").toExternalForm());
            imgKick = new Image(getClass().getResource("/player_kick.png").toExternalForm());
            imgUppercut = new Image(getClass().getResource("/player_uppercut.png").toExternalForm());
            imgThrow = new Image(getClass().getResource("/player_throw.png").toExternalForm());
            imgParry = new Image(getClass().getResource("/player_parry.png").toExternalForm());

            imgExplosion = new Image(getClass().getResource("/explosion.gif").toExternalForm());
            imgGerin = new Image(getClass().getResource("/gerin_sprite.png").toExternalForm());

            imgPaul = new Image(getClass().getResource("/paul_sprite.png").toExternalForm());
            imgBook = new Image(getClass().getResource("/book_pile.png").toExternalForm());

            itemImages[0] = new Image(getClass().getResource("/item_bottle.png").toExternalForm());
            itemImages[1] = new Image(getClass().getResource("/item_paper.png").toExternalForm());
            itemImages[2] = new Image(getClass().getResource("/item_can.png").toExternalForm());
            itemImages[3] = new Image(getClass().getResource("/item_egg_shells.png").toExternalForm());
            itemImages[4] = new Image(getClass().getResource("/item_banana.png").toExternalForm());
            itemImages[5] = new Image(getClass().getResource("/item_tissue.png").toExternalForm());
            itemImages[6] = new Image(getClass().getResource("/item_carton.png").toExternalForm());
        } catch (Exception e) {}
    }

    private void buildIntroScene() {
        introPane = new StackPane();
        introPane.setPrefSize(800, 600);

        try {
            ImageView bgView = new ImageView(new Image(getClass().getResource("/battle_background.png").toExternalForm()));
            bgView.setFitWidth(800); bgView.setFitHeight(600); bgView.setPreserveRatio(false);
            introPane.getChildren().add(bgView);
        } catch (Exception e) {
            introPane.setStyle("-fx-background-color: lightblue;");
        }

        introEnemyView = new ImageView();
        introEnemyView.setFitHeight(350);
        introEnemyView.setPreserveRatio(true);

        introDialog = new Label("Student Attacks!");
        introDialog.setTextFill(Color.WHITE);
        introDialog.setFont(Font.font("Courier New", FontWeight.BOLD, 36));
        introDialog.setStyle("-fx-background-color: rgba(0,0,0,0.8); -fx-padding: 15px; -fx-border-color: red; -fx-border-width: 2;");

        StackPane.setAlignment(introEnemyView, Pos.CENTER);
        StackPane.setAlignment(introDialog, Pos.BOTTOM_CENTER);
        StackPane.setMargin(introDialog, new Insets(0, 0, 50, 0));

        introPane.getChildren().addAll(introEnemyView, introDialog);
        introPane.setVisible(false);
    }

    private void buildEndScreen() {
        endScreenPane = new StackPane();
        endScreenPane.setPrefSize(800, 600);
        endScreenPane.setStyle("-fx-background-color: black;");

        try {
            ImageView endImg = new ImageView(new Image(getClass().getResource("/end_screen.png").toExternalForm()));
            endImg.setFitWidth(800); endImg.setFitHeight(600); endImg.setPreserveRatio(false);
            endScreenPane.getChildren().add(endImg);
        } catch (Exception e) {
            Label winLabel = new Label("YOU WIN");
            winLabel.setTextFill(Color.WHITE);
            winLabel.setFont(Font.font("Impact", FontWeight.BOLD, 72));
            endScreenPane.getChildren().add(winLabel);
        }
        endScreenPane.setVisible(false);
    }

    private void buildBattleScene() {
        battlePane = new BorderPane();
        battlePane.setVisible(false);
        battlePane.setStyle("-fx-background-color: white;");

        battleArena = new Pane();
        battleArena.setPrefSize(800, 460);

        try {
            ImageView bgView = new ImageView(new Image(getClass().getResource("/battle_background.png").toExternalForm()));
            bgView.setFitWidth(800); bgView.setFitHeight(460); bgView.setPreserveRatio(false);
            battleArena.getChildren().add(bgView);
        } catch (Exception e) {
            battleArena.getChildren().add(new Rectangle(800, 460, Color.LIGHTSKYBLUE));
        }

        for (TheGameModel.Platform p : model.getPlatforms()) {
            Rectangle rect = new Rectangle(p.x, p.y, p.width, p.height);
            rect.setFill(Color.web("#444444")); rect.setStroke(Color.BLACK); rect.setStrokeWidth(2);
            battleArena.getChildren().add(rect);
        }

        playerImageView = new ImageView();
        playerImageView.setFitWidth(80); playerImageView.setFitHeight(80);

        enemyImageView = new ImageView();
        enemyImageView.setFitWidth(120); enemyImageView.setPreserveRatio(true);

        explosionView = new ImageView(imgExplosion);
        explosionView.setFitWidth(150); explosionView.setFitHeight(150);
        explosionView.setVisible(false);

        playerHpLabel = new Label("HP: 100");
        playerHpLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        playerHpLabel.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-padding: 2px; -fx-background-radius: 5px;");

        enemyHpLabel = new Label("Student HP: 50");
        enemyHpLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        enemyHpLabel.setStyle("-fx-background-color: rgba(255,255,255,0.7); -fx-padding: 2px; -fx-background-radius: 5px;");

        turnTimerLabel = new Label("10.0s");
        turnTimerLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 36));
        turnTimerLabel.setTextFill(Color.WHITE);
        turnTimerLabel.setStyle("-fx-effect: dropshadow(two-pass-box, black, 2, 1.0, 0, 0);");
        turnTimerLabel.setLayoutX(350); turnTimerLabel.setLayoutY(20);

        parryStatusLabel = new Label("PARRY ON COOLDOWN!");
        parryStatusLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        parryStatusLabel.setTextFill(Color.ORANGE);
        parryStatusLabel.setStyle("-fx-effect: dropshadow(two-pass-box, black, 2, 1.0, 0, 0);");
        parryStatusLabel.setLayoutX(300); parryStatusLabel.setLayoutY(60);

        battleArena.getChildren().addAll(playerImageView, enemyImageView, playerHpLabel, enemyHpLabel, turnTimerLabel, parryStatusLabel, explosionView);

        HBox bottomArea = new HBox();
        bottomArea.setPrefSize(800, 140);
        bottomArea.setStyle("-fx-border-color: black; -fx-border-width: 4; -fx-background-color: #F8F8F8;");

        dialogText = new Label("...");
        dialogText.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        dialogText.setPadding(new Insets(20));
        dialogText.setPrefWidth(400);
        dialogText.setWrapText(true);

        actionMenu = new GridPane();
        actionMenu.setHgap(10); actionMenu.setVgap(10);
        actionMenu.setAlignment(Pos.CENTER);
        actionMenu.setPadding(new Insets(10));

        btnPunch = createBtn("Punch [1]");
        btnKick = createBtn("Kick [2]");
        btnUppercut = createBtn("Uppercut [3]");
        btnItem = createBtn("Throw Item [4]");

        actionMenu.add(btnPunch, 0, 0);
        actionMenu.add(btnKick, 1, 0);
        actionMenu.add(btnUppercut, 0, 1);
        actionMenu.add(btnItem, 1, 1);

        bottomArea.getChildren().addAll(dialogText, actionMenu);
        battlePane.setCenter(battleArena);
        battlePane.setBottom(bottomArea);
    }

    private void buildClassroomScene() {
        classroomPane = new Pane();
        classroomPane.setPrefSize(800, 600);

        try {
            ImageView classBg = new ImageView(new Image(getClass().getResource("/classroom_background.png").toExternalForm()));
            classBg.setFitWidth(800); classBg.setFitHeight(600); classBg.setPreserveRatio(false);
            classroomPane.getChildren().add(classBg);
        } catch (Exception e) {
            classroomPane.setStyle("-fx-background-color: #D2B48C; -fx-background-image: repeating-linear-gradient(45deg, transparent, transparent 10px, #C19A6B 10px, #C19A6B 20px);");
        }

        classroomEnemyView = new ImageView();
        classroomEnemyView.setFitWidth(80);
        classroomEnemyView.setPreserveRatio(true);
        classroomEnemyView.setLayoutX(360);
        classroomEnemyView.setLayoutY(260);

        classroomPane.getChildren().add(classroomEnemyView);
    }

    private void buildHallwayScene() {
        hallwayPane = new Pane();
        hallwayPane.setPrefSize(800, 600);

        try {
            ImageView hallBg = new ImageView(new Image(getClass().getResource("/hallway_background.jpg").toExternalForm()));
            hallBg.setFitWidth(800); hallBg.setFitHeight(600); hallBg.setPreserveRatio(false);
            hallwayPane.getChildren().add(hallBg);
        } catch (Exception e) {
            hallwayPane.setStyle("-fx-background-color: #555555;");
        }

        try {
            ImageView fountain = new ImageView(new Image(getClass().getResource("/healing_fountain.png").toExternalForm()));
            fountain.setFitWidth(150); fountain.setPreserveRatio(true);
            fountain.setLayoutX(325); fountain.setLayoutY(200);
            hallwayPane.getChildren().add(fountain);
        } catch (Exception e) {
            Rectangle fallbackFountain = new Rectangle(325, 200, 150, 100);
            fallbackFountain.setFill(Color.CYAN);
            hallwayPane.getChildren().add(fallbackFountain);
        }

        healPromptLabel = new Label("Heal in fountain? (Press E)");
        healPromptLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        healPromptLabel.setTextFill(Color.WHITE);
        healPromptLabel.setStyle("-fx-background-color: black; -fx-padding: 5px;");
        healPromptLabel.setLayoutX(220); healPromptLabel.setLayoutY(150);
        healPromptLabel.setVisible(false);

        healIndicatorLabel = new Label("+ FULLY HEALED +");
        healIndicatorLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
        healIndicatorLabel.setTextFill(Color.LIMEGREEN);
        healIndicatorLabel.setStyle("-fx-effect: dropshadow(two-pass-box, black, 2, 1.0, 0, 0);");
        healIndicatorLabel.setVisible(false);

        upgradePromptLabel = new Label("Upgrades (Press U)");
        upgradePromptLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        upgradePromptLabel.setTextFill(Color.WHITE);
        upgradePromptLabel.setStyle("-fx-background-color: black; -fx-padding: 5px;");
        upgradePromptLabel.setLayoutX(550); upgradePromptLabel.setLayoutY(20);

        hallwayPane.getChildren().addAll(healPromptLabel, healIndicatorLabel, upgradePromptLabel);

        overworldPlayerView = new ImageView();
        overworldPlayerView.setFitWidth(80);
        overworldPlayerView.setFitHeight(80);
    }

    private void buildUpgradeMenu() {
        upgradeMenuPane = new VBox(15);
        upgradeMenuPane.setAlignment(Pos.CENTER);
        upgradeMenuPane.setMaxSize(400, 450);
        upgradeMenuPane.setStyle("-fx-background-color: rgba(0,0,0,0.85); -fx-border-color: white; -fx-border-width: 4; -fx-padding: 20px;");
        upgradeMenuPane.setVisible(false);

        Label title = new Label("UPGRADES");
        title.setFont(Font.font("Courier New", FontWeight.BOLD, 30));
        title.setTextFill(Color.GOLD);

        spLabel = new Label("Skill Points: 0");
        spLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        spLabel.setTextFill(Color.WHITE);

        btnDnSpd = createSmallBtn("[-]"); btnUpSpd = createSmallBtn("[+]"); lblSpd = createStatLabel("Lv.1");
        btnDnJmp = createSmallBtn("[-]"); btnUpJmp = createSmallBtn("[+]"); lblJmp = createStatLabel("Lv.1");
        btnDnStr = createSmallBtn("[-]"); btnUpStr = createSmallBtn("[+]"); lblStr = createStatLabel("Lv.1");
        btnDnHp  = createSmallBtn("[-]"); btnUpHp  = createSmallBtn("[+]"); lblHp  = createStatLabel("Lv.1");
        btnDnPry = createSmallBtn("[-]"); btnUpPry = createSmallBtn("[+]"); lblPry = createStatLabel("Lv.1");

        btnCloseMenu = createBtn("Close Upgrades");

        upgradeMenuPane.getChildren().addAll(
                title, spLabel,
                createUpgradeRow("Speed", btnDnSpd, lblSpd, btnUpSpd),
                createUpgradeRow("Jump Power", btnDnJmp, lblJmp, btnUpJmp),
                createUpgradeRow("Strength", btnDnStr, lblStr, btnUpStr),
                createUpgradeRow("Max HP", btnDnHp, lblHp, btnUpHp),
                createUpgradeRow("Parry Dmg", btnDnPry, lblPry, btnUpPry),
                btnCloseMenu
        );
    }

    private HBox createUpgradeRow(String name, Button minus, Label lvl, Button plus) {
        Label nameLbl = new Label(name);
        nameLbl.setPrefWidth(120);
        nameLbl.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        nameLbl.setTextFill(Color.WHITE);

        HBox row = new HBox(15, nameLbl, minus, lvl, plus);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    private Button createSmallBtn(String text) {
        Button b = new Button(text);
        b.setPrefSize(40, 30);
        b.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        b.setStyle("-fx-background-color: white; -fx-cursor: hand;");
        b.setFocusTraversable(false);
        return b;
    }

    private Label createStatLabel(String text) {
        Label l = new Label(text);
        l.setPrefWidth(50);
        l.setAlignment(Pos.CENTER);
        l.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        l.setTextFill(Color.GOLD);
        return l;
    }

    private Button createBtn(String text) {
        Button b = new Button(text);
        b.setPrefSize(180, 40);
        b.setFont(Font.font("Courier New", FontWeight.BOLD, 14));
        b.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-cursor: hand;");
        b.setFocusTraversable(false);
        return b;
    }

    public void render() {
        TheGameModel.GameState state = model.getCurrentState();
        TheGameModel.Room room = model.getCurrentRoom();

        startScreenPane.setVisible(state == TheGameModel.GameState.START_SCREEN);
        introPane.setVisible(state == TheGameModel.GameState.BATTLE_INTRO);
        endScreenPane.setVisible(state == TheGameModel.GameState.END_SCREEN);

        battlePane.setVisible(state == TheGameModel.GameState.BATTLE_ENEMY_TURN ||
                state == TheGameModel.GameState.BATTLE_PLAYER_TURN ||
                state == TheGameModel.GameState.BATTLE_ACTION_DELAY ||
                state == TheGameModel.GameState.BATTLE_DEATH_ANIMATION);

        classroomPane.setVisible(state == TheGameModel.GameState.OVERWORLD && (room == TheGameModel.Room.CLASSROOM_1 || room == TheGameModel.Room.CLASSROOM_2));
        hallwayPane.setVisible(state == TheGameModel.GameState.OVERWORLD && room == TheGameModel.Room.HALLWAY);

        if (state == TheGameModel.GameState.START_SCREEN) {
            if ((model.getFramesElapsed() / 30) % 2 == 0) startPromptLabel.setTextFill(Color.WHITE);
            else startPromptLabel.setTextFill(Color.TRANSPARENT);
            return;
        }

        if (state == TheGameModel.GameState.BATTLE_INTRO) {
            if (model.getCurrentBattleId() == 1) {
                introEnemyView.setImage(imgGerin);
                introDialog.setText("Student Gerin attacks!");
            } else {
                introEnemyView.setImage(imgPaul);
                introDialog.setText("Student Paul attacks!");
            }
            return;
        }

        if (state == TheGameModel.GameState.OVERWORLD && room == TheGameModel.Room.HALLWAY && model.isUpgradeMenuOpen()) {
            upgradeMenuPane.setVisible(true);
            spLabel.setText("Skill Points: " + model.getSkillPoints());

            lblSpd.setText("Lv." + model.getStatLevelSpeed());
            lblJmp.setText("Lv." + model.getStatLevelJump());
            lblStr.setText("Lv." + model.getStatLevelStrength());
            lblHp.setText("Lv." + model.getStatLevelMaxHp());
            lblPry.setText("Lv." + model.getStatLevelParryDmg());

            boolean noPoints = model.getSkillPoints() <= 0;
            btnUpSpd.setDisable(noPoints); btnDnSpd.setDisable(model.getStatLevelSpeed() <= 1);
            btnUpJmp.setDisable(noPoints); btnDnJmp.setDisable(model.getStatLevelJump() <= 1);
            btnUpStr.setDisable(noPoints); btnDnStr.setDisable(model.getStatLevelStrength() <= 1);
            btnUpHp.setDisable(noPoints);  btnDnHp.setDisable(model.getStatLevelMaxHp() <= 1);
            btnUpPry.setDisable(noPoints); btnDnPry.setDisable(model.getStatLevelParryDmg() <= 1);
        } else {
            upgradeMenuPane.setVisible(false);
        }

        if (state == TheGameModel.GameState.OVERWORLD) {
            double currentOX = model.getOverworldX();
            double currentOY = model.getOverworldY();
            boolean isMoving = (currentOX != lastOverworldX || currentOY != lastOverworldY);

            if (room == TheGameModel.Room.HALLWAY && !hallwayPane.getChildren().contains(overworldPlayerView)) {
                hallwayPane.getChildren().add(overworldPlayerView);
            } else if (room != TheGameModel.Room.HALLWAY && !classroomPane.getChildren().contains(overworldPlayerView)) {
                classroomPane.getChildren().add(overworldPlayerView);
            }

            overworldPlayerView.setLayoutX(currentOX);
            overworldPlayerView.setLayoutY(currentOY);

            if (model.getHealGlowFrames() > 0) {
                overworldPlayerView.setEffect(new DropShadow(20, Color.YELLOW));

                healIndicatorLabel.setVisible(true);
                healIndicatorLabel.setLayoutX(325);
                healIndicatorLabel.setLayoutY(180 - (60 - model.getHealGlowFrames()) * 0.5);
                healIndicatorLabel.setOpacity(model.getHealGlowFrames() / 60.0);
            } else {
                overworldPlayerView.setEffect(null);
                healIndicatorLabel.setVisible(false);
            }

            if (imgStand != null) {
                boolean useFrame1 = (model.getFramesElapsed() / 10) % 2 == 0;
                switch (model.getOverworldFacing()) {
                    case UP: overworldPlayerView.setImage(imgBack); break;
                    case DOWN: overworldPlayerView.setImage(imgStand); break;
                    case LEFT: overworldPlayerView.setImage(isMoving ? (useFrame1 ? imgWalkL1 : imgWalkL2) : imgStand); break;
                    case RIGHT: overworldPlayerView.setImage(isMoving ? (useFrame1 ? imgWalkR1 : imgWalkR2) : imgStand); break;
                }
            }

            healPromptLabel.setVisible(model.canHeal());

            if (room == TheGameModel.Room.CLASSROOM_1 && !model.isBoss1Defeated()) {
                classroomEnemyView.setImage(imgGerin);
                classroomEnemyView.setScaleX(1.0);
                classroomEnemyView.setScaleY(1.0);
                classroomEnemyView.setVisible(true);
            } else if (room == TheGameModel.Room.CLASSROOM_2 && !model.isBoss2Defeated()) {
                classroomEnemyView.setImage(imgPaul);
                classroomEnemyView.setScaleX(1.5);
                classroomEnemyView.setScaleY(1.5);
                classroomEnemyView.setVisible(true);
            } else {
                classroomEnemyView.setVisible(false);
            }

            lastOverworldX = currentOX;
            lastOverworldY = currentOY;
            return;
        }

        if (battlePane.isVisible()) {
            actionMenu.setVisible(state == TheGameModel.GameState.BATTLE_PLAYER_TURN);
            boolean isPlayerTurn = (state == TheGameModel.GameState.BATTLE_PLAYER_TURN);
            btnPunch.setDisable(!isPlayerTurn); btnKick.setDisable(!isPlayerTurn);
            btnUppercut.setDisable(!isPlayerTurn); btnItem.setDisable(!isPlayerTurn);

            if (state == TheGameModel.GameState.BATTLE_DEATH_ANIMATION) {
                explosionView.setVisible(true);
                if (model.isPlayerDead()) {
                    playerImageView.setVisible(false);
                    explosionView.setLayoutX(model.getPlayerX() - 35); explosionView.setLayoutY(model.getPlayerY() - 35);
                } else if (model.isEnemyDead()) {
                    enemyImageView.setVisible(false);
                    explosionView.setLayoutX(model.getEnemyX() - 15); explosionView.setLayoutY(model.getEnemyY() - 15);
                }
            } else {
                explosionView.setVisible(false);
                playerImageView.setVisible(true); enemyImageView.setVisible(true);
            }

            if (model.getCurrentBattleId() == 1) {
                enemyImageView.setImage(imgGerin);
                enemyHpLabel.setText("Student Gerin HP: " + model.getEnemyHP());
            } else {
                enemyImageView.setImage(imgPaul);
                enemyHpLabel.setText("Student Paul HP: " + model.getEnemyHP());
            }

            if (!model.isPlayerDead() && imgStand != null) {
                double currentX = model.getPlayerX();
                if (model.getParryActiveFrames() > 0 || model.getParrySuccessFrames() > 0) {
                    playerImageView.setImage(imgParry);
                } else if (!model.getActiveAttackAnim().isEmpty()) {
                    switch (model.getActiveAttackAnim()) {
                        case "Punch": playerImageView.setImage(imgPunch); break;
                        case "Kick": playerImageView.setImage(imgKick); break;
                        case "Uppercut": playerImageView.setImage(imgUppercut); break;
                        case "Throw": playerImageView.setImage(imgThrow); break;
                    }
                } else {
                    boolean isMoving = (currentX != lastPlayerX);
                    if (currentX > lastPlayerX) facingRight = true;
                    else if (currentX < lastPlayerX) facingRight = false;

                    if (!model.isGrounded()) playerImageView.setImage(imgJump);
                    else if (isMoving) playerImageView.setImage(facingRight ? ((model.getFramesElapsed()/10)%2==0 ? imgWalkR1 : imgWalkR2) : ((model.getFramesElapsed()/10)%2==0 ? imgWalkL1 : imgWalkL2));
                    else playerImageView.setImage(imgStand);
                }
                lastPlayerX = currentX;
            }

            playerImageView.setLayoutX(model.getPlayerX()); playerImageView.setLayoutY(model.getPlayerY());
            enemyImageView.setLayoutX(model.getEnemyX()); enemyImageView.setLayoutY(model.getEnemyY());

            playerHpLabel.setText("HP: " + model.getPlayerHP() + "/" + model.getActualMaxHP());
            playerHpLabel.setLayoutX(model.getPlayerX()); playerHpLabel.setLayoutY(model.getPlayerY() + 90);

            enemyHpLabel.setLayoutX(model.getEnemyX()); enemyHpLabel.setLayoutY(model.getEnemyY() + 130);

            if (state == TheGameModel.GameState.BATTLE_PLAYER_TURN) {
                turnTimerLabel.setVisible(true);
                double secs = model.getTurnTimerFrames() / 60.0;
                turnTimerLabel.setText(String.format("%.1fs", secs));
                turnTimerLabel.setTextFill(secs <= 3.0 ? Color.RED : Color.WHITE);
            } else turnTimerLabel.setVisible(false);

            parryStatusLabel.setVisible(model.getParryCooldownFrames() > 0);

            battleArena.getChildren().removeAll(drawnBulletNodes);
            drawnBulletNodes.clear();

            for (TheGameModel.Bullet b : model.getBullets()) {
                if (b.type == 0) {
                    Circle c = new Circle(b.x, b.y, 8, Color.PURPLE);
                    c.setStroke(Color.WHITE); c.setStrokeWidth(2);
                    drawnBulletNodes.add(c);
                    battleArena.getChildren().add(c);
                } else if (b.type == 1 && imgBook != null) {
                    ImageView iv = new ImageView(imgBook);
                    iv.setFitWidth(32); iv.setFitHeight(32);
                    iv.setLayoutX(b.x - 16); iv.setLayoutY(b.y - 16);
                    iv.setRotate(model.getFramesElapsed() * 3);
                    drawnBulletNodes.add(iv);
                    battleArena.getChildren().add(iv);
                }
            }

            battleArena.getChildren().removeAll(drawnItems); drawnItems.clear();
            for (TheGameModel.ThrownItem item : model.getThrownItems()) {
                if (itemImages[item.imageIndex] != null) {
                    ImageView iv = new ImageView(itemImages[item.imageIndex]);
                    iv.setFitWidth(32); iv.setFitHeight(32); iv.setLayoutX(item.x); iv.setLayoutY(item.y);
                    iv.setRotate(model.getFramesElapsed() * 10);
                    drawnItems.add(iv); battleArena.getChildren().add(iv);
                }
            }

            battleArena.getChildren().removeAll(drawnDamageTexts); drawnDamageTexts.clear();
            for (TheGameModel.DamageText dt : model.getDamageTexts()) {
                Label lbl = new Label(dt.text);
                lbl.setFont(Font.font("Courier New", FontWeight.BOLD, 24));
                lbl.setTextFill(dt.text.equals("PARRIED!") ? Color.CYAN : Color.RED);
                lbl.setStyle("-fx-effect: dropshadow(two-pass-box, white, 2, 1.0, 0, 0);");
                lbl.setLayoutX(dt.x); lbl.setLayoutY(dt.y);
                lbl.setOpacity(Math.max(0, 1.0 - (dt.lifeFrames / 60.0)));
                drawnDamageTexts.add(lbl); battleArena.getChildren().add(lbl);
            }
        }
    }

    public void setDialogText(String text) { this.dialogText.setText(text); }
    public Scene getScene() { return scene; }
    public Node getPlayerNode() { return playerImageView; }

    public Button getBtnPunch() { return btnPunch; }
    public Button getBtnKick() { return btnKick; }
    public Button getBtnUppercut() { return btnUppercut; }
    public Button getBtnItem() { return btnItem; }

    public Button getBtnUpSpd() { return btnUpSpd; }
    public Button getBtnUpJmp() { return btnUpJmp; }
    public Button getBtnUpStr() { return btnUpStr; }
    public Button getBtnUpHp()  { return btnUpHp; }
    public Button getBtnUpPry() { return btnUpPry; }

    public Button getBtnDnSpd() { return btnDnSpd; }
    public Button getBtnDnJmp() { return btnDnJmp; }
    public Button getBtnDnStr() { return btnDnStr; }
    public Button getBtnDnHp()  { return btnDnHp; }
    public Button getBtnDnPry() { return btnDnPry; }

    public Button getBtnCloseMenu() { return btnCloseMenu; }
}