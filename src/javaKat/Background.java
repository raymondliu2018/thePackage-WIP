package javaKat;  

import java.awt.image.BufferedImage;
import javax.sound.sampled.Clip;
import javaKat.debugger.DebuggerTag;
final class Background extends Entity implements DebuggerTag {
    protected static Background instance;
    private static Album sprite;
    private Background() {
        super();
        rect.setLayer(0);
        sprite = new Album(this);
    }
    
    public static Background getInstance() {
        if (instance == null) {
            instance = new Background();
        }
        if (!Manager.findThisEntity(instance)){
            Manager.queueNewEntity(instance);
        }
        return instance;
    }
    
    protected void set(BufferedImage input) {
        if(input != null) {
            GameMaster.getFrame().setNewSize(input.getWidth(null),input.getHeight(null));
        }
        else {
            System.out.println("Frame did not resize: Invalid image");
        }
        sprite.eraseAllPages();
        sprite.addPageWithPicture(input,"main");
        sprite.setPage("main");
    }
    
    protected void set(Clip input) {
        jukeBox.addTrack(input,"main",true);
        jukeBox.loopCurrentTrack();
    }
    
    protected void set(float input) {
        jukeBox.setVolumeOfCurrentClip(input);
    }
    
    public void subUpdate(){
    }
}