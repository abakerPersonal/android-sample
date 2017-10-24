package com.sonos.abaker.android_sample.discover;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import static android.R.id.message;

/**
 * Created by alan.baker on 10/23/17.
 */

public class GroupDiscoveryService {
    private static final String LOG_TAG = GroupDiscoveryService.class.getSimpleName();

    // Relevant keys in SSDP M-SEARCH responses
    final static String WEBSOCKET_KEY = "WEBSOCK.SMARTSPEAKER.AUDIO";
    protected final static String HOUSEHOLD_KEY = "HOUSEHOLD.SMARTSPEAKER.AUDIO";
    private final static String GROUP_KEY = "GROUPINFO.SMARTSPEAKER.AUDIO";
    private final static String GROUP_ID = "gid";
    private final static String GROUP_COORDINATOR = "gc";
    private final static String GROUP_NAME = "gname";
    private final static String USN_KEY = "USN";
    private final static String NTS_KEY = "NTS";
    private final static String BOOT_ID_KEY = "BOOTID.UPNP.ORG";
    private final static String CONFIG_ID_KEY = "CONFIGID.UPNP.ORG";
    private final static String CACHE_CONTROL_KEY = "CACHE-CONTROL";

    private final static String MAX_AGE_KEY = "max-age";

    private final int SSDP_PORT = 1900; // UDP port for SSDP
    private final String MULTICAST_SSDP_IP = "239.255.255.250"; // Multicast address for SSDP
    private final String BROADCAST_SSDP_IP = "255.255.255.255"; // Broadcast address
    private final int MSEARCH_INTERVAL_IN_MSECS = 1000;

    private final static String SEARCH_TARGET = "urn:smartspeaker-audio:service:SpeakerGroup:1";

    private Thread msearchThread;
    private final SortedMap<String, Group> discoveredGroups = Collections.synchronizedSortedMap(new TreeMap<String,
            Group>());

    public void start() {
        discoveredGroups.clear();

        msearchThread = new MSearchThead();
        msearchThread.start();
    }

    public void stop() {
        if (msearchThread != null) {
            msearchThread.interrupt();
        }
    }

    private class MSearchThead extends Thread {

        public void run() {
            InetSocketAddress dstAddress;
            InetSocketAddress broadcastAddress;
            DatagramSocket socket = null;
            DatagramSocket broadcastSocket;

            try {
                dstAddress = new InetSocketAddress(InetAddress.getByName(MULTICAST_SSDP_IP), SSDP_PORT);
                broadcastAddress = new InetSocketAddress(InetAddress.getByName(BROADCAST_SSDP_IP), SSDP_PORT);

                // Construct the SSDP M-SEARCH request
                String discoveryMessage =
                        "M-SEARCH * HTTP/1.1\r\n" +
                                "HOST: " + MULTICAST_SSDP_IP + ":" + SSDP_PORT + "\r\n" +
                                "USER-AGENT: Android/version SonosSampleApp/1.0\r\n" +
                                "ST: " + SEARCH_TARGET + "\r\n" +
                                "MAN: \"ssdp:discover\"\r\n" +
                                "MX: 1\r\n" +
                                "\r\n";

                byte[] discoverMessageBytes = discoveryMessage.getBytes();

                DatagramPacket discoveryPacket = new DatagramPacket(discoverMessageBytes, discoverMessageBytes.length, dstAddress);
                DatagramPacket broadcastDiscoveryPacket = new DatagramPacket(discoverMessageBytes, discoverMessageBytes.length, broadcastAddress);
                DatagramPacket receivePacket;

                socket = new DatagramSocket(0);
                socket.setReuseAddress(true);
                socket.setSoTimeout(MSEARCH_INTERVAL_IN_MSECS);

                broadcastSocket = new DatagramSocket(0);
                broadcastSocket.setReuseAddress(true);
                broadcastSocket.setSoTimeout(MSEARCH_INTERVAL_IN_MSECS);

                receivePacket = new DatagramPacket(new byte[1536], 1536);

                int searchCounter = 0;
                long nextSearchTimeMillis = 0;

                do {
                    long currentTimeMilis = System.currentTimeMillis();
                    if (searchCounter < 4 && currentTimeMilis >= nextSearchTimeMillis) {
                        if (searchCounter == 3) {
                            //Update groups discoverd
                            Log.d(LOG_TAG, discoveredGroups.toString());
                        } else {
                            socket.send(discoveryPacket);
                            broadcastSocket.send(discoveryPacket);
                            nextSearchTimeMillis = currentTimeMilis + MSEARCH_INTERVAL_IN_MSECS;
                        }
                        searchCounter++;
                    }

                    int socketTimeout = (int) (nextSearchTimeMillis - currentTimeMilis);

                    try {
                        socket.setSoTimeout(searchCounter < 3 && socketTimeout > 0 ? socketTimeout : 1000);
                        socket.receive(receivePacket);
                        String message = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
                        processGroupsDiscovered(message);
                    } catch (SocketTimeoutException exception) {
                        // Do Nothing
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "There was an error communicating with the socket",
                                e);
                    }
                } while(!currentThread().isInterrupted());


            } catch (IOException e) {
                Log.e(LOG_TAG, "There was an error communicating with the socket", e);
            } finally {
                if (socket != null) {
                    socket.disconnect();
                    socket.close();
                }
            }
        }

    }

    private void processGroupsDiscovered(String message) {
        Group group = parseGroupResponse(message);
        if (group.getId() != null) {
            discoveredGroups.remove(group.getUuid());
            if(group.getNTS() == null || !group.getNTS().equals("ssdp:byebye")) {
                discoveredGroups.put(group.getUuid(), group);
            }
        }
    }

    private Group parseGroupResponse(String message) {
        StringTokenizer tokenizer = new StringTokenizer(message, "\r\n");
        Integer cacheTimeout;
        Group group = new Group();

        while (tokenizer.hasMoreElements()) {
            String string = tokenizer.nextToken();
            String[] tokens = string.split(":", 2);
            List<String> list = Arrays.asList(WEBSOCKET_KEY, GROUP_KEY, HOUSEHOLD_KEY, USN_KEY, NTS_KEY, BOOT_ID_KEY, CONFIG_ID_KEY,
                    CACHE_CONTROL_KEY);
            if (tokens.length > 1 && list.contains(tokens[0])) {
                switch (tokens[0]) {
                    case GROUP_KEY:
                        String[] sonosGroupTokens = tokens[1].split(";", 3);
                        for (String groupToken : sonosGroupTokens) {
                            String[] sonosGroupSubTokens = groupToken.split("=", 2);
                            if (sonosGroupSubTokens.length == 2) {
                                String value = sonosGroupSubTokens[1].trim().replaceAll("^\"|\"$", "")
                                        .replaceAll("([^\\\\])\\\\|^\\\\", "$1").replaceAll("\\\\\\\\", "\\\\");
                                switch (sonosGroupSubTokens[0].trim()) {
                                    case GROUP_ID:
                                        group.setId(value);
                                        break;
                                    case GROUP_NAME:
                                        group.setName(value);
                                        break;
                                    case GROUP_COORDINATOR:
                                        group.setGroupCoordinator(value.equals("1"));
                                        break;
                                }
                            }
                        }
                        break;
                    case HOUSEHOLD_KEY:
                        group.setHouseholdId(tokens[1].trim());
                        break;
                    case USN_KEY:
                        String[] uuidTokens = tokens[1].split(":", 2);
                        String[] uuidSubTokens = uuidTokens[1].split("::", 2);
                        group.setUuid(uuidSubTokens[0].trim());
                        break;
                    case CACHE_CONTROL_KEY:
                        String[] ageTokens = tokens[1].split("=", 2);
                        if (ageTokens[0].trim().equals(MAX_AGE_KEY)) {
                            cacheTimeout = Integer.valueOf(ageTokens[1].trim());
                            Calendar currentTime = Calendar.getInstance();
                            currentTime.add(Calendar.SECOND, cacheTimeout);
                            group.setMaxAge(currentTime.getTime());
                        }
                        break;
                    case WEBSOCKET_KEY:
                        group.setWebsocketURL(tokens[1].trim());
                        break;
                    case BOOT_ID_KEY:
                        group.setBootId(tokens[1].trim());
                        break;
                    case CONFIG_ID_KEY:
                        group.setConfigId(tokens[1].trim());
                        break;
                    case NTS_KEY:
                        group.setNTS(tokens[1].trim());
                        break;
                }
            }
        }
        return group;
    }
}
