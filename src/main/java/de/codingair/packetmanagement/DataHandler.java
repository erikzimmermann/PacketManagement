package de.codingair.packetmanagement;

import de.codingair.packetmanagement.exceptions.*;
import de.codingair.packetmanagement.handlers.MultiLayerPacketHandler;
import de.codingair.packetmanagement.handlers.PacketHandler;
import de.codingair.packetmanagement.handlers.ResponsibleMultiLayerPacketHandler;
import de.codingair.packetmanagement.handlers.ResponsiblePacketHandler;
import de.codingair.packetmanagement.packets.*;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.packetmanagement.utils.FormedPacket;
import de.codingair.packetmanagement.utils.ObjectMerger;
import de.codingair.packetmanagement.utils.Proxy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @param <C> The connection channel type.
 * @param <D> The sending data type.
 */
public abstract class DataHandler<C, D> {
    protected final String channelBackend, channelProxy;
    protected final ConcurrentHashMap<UUID, CompletableFuture<? extends ResponsePacket>> future = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<UUID, ObjectMerger<?>> keep = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<UUID, Long> timeSpecific = new ConcurrentHashMap<>();
    protected final Proxy proxy;
    protected final HashMap<Class<? extends Packet>, PacketHandler<?>> handlers = new HashMap<>();
    protected long timeOut = 250L;
    private Timer timeOutTimer = new Timer("DataHandler-TimeOut");
    private boolean running = false;

    public DataHandler(@NotNull String channelName, @NotNull Proxy proxy) {
        this.proxy = proxy;
        channelBackend = channelName + ":backend";
        channelProxy = channelName + ":proxy";
    }

    protected abstract boolean isConnected(Direction direction);

    public <P extends Packet> boolean registerHandler(@NotNull Class<? extends P> receiving, @NotNull PacketHandler<P> handler) {
        return handlers.put(receiving, handler) == null;
    }

    /**
     * @param data       Byte-Array to sent.
     * @param connection Connection which maybe will be used to send the data.
     * @param direction  The direction in which we need to send our data
     */
    protected abstract void send(D data, C connection, Direction direction);

    public void send(@NotNull Packet packet, @Nullable C connection, @NotNull Direction direction) {
        send(packet, connection, direction, null);
    }

    void send(@NotNull Packet packet, @Nullable C connection, @NotNull Direction direction, @Nullable UUID id) {
        send(processPacket(packet, id), connection, direction);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet, @Nullable C connection, @NotNull Direction direction) {
        return send(packet, connection, direction, 0);
    }

    public <A extends ResponsePacket> CompletableFuture<A> send(@NotNull RequestPacket<A> packet, @Nullable C connection, @NotNull Direction direction, long timeOut) {
        CompletableFuture<A> future = packet.buildFuture();
        if (!isConnected(direction)) {
            future.completeExceptionally(new NoConnectionException("No " + direction.name() + " connection established!"));
            return future;
        }

        send(processPacket(packet, registerFuture(timeOut, future)), connection, direction);
        return future;
    }

    /**
     * Stops (if running) the active TimeOut-Timer and removes all references to timeOuts and completable futures.
     */
    public void flush() {
        if (this.running) {
            this.timeOutTimer.cancel();
            this.timeOutTimer.purge();
            this.timeOutTimer = new Timer("DataHandler-TimeOut");
            running = false;
        }

        this.timeSpecific.clear();
        this.future.clear();
    }

    UUID generateID() {
        UUID id;
        do {
            id = UUID.randomUUID();
        } while (future.containsKey(id));
        return id;
    }

    @NotNull <P extends Packet, H extends PacketHandler<P>> H formHandler(P packet) throws NoHandlerException {
        PacketHandler<?> handler = handlers.get(packet.getClass());
        if (handler == null) throw new NoHandlerException(packet.getClass());

        try {
            //noinspection unchecked
            return (H) handler;
        } catch (ClassCastException e) {
            throw new HandlerException("PacketHandler " + handler + " cannot be used for packet " + packet.getClass() + "!", e);
        }
    }

    public abstract @NotNull FormedPacket convertReceivedData(@NotNull D data, @Nullable C connection, @NotNull Direction direction);

    /**
     * @param data       Sent data.
     * @param connection Connection which maybe will be used to send the data.
     * @param direction  Direction where we get our data from.
     */
    public <A extends ResponsePacket> void receive(@NotNull D data, @Nullable C connection, @NotNull Direction direction) {
        FormedPacket formedPacket = convertReceivedData(data, connection, direction);
        Packet packet = formedPacket.getPacket();
        boolean future = formedPacket.hasFuture();
        UUID id = formedPacket.getFutureId();

        if (future && packet instanceof ResponsePacket) {
            //noinspection unchecked
            receiveResponse((A) packet, id);
        } else if (future && packet instanceof RequestPacket) {
            @SuppressWarnings ("unchecked")
            RequestPacket<A> ap = (RequestPacket<A>) packet;
            ResponsiblePacketHandler<RequestPacket<A>, A> handler = formHandler(ap);

            if (handler instanceof ResponsibleMultiLayerPacketHandler) {
                ResponsibleMultiLayerPacketHandler<RequestPacket<A>, A> multi = (ResponsibleMultiLayerPacketHandler<RequestPacket<A>, A>) handler;
                if (multi.answer(ap, proxy, direction)) {
                    try {
                        multi.response(ap, proxy, connection, direction).thenAccept(response -> send(response, connection, direction, id));
                    } catch (Escalation e) {
                        Packet escalation = e.packet();

                        if (escalation instanceof RequestPacket) {
                            //Register response to origin
                            e.future().whenComplete((response, err) -> {
                                if (err != null) send(e.exceptional(err), connection, direction, id);
                                else send(response, connection, direction, id);
                            });

                            if (!isConnected(e.direction())) {
                                e.future().completeExceptionally(new NoConnectionException("No " + e.direction().name() + " connection established!"));
                            } else {
                                //Perform escalation
                                send(processPacket(e.packet(), registerFuture(e.timeOut(timeOut), e.future())), connection, e.direction());
                            }
                        } else {
                            //Perform simple escalation
                            send(processPacket(e.packet(), null), connection, e.direction());
                        }
                    }
                }
            } else if (handler.answer(ap, proxy, direction)) handler.response(ap, proxy, connection, direction).thenAccept(response -> send(response, connection, direction, id));
        } else {
            PacketHandler<Packet> handler = formHandler(packet);

            if (handler instanceof MultiLayerPacketHandler) {
                MultiLayerPacketHandler<Packet> multi = (MultiLayerPacketHandler<Packet>) handler;
                try {
                    multi.process(packet, proxy, connection, direction);
                } catch (Escalation e) {
                    send(e.packet(), connection, e.direction());
                }
            } else handler.process(packet, proxy, connection, direction);
        }
    }

    protected final <A extends ResponsePacket> void receiveResponse(A packet, UUID id) {
        @SuppressWarnings ("unchecked")
        ObjectMerger<A> merger = (ObjectMerger<A>) keep.get(id);
        if (merger != null) {
            if (merger.append(packet)) {
                //finalize
                packet = merger.complete(packet);
            } else return; //wait for other responses
        }

        this.timeSpecific.remove(id);
        CompletableFuture<?> cf = this.future.remove(id);

        if (cf == null) {
            //No CompletableFuture given! Must be a response to another handler or the future is already terminated by a time-out!
            return;
        }

        try {
            CompletableFuture<A> future = (CompletableFuture<A>) cf;
            future.complete(packet);
        } catch (ClassCastException e) {
            throw new HandlerResponseException("Response " + packet.getClass() + " does not fit to completable future " + future.getClass() + ". Check the response of your packet handler!", e);
        }
    }

    UUID registerFuture(long timeOut, @NotNull CompletableFuture<? extends ResponsePacket> future) {
        UUID id = generateID();

        if (timeOut > 0) {
            if (!running) checkTimer(); //check timer before registering packets
            this.timeSpecific.put(id, timeOut + System.currentTimeMillis());
        }
        this.future.put(id, future);

        return id;
    }

    public abstract D serializePacket(@NotNull Packet packet, boolean future, @Nullable UUID uuid);

    private D processPacket(@NotNull Packet packet, @Nullable UUID uuid) {
        boolean future = true;
        if (packet instanceof IgnoreFuture) {
            future = false;
            packet = ((IgnoreFuture) packet).getPacket();
        } else if (packet instanceof MergeFuture) {
            MergeFuture<?> options = (MergeFuture<?>) packet;
            packet = ((MergeFuture<?>) packet).getPacket();
            registerOutgoingPacket(packet, uuid, options);
        }

        if (future && packet instanceof AssignedPacket) {
            if (uuid == null) throw new NullPointerException("Cannot send assigned packet without UUID: " + packet.getClass());
        } else if (uuid != null) throw new UnsupportedIdException("Cannot send id (" + uuid + ") for unsupported packet class: " + packet.getClass());

        return serializePacket(packet, future, uuid);
    }

    protected void registerOutgoingPacket(Packet packet, @Nullable UUID uuid, MergeFuture<?> options) {
        if (uuid == null) throw new NullPointerException("Cannot send KeepFuture packet without UUID: " + packet.getClass());
        keep.put(uuid, new ObjectMerger<>(options.getResults(), options.getMerger()));
    }

    private synchronized void checkTimer() {
        if (running) return;
        running = true;

        this.timeOutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                timeSpecific.entrySet().removeIf(e -> {
                    if (time >= e.getValue()) {
                        ObjectMerger<?> merger = keep.remove(e.getKey());
                        CompletableFuture<?> cf = future.remove(e.getKey());
                        if (cf != null) cf.completeExceptionally(new TimeOutException("The requested packet took too long.", merger));
                        return true;
                    } else return false;
                });
            }
        }, timeOut, timeOut);
    }

    public String getChannelProxy() {
        return channelProxy;
    }

    public String getChannelBackend() {
        return channelBackend;
    }

    public <P extends Proxy> P getProxy() {
        //noinspection unchecked
        return (P) proxy;
    }
}
