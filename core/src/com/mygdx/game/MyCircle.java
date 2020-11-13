package com.mygdx.game;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class MyCircle {
    private float radius;
    private Vector2 position;

    public MyCircle(float xPos, float yPos, float radius){
        position = new Vector2(xPos, yPos);
        this.radius = radius;
    }

    public void translate(float xAmount, float yAmount){
        position.x += xAmount;
        position.y += yAmount;
    }

    public void setPos(float xAmount, float yAmount){
        position.x = xAmount;
        position.y = yAmount;
    }

    public void changeSizeBy(float changeAmount){
        radius += changeAmount;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public void render(ShapeRenderer render){
        render.circle(position.x, position.y, radius);
    }
}
