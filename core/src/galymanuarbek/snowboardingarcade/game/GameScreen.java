package galymanuarbek.snowboardingarcade.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {

    final myGame game;
    OrthographicCamera camera;
    Texture flagImage;
    Texture snowboardImage;
    Texture treeImage;
    Texture bigfootImage;
    static Sound loseSound;
    Music mainMusic;
    Rectangle snowboard;
    Vector3 touchPos;
    Array<Rectangle> flags;
    Array<Rectangle> trees;
    Array<Rectangle> bigfoots;
    long lastFlagTime;
    long lastTreeTime;
    long lastBigfootTime;
    static int flagsCollected;
    String dropString;


    public GameScreen (final myGame gam) {
        this.game = gam;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        touchPos = new Vector3();

        flagImage = new Texture("Flag.png");
        snowboardImage = new Texture("snowboard.png");
        treeImage = new Texture("tree.png");
        bigfootImage = new Texture("Gorilla.png");

        loseSound = Gdx.audio.newSound(Gdx.files.internal("losesound.wav"));
        mainMusic = Gdx.audio.newMusic(Gdx.files.internal("mainmusic.wav"));

        mainMusic.setLooping(true);
        mainMusic.play();

        snowboard = new Rectangle();
        snowboard.x = 800 / 2 - 24 / 2;
        snowboard.y = 346;
        snowboard.width = 24;
        snowboard.height = 64;

        trees = new Array<Rectangle>();
        spawnTree();

        flags = new Array<Rectangle>();
        spawnFlag();

        bigfoots = new Array<Rectangle>();
        spawnBigfoot();

    }

    private void spawnBigfoot(){
        Rectangle bigfoot = new Rectangle();
        bigfoot.x = MathUtils.random(0, 800-50);
        bigfoot.y = 0;
        bigfoot.width = 50;
        bigfoot.height = 50;
        bigfoots.add(bigfoot);
        lastBigfootTime = TimeUtils.millis();
    }

    private void spawnTree(){
        Rectangle tree = new Rectangle();
        tree.x = MathUtils.random(0, 800-38);
        tree.y = 0;
        tree.width = 28;
        tree.height = 56;
        trees.add(tree);
        lastTreeTime = TimeUtils.nanoTime();
    }


    private void spawnFlag(){
        Rectangle flag = new Rectangle();
        flag.x = MathUtils.random(50, 750-16);
        flag.y = 0;
        flag.width = 32;
        flag.height = 32;
        flags.add(flag);
        lastFlagTime = TimeUtils.nanoTime();
    }

    @Override
    public void render (float delta) {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.font.draw(game.batch, "Your score: " + flagsCollected, 10, 410);
        game.font.draw(game.batch, "High score: " + loseScreen.prefs.getInteger("score", 0), 10, 390);
        game.batch.draw(snowboardImage, snowboard.x, snowboard.y);
        for (Rectangle flagg: flags){
            game.batch.draw(flagImage, flagg.x, flagg.y);
        }
        for (Rectangle treee: trees){
            game.batch.draw(treeImage, treee.x-5, treee.y);
        }
        for (Rectangle bigfoott: bigfoots){
            game.batch.draw(bigfootImage, bigfoott.x, bigfoott.y);
        }
        game.batch.end();

        if(Gdx.input.isTouched()){
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            if(touchPos.x<=400) snowboard.x -= 450 * Gdx.graphics.getDeltaTime();
            if (touchPos.x>=400) snowboard.x += 450 * Gdx.graphics.getDeltaTime();
        }

        if ( GameScreen.flagsCollected > loseScreen.prefs.getInteger("score", 0)){
            loseScreen.prefs.putInteger("score", GameScreen.flagsCollected);
            loseScreen.prefs.flush();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) snowboard.x -= 450 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) snowboard.x += 450 * Gdx.graphics.getDeltaTime();

        if (snowboard.x < 0) snowboard.x = 0;
        if (snowboard.x > 800 - 24) snowboard.x = 800 - 24;

        if (TimeUtils.nanoTime() - lastFlagTime > 1000000000) spawnFlag();

        if (TimeUtils.nanoTime() - lastTreeTime > 1000000000) {
            spawnTree();
            spawnTree();
            spawnTree();
        }

        if (TimeUtils.millis() - lastBigfootTime > (15000)) spawnBigfoot();

        Iterator<Rectangle> iterFlags = flags.iterator();
        while (iterFlags.hasNext()){
            Rectangle flagg = iterFlags.next();
            flagg.y += 200 * Gdx.graphics.getDeltaTime();
            if (flagg.y - 32 > 480) iterFlags.remove();
            if (flagg.overlaps(snowboard)){
                flagsCollected++;
                // loseSound.play();
                iterFlags.remove();
            }
        }

        Iterator<Rectangle> iterTrees = trees.iterator();
        while (iterTrees.hasNext()){
            Rectangle treee = iterTrees.next();
            treee.y += 200 * Gdx.graphics.getDeltaTime();
            if (treee.y + 56 < 0) iterTrees.remove();
            if (treee.overlaps(snowboard)){
                game.setScreen(new loseScreen(game));
                dispose();
                mainMusic.stop();
            }
        }

        Iterator<Rectangle> iterBigfoots = bigfoots.iterator();
        while (iterBigfoots.hasNext()){
            Rectangle bigfoott = iterBigfoots.next();
            bigfoott.y += 200 * Gdx.graphics.getDeltaTime();
            if (bigfoott.y + 56 < 0) iterBigfoots.remove();
            if (Math.sqrt(Math.pow((bigfoott.x+25)-(snowboard.x+12),2)+Math.pow((bigfoott.y+25)-(snowboard.y+32),2))<250) {
                //game.setScreen(new MainMenuScreen(game));
                //dispose();
                bigfoott.x += (snowboard.x-bigfoott.x)*2.1*Gdx.graphics.getDeltaTime();
                bigfoott.y += (snowboard.y-bigfoott.y)*2.1*Gdx.graphics.getDeltaTime();
            }
            if (bigfoott.overlaps(snowboard)){
                mainMusic.stop();
                game.setScreen(new loseScreen(game));
                dispose();
                //loseSound.play();
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        flagImage.dispose();
        snowboardImage.dispose();
        loseSound.dispose();
        mainMusic.dispose();
    }

    @Override
    public void show() {
        mainMusic.play();
    }
}

