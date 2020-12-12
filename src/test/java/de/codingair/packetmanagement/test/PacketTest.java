package de.codingair.packetmanagement.test;

import de.codingair.packetmanagement.packets.exceptions.HandlerAlreadyPurgedException;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletionException;

public class PacketTest {
    private final TestDataHandler standard = new TestDataHandler();
    private final TestTimeOutHandler timeSpecific = new TestTimeOutHandler();

    @Test
    public void requestResponse() {
        Assertions.assertEquals(standard.send(new NameRequestPacket(0)).join().name(), "CodingAir");
        Assertions.assertEquals(standard.send(new NameRequestPacket(1)).join().name(), "UNKNOWN");
    }

    @Test
    public void timeOut() {
        Assertions.assertThrows(CompletionException.class, () -> timeSpecific.send(new NameRequestPacket(1), 50).join(),
                "de.codingair.packetmanagement.packets.exceptions.TimeOutException: The requested packet took too long.");
        timeSpecific.purge();

        Assertions.assertThrows(HandlerAlreadyPurgedException.class, () -> timeSpecific.send(new NameRequestPacket(1), 50).join());
    }
}
