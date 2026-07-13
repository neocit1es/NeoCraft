package org.neocraft.test;

import org.junit.jupiter.api.Test;
import org.neocraft.adapter.StubAdapter;
import org.neocraft.adapter.StubConfig;
import org.neocraft.adapter.StubRenderer;

import static org.junit.jupiter.api.Assertions.*;

class AdapterStubTest {

	@Test
	void adapterProvidesBrandingAndVersion() {
		StubAdapter a = new StubAdapter();
		assertEquals("NeoCraft v1.0.0", a.getClientBrand());
		assertEquals("Minecraft 1.12.2", a.getMinecraftVersion());
	}

	@Test
	void rendererCountsDrawCalls() {
		StubAdapter a = new StubAdapter();
		StubRenderer r = (StubRenderer) a.renderer();
		r.resetCount();
		r.drawRect(0, 0, 10, 10, 0xFFFFFFFF);
		r.drawRoundedRect(0, 0, 10, 10, 3, 0xFFFFFFFF);
		assertEquals(2, r.getDrawCount());
	}

	@Test
	void configPersistsPrimitives() {
		StubAdapter a = new StubAdapter();
		StubConfig c = (StubConfig) a.config();
		c.setBoolean("enabled", true);
		c.setInt("slider", 42);
		c.setDouble("d", 1.5);
		assertTrue(c.getBoolean("enabled", false));
		assertEquals(42, c.getInt("slider", 0));
		assertEquals(1.5, c.getDouble("d", 0.0), 1e-9);
	}

	@Test
	void configNamespacesAreIsolated() {
		StubAdapter a = new StubAdapter();
		StubConfig root = (StubConfig) a.config();
		root.set("shared", "root");
		var profile = root.namespace("profile");
		profile.set("x", "1");
		assertEquals("1", profile.get("x"));
		assertEquals("root", profile.get("shared"));
		assertNull(root.get("x"));
	}

	@Test
	void fontSplitsToWidth() {
		StubAdapter a = new StubAdapter();
		var lines = a.fonts().splitToWidth("hello world this is a test", 36);
		assertTrue(lines.size() >= 2);
	}

	@Test
	void timingAdvances() {
		StubAdapter a = new StubAdapter();
		long t0 = a.currentTimeMillis();
		a.advance(50);
		assertEquals(t0 + 50, a.currentTimeMillis());
	}
}
