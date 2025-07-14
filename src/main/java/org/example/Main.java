package org.example;

import org.example.model.NodeDto;
import org.example.utilities.AcidhProtocol;
import org.example.utilities.CoinCommitReveal;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.example.utilities.ObjectParser.*;

@SpringBootApplication

public class Main {
    public static void main(String[] args) {
        List<NodeDto> nodeDtos=new ArrayList<>();
        NodeDto nodeDto=new NodeDto();
        nodeDto.setId("A01");
        nodeDto.setName("node1");
        nodeDto.setMessage("1");
        nodeDto.setCommitment("123".getBytes());
        nodeDtos.add(nodeDto);
        NodeDto nodeDto1=new NodeDto();
        nodeDto1.setId("Aa02");
        nodeDto1.setName("node2");
        nodeDto1.setMessage("1");
        nodeDto1.setCommitment("456".getBytes());
        nodeDtos.add(nodeDto1);
        Map<String, byte[]> commit = CoinCommitReveal.commitPhase(nodeDtos);
        String serialized = mapToHexString(commit);
        if(parseValidHexMap(serialized)) {
            Map<String, byte[]> stringMap = parseStringToByteMap(serialized);
        }
        SpringApplication.run(Main.class, args);
    }
}