package com.triste.codesearch.pagerank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import com.triste.codesearch.database.Save.Relationships;

/*import SearchInDb.SearchClass;
import SearchInDb.SearchInDb;
import SearchInDb.SetClassPR;
import SearchInDb.ValueInMap;
import SearchInDb.SearchInDb.RelTypes;*/

public class CalClassPRK {
	public static int nodeNumber;
	public static double d = 0.85;
	public static double boundary = 0.0000000003;
	public static List<Long> nodeIdList = new ArrayList<Long>();

	public void setClassNodeList() {
		// SearchInDb.startDb();
		Label label = DynamicLabel.label("classLabel");
		try (Transaction tx = Database.database.beginTx()) {
			for (Node node : Database.database.findNodesByLabelAndProperty(
					label, "type", "class")) {
				nodeIdList.add(node.getId());
				tx.success();
			}

		}
	}

	public Map getGraphMap(List nodeIdList) {
		Map graphMap = new HashMap<Long, List<ValueInMap>>();
		//SearchClass searchClass = new SearchClass();
		ArrayList<Node> resultNodes;
		try (Transaction tx = Database.database.beginTx()) {
			System.out.println(nodeIdList.size() + "\n\n\n\n\n\n");
			for (int i = 0; i < nodeIdList.size(); i++) {
				System.out.println(i);
				Node node = Database.database.getNodeById((long) nodeIdList
						.get(i));
				resultNodes = new ArrayList<Node>();
				for (Relationship iter : node.getRelationships(Relationships.EXTENDS,
						Direction.OUTGOING)) {
					resultNodes.add(iter.getEndNode());
				}
				for (Relationship iter : node.getRelationships(Relationships.IMPLEMENTS,
						Direction.OUTGOING)) {
					resultNodes.add(iter.getEndNode());
				}
				for (int j = 0; j < resultNodes.size(); j++) {
					int index = nodeIdList.indexOf(resultNodes.get(j).getId());
					List<ValueInMap> getList = new ArrayList<ValueInMap>();
					ValueInMap valueInMap = new ValueInMap(i + 1, (double) 1
							/ resultNodes.size());
					if (graphMap.get(index + 1) != null) {
						getList = (List) graphMap.get(index + 1);
						getList.add(valueInMap);
					} else {
						getList.add(valueInMap);
					}
					graphMap.put(index + 1, getList);

				}
			}
			tx.success();
		}
		return graphMap;

	}

	public void setPK() {
		ValueInMap valueInMap;
		setClassNodeList();
		Map<Long, List<ValueInMap>> graphMap = getGraphMap(nodeIdList);
		final int nodeNumber = nodeIdList.size();
		double p1[] = new double[nodeNumber + 1];
		double p2[] = new double[nodeNumber + 1];
		for (int i = 1; i <= nodeNumber; i++) {
			p2[i] = (double) 1 / nodeNumber;
		}
		for (int i = 1; i <= nodeNumber; i++) {
			p1[i] = 0;
		}
		while (check(p1, p2)) {
			for (int i = 1; i <= nodeNumber; i++) {
				p1[i] = p2[i];
			}
			for (int i = 1; i <= nodeNumber; i++) {
				p2[i] = 0;
				if (graphMap.get(i) != null) {
					List getList = (List) graphMap.get(i);
					for (int j = 0; j < getList.size(); j++) {
						valueInMap = (ValueInMap) getList.get(j);
						p2[i] += p1[valueInMap.column] * (valueInMap.value);
					}
				}

				p2[i] = d * p2[i] + (1 - d) / nodeNumber;
			}
		}
		for (int i = 1; i <= nodeNumber; i++) {
			System.out.println(p2[i]);
		}
		setPRInDb(p2, nodeIdList);

	}

	public static boolean check(double[] p1, double[] p2) {
		boolean flag = false;
		final int nodeNumber = nodeIdList.size();
		for (int i = 1; i <= nodeNumber; i++) {
			if (((p1[i] - p2[i]) > boundary)
					|| ((p1[i] - p2[i]) < (0 - boundary))) {
				flag = true;
			}
		}
		return flag;

	}

	public void setPRInDb(double[] p2, List<Long> nodeIdList) {
		try (Transaction tx = Database.database.beginTx()) {
			for (int i = 0; i < nodeIdList.size(); i++) {
				Node node = Database.database.getNodeById((long) nodeIdList
						.get(i));
				node.setProperty("pageRank", p2[i + 1]);
			}
			tx.success();
		}
		//Datebase
	}

	public static void main(String argv[]) {
		/*SetClassPR setClassPR = new SetClassPR();
		setClassPR.setPK();
		System.out.println(nodeIdList.size());*/
	}
}
