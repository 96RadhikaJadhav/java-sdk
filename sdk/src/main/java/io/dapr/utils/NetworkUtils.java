/*
 * Copyright (c) Microsoft Corporation.
 * Licensed under the MIT License.
 */

package io.dapr.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Utility methods for network, internal to Dapr SDK.
 */
public final class NetworkUtils {

  private NetworkUtils() {
  }

  /**
   * Tries to connect to a socket, retrying every 1 second.
   * @param host Host to connect to.
   * @param port Port to connect to.
   * @param timeoutInMilliseconds Timeout in milliseconds to give up trying.
   * @throws InterruptedException If retry is interrupted.
   */
  public static void waitForSocket(String host, int port, int timeoutInMilliseconds) throws InterruptedException {
    long started = System.currentTimeMillis();
    Retry.callWithRetry(() -> {
      try {
        try (Socket socket = new Socket()) {
          // timeout cannot be negative.
          // zero timeout means infinite, so 1 is the practical minimum.
          int remainingTimeout = (int) Math.max(1, timeoutInMilliseconds - (System.currentTimeMillis() - started));
          socket.connect(new InetSocketAddress(host, port), remainingTimeout);
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }, timeoutInMilliseconds);
  }
}
