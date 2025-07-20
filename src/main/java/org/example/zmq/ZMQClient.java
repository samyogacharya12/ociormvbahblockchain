package org.example.zmq;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.NodeDto;
import org.example.model.Share;
import org.example.utilities.AcidhProtocol;
import org.example.utilities.CoinCommitReveal;
import org.example.utilities.LeaderProtocal;
import org.example.utilities.ObjectParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.example.utilities.CoinCommitReveal.generateRandomBytes;
import static org.example.utilities.ObjectParser.mapToHexString;
import static org.example.utilities.ObjectParser.parseValidHexMap;

public class ZMQClient {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ZMQClient.class);
        NodeDto nodeDto = new NodeDto();
        AtomicReference<Map<String, byte[]>> commits = new AtomicReference<>();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        List<NodeDto> nodeDtos = new ArrayList<>();
        if (args.length < 2) {
            System.out.println("Usage: java ChatNode <pub-port> <peer-pub-addr1> <peer-pub-addr2> ...");
            return;
        }
        int pubPort = Integer.parseInt(args[0]);

        try (ZContext context = new ZContext()) {
            ZMQ.Socket pub = context.createSocket(ZMQ.PUB);
            pub.bind("tcp://*:" + pubPort);
            System.out.println("Publishing on port " + pubPort);
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
                    if (ObjectParser.isListOfNodeDto(msg)) {
                        try {
                            List<NodeDto> nodes = objectMapper.readValue(msg, new TypeReference<>() {
                            });
                            nodes.forEach(nodeDto1 -> {
                                if (!nodeDto1.getName().equalsIgnoreCase(nodeDto.getName())) {
                                    nodeDtos.add(nodeDto1);
                                }
                            });
                        } catch (Exception exception) {
                            logger.error("Error {}", exception);
                        }
                    } else if(parseValidHexMap(msg)) {
                        logger.info("pushed data {}", msg);
                        final Map<String, byte[]> parseStringToByteMap = ObjectParser.parseStringToByteMap(msg);
                        commits.set(parseStringToByteMap);
                        logger.info("commits");
                    }

                    System.out.println("\n[Received] " + msg + "\n> ");
                }
            });
            receiver.start();
            nodeDto.setId(ObjectParser.generate16BitUUID());
            System.out.println("your node id is  " + nodeDto.getId());
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter Name of your node ");
            String nodeName = scanner.nextLine();
            nodeDto.setName(nodeName);
            System.out.println("your node name is " + nodeDto.getName());
            System.out.println("Enter Message ");
            String message = scanner.nextLine();
            nodeDto.setMessage(message);
            nodeDto.setRi(generateRandomBytes());
            System.out.println("message is " + nodeDto.getMessage());
            if (!nodeDtos.contains(nodeDto)) {
                nodeDtos.add(nodeDto.clone());
            }
            try {
                String json = objectMapper.writeValueAsString(nodeDtos);
                pub.send(json);
                List<Map<String, Share>> maps = AcidhProtocol.startAcidhInstances(nodeDto);
                if (Objects.nonNull(maps) && !maps.isEmpty()) {
                    String shareJson = objectMapper.writeValueAsString(maps);
                    pub.send(shareJson);
                }
                String voteMsg = objectMapper.writeValueAsString(AcidhProtocol.voteMessage);
                pub.send(voteMsg);
                logger.info(voteMsg);
                String locMsg = objectMapper.writeValueAsString(AcidhProtocol.lockMessage);
                pub.send(locMsg);
                logger.info(locMsg);
                String readyMessage = objectMapper.writeValueAsString(AcidhProtocol.readyMessage);
                pub.send(readyMessage);
                logger.info(readyMessage);
                String finishMessage = objectMapper.writeValueAsString(AcidhProtocol.finishMessage);
                pub.send(finishMessage);
                logger.info(finishMessage);
                String electionMessage = objectMapper.writeValueAsString(AcidhProtocol.electionMessage);
                pub.send(electionMessage);
                logger.info(electionMessage);
                String confirmMessage = objectMapper.writeValueAsString(AcidhProtocol.confirmMessage);
                pub.send(confirmMessage);
                logger.info(confirmMessage);
                Set<String> seen = new HashSet<>();
                List<NodeDto> nodeDtoList = nodeDtos.stream().filter(p -> seen.add(p.getName())).toList();
                nodeDtoList.forEach(nodeDto1 -> {
                    if(maps!=null){
                     maps.forEach(stringShareMap -> {
                         Share share=stringShareMap.get(nodeDto1.getId());
                         if(Objects.nonNull(share) && Objects.nonNull(share.getCommitment())) {
                             nodeDto1.setCommitment(share.getCommitment().getBytes());
                         }
                     });
                    }
                });
                String shareJson = objectMapper.writeValueAsString(nodeDtoList);
                pub.send(shareJson);
                if(Objects.isNull(commits.get())) {
                    logger.info("commitment is not present");
                    Map<String, byte[]> commit = CoinCommitReveal.commitPhase(nodeDtoList);
                    String serialized = mapToHexString(commit);
                    pub.send(serialized);
                } else {
                    logger.info("leader {}", commits.get());
                   int leader= LeaderProtocal.getLeader(commits.get(), nodeDtoList);
                   logger.info("leader {}", leader);
                }
            } catch (Exception exception) {
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
