package org.example.utilities;

import lombok.extern.slf4j.Slf4j;
import org.example.model.NodeDto;
import org.example.model.Share;
import org.example.zmq.ShareUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AcidhProtocol {


    private final static Logger logs=LoggerFactory.getLogger(AcidhProtocol.class);
    static final int N = 4;
    static final int T = 1;

    static int quorum = N - T;
    static final int confirmQuorum = (2 * T) + 1;

    // Protocol state maps
    static Map<Integer, Integer> L_lock = new HashMap<>();
   static Map<Integer, Integer> R_ready = new HashMap<>();
    static Map<Integer, Integer> F_finish = new HashMap<>();
   static Map<String, Share> S_shares = new HashMap<>();
   static Map<String, Integer> H_hash = new HashMap<>();

    static Map<String, Integer> votes = new HashMap<>();
    public   static   Map<String, String> voteMessage = new HashMap<>();
    static Map<String, Integer> locks = new HashMap<>();

   public   static Map<String, String> lockMessage = new HashMap<>();
    static Map<String, Integer> readies = new HashMap<>();

   public static Map<String, String> readyMessage = new HashMap<>();
    static Map<String, Integer> finishes = new HashMap<>();

   public static Map<String, String> finishMessage = new HashMap<>();
    static Map<String, Integer> elections = new HashMap<>();

    public  static Map<String, String> electionMessage = new HashMap<>();
    static Map<String, Integer> confirms = new HashMap<>();

    public static Map<String, String> confirmMessage = new HashMap<>();


    public static List<Map<String, Share>> startAcidhInstances(NodeDto nodeDto){
        try {
            byte[][] shares = ShareUtils.encodeMessageWithReedSolomon(nodeDto.getMessage().getBytes(),
                    N, T);
            List<byte[]> shareList = new ArrayList<>(Arrays.asList(shares));
            MerkleTree tree = new MerkleTree(shareList);
            return processAcidhInstances(nodeDto, shares, tree);

        } catch (Exception exception){
            logs.error("startAcidhInstances {}", exception);
        }

        return null;
    }

    public static List<Map<String, Share>> processAcidhInstances(NodeDto nodeDto,
                                                    byte[][] shares,
                                                    MerkleTree tree){
        Logger logs=LoggerFactory.getLogger(AcidhProtocol.class);
        List<Map<String, Share>> listOfMaps = new ArrayList<>();
        try {
            Map<String, Share> shareMap = new HashMap<>();
            Share share = new Share();
            String commitment = ObjectParser.bytesToHex(tree.buildRoot());
            share.setCommitment(commitment);
            for (int j = 1; j <= N; j++) {
                byte[] yj = shares[j-1];
                if(Objects.nonNull(yj)) {
                    List<byte[]> proof = tree.getProof(j-1);
                    if (Objects.nonNull(proof)) {
                        share.setProof(proof);
                        share.setShare(yj);
                        shareMap.put(nodeDto.getId(), share);
                        listOfMaps.add(shareMap);
                        sendShare(nodeDto.getId(), commitment, share.getShare(), proof, j
                                , nodeDto);
                    }
                }
            }
        } catch (Exception exception){
             logs.error("processAcidhInstances {}",exception);
        }
        return listOfMaps;
    }

    public static void sendShare(String id, String commitment, byte[] share,
                                 List<byte[]> proof, int nodeId, NodeDto nodeDto) throws Exception {
        // VcVerify step
        boolean verified = MerkleTree.verifyProof(nodeId - 1, share, proof,
                ObjectParser.hexToBytes(commitment));

        if (verified) {
            nodeDto.setCommitment(commitment.getBytes());
            S_shares.put(id, new Share(commitment, share, proof));
            H_hash.put(commitment, nodeId);
            sendVote(id, commitment, nodeDto);
        }
    }

    static void sendVote(String id, String commitment,
                  NodeDto nodeDto) {
        votes.put(commitment, votes.getOrDefault(commitment, 0) + 1);
        voteMessage.put(nodeDto.getId()+ " VOTE ", votes.toString());
        logs.info("Node Number {}",N);
        logs.info("votes {}", votes.get(commitment));
        if (votes.get(commitment) >= quorum) {
           sendLock(id, commitment,nodeDto);
        }
    }

    static void sendLock(String id, String commitment, NodeDto nodeDto) {
        locks.put(commitment, locks.getOrDefault(commitment, 0) + 1);
        lockMessage.put(nodeDto.getName()+ " LOCK ", locks.toString());
        logs.info("Node Number {}",N);
        logs.info("locks {}", locks.get(commitment));
        if (H_hash.containsKey(commitment)) {
            int jStar = H_hash.get(commitment);
            L_lock.put(jStar, 1);
            sendReady(id, commitment, nodeDto);
        }
    }
   static void sendReady(String id, String commitment ,NodeDto nodeDto) {
        readies.put(commitment, readies.getOrDefault(commitment, 0) + 1);
        readyMessage.put(nodeDto.getName()+ " READY ", locks.toString());
       logs.info("Node Number {}",N);
       logs.info("Readies {}", readies.get(commitment));
       if (H_hash.containsKey(commitment)) {
            int jStar = H_hash.get(commitment);
            R_ready.put(jStar, 1);
            sendFinish(id, commitment, jStar, nodeDto);
        }
    }

    // ACID-finish phase: send ("FINISH", ID) to proposer
    static void sendFinish(String id, String commitment, int jStar ,NodeDto nodeDto) {
        finishes.put(commitment, finishes.getOrDefault(commitment, 0) + 1);
        finishMessage.put(nodeDto.getName()+ " FINISH ", finishes.toString());
        logs.info("Finish {}", finishMessage.get(commitment));
        if (H_hash.containsKey(commitment)) {
            F_finish.put(jStar, 1);
            sendElection(id, commitment, nodeDto);
        }
    }

    // Vote for election phase
    static void sendElection(String id, String commitment ,NodeDto nodeDto) {
        elections.put(commitment, elections.getOrDefault(commitment, 0) + 1);
        electionMessage.put(nodeDto.getName()+ " ELECTION ", elections.toString());
        logs.info("Election {}", electionMessage.get(commitment));
        if (H_hash.containsKey(commitment)) {
            sendConfirm(id, commitment, nodeDto);
        }
    }

    // Confirm phase
   static void sendConfirm(String id, String commitment ,NodeDto nodeDto) {
        confirms.put(commitment, confirms.getOrDefault(commitment, 0) + 1);
       logs.info("Confirm {}", electionMessage.get(commitment));
        if (confirms.get(commitment) >= confirmQuorum) {
            confirmMessage.put(nodeDto.getName()+ " CONFIRM ", confirms.toString());
        }
    }








}
