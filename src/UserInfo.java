
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserInfo {
    private static final int degree_sensitivity = 2;
    private static final int numOfAff_sensitivity = 2;
    public String ID;
    private int position;
    public LinkedList<String> Affiliation; // 附属信息
    public LinkedList<String> Prob_Affiliation;
    public LinkedList<String> adjOutList;
    public LinkedList<String> adjInList;
    public Map<String, Float> candidateSim;  //save the correlation with candidate nodes
    public LinkedList<String> firstCandidateSorted;
    public LinkedList<String> secondCandidateSorted;
    public LinkedList<String> Prob_AdjList;

    public int out_degree;
    public int in_degree;
    public int o_degree;
    public int i_degree;
    public int numOfAff;
    public int numOfPerAff;

    public UserInfo(String ID, int position) {
        this.ID = ID;
        this.position = position;
        this.adjOutList = new LinkedList<String>();
        this.adjInList = new LinkedList<String>();
        this.Affiliation = new LinkedList<String>();
        this.Prob_Affiliation = new LinkedList<String>();
        this.firstCandidateSorted = new LinkedList<String>();
        this.secondCandidateSorted = new LinkedList<String>();
        this.candidateSim = new HashMap<String, Float>();
        this.Prob_AdjList=new LinkedList<String>();
    }

    public float get1_simValue(String key) {
        if (candidateSim.get(key) == null)
            return 0.0f;
        else
            return candidateSim.get(key);
    }

    public void add_Sim(String key, float value) {
        candidateSim.put(key, value);
    }

    public void setPeDegree(boolean perturb,double epsilon1) {
        int odegree = out_degree;
        int idegree = in_degree;
        if (perturb){
            odegree += differiencialNoise(degree_sensitivity,epsilon1);
            idegree += differiencialNoise(degree_sensitivity,epsilon1);
            while ((odegree < 0) || (idegree < 0)
                    || ((odegree == 0) && (idegree == 0))
                    || (odegree > candidateSim.size())
                    || (idegree > candidateSim.size())) {
                odegree = out_degree + differiencialNoise(degree_sensitivity,epsilon1);
                idegree = in_degree + differiencialNoise(degree_sensitivity,epsilon1);
            }
        }
        this.o_degree = odegree;
        this.i_degree = idegree;
    }

    public void setNumOfAff() {
        this.numOfAff = Affiliation.size();
    }
    public void setPerNumOfAff(double epsilon2) {
        System.out.println(this.Affiliation);
        int num = Affiliation.size() + differiencialNoise(numOfAff_sensitivity,epsilon2);
        while((num<Math.sqrt(Affiliation.size()))||(num>4*Affiliation.size())){
            num = Affiliation.size() + differiencialNoise(numOfAff_sensitivity,epsilon2);
        }
        this.numOfPerAff = num;
    }
    public int getODegree() {
        return o_degree;
    }
    public int getIDegree() {
        return i_degree;
    }


    public void setFistCandidateSorted(LinkedList<String> list) {
        this.firstCandidateSorted = list;
    }

    public String toString() {
        String M = "ID:" + ID + "位置：" + position + ";出度： " + getODegree()
                + ";入度：" + getIDegree();
        return M;
    }
    public String AttributeToString(){
        String N=ID;
        for(String s:Affiliation)
            N+=" "+s;
        return N;
    }
    public String PerAttributeToString(){
        String N=ID+":";
        for(String s:Prob_Affiliation)
            N+=" "+s;
        return N;
    }



    public LinkedList<String> AllHip1User() {
        return (LinkedList<String>) union(adjInList, adjOutList);
    }

    public void addAdjOut(String value) {
        adjOutList.add(value);
    }

    public int getPerAffSize() {
        return Prob_Affiliation.size();
    }

    public void addAdjIn(String value) {
        adjInList.add(value);
    }

    public void addAttri(String value) {
        Affiliation.add(value);
    }


    public void setDegree() {
        int o = adjOutList.size();
        this.out_degree = o;
        this.in_degree = adjInList.size();
    }

    public int getOutDegree() {
        return out_degree;
    }

    public int getInDegree() {
        return in_degree;
    }

    public int differiencialNoise(int sensitivity,double epsilon) {

        double uniformDistributionVar, noise;
        uniformDistributionVar = Math.random();
        if (uniformDistributionVar == 0) {
            noise = Double.NEGATIVE_INFINITY;
        } else if (0 < uniformDistributionVar && uniformDistributionVar < 0.5) {
            noise = sensitivity / epsilon
                    * Math.log(2 * uniformDistributionVar);
        } else{
//            System.out.println(epsilon);

            noise = -sensitivity / epsilon
                    * Math.log(2 - 2 * uniformDistributionVar);
        }
//        System.out.println("noise"+noise);
        BigDecimal n = new BigDecimal(noise).setScale(0,BigDecimal.ROUND_HALF_UP);
//        System.out.println("n"+n);
//        System.out.println("n.intValue()"+n.intValue());

        return n.intValue();
    }

    public <T> List<T> union(LinkedList<T> ls, LinkedList<T> ls2) {
        List<T> list = new LinkedList<T>(ls);
        List<T> list2 = new LinkedList<T>(ls2);
        list2.removeAll(list);
        list.addAll(list2);

        return list;
    }


}
