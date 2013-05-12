package net.adam_keenan.voxel.world;

import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

public class JBulletPhysics {
	
	static DiscreteDynamicsWorld dynamicWorld;
	static CollisionShape cube = new BoxShape(new Vector3f(.5f, .5f, .5f));
	static CollisionShape ground;
	
	static RigidBodyConstructionInfo fallRigidBodyCI;
	static DefaultMotionState fallMotionState;
	
	static ArrayList<RigidBody> cubeList = new ArrayList<RigidBody>();
	
	static JBulletPhysics instance = new JBulletPhysics();
	
	private JBulletPhysics() {
		BroadphaseInterface broadphase = new DbvtBroadphase();
		DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();
		CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
		
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		
		dynamicWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		
		// set the gravity of our world
		dynamicWorld.setGravity(new Vector3f(0, -9.8f, 0));
		
		// setup our collision shapes
		ground = new StaticPlaneShape(new Vector3f(0, 1, 0), 1);
		
		// setup the motion state																							// Position vector
		DefaultMotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 10, 0), 1.0f)));
		
		RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(0, groundMotionState, ground, new Vector3f(0, 0, 0));
		RigidBody groundRigidBody = new RigidBody(groundRigidBodyCI);
		
		dynamicWorld.addRigidBody(groundRigidBody); // add our ground to the dynamic world..

		int mass = 1;
		Vector3f fallInertia = new Vector3f(0, 0, 0);
		cube.calculateLocalInertia(mass, fallInertia);
		fallMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 30, 0), 1.0f)));
		fallRigidBodyCI = new RigidBodyConstructionInfo(1, fallMotionState, cube, fallInertia);

	}
	
	public static void step() {
		dynamicWorld.stepSimulation(1/60f);
	}
	
	public static CollisionShape getGround() {
		return ground;
	}
	
	public static void addCube(int mass) {
		RigidBody rigidBody = new RigidBody(fallRigidBodyCI);
		cubeList.add(rigidBody);
		dynamicWorld.addRigidBody(rigidBody);
	}
	
	public static RigidBody getCube(int num) {
		return cubeList.get(num);
	}
	
	public static void addObjectToWorld(CollisionObject obj) {
		dynamicWorld.addCollisionObject(obj);
	}
}
