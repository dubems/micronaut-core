/*
 * Copyright 2017 original authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.particleframework.core.io.socket;

import javax.net.ServerSocketFactory;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Random;

import static org.particleframework.core.util.ArgumentUtils.check;
/**
 * Utility methods for dealing with sockets
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public class SocketUtils {

    private static final int MIN_PORT_RANGE = 1024;

    // no unsigned short type in Java so use constant
    private static final int MAX_PORT_RANGE = 65535;

    private static final Random random = new Random(System.currentTimeMillis());

    /**
     * Finds an available TCP port
     *
     * @return The available port
     */
    public static int findAvailableTcpPort() {
        return findAvailableTcpPort(MIN_PORT_RANGE+1, MAX_PORT_RANGE);
    }

    /**
     * Finds an available TCP port
     *
     * @param minPortRange The minimum port range
     * @param maxPortRange The maximum port range
     * @return The available port
     */
    public static int findAvailableTcpPort(int minPortRange, int maxPortRange) {
        check(()-> minPortRange > MIN_PORT_RANGE)
            .orElseFail("Port minimum value must be greater than " + MIN_PORT_RANGE);
        check(()-> maxPortRange >= minPortRange)
                .orElseFail("Max port range must be greater than minimum port range");
        check(()-> maxPortRange <= MAX_PORT_RANGE)
                .orElseFail("Port maximum value must be less than " + MAX_PORT_RANGE);

        int currentPort = nextPort(minPortRange, maxPortRange);
        while(!isTcpPortAvailable(currentPort)) {
            currentPort = nextPort(minPortRange, maxPortRange);
        }
        return currentPort;
    }

    /**
     * Check whether the given TCP port is available
     *
     * @param currentPort The port
     * @return True if it is
     */
    public static boolean isTcpPortAvailable(int currentPort) {
        try {
            ServerSocket serverSocket = ServerSocketFactory
                                            .getDefault()
                                            .createServerSocket(currentPort,1, InetAddress.getByName("localhost"));
            serverSocket.close();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private static int nextPort(int minPortRange, int maxPortRange) {
        int seed = maxPortRange - minPortRange;
        return random.nextInt(seed) + minPortRange;
    }
}