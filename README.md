# Distributed Snapshot System

## Description

This is a Java project that simulates a **distributed system** made up of multiple connected **nodes**. These nodes exchange messages and support taking a **snapshot** of the current system state at any time.

The system supports two snapshot algorithms:

- **Acharya-Badrinath (AB)**
- **Alagar-Venkatesan (AV)**

The algorithm used is selected in the configuration file before starting the system.

## Features

- **Asynchronous communication** – messages can arrive out of order (non-FIFO).
- **Flexible network topology** – nodes are connected based on a custom graph (not all-to-all).
- **Bitcake transfers** – each node holds and exchanges bitcake values with neighbors.
- **Manual snapshot triggering** – snapshots can be started from any node, one at a time.
- **Config-based setup** – all node settings and connections are defined in a config file.
- **Simulated network delay** – artificial delays are added to outgoing messages.
- **Scripted execution** – each node can read commands from a text file and write output to its own file.

## Configuration

The configuration file includes:

- Number of nodes
- Port number for each node
- List of neighbors for each node
- Snapshot algorithm to use (`ab` or `av`)

**Example:**
```properties
nodes=3
node0.port=5000
node0.neighbors=1,2
snapshot=ab
```

## How to Run
Configure all nodes in the configuration file.

Start each node using CLI or a script file.

Interact with nodes using commands or script files.

Trigger a snapshot from any node (if no snapshot is already running).

Check each node’s output to see the recorded snapshot state.

## Notes
Only one snapshot can run at a time. If you try to start another while one is in progress, the system will report an error.

Messages are only allowed between nodes that are listed as neighbors.

All nodes run locally (localhost) on different ports.
