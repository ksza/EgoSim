package dk.itu.bodysim.context;

import dk.itu.bodysim.context.server.ContextServerApplication;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 *
 * @author kszanto
 */
public class EgocentricContextManager extends AbstractAppState {

    private SimpleApplication app;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    private BulletAppState physics;
    private Component serverComponent;
    private Camera cam;

    private static EgocentricContextManager instance;
    public static EgocentricContextManager getInstance() {
        return instance;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        instance = this;
        
        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort = this.app.getViewPort();
        this.physics = this.stateManager.getState(BulletAppState.class);


        /* start up the rest server */
        serverComponent = new Component();

        // Add a new HTTP server listening on port 8182.
        serverComponent.getServers().add(Protocol.HTTP, 8182);

        // Attach the sample application.
        serverComponent.getDefaultHost().attach("/context",
                new ContextServerApplication());
        try {
            // Start the component.
            serverComponent.start();
        } catch (Exception ex) {
            Logger.getLogger(EgocentricContextManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Set<Spatial> perceptionSpace = new HashSet<Spatial>();
    
    public void determineSpaces(final Node node) {
        
        final Set<Spatial> tempPerceptionSpace = new HashSet<Spatial>();
        computePerceptionSpace(node, tempPerceptionSpace);
        setPerceptionSpace(tempPerceptionSpace);
    }
    
    private synchronized void setPerceptionSpace(final Set<Spatial> data) {
        perceptionSpace.clear();
        perceptionSpace.addAll(data);
    }

    public synchronized Set<Spatial> getPerceptionSpace() {
        return perceptionSpace;
    }        
    
    private void computePerceptionSpace(final Node node, final Set<Spatial> perceptionSpace) {

        for (Spatial element : node.getChildren()) {

            if (element.getCullHint() != Spatial.CullHint.Never) {

                final EgocentricContextData data = element.getUserData(EgocentricContextData.TAG);
                if (data != null && element.checkCulling(cam)) {
                    perceptionSpace.add(element);
                }

                final List<Spatial> children = node.getChildren();
                if (children != null && children.size() > 0) {

                    for (final Spatial child : children) {


                        if (Node.class.isAssignableFrom(child.getClass())) {
                            computePerceptionSpace((Node) child, perceptionSpace);
                        }
                    }
                }
            }
        }
    }               
    
    @Override
    public void cleanup() {
        super.cleanup();

        if (serverComponent != null && serverComponent.isStarted()) {
            try {
                serverComponent.stop();
            } catch (Exception ex) {
                Logger.getLogger(EgocentricContextManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
    }
}
