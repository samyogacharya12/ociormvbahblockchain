package org.example.zmq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.NodeDto;
import org.example.model.Share;
import org.example.utilities.AcidhProtocol;
import org.example.utilities.ObjectParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ZMQClient {

    public static void main(String[] args) {
        Logger logger= LoggerFactory.getLogger(ZMQClient.class);
        NodeDto nodeDto=new NodeDto();
        ObjectMapper objectMapper=new ObjectMapper();
        List<NodeDto> nodeDtos=new ArrayList<>();
        if (args.length < 2) {
            System.out.println("Usage: java ChatNode <pub-port> <peer-pub-addr1> <peer-pub-addr2> ...");
            return;
        }
        AtomicInteger nodeNumber= new AtomicInteger();
        int pubPort = Integer.parseInt(args[0]);

        try (ZContext context = new ZContext()) {
            // PUB: to broadcast messages
            ZMQ.Socket pub = context.createSocket(ZMQ.PUB);
            pub.bind("tcp://*:" + pubPort);
            System.out.println("Publishing on port " + pubPort);

            // SUB: to receive messages
            ZMQ.Socket sub = context.createSocket(ZMQ.SUB);
            sub.subscribe("".getBytes()); // subscribe to all messages

            for (int i = 1; i < args.length; i++) {
                String peer = args[i];
                sub.connect("tcp://" + peer);
                System.out.println("Connected to peer PUB at tcp://" + peer);
            }

            Thread receiver = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    String msg = sub.recvStr();
                    if (msg.startsWith("[SERVER]")) {
                        String cleanMsg = msg.substring(8).trim();
                        if(ObjectParser.isNumeric(cleanMsg)) {
                            nodeNumber.set(Integer.parseInt(cleanMsg));

                        }
                    } else if(ObjectParser.isJsonList(msg)){
                        try {
                            List<NodeDto> nodes = objectMapper.readValue(msg, new
                                            TypeReference<>() {
                                            });
                                     nodes.forEach(nodeDto1 -> {
                                         if(!nodeDto1.getName()
                                                 .equalsIgnoreCase(nodeDto.getName())){
                                             nodeDtos.add(nodeDto1);
                                         }
                                     });
                        } catch (Exception exception){
                            logger.error("Error {}", exception);
                        }
                    }

                    System.out.println("\n[Received] " + msg + "\n> ");
                }
            });
            
            receiver.start();
            nodeDto.setId(ObjectParser.generate16BitUUID());
            System.out.println("your node id is  " +nodeDto.getId());
            // Input loop: Send messages
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter Name of your node ");
            String nodeName = scanner.nextLine();
            nodeDto.setName(nodeName);
            System.out.println("your node name is " +nodeDto.getName());
            System.out.println("Enter Message ");
            String message = scanner.nextLine();
            nodeDto.setMessage(message);
            System.out.println("message is " +nodeDto.getMessage());
            if(!nodeDtos.contains(nodeDto)) {
                nodeDtos.add(nodeDto.clone());
            }
            try {
                String json = objectMapper.writeValueAsString(nodeDtos);
                pub.send(json);
                List<Map<String, Share>> maps=AcidhProtocol.startAcidhInstances(nodeDto);
                if(Objects.nonNull(maps) && !maps.isEmpty()) {
                    String shareJson = objectMapper.writeValueAsString(maps);
                    pub.send(shareJson);
                }
                String voteMsg=objectMapper.writeValueAsString(AcidhProtocol.voteMessage);
                pub.send(voteMsg);
                logger.info(voteMsg);
                String locMsg=objectMapper.writeValueAsString(AcidhProtocol.lockMessage);
                pub.send(locMsg);
                logger.info(locMsg);
                String readyMessage=objectMapper.writeValueAsString(AcidhProtocol.readyMessage);
                pub.send(readyMessage);
                logger.info(readyMessage);
                String finishMessage=objectMapper.writeValueAsString(AcidhProtocol.finishMessage);
                pub.send(finishMessage);
                logger.info(finishMessage);
                String electionMessage=objectMapper.writeValueAsString(AcidhProtocol.electionMessage);
                pub.send(electionMessage);
                logger.info(electionMessage);
                String confirmMessage=objectMapper.writeValueAsString(AcidhProtocol.confirmMessage);
                pub.send(confirmMessage);
                logger.info(confirmMessage);
                Set<String> seen = new HashSet<>();
                List<NodeDto> nodeDtoList = nodeDtos.stream()
                        .filter(p -> seen.add(p.getName()))
                        .toList();
                logger.info("the sze of the node is {}", nodeDtoList.size());
                for (NodeDto nodeDto1: nodeDtoList){
                    System.out.println(" Name " +nodeDto1.getName());
                }
            } catch (Exception exception){
                 logger.error(" Error while writing {} ", exception);
            }
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                if ("exit".equalsIgnoreCase(input)) break;
                System.out.println(input);
                pub.send(input);
            }

            receiver.interrupt();
            pub.close();
            sub.close();
        }
    }
}
