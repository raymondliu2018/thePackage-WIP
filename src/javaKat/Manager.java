package javaKat;  

import java.util.ArrayList;
import javaKat.debugger.DebuggerTag;

public final class Manager extends Manipulator implements GameData{
    private static ArrayList<VoidCommand> queuedCommands = new ArrayList<>();
    protected static void run() {
        if (enabled){
            for (VoidCommand command : queuedCommands) {
                command.run();
            }
            queuedCommands.clear();
        }
    }
    
    public static boolean commandsQueued(Object requestor) {
        if (requestor instanceof DebuggerTag){
            return queuedCommands.isEmpty();
        }
        throw new IllegalArgumentException(DebuggerTag.DEBUGGER_MESSAGE);
    }
    
    public static void queueWipeAll() {
        queuedCommands.add(() -> {wipeAll();});
    }
    
    public static void wipeAll() {
         GameData.allEntities.clear();
         GameData.sprites.clear();
         GameData.focusedEntities.clear();
         GameData.keys.clear();
         GameData.buttons.clear();
         GameData.layers.clear();
         GameData.imageFiles.clear();
         GameData.images.clear();
         GameData.stats.clear();
         GameData.enders.clear();
    }
    
    public static void queueNewEntity( Entity input ) {
        initEntity(input);
    }
    
    public static void addSprite(Sprite input){
        initSprite(input);
    }
                
    public static void addStat( Text input ) {
        queuedCommands.add(() -> {GameData.stats.add(input);});
    }
    
    public static void removeStat( Text input ) {
        queuedCommands.add(() -> {GameData.stats.remove(input);});
    }
    
    public static boolean findThisEntity( Entity input ) {
        return GameData.allEntities.indexOf(input) != -1;
    }
    
    private static void initEntity( Entity input ) {
        queuedCommands.add(() -> {if (!input.superCalled()){throw new RuntimeException("Super not called on " + input);}});
        queuedCommands.add(() -> {GameData.keys.addAll(input.getKeys());});
        queuedCommands.add(() -> {GameData.buttons.addAll(input.getButtons());});
        initLayer(input,input.getLayer());
        initEnder(input);
        initFocused(input);
        initSprite(input.getSprite());
        queuedCommands.add(() -> {GameData.allEntities.add(input);});
    }
    
    public static void removeEntity( Entity input ){
        queuedCommands.add(() -> {GameData.keys.removeAll(input.getKeys());});
        queuedCommands.add(() -> {GameData.buttons.removeAll(input.getButtons());});
        queuedCommands.add(() -> {GameData.stats.removeAll(input.getStats());});
        removeLayer(input,input.getLayer());
        removeSprite(input,input.getLayer());
        removeEnder(input);
        queuedCommands.add(() -> {GameData.allEntities.remove(input);});
    }
      
    protected static void switchLayer( Entity input, int from, int to ){
        removeLayer(input, from);
        initLayer(input, to);
        removeSprite(input, from);
        initSprite(input.getSprite());
    }
    
    public static void addLonelySprite( Sprite input, int to ) {
        queuedCommands.add(() -> {Utility.addSprite(input, to);});
    }
    
    public static void removeLonelySprite( Sprite input, int from ) {
        queuedCommands.add(() -> {
            GameData.sprites.get(from).remove(input);
        });
    }
    
    private static void initSprite(Sprite input) {
        queuedCommands.add(() -> {Utility.addSprite(input, input.getLayer());});
    }
    
    private static void initLayer( Entity input, int to ) {
        queuedCommands.add(() -> {Utility.addLayer(input,to);});
    }
    
    private static void initFocused( Entity input ){
        queuedCommands.add(() -> {
            if( input.getFocused() ) {
                GameData.focusedEntities.add(input);
            }
        });
    }
    
    private static void initEnder( Entity input ) {
        queuedCommands.add(() -> {
            if (input instanceof Ender) {
                GameData.enders.add((Ender)input);
            }
        });
    }
    
    private static void removeFocused( Entity input ){
        queuedCommands.add(() -> {
            if( input.getFocused() ) {
                GameData.focusedEntities.remove(input);
            }
        });
    }
    
    private static void removeSprite( Entity input, int from ){
        removeFocused(input);
        queuedCommands.add(() -> {
            try {
                GameData.sprites.get(from).remove(input.getSprite());
            } catch (IndexOutOfBoundsException exception) {}});
    }
    
    private static void removeLayer( Entity input, int from ){
        queuedCommands.add(() -> {
            try {
                GameData.layers.get(from).remove(input.getRect());
            } catch (IndexOutOfBoundsException exception) {}});
    }
    
    private static void removeEnder( Entity input ) {
         queuedCommands.add(() -> {
            if( input instanceof Ender ) {
                GameData.enders.remove((Ender) input);
            }
        });
    }
}