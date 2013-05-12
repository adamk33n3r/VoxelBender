/*
 * Adam Keenan, 2013
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-sa/3.0/.
 */

package net.adam_keenan.voxel;

import static org.lwjgl.opengl.GL11.*;

import java.awt.SystemTray;
import java.nio.FloatBuffer;

import net.adam_keenan.voxel.utils.TextureLoader;
import net.adam_keenan.voxel.utils.TextureLoader.Textures;
import net.adam_keenan.voxel.world.Arena;
import net.adam_keenan.voxel.world.player.BendingStyle.Element;
import net.adam_keenan.voxel.world.player.Player;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class VoxelBender {
	
	public final static int WINDOW_WIDTH = 856;
	public final static int WINDOW_HEIGHT = 480;
	
	private Arena arena;
	private Player player;
	
	private final boolean vSync = false;
	
	private long lastFrame;
	private long lastFPS;
	private float fps, curFPS;
	private int delta;
	
//	private void setUpLighting() {
//		glShadeModel(GL_SMOOTH);
//		glEnable(GL_DEPTH_TEST);
//		glEnable(GL_LIGHTING);
//		glEnable(GL_LIGHT0);
//		glLightModel(GL_LIGHT_MODEL_AMBIENT, asFlippedFloatBuffer(new float[] { 0.1f, 0.1f, 0.1f, 1f }));
//		glLight(GL_LIGHT0, GL_POSITION, asFlippedFloatBuffer(new float[] { 0, 0, 0, 1 }));
//		glLight(GL_LIGHT0, GL_DIFFUSE, asFlippedFloatBuffer(new float[] { 1, 1, 1, 1 }));
//		
//		glEnable(GL_COLOR_MATERIAL);
//		glColorMaterial(GL_FRONT, GL_DIFFUSE);
//	}
	
	public static FloatBuffer asFlippedFloatBuffer(float... values) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(values.length);
		buffer.put(values);
		buffer.flip();
		return buffer;
	}
	
	private void initGL() {
		glShadeModel(GL_SMOOTH);
		//		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glClearColor(0.53f, 0.8f, 0.98f, 0.0f); // Sky blue
		glClearDepth(1.0);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		
//		glEnable(GL_CULL_FACE);
//		glCullFace(GL_BACK);
		
		// Transparency
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, 0.0f);
		
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
	}

	public void start() {
		
		createWindow();
		initGL();
		
		getDelta();
		lastFPS = getTime();
		arena = new Arena();
		arena.genArena();
		player = new Player(Element.FIRE, arena, 10, 17, 10);
		player.processMouse();
		TextureLoader.loadTextures(false);
		TextureLoader.bind(Textures.SHEET);
		
		run();
	}
	
	private void run() {
			
			Display.setVSyncEnabled(vSync);
			Mouse.setGrabbed(true);
			while (!Display.isCloseRequested()) {
				delta = getDelta();
				
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				
				glLoadIdentity();
				
	//			font.draw("test", -.9f, 0, .3f, .225f);
				player.update();
				
				arena.update();
				arena.render();
				
//				long time = System.currentTimeMillis();
				player.render();
//				System.out.println(System.currentTimeMillis() - time);
				
				Display.update();
//				Display.sync(60);
				processInput(delta);
				updateFPS();
				Display.setTitle("VoxelBender - FPS: " + curFPS + " - Delta: " + delta + " - Java Version: " + System.getProperty("java.version"));
				
			}
			Display.destroy();
			System.exit(0);
			
		}

	private long getTime() {
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
	
	private int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;
		return delta;
	}
	
	private void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			curFPS = fps;
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	private void createWindow() {
		//Display.setFullscreen(false);
		/*DisplayMode d[] = Display.getAvailableDisplayModes();
		for (int i = 0; i < d.length; i++) {
			if (d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
				displayMode = d[i];
				break;
			}
		}
		Display.setDisplayMode(displayMode);*/
		try {
			Display.setDisplayMode(new DisplayMode(WINDOW_WIDTH, WINDOW_HEIGHT));
			Display.setTitle("VoxelBender");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	private void processInput(int delta) {
//		if (Mouse.isButtonDown(0) && !Mouse.isGrabbed())
//			Mouse.setGrabbed(true);
//		} else if (Mouse.isButtonDown(1)) {
//			Mouse.setGrabbed(false);
//		}
//		if (Mouse.isGrabbed())
			player.processMouse();
		player.processKeyboard(delta);
		
	}
	
	public static void main(String[] args) throws LWJGLException {
		VoxelBender r = new VoxelBender();
		r.start();
	}
}
