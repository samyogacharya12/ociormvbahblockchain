OciorMVBAH (Optimized Common-coin-based Iterative Ordered Randomized Multi-Valued Byzantine Agreement with Hash commitments) is a robust, hash-based asynchronous multi-valued Byzantine agreement protocol. 
It extends the foundational ideas of Multi-Valued Byzantine Agreement (MVBA) and enhances them by incorporating erasure coding, Merkle tree commitments, and a cryptographic common coin for randomized progress.
The protocol ensures agreement among non-faulty nodes on a single valid proposal out of many, despite adversarial behavior and network asynchrony.
OciorMVBAH is composed of multiple interacting subprotocols:
ACIDh for asynchronous commit, information dispersal, and validation via hash commitments
DRh for data retrieval with hash-verified fragments
ABBBA and ABBA for asynchronous binary agreement
Common Coin for randomness to break symmetry and ensure probabilistic liveness
Election Protocol for randomized leader selection and coordination
The design is modular, fault-resilient, and tailored for settings with 
n≥3t+1, where 
t is the maximum number of Byzantine nodes. The use of Merkle trees and verifiable commitments allows for succinct data verification, while the integration of erasure coding enables efficient message dispersal and reconstruction
with minimal
overhead.

In this project we have four independent nodes and we can start the nodes in the terminal through the following command

./gradlew runZMQClientA --args="6001 127.0.0.1:6002 127.0.0.1:6003 127.0.0.1:6004 127.0.0.1:7000"
./gradlew runZMQClientB --args="6002 127.0.0.1:6001 127.0.0.1:6003 127.0.0.1:6004 127.0.0.1:7000"
./gradlew runZMQClientC --args="6003 127.0.0.1:6001 127.0.0.1:6002 127.0.0.1:6004 127.0.0.1:7000"
./gradlew runZMQClientD --args="6004 127.0.0.1:6001 127.0.0.1:6002 127.0.0.1:6003 127.0.0.1:7000"
