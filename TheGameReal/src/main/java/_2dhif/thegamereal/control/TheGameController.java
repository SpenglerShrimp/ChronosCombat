package _2dhif.thegamereal.control;

import _2dhif.thegamereal.model.TheGameModel;
import _2dhif.thegamereal.view.TheGameView;
import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TheGameController {
    private TheGameModel model;
    private TheGameView view;

    private boolean jumpKey, left, right, up, down, interactKey;
    private boolean parryInput = false, prevParryInput = false;
    private Random random = new Random();

    private List<Rectangle2D> classroomWalls = new ArrayList<>();

    public TheGameController(TheGameModel model, TheGameView view) {
        this.model = model;
        this.view = view;
        setupInputs();
        setupButtons();

        initializeCollisionMap();

        view.playMenuMusic();

        startGameLoop();
    }

    private void initializeCollisionMap() {
        classroomWalls.add(new Rectangle2D(0, 0, 800, 130));

        classroomWalls.add(new Rectangle2D(50, 250, 200, 70));
        classroomWalls.add(new Rectangle2D(300, 250, 200, 70));
        classroomWalls.add(new Rectangle2D(550, 250, 200, 70));

        classroomWalls.add(new Rectangle2D(50, 380, 200, 70));
        classroomWalls.add(new Rectangle2D(300, 380, 200, 70));
        classroomWalls.add(new Rectangle2D(550, 380, 200, 70));

        classroomWalls.add(new Rectangle2D(50, 480, 200, 50));
        classroomWalls.add(new Rectangle2D(300, 480, 200, 50));
        classroomWalls.add(new Rectangle2D(550, 480, 200, 50));

        classroomWalls.add(new Rectangle2D(0, 560, 800, 40));
        classroomWalls.add(new Rectangle2D(780, 0, 20, 600));
    }

    private void setupInputs() {
        view.getScene().setOnKeyPressed(e -> {
            if (model.getCurrentState() == TheGameModel.GameState.START_SCREEN) {
                if (e.getCode() == KeyCode.ENTER) {
                    view.stopMenuMusic();
                    model.resetFrames();
                    model.setCurrentState(TheGameModel.GameState.OVERWORLD);

                    if (!model.isDebugModeActive()) {
                        model.setCurrentRoom(TheGameModel.Room.CLASSROOM_1);
                        model.setOverworldX(100);
                        model.setOverworldY(200);
                    }
                }
                return;
            }

            if (model.getCurrentState() == TheGameModel.GameState.BATTLE_PLAYER_TURN) {
                if (e.getCode() == KeyCode.DIGIT1 || e.getCode() == KeyCode.NUMPAD1) view.getBtnPunch().fire();
                if (e.getCode() == KeyCode.DIGIT2 || e.getCode() == KeyCode.NUMPAD2) view.getBtnKick().fire();
                if (e.getCode() == KeyCode.DIGIT3 || e.getCode() == KeyCode.NUMPAD3) view.getBtnUppercut().fire();
                if (e.getCode() == KeyCode.DIGIT4 || e.getCode() == KeyCode.NUMPAD4) view.getBtnItem().fire();
            }

            if (e.getCode() == KeyCode.SPACE) jumpKey = true;
            if (e.getCode() == KeyCode.W) up = true;
            if (e.getCode() == KeyCode.S) down = true;
            if (e.getCode() == KeyCode.A) left = true;
            if (e.getCode() == KeyCode.D) right = true;
            if (e.getCode() == KeyCode.F) parryInput = true;
            if (e.getCode() == KeyCode.E) interactKey = true;

            if (e.getCode() == KeyCode.U) {
                if (model.getCurrentState() == TheGameModel.GameState.OVERWORLD && model.getCurrentRoom() == TheGameModel.Room.HALLWAY) {
                    model.setUpgradeMenuOpen(!model.isUpgradeMenuOpen());
                }
            }
        });

        view.getScene().setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.SPACE) jumpKey = false;
            if (e.getCode() == KeyCode.W) up = false;
            if (e.getCode() == KeyCode.S) down = false;
            if (e.getCode() == KeyCode.A) left = false;
            if (e.getCode() == KeyCode.D) right = false;
            if (e.getCode() == KeyCode.F) parryInput = false;
            if (e.getCode() == KeyCode.E) interactKey = false;
        });

        view.getScene().setOnMousePressed(e -> { if (e.getButton() == MouseButton.SECONDARY) parryInput = true; });
        view.getScene().setOnMouseReleased(e -> { if (e.getButton() == MouseButton.SECONDARY) parryInput = false; });
    }

    private void setupButtons() {
        view.getBtnPunch().setOnAction(e -> executePlayerMelee("Punch", 10, 110));
        view.getBtnKick().setOnAction(e -> executePlayerMelee("Kick", 15, 140));
        view.getBtnUppercut().setOnAction(e -> executePlayerMelee("Uppercut", 20, 90));
        view.getBtnItem().setOnAction(e -> executePlayerThrow("Throw Item", 10));

        view.getBtnUpSpd().setOnAction(e -> model.upgradeSpeed());
        view.getBtnUpJmp().setOnAction(e -> model.upgradeJump());
        view.getBtnUpStr().setOnAction(e -> model.upgradeStrength());
        view.getBtnUpHp().setOnAction(e -> model.upgradeMaxHp());
        view.getBtnUpPry().setOnAction(e -> model.upgradeParryDmg());

        view.getBtnDnSpd().setOnAction(e -> model.downgradeSpeed());
        view.getBtnDnJmp().setOnAction(e -> model.downgradeJump());
        view.getBtnDnStr().setOnAction(e -> model.downgradeStrength());
        view.getBtnDnHp().setOnAction(e -> model.downgradeMaxHp());
        view.getBtnDnPry().setOnAction(e -> model.downgradeParryDmg());

        view.getBtnCloseMenu().setOnAction(e -> model.setUpgradeMenuOpen(false));
    }

    private void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 15_000_000) {
                    lastUpdate = now;
                    model.addFrame();

                    switch (model.getCurrentState()) {
                        case START_SCREEN:
                        case END_SCREEN:
                            break;

                        case BATTLE_INTRO:
                            if (model.getFramesElapsed() >= 120) {
                                startEnemyTurn();
                            }
                            break;

                        case BATTLE_ENEMY_TURN:
                            handlePlayerMovement();
                            handleEnemyMovement();
                            handleParryInput();
                            handleEnemyAttack();
                            updateDamageTexts();

                            if (model.getPlayerHP() <= 0 && !model.isPlayerDead()) {
                                triggerDeathAnimation(true);
                            } else if (model.getFramesElapsed() >= 360 && !model.isPlayerDead()) {
                                startPlayerTurn();
                            }
                            break;

                        case BATTLE_PLAYER_TURN:
                            handlePlayerMovement();
                            updateDamageTexts();

                            model.setTurnTimerFrames(model.getTurnTimerFrames() - 1);
                            if (model.getTurnTimerFrames() <= 0) {
                                model.getDamageTexts().add(new TheGameModel.DamageText(model.getPlayerX() + 20, model.getPlayerY() - 20, "TIME UP!"));
                                view.setDialogText("You took too long to decide!");
                                model.resetFrames();
                                model.setCurrentState(TheGameModel.GameState.BATTLE_ACTION_DELAY);
                            }
                            break;

                        case BATTLE_ACTION_DELAY:
                            handlePlayerMovement();
                            updateDamageTexts();
                            handleThrownItems();

                            if (model.getEnemyHP() <= 0 && !model.isEnemyDead()) {
                                triggerDeathAnimation(false);
                            } else if (model.getThrownItems().isEmpty()) {
                                if (model.getFramesElapsed() >= 45) {
                                    model.setActiveAttackAnim("");
                                    startEnemyTurn();
                                }
                            }
                            break;

                        case BATTLE_DEATH_ANIMATION:
                            if (model.getFramesElapsed() >= 90) {
                                if (model.isPlayerDead()) {
                                    handlePlayerDeath();
                                } else if (model.isEnemyDead()) {
                                    handleBossVictory();
                                }
                            }
                            break;

                        case OVERWORLD:
                            handleOverworldMovement();
                            handleHallwayLogic();
                            break;
                    }

                    prevParryInput = parryInput;
                    view.render();
                }
            }
        };
        timer.start();
    }

    private void triggerDeathAnimation(boolean playerDied) {
        if (playerDied) {
            model.setPlayerDead(true);
            view.setDialogText("You failed! You're going to have to retake this class...");
        } else {
            model.setEnemyDead(true);

            String bossName = (model.getCurrentBattleId() == 1) ? "Gerin" : "Paul";
            int gainedSP = (model.getCurrentBattleId() == 1) ? 3 : 6;

            view.setDialogText("Student " + bossName + " was defeated! You earned " + gainedSP + " Skill Points!");
        }

        model.setActiveAttackAnim("");
        model.resetFrames();
        model.setCurrentState(TheGameModel.GameState.BATTLE_DEATH_ANIMATION);
    }

    private void handlePlayerDeath() {
        model.setPlayerHP(model.getActualMaxHP());
        model.setPlayerDead(false);
        model.setEnemyDead(false);
        model.setActiveAttackAnim("");

        if (model.getCurrentBattleId() == 1) {
            model.setCurrentState(TheGameModel.GameState.OVERWORLD);
            model.setCurrentRoom(TheGameModel.Room.CLASSROOM_1);
            model.setOverworldX(50);
            model.setOverworldY(200);
        } else {
            model.setCurrentState(TheGameModel.GameState.OVERWORLD);
            model.setCurrentRoom(TheGameModel.Room.CLASSROOM_2);
            model.setOverworldX(50);
            model.setOverworldY(200);
        }
    }

    private void handleBossVictory() {
        model.setPlayerDead(false);
        model.setEnemyDead(false);
        model.setActiveAttackAnim("");

        int gainedSP = (model.getCurrentBattleId() == 1) ? 3 : 6;
        model.addSkillPoints(gainedSP);

        model.setCurrentState(TheGameModel.GameState.OVERWORLD);

        if (model.getCurrentBattleId() == 1) {
            model.setBoss1Defeated(true);
            model.setCurrentRoom(TheGameModel.Room.CLASSROOM_1);
            model.setOverworldX(360); model.setOverworldY(260);
        } else {
            model.setBoss2Defeated(true);
            model.setCurrentRoom(TheGameModel.Room.CLASSROOM_2);
            model.setOverworldX(400); model.setOverworldY(300);
        }
        up = false; down = false; left = false; right = false;
    }

    private void startBattle(int battleId) {
        model.setCurrentBattleId(battleId);

        if (battleId == 1) {
            model.setEnemyHP(50);
        } else {
            model.setEnemyHP(250);
        }

        model.setPlayerX(100); model.setPlayerY(260);
        model.setEnemyX(600); model.setEnemyY(50);
        model.getBullets().clear(); model.getDamageTexts().clear(); model.getThrownItems().clear();
        model.setActiveAttackAnim("");

        model.setCurrentState(TheGameModel.GameState.BATTLE_INTRO);
        model.resetFrames();
    }

    private void startEnemyTurn() {
        model.setCurrentState(TheGameModel.GameState.BATTLE_ENEMY_TURN);

        String bossName = (model.getCurrentBattleId() == 1) ? "Gerin" : "Paul";
        view.setDialogText("Student " + bossName + " attacks! Press M2 or F to PARRY!");

        model.resetFrames();
        left = false; right = false; jumpKey = false; parryInput = false;
        model.setParryActiveFrames(0);
        model.setActiveAttackAnim("");
    }

    private void startPlayerTurn() {
        model.setCurrentState(TheGameModel.GameState.BATTLE_PLAYER_TURN);
        view.setDialogText("Choose your attack quickly! (Use keys 1-4)");

        int waitTime = (model.getCurrentBattleId() == 1) ? 600 : 420;
        model.setTurnTimerFrames(waitTime);

        model.getBullets().clear();
        model.setActiveAttackAnim("");
    }

    private void executePlayerMelee(String attackName, int baseDamage, double range) {
        model.setActiveAttackAnim(attackName);

        double pCX = model.getPlayerX() + 40, pCY = model.getPlayerY() + 40;
        double eCX = model.getEnemyX() + 60, eCY = model.getEnemyY() + 60;
        double dist = Math.sqrt(Math.pow(pCX - eCX, 2) + Math.pow(pCY - eCY, 2));

        if (dist <= range) {
            int actualDamage = (int)(baseDamage * model.getActualStrengthMult());
            model.damageEnemy(actualDamage);
            model.getDamageTexts().add(new TheGameModel.DamageText(model.getEnemyX() + 40, model.getEnemyY() - 20, "-" + actualDamage));
            view.setDialogText("Direct hit! You used " + attackName + "!");
        } else {
            model.getDamageTexts().add(new TheGameModel.DamageText(model.getEnemyX() + 40, model.getEnemyY() - 20, "MISS!"));
            view.setDialogText("You swung... but missed!");
        }

        model.resetFrames();
        model.setCurrentState(TheGameModel.GameState.BATTLE_ACTION_DELAY);
    }

    private void executePlayerThrow(String attackName, int baseDamage) {
        model.setActiveAttackAnim("Throw");
        view.setDialogText("You hurled a random item!");

        double startX = model.getPlayerX() + 40, startY = model.getPlayerY() + 40;
        double targetX = model.getEnemyX() + 60, targetY = model.getEnemyY() + 60;
        double diffX = targetX - startX, diffY = targetY - startY;
        double length = Math.sqrt(diffX * diffX + diffY * diffY);

        double speed = 12;
        double speedX = (diffX / length) * speed, speedY = (diffY / length) * speed;

        int randomItemIndex = random.nextInt(7);
        model.getThrownItems().add(new TheGameModel.ThrownItem(startX, startY, speedX, speedY, randomItemIndex));

        model.resetFrames();
        model.setCurrentState(TheGameModel.GameState.BATTLE_ACTION_DELAY);
    }

    private void handleOverworldMovement() {
        if (model.isUpgradeMenuOpen()) return;

        double speed = model.getActualSpeed();
        double px = model.getOverworldX();
        double py = model.getOverworldY();
        double nextX = px;
        double nextY = py;

        if (up) { nextY -= speed; model.setOverworldFacing(TheGameModel.Direction.UP); }
        if (down) { nextY += speed; model.setOverworldFacing(TheGameModel.Direction.DOWN); }
        if (left) { nextX -= speed; model.setOverworldFacing(TheGameModel.Direction.LEFT); }
        if (right) { nextX += speed; model.setOverworldFacing(TheGameModel.Direction.RIGHT); }

        boolean inClassroom = model.getCurrentRoom() == TheGameModel.Room.CLASSROOM_1 ||
                model.getCurrentRoom() == TheGameModel.Room.CLASSROOM_2;

        if (inClassroom) {
            double feetY = nextY + 75;
            double centerX = nextX + 40;

            if (feetY < 120) nextY = 120 - 75;
            if (feetY > 350) nextY = 350 - 75;
            if (centerX < 80) nextX = 80 - 40;

            if (centerX > 630) {
                if (feetY >= 200 && feetY <= 480) {
                } else {
                    nextX = 630 - 40;
                }
            }

        } else if (model.getCurrentRoom() == TheGameModel.Room.HALLWAY) {
            double feetY = nextY + 80;
            double yMin = 130 + (nextX / 800.0) * 120;
            double yMaxFeet = 550 - (nextX / 800.0) * 200;

            if (nextY < yMin) nextY = yMin;
            if (feetY > yMaxFeet) nextY = yMaxFeet - 80;

            if (nextX >= 750) {
                feetY = nextY + 80;
                if (feetY >= 300 && feetY <= 400) {
                    model.setCurrentRoom(TheGameModel.Room.CLASSROOM_2);
                    nextX = 50;
                } else {
                    nextX = 749;
                }
            }
        }

        if (nextX < 0) nextX = 0;
        if (nextX > 800 && model.getCurrentRoom() != TheGameModel.Room.HALLWAY) nextX = 800;
        if (nextY < 0) nextY = 0;
        if (nextY > 600 - 80) nextY = 600 - 80;

        px = nextX; py = nextY;

        if (px >= 750 && model.getCurrentRoom() == TheGameModel.Room.CLASSROOM_1) {
            model.setCurrentRoom(TheGameModel.Room.HALLWAY);
            px = 50;

            if (model.getSkillPoints() > 0) {
                model.setUpgradeMenuOpen(true);
            }
        }

        if (px >= 750 && model.getCurrentRoom() == TheGameModel.Room.CLASSROOM_2 && model.isBoss2Defeated()) {
            model.setCurrentState(TheGameModel.GameState.END_SCREEN);
        }

        model.setOverworldX(px);
        model.setOverworldY(py);

        if (model.getCurrentState() == TheGameModel.GameState.OVERWORLD && model.getCurrentRoom() == TheGameModel.Room.CLASSROOM_1 && !model.isBoss1Defeated()) {
            double ex = 360;
            if (Math.abs(px - ex) < 15) {
                startBattle(1);
                up = false; down = false; left = false; right = false;
            }
        }

        if (model.getCurrentState() == TheGameModel.GameState.OVERWORLD && model.getCurrentRoom() == TheGameModel.Room.CLASSROOM_2 && !model.isBoss2Defeated()) {
            double ex = 360;
            if (Math.abs(px - ex) < 15) {
                startBattle(2);
                up = false; down = false; left = false; right = false;
            }
        }
    }

    private void handleHallwayLogic() {
        if (model.getHealGlowFrames() > 0) model.setHealGlowFrames(model.getHealGlowFrames() - 1);

        if (model.getCurrentRoom() == TheGameModel.Room.HALLWAY) {
            double distToFountain = Math.sqrt(Math.pow(model.getOverworldX() - 325, 2) + Math.pow(model.getOverworldY() - 200, 2));
            model.setCanHeal(distToFountain < 150);

            if (model.canHeal() && interactKey) {
                model.setPlayerHP(model.getActualMaxHP());
                model.setHealGlowFrames(60);
                interactKey = false;
            }
        } else {
            model.setCanHeal(false);
        }
    }

    private void handleThrownItems() {
        Iterator<TheGameModel.ThrownItem> iter = model.getThrownItems().iterator();
        while (iter.hasNext()) {
            TheGameModel.ThrownItem item = iter.next();
            item.x += item.speedX; item.y += item.speedY;

            if (item.x > model.getEnemyX() && item.x < model.getEnemyX() + 120 &&
                    item.y > model.getEnemyY() && item.y < model.getEnemyY() + 120) {

                int damage = (int)(10 * model.getActualStrengthMult());
                model.damageEnemy(damage);
                model.getDamageTexts().add(new TheGameModel.DamageText(model.getEnemyX() + 40, model.getEnemyY() - 20, "-" + damage));
                iter.remove();

                model.setActiveAttackAnim("");
            } else if (item.x < -100 || item.x > 900 || item.y < -100 || item.y > 600) {
                iter.remove();
                model.setActiveAttackAnim("");
            }
        }
    }

    private void handleParryInput() {
        if (model.getParrySuccessFrames() > 0) model.setParrySuccessFrames(model.getParrySuccessFrames() - 1);
        if (model.getParryCooldownFrames() > 0) model.setParryCooldownFrames(model.getParryCooldownFrames() - 1);

        if (model.getParryActiveFrames() > 0) {
            model.setParryActiveFrames(model.getParryActiveFrames() - 1);
            if (model.getParryActiveFrames() == 0) model.setParryCooldownFrames(120);
        }

        if (parryInput && !prevParryInput) {
            if (model.getParryCooldownFrames() == 0 && model.getParryActiveFrames() == 0) {
                model.setParryActiveFrames(15);
            }
        }
    }

    private void updateDamageTexts() {
        Iterator<TheGameModel.DamageText> iter = model.getDamageTexts().iterator();
        while (iter.hasNext()) {
            TheGameModel.DamageText dt = iter.next();
            dt.y -= 1; dt.lifeFrames++;
            if (dt.lifeFrames > 60) iter.remove();
        }
    }

    private void handlePlayerMovement() {
        double px = model.getPlayerX(), py = model.getPlayerY(), vy = model.getPlayerVelocityY();
        double speed = model.getActualSpeed();

        if (left && px > 0) px -= speed;
        if (right && px < 800 - 80) px += speed;

        if (jumpKey && model.isGrounded()) {
            vy = model.getActualJump();
            model.setGrounded(false);
        }

        vy += 0.6;
        double nextY = py + vy;
        boolean isGroundedNow = false;

        if (vy > 0) {
            for (TheGameModel.Platform p : model.getPlatforms()) {
                if (px + 60 > p.x && px < p.x + p.width) {
                    if (py + 80 <= p.y && nextY + 80 >= p.y) {
                        nextY = p.y - 80; vy = 0; isGroundedNow = true;
                    }
                }
            }
            if (nextY >= 460 - 80) { nextY = 460 - 80; vy = 0; isGroundedNow = true; }
        }
        model.setPlayerX(px); model.setPlayerY(nextY); model.setPlayerVelocityY(vy); model.setGrounded(isGroundedNow);
    }

    private void handleEnemyMovement() {
        if (model.getFramesElapsed() % 60 == 0) {
            model.setEnemyTargetX(400 + Math.random() * 250);
            model.setEnemyTargetY(50 + Math.random() * 250);
        }
        double dx = model.getEnemyTargetX() - model.getEnemyX(), dy = model.getEnemyTargetY() - model.getEnemyY();
        model.setEnemyX(model.getEnemyX() + dx * 0.05); model.setEnemyY(model.getEnemyY() + dy * 0.05);
    }

    private void handleEnemyAttack() {
        if (model.getCurrentBattleId() == 1) {
            if (model.getFramesElapsed() % 90 == 0) {
                double startX = model.getEnemyX() + 60, startY = model.getEnemyY() + 60;
                double diffX = (model.getPlayerX() + 40) - startX, diffY = (model.getPlayerY() + 40) - startY;
                double length = Math.sqrt(diffX * diffX + diffY * diffY);
                double bulletSpeed = 6;
                model.getBullets().add(new TheGameModel.Bullet(startX, startY, (diffX / length) * bulletSpeed, (diffY / length) * bulletSpeed, 0, 0));
            }
        } else {
            if (model.getFramesElapsed() % 80 == 0) {
                double startX = model.getEnemyX() + 60, startY = model.getEnemyY() + 60;
                int numBooks = 10;
                for (int i = 0; i < numBooks; i++) {
                    double angle = (2 * Math.PI / numBooks) * i;
                    double r = 60;
                    double spawnX = startX + Math.cos(angle) * r;
                    double spawnY = startY + Math.sin(angle) * r;
                    model.getBullets().add(new TheGameModel.Bullet(spawnX, spawnY, 0, 0, 1, i));
                }
            }
        }

        Iterator<TheGameModel.Bullet> iter = model.getBullets().iterator();
        while (iter.hasNext()) {
            TheGameModel.Bullet b = iter.next();

            b.frames++;

            if (b.type == 1 && b.frames == 20) {
                double playerCenterX = model.getPlayerX() + 40;
                double playerCenterY = model.getPlayerY() + 40;
                double bossCenterX = model.getEnemyX() + 60;
                double bossCenterY = model.getEnemyY() + 60;

                double baseAngleToPlayer = Math.atan2(playerCenterY - bossCenterY, playerCenterX - bossCenterX);
                double totalSpreadArc = Math.PI / 2;
                double angleOffset = baseAngleToPlayer - (totalSpreadArc / 2) + (totalSpreadArc / 9) * b.index;

                double speed = 12;
                b.speedX = Math.cos(angleOffset) * speed;
                b.speedY = Math.sin(angleOffset) * speed;
            }

            b.x += b.speedX;
            b.y += b.speedY;

            if (view.getPlayerNode().getBoundsInParent().contains(b.x, b.y)) {
                if (model.getParryActiveFrames() > 0) {
                    model.getDamageTexts().add(new TheGameModel.DamageText(model.getPlayerX() + 20, model.getPlayerY() - 20, "PARRIED!"));
                    model.setParryActiveFrames(0);
                    model.setParrySuccessFrames(20);

                    int parryDmg = model.getActualParryDmg();
                    if (parryDmg > 0) {
                        model.damageEnemy(parryDmg);
                        model.getDamageTexts().add(new TheGameModel.DamageText(model.getEnemyX() + 40, model.getEnemyY() - 20, "-" + parryDmg));

                        if (model.getEnemyHP() <= 0) {
                            iter.remove();
                            triggerDeathAnimation(false);
                            return;
                        }
                    }
                } else {
                    int damageToPlayer = (model.getCurrentBattleId() == 1) ? 10 : 15;
                    model.damagePlayer(damageToPlayer);
                    model.getDamageTexts().add(new TheGameModel.DamageText(model.getPlayerX() + 20, model.getPlayerY() - 20, "-" + damageToPlayer));
                }

                iter.remove();
            } else if (b.x < 0 || b.x > 800 || b.y < 0 || b.y > 460) {
                iter.remove();
            }
        }
    }
}