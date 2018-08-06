//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class SimCompute {
    private static final float per_parameter = 0.8f; //attribute disturb
    private static final float per_Const = 0.9f;
//    private static Logger logger = LoggerFactory.getLogger(SimCompute.class);
    private UserList ul;
    private AttributeList al;
    private List<String> edges;
    private int sumLength;

    public SimCompute() {
        ul = new UserList();
        al = new AttributeList();
        edges = new ArrayList<String>();
    }

    public static File getFileFromSd(File path, String fileDot) {
        File[] files = path.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (fileName.endsWith(fileDot)) {
                    System.out.println(file);
                    return file;
                }
            }
        }
        System.out.println("该社交网络不存在" + fileDot + "类型文件");
        return null;
    }

    public boolean preProcess(File DATASET_PATH,boolean perturb, boolean directer, double epsilon) {
        boolean attrNode = true;
        try {
            ul.createUserList(getFileFromSd(DATASET_PATH, "node"));
            ul.createAdjMember(getFileFromSd(DATASET_PATH, "edge"), directer);
            sumLength = ul.initUserInfo(perturb,epsilon); // 初始化出入度及排序
            if ((getFileFromSd(DATASET_PATH, "attributeNode") == null) || (getFileFromSd(DATASET_PATH, "attributeEdge") == null)) {
                attrNode = false;
                System.out.println("该社交网络为无属性的简单图");
            } else {
                ul.createAttribute(getFileFromSd(DATASET_PATH, "attributeEdge"));
                al.createAttList(getFileFromSd(DATASET_PATH, "attributeNode"));
                al.createMember(getFileFromSd(DATASET_PATH, "attributeEdge"));
                AttributeComputeUtil.initAttributeInfo(al, ul);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return attrNode;
    }

    public void computeErrorProcess() {
        AttributeComputeUtil.computePercoefficient(al, ul);
        System.out.println("属性聚集系数平均相对误差为" + AttributeComputeUtil.Mae_Attcoefficient(al));
        System.out.println("属性聚集系数海宁哥距离" + AttributeComputeUtil.hellinger_Attcoefficient(al));

    }

    public void generateAttribute(File DATASET_PATH, String dateSet,double epsilon2) throws IOException {
        LinkedList<String> userSet = ul.AllUser();
        List<String> attInfo = new LinkedList<String>();
        List<String> actInfo = new LinkedList<String>();
        for (String user : userSet) {
            UserInfo u = ul.getUser(user);
            u.setNumOfAff();
            u.setPerNumOfAff(epsilon2);
            int i = 0;
            while (u.Prob_Affiliation.isEmpty()) {
                i = 0;
                for (String att : ul.getUser(user).Affiliation) { // 用户user的所有属性的聚集系数
                    float coe = al.getGroup(att).coefficent;
                    double c = per_Const
                            * (per_parameter + (1 - per_parameter)
                            * Math.sqrt(coe));
                    double uniformDistributionVar = Math.random();
                    if (uniformDistributionVar < c) {
                        u.Prob_Affiliation.add(att);
                        al.getGroup(att).addPerMember(user);  //扰动后的属性成员
                        i++;

                    }
                    if (u.getPerAffSize() >= u.numOfPerAff)
                        break;  //break "for"
                }
                int index = 0;
                System.out.println(user + "熟悉的节点：" + u.firstCandidateSorted);
                while (u.getPerAffSize() < u.numOfPerAff) {

                    String ID = u.firstCandidateSorted.get(index);
                    index++;
                    List<String> a = new LinkedList<String>(
                            ul.getUser(ID).Affiliation);
                    a.removeAll(u.Prob_Affiliation);
                    if (!a.isEmpty()) {
                        for (String can : a) {
                            float coe = al.getGroup(can).coefficent;
                            double c = 1
                                    - per_Const
                                    * (per_parameter + (1 - per_parameter)
                                    * Math.sqrt(coe));
                            double uniformDistributionVar = Math.random();
                            if (uniformDistributionVar < c) {
                                u.Prob_Affiliation.add(can);
                                //System.out.print("用户"+user);
                                al.getGroup(can).addPerMember(user);//扰动后的属性成员
                            }
                            if (u.getPerAffSize() >= u.numOfPerAff)
                                break;
                        }
                    }
                    if ((index >= u.firstCandidateSorted.size())
                            || (u.getPerAffSize() >= u.numOfPerAff))
                        break;
                }
            }
            System.out.println(user + "[真实属性个数" + u.Affiliation.size()
                    + "][扰动后属性" + u.Prob_Affiliation.size() + "]保持原有" + i
                    + u.Prob_Affiliation);

            attInfo.add(u.PerAttributeToString());
            actInfo.add(u.AttributeToString());
        }
        FileUtil.writeTextFile(DATASET_PATH.getAbsolutePath() + "/dateSet.att", attInfo);
        FileUtil.writeTextFile(DATASET_PATH + "ActAttributeInfo.att", actInfo);
    }

    public void generateUDLink(File DATASET_PATH,String dataSet) throws IOException { //图生成过程
        LinkedList<String> userSet = ul.AllUser();
        LinkedList<String> sequence = new LinkedList<>();
        if (sumLength % 2 == 1) {
            System.out.println("扰动后度的和为奇数，不符合无向图的要求.正在进行处理。。。");
            String ID1 = ul.getFirstNodeID(userSet);
            ul.getUser(ID1).o_degree = ul.getUser(ID1).getODegree() - 1;
        }
        for (String user : userSet) { //按照出度倍将用户加入sequence中
            for (int num = 0; num < ul.getUser(user).getODegree(); num++) {
                sequence.add(user);
            }
        }
        while (!sequence.isEmpty()) {
            int radom = (int) (Math.random() * sequence.size());
            String ID1 = sequence.get(radom);  // sample vi

            if (sequence.isEmpty())
                break;
            UserInfo node1 = ul.getUser(ID1);
            if (!(node1.firstCandidateSorted).isEmpty()) {
                FtSample ftSample=new FtSample();
                int seqNum=ftSample.getPrizeIndex(node1.firstCandidateSorted,node1.candidateSim);
                String IDn=node1.firstCandidateSorted.get(seqNum);
                UserInfo noden = ul.getUser(IDn);
                while (!sequence.contains(IDn)) {
                    node1.firstCandidateSorted.remove(IDn);
                    if ((node1.firstCandidateSorted).isEmpty()) {
                        IDn = null;
                        break;
                    } else
                        IDn = node1.firstCandidateSorted.get(ftSample.getPrizeIndex(node1.firstCandidateSorted,node1.candidateSim));
                }
                if (IDn != null) {
                    String edge = ID1 + " " + IDn;
                    node1.Prob_AdjList.add(IDn);
                    noden.Prob_AdjList.add(ID1);
                    sequence.remove(ID1);
                    sequence.remove(IDn);
                    node1.firstCandidateSorted.remove(IDn);
                    noden.firstCandidateSorted.remove(ID1);
                    edges.add(edge);
                }
            }
            else {
                radom = (int) (Math.random() * sequence.size());
                String IDn = sequence.get(radom);
                UserInfo noden = ul.getUser(IDn);
                while (ID1.equals(IDn) || node1.Prob_AdjList.contains(IDn) || noden.Prob_AdjList.contains(ID1)) {
                    radom = (int) (Math.random() * sequence.size());
                    System.out.println("190radom"+radom);
                    IDn = sequence.get(radom);
                }
                String edge = ID1 + " " + IDn;
                node1.Prob_AdjList.add(IDn);
                noden.Prob_AdjList.add(ID1);
                sequence.remove(ID1);
                sequence.remove(IDn);
                edges.add(edge);
            }
        }
        System.out.println("合成图边集大小" + edges.size());
        FileUtil.writeTextFile(DATASET_PATH.getAbsolutePath() +dataSet+ ".edges", edges);

    }

    void generateDLink(File DATASET_PATH, String dataSet) throws IOException {
        LinkedList<String> userSet = ul.AllUser();
        LinkedList<String> Outsequence = new LinkedList<String>();
        LinkedList<String> Insequence = new LinkedList<String>();
        for (String user : userSet) {
            for (int num = 0; num < ul.getUser(user).getODegree(); num++) {
                Outsequence.add(user);
            }
            for (int num = 0; num < ul.getUser(user).getIDegree(); num++) {
                Insequence.add(user);
            }
        }
        int length = Outsequence.size();
        System.out.println("出度和" + length);
        System.out.println("入度和" + Insequence.size());
        while ((!Outsequence.isEmpty()) && (!Insequence.isEmpty())) {
            int radom = (int) (Math.random() * Outsequence.size());
            String ID1 = Outsequence.get(radom);
            UserInfo node1 = ul.getUser(ID1);
            if (!(node1.firstCandidateSorted).isEmpty()) {
                FtSample ftSample=new FtSample();
                int seqNum=ftSample.getPrizeIndex(node1.firstCandidateSorted,node1.candidateSim);
                String IDn=node1.firstCandidateSorted.get(seqNum);
                while (!Insequence.contains(IDn)) {
                    node1.firstCandidateSorted.remove(IDn);
                    if ((node1.firstCandidateSorted).isEmpty()) {
                        IDn = null;
                        break;
                    } else
                        IDn = node1.firstCandidateSorted.get(ftSample.getPrizeIndex(node1.firstCandidateSorted,node1.candidateSim));
                }
                if (IDn != null) {
                    String edge = ID1 + " " + IDn;
                    node1.Prob_AdjList.add(IDn);
                    Outsequence.remove(ID1);
                    Insequence.remove(IDn);
                    node1.firstCandidateSorted.remove(IDn);
                    edges.add(edge);
                }

            }
            else {
                radom = (int) (Math.random() * Insequence.size());
                String IDn = Insequence.get(radom);
                while (ID1 == IDn || node1.Prob_AdjList.contains(IDn)) {
                    radom = (int) (Math.random() * Insequence.size());
//                    System.out.println("251radom"+radom);
                    IDn = Insequence.get(radom);
                }
                String edge = ID1 + " " + IDn;
                node1.Prob_AdjList.add(IDn);
                Outsequence.remove(ID1);
                Insequence.remove(IDn);
                edges.add(edge);
            }


        }
        System.out.println("边集大小" + edges.size());
        FileUtil.writeTextFile(DATASET_PATH.getAbsolutePath() + dataSet+".edges", edges);
    }


}
