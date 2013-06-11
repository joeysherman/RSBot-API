package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Locatable;
import org.powerbot.script.wrappers.Player;
import org.powerbot.script.wrappers.Tile;

import java.awt.event.KeyEvent;

public class Camera extends ClientLink {
	public Camera(ClientFactory factory) {
		super(factory);
	}

	public int getX() {
		Client client = ctx.getClient();
		return client != null ? client.getCamPosX() : -1;
	}

	public int getY() {
		Client client = ctx.getClient();
		return client != null ? client.getCamPosY() : -1;
	}

	public int getZ() {
		Client client = ctx.getClient();
		return client != null ? client.getCamPosZ() : -1;
	}

	public int getYaw() {
		Client client = ctx.getClient();
		return client != null ? (int) (client.getCameraYaw() / 45.51) : -1;
	}

	public int getPitch() {
		Client client = ctx.getClient();
		return client != null ? (int) ((client.getCameraPitch() - 1024) / 20.48) : -1;
	}

	public int setPitch(final int pitch) {
		int p = getPitch();
		if (p == pitch) return 0;
		final boolean up = pitch > p;
		ctx.keyboard.pressKey(up ? KeyEvent.VK_UP : KeyEvent.VK_DOWN);
		int curr;
		final Timer timer = new Timer(100);
		while (timer.isRunning()) {
			curr = getPitch();
			if (curr != p) {
				p = curr;
				timer.reset();
			}
			if (up && curr >= pitch) break;
			else if (!up && curr <= pitch) break;

			Delay.sleep(5, 10);
		}
		ctx.keyboard.releaseKey(up ? KeyEvent.VK_UP : KeyEvent.VK_DOWN);
		return p - pitch;
	}

	public void setAngle(final char direction) {
		switch (direction) {
			case 'n':
				setAngle(0);
				break;
			case 'w':
				setAngle(90);
				break;
			case 's':
				setAngle(180);
				break;
			case 'e':
				setAngle(270);
				break;
			default:
				throw new RuntimeException("invalid direction " + direction + ", expecting n,w,s,e");
		}
	}

	public void setAngle(int degrees) {
		degrees %= 360;
		if (getAngleTo(degrees) > 5) {
			ctx.keyboard.pressKey(KeyEvent.VK_LEFT);
			final Timer timer = new Timer(500);
			int ang, prev = -1;
			while ((ang = getAngleTo(degrees)) > 5 && timer.isRunning()) {
				if (ang != prev) {
					timer.reset();
				}
				prev = ang;
				Delay.sleep(10, 15);
			}
			ctx.keyboard.releaseKey(KeyEvent.VK_LEFT);
		} else if (getAngleTo(degrees) < -5) {
			ctx.keyboard.pressKey(KeyEvent.VK_RIGHT);
			final Timer timer = new Timer(500);
			int ang, prev = -1;
			while ((ang = getAngleTo(degrees)) < -5 && timer.isRunning()) {
				if (ang != prev) {
					timer.reset();
				}
				prev = ang;
				Delay.sleep(10, 15);
			}
			ctx.keyboard.releaseKey(KeyEvent.VK_RIGHT);
		}
	}

	public int getAngleTo(final int degrees) {
		int ca = getYaw();
		if (ca < degrees) {
			ca += 360;
		}
		int da = ca - degrees;
		if (da > 180) {
			da -= 360;
		}
		return da;
	}

	public void turnTo(final Locatable l) {
		turnTo(l, 0);
	}

	public void turnTo(final Locatable l, final int dev) {
		final int a = getAngleToLocatable(l);
		if (dev == 0) setAngle(a);
		else setAngle(Random.nextInt(a - dev, a + dev + 1));
	}

	private int getAngleToLocatable(final Locatable mobile) {
		final Player local = ctx.players.getLocal();
		final Tile t1 = local != null ? local.getLocation() : null;
		final Tile t2 = mobile.getLocation();
		return t1 != null && t2 != null ? ((int) Math.toDegrees(Math.atan2(t2.getY() - t1.getY(), t2.getX() - t1.getX()))) - 90 : 0;
	}
}
