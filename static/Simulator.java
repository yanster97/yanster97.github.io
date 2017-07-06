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



public class Simulator {
	public static class Node {
		public int id;
		public String virt_addr;

		public Node(int id, String virt_addr) {
			this.id = id;
			this.virt_addr = virt_addr;
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
					Edge tar_edge = edges.get(edgenum);
					//outputStream.format(tar_edge.src_id + " " + tar_edge.dest_id + " " + tar_edge.jmp_targ + " " + tar_edge.taken + "\n");
					Node src_node = nodes.get(tar_edge.src_id);
					Node dest_node = nodes.get(tar_edge.dest_id);

					int va1 = Integer.parseInt(src_node.virt_addr.replaceAll("0x", ""), 16);
					int va2 = Integer.parseInt(dest_node.virt_addr.replaceAll("0x", ""), 16);
					int blocksize;

					if(tar_edge.taken == 1) {
						outputStream.format("0x" + Integer.toHexString(va1) + "\n");
						va1 = Integer.parseInt(tar_edge.jmp_targ.replaceAll("0x", ""), 16);
					}
					blocksize = va2 - va1;
						
					long inst_size;
					if(tar_edge.inst_cnt == 0) {
						inst_size = 0;
					}
					else {
						inst_size = blocksize / tar_edge.inst_cnt;
					}
					long counter = tar_edge.inst_cnt;

					while(counter > 0)
					{
						outputStream.format("0x" + Integer.toHexString(va1) + "\n");
						va1 += inst_size;
						counter--;
					}
				}
			}
			else if(tokens[0].equals("NODE")) {
				Node cur_node;
				if(tokens[1].equals("0")) {
					cur_node = new Node(0, "0");
				}
				else{
					int id = Integer.parseInt(tokens[1]);
					cur_node = new Node(id, tokens[2]);
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
			}
			/*else{
            	for(String token: tokens) {
                	outputStream.format(token + " ");
            	}
			}
			outputStream.format("\n");*/
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
