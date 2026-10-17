package _2dhif.thegamereal.model;

import java.util.ArrayList;
import java.util.List;

public class TheGameModel {
    public enum GameState {
        START_SCREEN,
        BATTLE_INTRO,
        BATTLE_ENEMY_TURN, BATTLE_PLAYER_TURN, BATTLE_ACTION_DELAY, BATTLE_DEATH_ANIMATION,
        OVERWORLD,
        END_SCREEN
    }

    public enum Direction { UP, DOWN, LEFT, RIGHT }
    public enum Room { CLASSROOM_1, HALLWAY, CLASSROOM_2 }

    public enum DebugState {
        DEFAULT, AFTER_GERIN, BEFORE_PAUL, AFTER_PAUL
    }

    private GameState currentState = GameState.START_SCREEN;
    private int framesElapsed = 0;

    private double playerX = 100, playerY = 260, playerVelocityY = 0;
    private boolean grounded = false;
    private int playerHP = 100;

    private int enemyHP = 50;
    private double enemyX = 600, enemyY = 50, enemyTargetX = 600, enemyTargetY = 50;

    private int turnTimerFrames = 600;
    private int parryActiveFrames = 0;
    private int parryCooldownFrames = 0;
    private int parrySuccessFrames = 0;
    private String activeAttackAnim = "";

    private boolean playerDead = false;
    private boolean enemyDead = false;

    private int currentBattleId = 1;
    private boolean boss1Defeated = false;
    private boolean boss2Defeated = false;
    private Room currentRoom = Room.CLASSROOM_1;

    private double overworldX = 400, overworldY = 300;
    private Direction overworldFacing = Direction.DOWN;

    private int skillPoints = 0;
    private int statLevelSpeed = 1;
    private int statLevelJump = 1;
    private int statLevelStrength = 1;
    private int statLevelMaxHp = 1;
    private int statLevelParryDmg = 1;

    private boolean upgradeMenuOpen = false;
    private boolean canHeal = false;
    private int healGlowFrames = 0;

    private boolean debugModeActive = false;

    private List<Bullet> bullets = new ArrayList<>();
    private List<Platform> platforms = new ArrayList<>();
    private List<DamageText> damageTexts = new ArrayList<>();
    private List<ThrownItem> thrownItems = new ArrayList<>();

    public static class Bullet {
        public double x, y, speedX, speedY;
        public int type;
        public int frames = 0;
        public int index = 0;

        public Bullet(double x, double y, double speedX, double speedY, int type, int index) {
            this.x = x; this.y = y; this.speedX = speedX; this.speedY = speedY; this.type = type; this.index = index;
        }
    }

    public static class ThrownItem {
        public double x, y, speedX, speedY;
        public int imageIndex;
        public ThrownItem(double x, double y, double speedX, double speedY, int imageIndex) {
            this.x = x; this.y = y; this.speedX = speedX; this.speedY = speedY; this.imageIndex = imageIndex;
        }
    }

    public static class Platform {
        public double x, y, width, height;
        public Platform(double x, double y, double width, double height) { this.x = x; this.y = y; this.width = width; this.height = height; }
    }

    public static class DamageText {
        public double x, y; public String text; public int lifeFrames;
        public DamageText(double x, double y, String text) { this.x = x; this.y = y; this.text = text; this.lifeFrames = 0; }
    }

    public TheGameModel() {
        platforms.add(new Platform(50, 350, 150, 15));
        platforms.add(new Platform(250, 250, 100, 15));
        platforms.add(new Platform(50, 150, 100, 15));
        platforms.add(new Platform(550, 250, 100, 15));
    }

    public void loadDebugState(DebugState state, int customSkillPoints) {
        this.skillPoints = customSkillPoints;
        this.debugModeActive = true;

        switch (state) {
            case DEFAULT:
                this.currentRoom = Room.CLASSROOM_1;
                this.boss1Defeated = false;
                this.boss2Defeated = false;
                this.overworldX = 100;
                this.overworldY = 300;
                break;
            case AFTER_GERIN:
                this.currentRoom = Room.CLASSROOM_1;
                this.boss1Defeated = true;
                this.boss2Defeated = false;
                this.overworldX = 360;
                this.overworldY = 260;
                break;
            case BEFORE_PAUL:
                this.currentRoom = Room.CLASSROOM_2;
                this.boss1Defeated = true;
                this.boss2Defeated = false;
                this.overworldX = 100;
                this.overworldY = 300;
                break;
            case AFTER_PAUL:
                this.currentRoom = Room.CLASSROOM_2;
                this.boss1Defeated = true;
                this.boss2Defeated = true;
                this.overworldX = 400;
                this.overworldY = 300;
                break;
        }
    }

    public double getActualSpeed() { return 5.0 + (statLevelSpeed - 1) * 1.5; }
    public double getActualJump() { return -12.0 - (statLevelJump - 1) * 1.5; }
    public double getActualStrengthMult() { return 1.0 + (statLevelStrength - 1) * 0.5; }
    public int getActualMaxHP() { return 100 + (statLevelMaxHp - 1) * 25; }
    public int getActualParryDmg() { return (statLevelParryDmg - 1) * 10; }

    public GameState getCurrentState() { return currentState; }
    public void setCurrentState(GameState state) { this.currentState = state; }
    public int getFramesElapsed() { return framesElapsed; }
    public void addFrame() { this.framesElapsed++; }
    public void resetFrames() { this.framesElapsed = 0; }
    public double getPlayerX() { return playerX; }
    public double getPlayerY() { return playerY; }
    public void setPlayerX(double x) { this.playerX = x; }
    public void setPlayerY(double y) { this.playerY = y; }
    public double getPlayerVelocityY() { return playerVelocityY; }
    public void setPlayerVelocityY(double vy) { this.playerVelocityY = vy; }
    public boolean isGrounded() { return grounded; }
    public void setGrounded(boolean g) { this.grounded = g; }
    public int getPlayerHP() { return playerHP; }
    public void setPlayerHP(int hp) { this.playerHP = Math.min(hp, getActualMaxHP()); }
    public void damagePlayer(int amount) { this.playerHP -= amount; }
    public int getEnemyHP() { return enemyHP; }
    public void setEnemyHP(int hp) { this.enemyHP = hp; }
    public void damageEnemy(int amount) { this.enemyHP -= amount; }
    public double getEnemyX() { return enemyX; }
    public void setEnemyX(double x) { this.enemyX = x; }
    public double getEnemyY() { return enemyY; }
    public void setEnemyY(double y) { this.enemyY = y; }
    public double getEnemyTargetX() { return enemyTargetX; }
    public void setEnemyTargetX(double tx) { this.enemyTargetX = tx; }
    public double getEnemyTargetY() { return enemyTargetY; }
    public void setEnemyTargetY(double ty) { this.enemyTargetY = ty; }
    public List<Bullet> getBullets() { return bullets; }
    public List<Platform> getPlatforms() { return platforms; }
    public List<DamageText> getDamageTexts() { return damageTexts; }
    public int getTurnTimerFrames() { return turnTimerFrames; }
    public void setTurnTimerFrames(int t) { this.turnTimerFrames = t; }
    public int getParryActiveFrames() { return parryActiveFrames; }
    public void setParryActiveFrames(int p) { this.parryActiveFrames = p; }
    public int getParryCooldownFrames() { return parryCooldownFrames; }
    public void setParryCooldownFrames(int p) { this.parryCooldownFrames = p; }
    public int getParrySuccessFrames() { return parrySuccessFrames; }
    public void setParrySuccessFrames(int p) { this.parrySuccessFrames = p; }
    public String getActiveAttackAnim() { return activeAttackAnim; }
    public void setActiveAttackAnim(String a) { this.activeAttackAnim = a; }
    public List<ThrownItem> getThrownItems() { return thrownItems; }
    public boolean isPlayerDead() { return playerDead; }
    public void setPlayerDead(boolean dead) { this.playerDead = dead; }
    public boolean isEnemyDead() { return enemyDead; }
    public void setEnemyDead(boolean dead) { this.enemyDead = dead; }

    public double getOverworldX() { return overworldX; }
    public void setOverworldX(double x) { this.overworldX = x; }
    public double getOverworldY() { return overworldY; }
    public void setOverworldY(double y) { this.overworldY = y; }
    public Direction getOverworldFacing() { return overworldFacing; }
    public void setOverworldFacing(Direction dir) { this.overworldFacing = dir; }
    public Room getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(Room r) { this.currentRoom = r; }
    public int getCurrentBattleId() { return currentBattleId; }
    public void setCurrentBattleId(int id) { this.currentBattleId = id; }

    public boolean isBoss1Defeated() { return boss1Defeated; }
    public void setBoss1Defeated(boolean d) { this.boss1Defeated = d; }
    public boolean isBoss2Defeated() { return boss2Defeated; }
    public void setBoss2Defeated(boolean d) { this.boss2Defeated = d; }
    public boolean isDebugModeActive() { return debugModeActive; }

    public int getSkillPoints() { return skillPoints; }
    public void addSkillPoints(int pts) { this.skillPoints += pts; }

    public boolean isUpgradeMenuOpen() { return upgradeMenuOpen; }
    public void setUpgradeMenuOpen(boolean open) { this.upgradeMenuOpen = open; }

    public int getStatLevelSpeed() { return statLevelSpeed; }
    public void upgradeSpeed() { statLevelSpeed++; skillPoints--; }
    public void downgradeSpeed() { if(statLevelSpeed > 1) { statLevelSpeed--; skillPoints++; } }

    public int getStatLevelJump() { return statLevelJump; }
    public void upgradeJump() { statLevelJump++; skillPoints--; }
    public void downgradeJump() { if(statLevelJump > 1) { statLevelJump--; skillPoints++; } }

    public int getStatLevelStrength() { return statLevelStrength; }
    public void upgradeStrength() { statLevelStrength++; skillPoints--; }
    public void downgradeStrength() { if(statLevelStrength > 1) { statLevelStrength--; skillPoints++; } }

    public int getStatLevelMaxHp() { return statLevelMaxHp; }
    public void upgradeMaxHp() { statLevelMaxHp++; skillPoints--; setPlayerHP(getActualMaxHP()); }
    public void downgradeMaxHp() { if(statLevelMaxHp > 1) { statLevelMaxHp--; skillPoints++; setPlayerHP(Math.min(playerHP, getActualMaxHP())); } }

    public int getStatLevelParryDmg() { return statLevelParryDmg; }
    public void upgradeParryDmg() { statLevelParryDmg++; skillPoints--; }
    public void downgradeParryDmg() { if(statLevelParryDmg > 1) { statLevelParryDmg--; skillPoints++; } }

    public boolean canHeal() { return canHeal; }
    public void setCanHeal(boolean h) { this.canHeal = h; }
    public int getHealGlowFrames() { return healGlowFrames; }
    public void setHealGlowFrames(int f) { this.healGlowFrames = f; }
}