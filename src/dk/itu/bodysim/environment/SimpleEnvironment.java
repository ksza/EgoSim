package dk.itu.bodysim.environment;

import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * A really simple scene resembling the structure of a room. It comprises of
 * floor, walls and several items placed arround the room.
 *
 * @author kszanto
 */
public class SimpleEnvironment extends Node {

    private static final String SCENE_NAME = "SIMPLE_ENVIRONMENT";
    private final AssetManager assetManager;

    public SimpleEnvironment(final AssetManager assetManager) {

        super(SCENE_NAME);

        this.assetManager = assetManager;
        
        this.attachChild(makeCube("a Dragon", -2f, 0f, 1f));
        this.attachChild(makeCube("a tin can", 1f, -2f, 0f));
        this.attachChild(makeCube("the Sheriff", 0f, 1f, -2f));
        this.attachChild(makeCube("the Deputy", 1f, 0f, -4f));
        this.attachChild(makeFloor());
        this.attachChild(makeCharacter());        
    }

    private Geometry makeCube(String name, float x, float y, float z) {
        Box box = new Box(1, 1, 1);
        Geometry cube = new Geometry(name, box);
        cube.setLocalTranslation(x, y, z);
        Material mat1 = new Material(this.assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.randomColor());
        cube.setMaterial(mat1);
        return cube;
    }

    private Geometry makeFloor() {
        Box box = new Box(100, .2f, 100);
        Geometry floor = new Geometry("the Floor", box);
        floor.setLocalTranslation(0, -4, -5);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.Gray);
        floor.setMaterial(mat1);
        return floor;
    }

    private Spatial makeCharacter() {
        // load a character from jme3test-test-data
        Spatial golem = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        golem.scale(0.5f);
        golem.setLocalTranslation(-1.0f, -1.5f, -0.6f);

        // We must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        golem.addLight(sun);
        return golem;
    }
}