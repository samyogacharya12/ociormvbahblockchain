package org.example.zmq;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ZMQServer {
    public static void main(String[] args) {
        int pubPort = 7000; // Server broadcasts on this port

        try (ZContext context = new ZContext()) {
            ZMQ.Socket pub = context.createSocket(ZMQ.PUB);
            pub.bind("tcp://*:" + pubPort);
            System.out.println("Broadcast Server running at tcp://*:" + pubPort);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("[Server] > ");
                String msg = scanner.nextLine();
                if ("exit".equalsIgnoreCase(msg)) break;

                pub.send("[SERVER] " + msg);
                System.out.println("[Broadcasted] " + msg);
            }

            pub.close();
        }
    }
}
