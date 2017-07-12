import java.lang.Exception;
import java.lang.Long;
import java.lang.String;
import java.lang.System;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.util.*;
import java.math.BigInteger;


public class Simulator {
	public static class Node {
		public int id;
		public String virt_addr;
		public int opsize;

		public Node(int id, String virt_addr, int opsize) {
			this.id = id;
			this.virt_addr = virt_addr;
			this.opsize = opsize;
		}
	}

	public static class Edge {
		public int id;
		public int src_id;
		public int dest_id;
		public int taken;
		public String jmp_targ;
		public long inst_cnt;

		public Edge(int id, int src_id, int dest_id, int taken, String jmp_targ, long inst_cnt) {
			this.id = id;
			this.src_id = src_id;
			this.dest_id = dest_id;
			this.taken = taken;
			this.jmp_targ = jmp_targ;
			this.inst_cnt = inst_cnt;
		}
	}

    public static void simulate(InputStream incomingStream, PrintStream outputStream) throws Exception {
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<Edge> edges = new ArrayList<Edge>();

        BufferedReader r = new BufferedReader(new InputStreamReader(incomingStream));
        String line;

        while (true) {
            line = r.readLine();
            if (line == null) {
                break;
            }
            String [] tokens = line.split("\\s+");
			if(tokens[0].equals("BT9_EDGE_SEQUENCE")) {
				//int[] numZ = new int[(edges.size() - 1)];
				//int total_inst = 0;
				while (true) {
					line = r.readLine();
					if (line == null) {
						break;
					}
					tokens = line.split("\\s+");

					if(tokens[0].equals("EOF")){
						break;
					}

					int edgenum = Integer.parseInt(tokens[0]);
					//numZ[edgenum]++;
					Edge tar_edge = edges.get(edgenum);
					Node src_node = nodes.get(tar_edge.src_id);
					Node dest_node = nodes.get(tar_edge.dest_id);

					BigInteger va1 = new BigInteger(src_node.virt_addr.replaceAll("0x", ""), 16);
					BigInteger va2 = new BigInteger(dest_node.virt_addr.replaceAll("0x", ""), 16);
					BigInteger blocksize;	
					
					outputStream.format("0x" + va1.toString(16) + "\n");
					if(tar_edge.taken == 1) {
						va1 = new BigInteger(tar_edge.jmp_targ.replaceAll("0x", ""), 16);
					}
					else {
						BigInteger opsize = BigInteger.valueOf(src_node.opsize);
						va1 = opsize.add(va1);
					}
					blocksize = va2.subtract(va1);
						
					BigInteger inst_size;
					if(tar_edge.inst_cnt == 0) {
						inst_size = BigInteger.ZERO;
						//total_inst++;
					}
					else {
						BigInteger inst_cnt = BigInteger.valueOf(tar_edge.inst_cnt);
						//total_inst += (tar_edge.inst_cnt + 1);
						inst_size = blocksize.divide(inst_cnt);
						if(inst_size.compareTo(BigInteger.valueOf(32)) == 1) {
							inst_size = BigInteger.valueOf(4);
						}
					}
					long counter = tar_edge.inst_cnt;

					if(edgenum == 0) {
						while(counter > 0) {
							BigInteger va3 = va2.subtract(BigInteger.valueOf(counter).multiply(inst_size));
							outputStream.format("0x" + va3.toString(16) + "\n");
							counter--;
						}
					}
					else {
						while(counter > 0) {
							outputStream.format("0x" + va1.toString(16) + "\n");
							va1 = inst_size.add(va1);
							counter--;
						}
					}
				}
				/*for(int z: numZ) {
					outputStream.format(z + "\n");
				}*/
				//outputStream.format(total_inst + "\n");
			}
			else if(tokens[0].equals("NODE")) {
				Node cur_node;
				if(tokens[1].equals("0")) {
					cur_node = new Node(0, "0", 0);
				}
				else{
					int id = Integer.parseInt(tokens[1]);
					int opsize = Integer.parseInt(tokens[5]);	
					cur_node = new Node(id, tokens[2], opsize);
				}
				nodes.add(cur_node);
			}
			else if(tokens[0].equals("EDGE")) {
				int id = Integer.parseInt(tokens[1]);
				int src_id = Integer.parseInt(tokens[2]);
				int dest_id = Integer.parseInt(tokens[3]);
				String jmp_tar = tokens[5];
				long inst_cnt = Long.parseLong(tokens[7]);
				int taken;
				if(tokens[4].equals("N")) {
					taken = 0;
				}
				else {
					taken = 1;
				}
				Edge new_edge = new Edge(id, src_id, dest_id, taken, jmp_tar, inst_cnt);
				edges.add(new_edge);
				//outputStream.format(tokens[9] + "\n");	
			}
        }
    }

    public static void main(String[] args) throws Exception {
        InputStream inputStream = System.in;
        PrintStream outputStream = System.out;
		int edgenum = -1;

        if(args.length >= 1) {
            inputStream = new FileInputStream(args[0]);
        }

        if(args.length >= 2) {
            outputStream = new PrintStream(args[1]);
        }

        Simulator.simulate(inputStream, outputStream);
    }
}
