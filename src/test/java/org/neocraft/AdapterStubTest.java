package org.neocraft;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.neocraft.client.NeoCraft;
import org.neocraft.adapter.McpAdapter;
public class AdapterStubTest {
    @Test
    public void testInitialization() {
        NeoCraft.init(new McpAdapter());
        assertNotNull(NeoCraft.get());
        assertEquals("StubFontRenderer", NeoCraft.get().adapter().getFontRenderer());
        NeoCraft.get().shutdown();
    }
}
