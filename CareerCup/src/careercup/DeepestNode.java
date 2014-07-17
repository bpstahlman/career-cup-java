/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package careercup;

/**
 *
 * @author stahlmanb
 */
public class DeepestNode {
	static void runDeepestNodeTest() {
		Node tree =
			new Node("1",
				new Node("2",
					new Node("3",
						new Node("4",
							new Node("4.1",
								new Node("4.2",
									new Node("4.3"),
									null),
								null),
							new Node("4.4")),
						new Node("5")),
					new Node("6",
						new Node("7"),
						new Node("8",
							new Node("9"),
							new Node("10",
								null,
								new Node("11"))))),
				new Node("12"));
		MaxCt deepest = tree.findDeepest(0);
		System.out.println("name: " + deepest.node.name + ", depth=" + deepest.depth);

	}
	static class MaxCt {
		Node node;
		int depth;
		MaxCt(Node node, int depth) {
			this.node = node;
			this.depth = depth;
		}
	}
	static class Node {
		String name;
		Node lt, rt;
		Node(String name) {
			this.name = name;
			lt = rt = null;
		}
		Node(String name, Node lt, Node rt) {
			this.name = name;
			this.lt = lt;
			this.rt = rt;
		}
		MaxCt findDeepest(int depth) {
			if (lt == null && rt == null)
				return new MaxCt(this, depth);
			// Non-leaf
			MaxCt ltm = null, rtm = null;
			if (lt != null)
				ltm = lt.findDeepest(depth + 1);
			if (rt != null)
				rtm = rt.findDeepest(depth + 1);
			return ltm == null ? rtm : rtm == null ? ltm : ltm.depth > rtm.depth ? ltm : rtm;
		}
	}
}
