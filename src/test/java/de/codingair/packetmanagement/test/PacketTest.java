package de.codingair.packetmanagement.test;

import de.codingair.packetmanagement.exceptions.UnknownPacketException;
import de.codingair.packetmanagement.packets.impl.SuccessPacket;
import de.codingair.packetmanagement.test.datahandlers.*;
import de.codingair.packetmanagement.test.packets.MultiLayerNameRequestPacket;
import de.codingair.packetmanagement.test.packets.NameRequestPacket;
import de.codingair.packetmanagement.test.packets.SimplePacket;
import de.codingair.packetmanagement.test.packets.UnknownPacket;
import de.codingair.packetmanagement.test.proxies.SecondTestProxy;
import de.codingair.packetmanagement.test.proxies.TestProxy;
import de.codingair.packetmanagement.utils.Direction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletionException;

public class PacketTest {
    private final TestStreamDataHandler standard = new TestStreamDataHandler();
    private final TestTimeOutHandlerStream timeSpecific = new TestTimeOutHandlerStream();
    private final OneWayStreamDataHandler oneWay = new OneWayStreamDataHandler();
    private final MultiLayerHelper multiLayer = new MultiLayerHelper(Direction.DOWN, new TestProxy());

    public PacketTest() {
        oneWay.other = new OneWayStreamDataHandler();

        TestMultiLayerStreamDataHandler multiLayerDataHandler = new TestMultiLayerStreamDataHandler();
        multiLayer.other = multiLayerDataHandler;
        MultiLayerHelper multiLayerHelperBackend = new MultiLayerHelper(Direction.UP, new SecondTestProxy());
        multiLayerHelperBackend.other = multiLayerDataHandler;
        multiLayerDataHandler.first = multiLayer;
        multiLayerDataHandler.second = multiLayerHelperBackend;
    }

    @Test
    void simple() {
        Assertions.assertThrows(NullPointerException.class, () -> oneWay.send(new SuccessPacket()),
                "Cannot send assigned packet without UUID: class de.codingair.packetmanagement.packets.impl.SuccessPacket");
        Assertions.assertThrows(UnknownPacketException.class, () -> oneWay.send(new UnknownPacket()),
                "class de.codingair.packetmanagement.test.packets.UnknownPacket is not registered!");
        Assertions.assertDoesNotThrow(() -> oneWay.send(new SimplePacket()));
    }

    @Test
    void requestResponse() {
        Assertions.assertEquals(standard.send(new NameRequestPacket(0)).join().a(), "CodingAir");
        Assertions.assertEquals(standard.send(new NameRequestPacket(1)).join().a(), "UNKNOWN");
    }

    @Test
    void timeOut() {
        Assertions.assertThrows(CompletionException.class, () -> timeSpecific.send(new NameRequestPacket(1), 5).join(),
                "de.codingair.packetmanagement.exceptions.TimeOutException: The requested packet took too long.");
        timeSpecific.flush();

        //still working
        Assertions.assertThrows(CompletionException.class, () -> timeSpecific.send(new NameRequestPacket(1), 5).join(),
                "de.codingair.packetmanagement.exceptions.TimeOutException: The requested packet took too long.");
        timeSpecific.flush();

        Assertions.assertEquals(standard.send(new NameRequestPacket(1), 200).join().a(), "UNKNOWN");
        standard.flush();
    }

    @Test
    void multiLayer() {
        Assertions.assertEquals(multiLayer.send(new MultiLayerNameRequestPacket()).join().a(), "CodingAir");
    }

    @Test
    void gsonTest() {
        TestGsonDataHandler gson = new TestGsonDataHandler();
        Assertions.assertEquals(gson.send(new NameRequestPacket(0)).join().a(), "CodingAir");
    }
}
