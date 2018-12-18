package State;

import java.awt.*;
import java.io.*;
import java.net.URL;
import javax.sound.sampled.AudioFormat;
import javax.swing.ImageIcon;
import Sound.*;

/**
 *  Code created by David Brackeen
 *  Copyright (c) 2003, David Brackeen
 */
public class ResourceManager {

    public GraphicsConfiguration gc;

    private SoundManager soundManager;

    // Uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    public static final AudioFormat PLAYBACK_FORMAT = new AudioFormat(44100, 16, 1, true, false);

    // Create a new ResourceManager with the specified GraphicsConfiguration
    public ResourceManager(GraphicsConfiguration gc, SoundManager soundManager) {

        this.gc = gc;

        this.soundManager = soundManager;

        try {

            java.util.Enumeration e = getClass().getClassLoader().getResources("State.ResourceManager");

            while(e.hasMoreElements()) System.out.println(e.nextElement());

        } catch (IOException ex) { }
    }

    // Get an image from the images directory
    public Image loadImage(String name) {

        String filename = "Resources/Images/" + name;
        return new ImageIcon(getResource(filename)).getImage();
    }

    // Get Resources
    public URL getResource(String filename) { return getClass().getClassLoader().getResource(filename); }

    // Get Resource as Input Stream
    public InputStream getResourceAsStream(String filename) { return getClass().getClassLoader().getResourceAsStream(filename); }

    // Load Sound Manager
    public Sound loadSound(String name) {

        String filename = "Resources/Sounds/" + name;
        return soundManager.getSound(getResourceAsStream(filename));
    }

} // End of Class.