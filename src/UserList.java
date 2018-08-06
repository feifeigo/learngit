//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

public class UserList {
//    private static Logger logger = LoggerFactory.getLogger(UserList.class);
    private float alpha = 0.75f;
    private Map<String, UserInfo> userList; // ID map userInformation
    private final static int IN_DEGREE = 1;
    private final static int OUT_DEGREE = 2;
    private int index = 3; //对应于论文中公式2-9的伽马
    private int walk_step = 4;
    //java.text.DecimalFormat df = new java.text.DecimalFormat("0.00000");

    public UserList() {
        userList = new LinkedHashMap<String, UserInfo>();   //map,userID：UserInfo
    }

     LinkedList<String> AllUser() {
        Collection<String> keyset = userList.keySet();
        LinkedList<String> list = new LinkedList<String>(keyset);
        // System.out.println(list);
        return list;
    }

    private void setUserList(Map<String, UserInfo> userList) {
        this.userList = userList;
    }

    UserInfo getUser(String id) {
        return userList.get(id);
    }

    private void addUser(String key, UserInfo value) {
        userList.put(key, value);
    }

    public boolean containsUser(int key) {
        return userList.containsKey(key);
    }

    public void checkSequence(LinkedList<String> list, int type) {
        int i = 0;
        if ((type == OUT_DEGREE)
                && (getUser(list.getFirst()).out_degree < getUser(list.get(i)).out_degree)) { // ����˳��
            UserInfo node1 = getUser(list.getFirst());
            i++;
            while (node1.out_degree < getUser(list.get(i)).out_degree) {

                if (i == list.size())
                    break;
                i++;
            }
            list.add(i, node1.ID);
            list.removeFirst();
        }
        if ((type == IN_DEGREE)
                && (getUser(list.getFirst()).i_degree < getUser(list.get(i)).i_degree)) { // ����˳��
            UserInfo node1 = getUser(list.getFirst());
            i++;
            while (node1.i_degree < getUser(list.get(i)).i_degree) {
                if (i == list.size()) {
                    break;
                }
                i++;
            }
            list.add(i, node1.ID);
            list.removeFirst();
        }

    }


     String getFirstNodeID(LinkedList<String> list) { // 取得序列中的第一个节点
        if (!list.isEmpty()) {
            return list.getFirst();
        } else return null;
    }

    // create userList
    public void createUserList(File fileName) throws IOException {
//        logger.debug("Reading text file " + fileName + "...");
        String pltLine;
        String[] elementStrings;
        RandomAccessFile plt = new RandomAccessFile(fileName, "r");
        int num = 0;
        while ((pltLine = plt.readLine()) != null) {
            elementStrings = pltLine.split(" ");
            addUser(elementStrings[0], new UserInfo(elementStrings[0], num));
            num++;
        }
        try {
            plt.close();
            System.out.println("创建用户列表成功");
        } catch (IOException e) {
            // TODO: handle exception while trying to close a
            e.printStackTrace();
        }
        setUserList(userList);
    }

     void createAttribute(File fileName) throws IOException {
//        logger.debug("Reading text file " + fileName + "...");
        String pltLine;
        String[] elementStrings;
        File parh = fileName.getParentFile();
        File file = new File(parh + "/realAttribute.txt");
        RandomAccessFile plt = new RandomAccessFile(fileName, "r");
        while ((pltLine = plt.readLine()) != null) {
            elementStrings = pltLine.split(" ");
            if (userList.containsKey(elementStrings[0])) {
                getUser(elementStrings[0]).addAttri(elementStrings[1]);
            }


        }
        try {
            plt.close();
            // writer.close();
            System.out.println("读取用户属性列表成功");

        } catch (IOException e) {
            // TODO: handle exception while trying to close a
            // *.plt
            e.printStackTrace();
        }
    }

    public void createAdjMember(File fileName, boolean directer) throws IOException {
//        logger.debug("Reading text file " + fileName + "...");
        String pltLine;
        String[] elementStrings;
        RandomAccessFile plt = new RandomAccessFile(fileName, "r");
        if (directer) {
            System.out.println("direcedGraph");
            while ((pltLine = plt.readLine()) != null) {
                elementStrings = pltLine.split(" ");
                getUser(elementStrings[0]).addAdjOut(elementStrings[1]);
                getUser(elementStrings[1]).addAdjIn(elementStrings[0]);

            }
        } else {
            System.out.println("unDirecedGraph");
            while ((pltLine = plt.readLine()) != null) {
                elementStrings = pltLine.split(" ");
                getUser(elementStrings[0]).addAdjOut(elementStrings[1]);
                getUser(elementStrings[0]).addAdjIn(elementStrings[1]);
                getUser(elementStrings[1]).addAdjOut(elementStrings[0]);
                getUser(elementStrings[1]).addAdjIn(elementStrings[0]);
            }
        }

        try {
            plt.close();
            // writer.close();


        } catch (IOException e) {
            // TODO: handle exception while trying to close a
            // *.plt
            e.printStackTrace();
        }
    }

 //Specify the classification attributes and generate the attribute file
    public void generateAttributeFile(File path, String fileName) throws IOException {
        LinkedList<String> group = new LinkedList<String>(); //指定已有属性
        LinkedList<String> groupInfo = new LinkedList<String>();
        group.add("30");
        group.add("82");
        group.add("81");
        group.add("23");
        group.add("367");
        group.add("86");
        group.add("83");
        group.add("28");
        group.add("26");
        group.add("77");
        group.add("60");
        group.add("326");
        group.add("79");

        for (String user : AllUser()) {
            String context = user + " ";
            UserInfo u = getUser(user);
            for (String s : group) {
                if (u.Affiliation.contains(s))
                    context += "1" + " ";
                else context += "0" + " ";
            }
            groupInfo.add(context);

        }
        FileUtil.writeTextFile(path + fileName, groupInfo);
    }

    // 用户的邻接列表


    private void count_n_hop_neighbor(Set<String> n_hop_neighbor, UserInfo u, int hop) {
        for (int i = 1; i < hop; i++) {
//            System.out.println(u.AllHip1User());

            for (String neighbor : u.AllHip1User()) {
                n_hop_neighbor.addAll(getUser(neighbor).AllHip1User());
//                System.out.println(n_hop_neighbor);
//                System.out.println(n_hop_neighbor.size());
            }
        }
    }

    public float[][] mmltiple(float[][] a, float[][] b) {
        float[][] result = new float[a.length][b[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                for (int k = 0; k < a[0].length; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    private float[][] add(float[][] a, float[][] b) {
        float[][] result = new float[a.length][b[0].length];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                result[i][j] = a[i][j] + b[i][j];
            }
        }
        return result;
    }

    private void LRW(UserInfo u) {
        Set<String> n_hop_neighbor = new TreeSet<>();
//        System.out.println(u.AllHip1User());
        for (String a : u.AllHip1User())
            n_hop_neighbor.add(a);
//        System.out.println(n_hop_neighbor);
//        System.out.println(n_hop_neighbor.size());
        count_n_hop_neighbor(n_hop_neighbor, u, walk_step); //get the n_hop_neighbor
        Map<Integer, String> seq2id = new HashMap<>();
        Map<String, Integer> id2seq = new HashMap<>();
        int seq = 0;
//        System.out.println(n_hop_neighbor);
        for (String user : n_hop_neighbor) {
            seq2id.put(seq, user);
//            System.out.println(seq2id.get(seq));
            id2seq.put(user, seq++);
        }
//        System.out.println(seq2id);
//        System.out.println(seq2id.size());
//        System.out.println(id2seq);
//        System.out.println(id2seq.size());
//        System.out.println(n_hop_neighbor);
//        System.out.println(n_hop_neighbor.size());
        int n_hop_neighbor_size = n_hop_neighbor.size();
        float[][] transMatrix = new float[n_hop_neighbor_size][n_hop_neighbor_size];
        float[][] simMatrix = new float[n_hop_neighbor_size][n_hop_neighbor_size];
        float[][] tempMatrix= new float[n_hop_neighbor_size][n_hop_neighbor_size] ;

        for (int row = 0; row < n_hop_neighbor_size; row++) {   //initial 1-step transMatrix and simMatrix
//            System.out.println("seq2id.get"+row+ seq2id.get(row));
            for (int col = 0; col < n_hop_neighbor_size; col++) {
//                System.out.println("self.getUser(seq2id.get(i))"+getUser(seq2id.get(row)));
                simMatrix[row][col] = 0;
                tempMatrix[row][col] = 0;
                if (row == col)
                    transMatrix[row][col] = 1;

                else if (getUser(seq2id.get(row)).AllHip1User().contains(seq2id.get(col)))
                    transMatrix[row][col] = getUser(seq2id.get(row)).get1_simValue(seq2id.get(col));
                else
                    transMatrix[row][col] = 0;
//                row=34;
//                System.out.println("getUser(seq2id.get("+row+")).AllHip1User()"+getUser(seq2id.get(row)).AllHip1User());

//               if (getUser(seq2id.get(row)).AllHip1User().contains(seq2id.get(col))) {
//
////                   System.out.println("seq2id.get(row)"+seq2id.get(row));
////                   System.out.println("getUser(seq2id.get(row)).id"+getUser(seq2id.get(row)).ID);
////                   System.out.println("col"+col);
////                   System.out.println("seq2id.get(col)"+seq2id.get(col));
//                   float re =getUser(seq2id.get(row)).get1_simValue(seq2id.get(col));
////                   System.out.println("id"+u.ID+"row"+row+"col"+col+"re"+re);
////                   System.out.println(re);
//                   transMatrix[row][col] = re;
               }

        }
        simMatrix = add(simMatrix, transMatrix); //initial beginning simMatrix
        for (int hop = 1; hop < walk_step; hop++) {
            tempMatrix= mmltiple(simMatrix, transMatrix);
            simMatrix = add(simMatrix,tempMatrix );
            // v->u(the 3th step of algorithm 3.1)simMatrix[u][v]+=temMatrix[u][v]
        }
        int target_user_seq = id2seq.get(u.ID);
        for (int sequence = 0; sequence < n_hop_neighbor_size; sequence++) {
            String v_id=seq2id.get(sequence);
            float sim_uv= simMatrix[target_user_seq][sequence];
            UserInfo v=getUser(v_id);
            float weight=(float)Math.pow(sim_uv, index) * v.getIDegree(); //force compute
            u.add_Sim(v_id,weight); //save the corelation
        }

}

    //initialazation. includes: 1-step correlation and random walk
     int initUserInfo(boolean perturb, double epsilon) {
        LinkedList<String> userSet = AllUser();
//         System.out.println(userSet);
//         System.out.println(userSet.size());
       // int userNum = userSet.size();
        int ousumEdge = 0;
        int insumEdge = 0;
         for (String ID : userSet) {
             UserInfo u = getUser(ID);
             u.setDegree();
//             u.setNumOfAff();
//             System.out.println("u.numOfAff+"+u.numOfAff);
             ousumEdge += u.getOutDegree();
             insumEdge += u.getInDegree();

         }
        System.out.println("出度和" + ousumEdge + "入度和" + insumEdge);

         for (String anUserSet : userSet) {  //为每个用户初始化one-hop相关度
             float sum = 0.0f;
             UserInfo u = getUser(anUserSet);
//             System.out.println("uid"+u.ID);
             if (!u.adjInList.isEmpty()) {
//                 System.out.println(u.adjInList);
                 for (String a : u.adjInList) {
//                     UserInfo k = getUser(a);
                     float sim_ku = (float) (1.0 - alpha) / u.in_degree;
//                     System.out.println("u.in_degree"+u.in_degree+"sim_ku"+sim_ku);
                     u.add_Sim(a, (u.get1_simValue(a) + sim_ku));
//                     System.out.println("candidateSimin"+u.candidateSim.get(a));
                 }
             }
             if (!u.adjOutList.isEmpty()) {
                 for (String a : u.adjOutList) {
                     UserInfo k = getUser(a);
                     float sim_ku = (alpha / (float) u.out_degree) + (float) (1.0 - alpha);
                     u.add_Sim(a, ( u.get1_simValue(a) + sim_ku));
//                     System.out.println("candidateSimout"+u.candidateSim.get(a));
                 }
             }
             for (String a : u.candidateSim.keySet()) {
                 sum += u.candidateSim.get(a);
             }
//             System.out.println(sum);
             if (sum != 0.0f) {
                 for (String a : u.candidateSim.keySet()) {
                     float value = u.candidateSim.get(a);
                     u.add_Sim(a, (value / sum));  //初始化相关度归一化处理
                 }
             }
         }
        System.out.println("候选节点集相关度初始化成功！");

        int osumEdge = 0;
        int isumEdge = 0;
//         System.out.println(userSet);
//         for (String anUserSet : userSet){
//             UserInfo uu = getUser(anUserSet);
//             if (uu.ID.equals("33"))
//             {
//                 System.out.println(uu.ID);
//                 System.out.println(uu.out_degree);
//                    System.out.println(uu.in_degree);}
//
//
//         }

             //call random walk correlation computation, you can change the walk_step.
         for (String anUserSet : userSet) {
             UserInfo u = getUser(anUserSet);
//                UserInfo u = getUser("33");
//             System.out.println("uid" +u.ID);

             LRW(u);
             u.setPeDegree(perturb, epsilon);  //degree differential disturb
             osumEdge += u.getODegree();
             isumEdge += u.getIDegree();
         }
        System.out.println("差分隐私扰动后：出度和" + osumEdge + "入度和" + isumEdge);

         for (String anUserSet : userSet) {
             UserInfo u = getUser(anUserSet);
             Map<String, Float> candidate = u.candidateSim;
             if (candidate.containsKey(u.ID)) {   //remove itself from its candidates
                 candidate.remove(u.ID);
             }
             LinkedList<String> list1 = new LinkedList<>(candidate.keySet());
             u.setFistCandidateSorted(list1);
         }
         //System.out.println("bianji:"+osumEdge);
        return osumEdge;
    }


}
