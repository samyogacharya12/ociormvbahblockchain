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
        System.out.println(" we are inside this method " +nodes.size());
        byte[] combined = revealAndCombine(nodes, commits);
        System.out.println(" we are inside this method " +combined);
        int selectedIndex = getCoinIndex(combined, nodes.size());
        System.out.println(" we are inside selected index " +selectedIndex);
        System.out.println("🎯 Selected leader: Node " + nodes.get(selectedIndex).getId());
        return selectedIndex;
    }
}
