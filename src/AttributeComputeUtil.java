
import java.util.LinkedList;

public class AttributeComputeUtil {
    public static void initAttributeInfo(AttributeList al,UserList ul) {
        LinkedList<String> attSet = al.AllAttribute();  //所有的属性
        for (String attName : attSet) {
            int linkNum = 0;
            al.getGroup(attName).setNum_member();
            for (String user : al.getGroup(attName).member) { // 拥有该属性的用户
                LinkedList<String> tmp = new LinkedList<String>(
                        al.getGroup(attName).member);
                System.out.println("拥有该属性的用户"+tmp);
                tmp.remove(user);
                System.out.println("用户的邻居"+ul.getUser(user).adjOutList);
                tmp.retainAll(ul.getUser(user).adjOutList);
                linkNum += tmp.size();
            }
            al.getGroup(attName).setLink_member(linkNum);
            al.getGroup(attName).setCoefficent(linkNum);
            //
        }
        System.out.println("属性聚集系数计算完毕");
    }

    //计算扰动后的属性聚集系数
    public  static void computePercoefficient(AttributeList al,UserList ul) {
        LinkedList<String> attSet = al.AllAttribute();

        for (String attName : attSet) {
            int linkNum = 0;
            al.getGroup(attName).setNum_perturbmember();    //设置扰动后的属性拥有的成员个数
            for (String user : al.getGroup(attName).perturb_Member) {
                LinkedList<String> tmp = new LinkedList<String>(
                        al.getGroup(attName).perturb_Member);
                tmp.remove(user);
                tmp.retainAll(ul.getUser(user).Prob_AdjList);
                linkNum += tmp.size();
            }
            //	System.out.print("attName"+attName+"linkNum"+linkNum);
            al.getGroup(attName).setPerLink_member(linkNum);
            al.getGroup(attName).setPerCoefficent(linkNum);
        }
    }
    //属性聚集系数平均相对误差
    public static float Mae_Attcoefficient(AttributeList al) {
        int nvals = al.Size();
        LinkedList<String> attSet = al.AllAttribute();
        double mae = 0;
        for (String attName : attSet) {
            mae += Math.abs(al.getGroup(attName).coefficent
                    - al.getGroup(attName).perCoefficent);
        }
        return (float) (mae / nvals);
    }
    //属性的海宁格距离
    public static float hellinger_Attcoefficient(AttributeList al) {
        double sum = 0;
        double suma = 0;
        double distance = 0;
        LinkedList<String> attSet = al.AllAttribute();
        for (String attName : attSet) {
            sum += al.getGroup(attName).coefficent;
            suma += al.getGroup(attName).perCoefficent;
        }
        System.out.println("sum"+sum);
        System.out.println("suma"+suma);
        for (String attName : attSet) {
            double v1 = al.getGroup(attName).coefficent / sum;
            double v2 = al.getGroup(attName).perCoefficent / suma;
            distance += Math.pow(v1 - v2, 2);
        }
        System.out.println("distance"+distance);
        return (float) (distance / Math.sqrt(2));
    }

}
