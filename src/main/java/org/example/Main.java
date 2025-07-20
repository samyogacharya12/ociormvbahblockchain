package org.example;

import org.example.model.NodeDto;
import org.example.utilities.AcidhProtocol;
import org.example.utilities.CoinCommitReveal;
import org.example.utilities.ObjectParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.example.utilities.ObjectParser.*;

@SpringBootApplication

public class Main {
    public static void main(String[] args) {
            SpringApplication.run(Main.class, args);
    }
}