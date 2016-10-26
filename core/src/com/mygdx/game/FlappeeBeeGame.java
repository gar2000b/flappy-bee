package com.mygdx.game;

import com.badlogic.gdx.Game;

public class FlappeeBeeGame extends Game {


    @Override
    public void create() {
        setScreen(new StartScreen(this));
    }
}
