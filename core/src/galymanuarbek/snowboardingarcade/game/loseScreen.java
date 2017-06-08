package galymanuarbek.snowboardingarcade.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Created by User on 11.03.2017.
 */

public class loseScreen implements Screen {
    final myGame game;
    OrthographicCamera camera;
    Texture loseImage;
    long newGameTime;
    static Preferences prefs = Gdx.app.getPreferences("My Preferences");

    public loseScreen(final myGame gam) {
        game = gam;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        loseImage = new Texture("lost.png");

        newGameTime = TimeUtils.nanoTime();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.font.draw(game.batch, "You lose!", 300, 200);
        game.font.draw(game.batch, "Tap anywhere to begin!", 300, 150);
        game.font.draw(game.batch, "Score:" + GameScreen.flagsCollected, 300, 100);
        GameScreen.loseSound.play();

        if ( GameScreen.flagsCollected > prefs.getInteger("score", 0)){
            prefs.putInteger("score", GameScreen.flagsCollected);
            prefs.flush();
        }
        game.font.draw(game.batch, "High Score:" + prefs.getInteger("score", 0), 300, 50);

        game.batch.draw(loseImage, 300, 150);
        game.batch.end();

        if (Gdx.input.isTouched()&&TimeUtils.nanoTime() - newGameTime > 1000000000){
            GameScreen.flagsCollected = 0;
            game.setScreen(new GameScreen(game));
            dispose();
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

    }
}

