package org.example.utilities;

import lombok.experimental.UtilityClass;
import org.example.model.NodeDto;

import java.util.List;
import java.util.Map;
import static org.example.utilities.CoinCommitReveal.getCoinIndex;
import static org.example.utilities.CoinCommitReveal.revealAndCombine;

@UtilityClass
public class LeaderProtocal {

    public static Integer getLeader(Map<String, byte[]> commits, List<NodeDto> nodes){
        byte[] combined = revealAndCombine(nodes, commits);
        int selectedIndex = getCoinIndex(combined, nodes.size());
        System.out.println("🎯 Selected leader: Node " + nodes.get(selectedIndex).getId());
        return selectedIndex;
    }
}
